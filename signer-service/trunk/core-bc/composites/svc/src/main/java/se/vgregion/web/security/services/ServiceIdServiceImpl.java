package se.vgregion.web.security.services;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * This implementation of {@link ServiceIdService} uses a property file to keep track of the allowed service ids.
 * The property file is monitored for file changes and the class reloads the properties into the memory.
 *
 * @author Patrik Bergström
 */
public class ServiceIdServiceImpl implements ServiceIdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceIdServiceImpl.class);

    private File propertyFile;
    private Properties properties = new Properties();

    /**
     * Constructor.
     *
     * @param fileName the file name of the property file to use for storage of allowed service ids.
     * @param scanInterval the interval, in milliseconds, to poll for changes in the property file.
     *
     * @throws IOException when something fails in relation to the input and output of the property file
     */
    public ServiceIdServiceImpl(String fileName, long scanInterval) throws IOException {
        propertyFile = new File(fileName);
        if (!propertyFile.exists()) {
            createPropertyFile();
            LOGGER.info(String.format("Created %s as property file.", propertyFile.getAbsolutePath()));
        } else {
            LOGGER.info(String.format("Using existing %s as property file.", propertyFile.getAbsolutePath()));
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(propertyFile);
            properties.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        //start file monitor on the property file
        startFileMonitor(scanInterval);
    }

    private void createPropertyFile() throws IOException {
        boolean created = propertyFile.createNewFile();
        if (!created) {
            throw new IllegalStateException("Could not create property file. Cannot continue.");
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(propertyFile);
            properties.store(out, "Modified by " + this.getClass().getName() + ". Store entries e.g. like "
                    + "72f6929e-8b72-4e1b-8b7e-49b99cc6c43b=The service name");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void startFileMonitor(long scanInterval) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        FileObject listenFile = fsManager.resolveFile(propertyFile.getAbsolutePath());
        DefaultFileMonitor defaultFileMonitor = new DefaultFileMonitor(new ServiceIdFileMonitor(this));
        defaultFileMonitor.setDelay(scanInterval);
        defaultFileMonitor.addFile(listenFile);
        defaultFileMonitor.start();
        LOGGER.info("Started file monitor on " + propertyFile.getAbsolutePath());
    }

    @Override
    public boolean containsServiceId(String serviceId) {
        return properties.containsKey(serviceId);
    }

    @Override
    public String getApplicationName(String serviceId) {
        return properties.getProperty(serviceId);
    }

    @Override
    public void storeServiceId(String serviceId, String appName) throws IOException {
        properties.put(serviceId, appName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(propertyFile);
            properties.store(out, "Modified by " + this.getClass().getName());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void reloadProperties() {
        FileInputStream in = null;
        try {
            in = new FileInputStream(propertyFile);
            properties.load(in);
        } catch (IOException e) {
            LOGGER.error("Failed to load properties.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final class ServiceIdFileMonitor implements FileListener {

        private ServiceIdServiceImpl service;

        private ServiceIdFileMonitor(ServiceIdServiceImpl service) {
            this.service = service;
        }

        @Override
        public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {

        }

        @Override
        public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {

        }

        @Override
        public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
            LOGGER.info("Properties file changed. Reloading properties.");
            service.reloadProperties();
        }
    }

    /**
     * A main method to generate {@link UUID}s.
     *
     * @param args args
     */
    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
    }
}
