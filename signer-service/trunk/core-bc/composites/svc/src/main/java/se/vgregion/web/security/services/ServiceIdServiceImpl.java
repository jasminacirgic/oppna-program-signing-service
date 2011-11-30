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
 * @author Patrik Bergström
 */
public class ServiceIdServiceImpl implements ServiceIdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceIdServiceImpl.class);

    private File propertyFile;
    private Properties properties = new Properties();

    public ServiceIdServiceImpl(String fileName, long scanInterval) throws IOException {
        propertyFile = new File(fileName);
        if (!propertyFile.exists()) {
            createPropertyFile();
            LOGGER.info(String.format("Created %s as property file.", propertyFile.getAbsolutePath()));
        } else {
            LOGGER.info(String.format("Using existing %s as property file.", propertyFile.getAbsolutePath()));
        }

        properties.load(new FileInputStream(propertyFile));

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
            properties.store(out, "Modified by " + this.getClass().getName() + ". Store entries e.g. like " +
                    "72f6929e-8b72-4e1b-8b7e-49b99cc6c43b=The service name");
        } finally {
            out.close();
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
    public String getAppName(String serviceId) {
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
            out.close();
        }
    }

    private void reloadProperties() {
        try {
            properties.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            LOGGER.error("Failed to load properties.", e);
        }
    }

    private static class ServiceIdFileMonitor implements FileListener {

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

    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
    }
}
