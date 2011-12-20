package se.vgregion.web.security.services;

import org.junit.*;

import java.io.*;

import static org.junit.Assert.*;

/**
 * @author Patrik Bergström
 */
public class ServiceIdServiceImplTest {

    private static final String SERVICE_ID_TEST_FILE_NAME = "serviceIdTestFile.safeToRemove";
    private static final String MY_SERVICE_ID = "MY_SERVICE_ID";
    private static final String MY_SERVICE_NAME = "MY_SERVICE_NAME";

    private ServiceIdService service;

    @BeforeClass
    public static void setupFile() throws IOException {
        ServiceIdServiceImpl serviceIdTestFile = new ServiceIdServiceImpl(SERVICE_ID_TEST_FILE_NAME, 100000000);
        File file = new File(SERVICE_ID_TEST_FILE_NAME);
        if (!file.exists()) {
            throw new IllegalStateException("The file " + file.getAbsolutePath() + " wasn't created by the service.");
        } else {
            System.out.println(file.getAbsolutePath() + " was created.");
        }
        serviceIdTestFile.storeServiceId(MY_SERVICE_ID, MY_SERVICE_NAME);
    }
    
    @AfterClass
    public static void tearDown() {
        File file = new File(SERVICE_ID_TEST_FILE_NAME);
        boolean delete = file.delete();
        System.out.println("Deletion of " + file.getAbsolutePath() + ": Success=" + delete);
    }

    @Before
    public void setup() throws IOException {
        service = new ServiceIdServiceImpl(SERVICE_ID_TEST_FILE_NAME, 100);
    }
    
    @Test
    public void testContainsServiceId() throws Exception {
        boolean contains = service.containsServiceId(MY_SERVICE_ID);
        assertTrue(contains);
    }

    @Test
    public void testGetApplicationName() throws Exception {
        String applicationName = service.getApplicationName(MY_SERVICE_ID);
        assertEquals(MY_SERVICE_NAME, applicationName);
    }

    @Test
    public void testStoreServiceId() throws Exception {
        service.storeServiceId("anotherServiceId", "anotherServiceName");
        boolean contains = service.containsServiceId("anotherServiceId");
        assertTrue(contains);
    }

    @Test
    public void testReloadPropertiesAfterFileIsChanged() throws IOException, InterruptedException {
        String newLine = System.getProperty("line.separator");

        File file = new File(SERVICE_ID_TEST_FILE_NAME);
        FileWriter fileWriter = new FileWriter(file, true);

        fileWriter.append("externallyAddedServiceId=name" + newLine);
        fileWriter.close();

        FileReader in = new FileReader(file);
        BufferedReader br = new BufferedReader(in);

        System.out.println("File contents:");
        String buf;
        while ((buf = br.readLine()) != null) {
            System.out.println(buf);
        }
        
        br.close();
        in.close();
        
        //first check that it doesn't exist directly
        assertFalse(service.containsServiceId("externallyAddedServiceId"));

        Thread.sleep(3000);

        System.out.println("Finished waiting. Checking again...");
        assertTrue(service.containsServiceId("externallyAddedServiceId"));
    }
    
}
