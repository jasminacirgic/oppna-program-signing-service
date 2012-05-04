package se.vgregion.security.sign;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.signera.signature._1.SignatureFormat;
import se.vgregion.signera.signature._1.SignatureStatus;
import se.vgregion.signera.signature._1.SignatureVerificationRequest;
import se.vgregion.signera.signature._1.SignatureVerificationResponse;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.ServiceIdService;
import se.vgregion.web.security.services.SignatureService;

/**
 * @author Patrik Bergström
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context-test.xml"})
public class RestSignControllerTest {

    private RestSignController controller;
    private String baseAddress = "http://localhost:9000/service";

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        TicketManager ticketManager = TicketManager.getInstance();
        ServiceIdService service = mock(ServiceIdService.class);
        when(service.containsServiceId(eq("existingServiceId"))).thenReturn(true);
        when(service.containsServiceId(eq("nonExistingServiceId"))).thenReturn(false);
        ticketManager.setServiceIdService(service);

        controller = new RestSignController(Mockito.mock(SignatureService.class), mock(Repository.class),
                ticketManager);

        //Start test server
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(RestSignController.class);
        sf.setResourceProvider(RestSignController.class, new SingletonResourceProvider(controller));
        sf.setAddress(baseAddress);
        sf.create();
    }

    @Test
    public void testVerifySignatureWithXmlDigSig() throws IOException, JAXBException {

        String signature = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></CanonicalizationMethod><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></SignatureMethod><Reference Type=\"http://www.bankid.com/signature/v1.0.0/types\" URI=\"#bidSignedData\"><Transforms><Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></Transform></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"></DigestMethod><DigestValue>kCVO0IxVlrqEhZeuZRv82wZJuovyf/eNINAcAUgfN6s=</DigestValue></Reference><Reference URI=\"#bidKeyInfo\"><Transforms><Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></Transform></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"></DigestMethod><DigestValue>FTApDbR+g+MtIMk9xPB53MTVPdw6b2VvMfhfZ+6EjNI=</DigestValue></Reference></SignedInfo><SignatureValue>XW1/KgfJqWn8fCSzYUmy2+2BWP4h0w/jkhTV+Ur6jpo26lez6olnACAuu+bWkf+QfhkoVOsylKAhMx1kaoYwjWedIfglPBN50b+MlxTE8beQL9637QRUPBnxoLEgt1VJVp2FtHinLb35fW+nuey+9uiT6CZNRQ2tTAYdd/lNRyY=</SignatureValue><KeyInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"bidKeyInfo\"><X509Data><X509Certificate>MIIDyjCCArKgAwIBAgIIVEpfAkREv5swDQYJKoZIhvcNAQEFBQAwcjELMAkGA1UEBhMCU0UxHTAbBgNVBAoMFFRlc3RiYW5rIEEgQUIgKHB1YmwpMRUwEwYDVQQFEwwxMTExMTExMTExMTExLTArBgNVBAMMJFRlc3RiYW5rIEEgZS1DdXN0b21lciBDQTEgZm9yIEJhbmtJRDAeFw0xMDA1MjAyMjAwMDBaFw0xMzA1MjAyMTU5NTlaMIGyMQswCQYDVQQGEwJTRTEdMBsGA1UECgwUVGVzdGJhbmsgQSBBQiAocHVibCkxEDAOBgNVBAQMB0phbnNzb24xDTALBgNVBCoMBMOFa2UxFTATBgNVBAUTDDE5MTcwNDEwOTI3OTE1MDMGA1UEKQwsKDEwMDUyMSAxNS4wMCkgw4VrZSBKYW5zc29uIC0gQmFua0lEIHDDpSBmaWwxFTATBgNVBAMMDMOFa2UgSmFuc3NvbjCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAkOWcTLOE9IytWh1T2W+9+OWruu87FCTktjgSPZR1R7Uw8pvWUAuCohkW7R5YLlEqjeJmLLTvIkruLCWY6mmM7GfJqL74pevsKLwIhtCEzZPVW+ONAT6UE/3roIeFyyic6uTVQKdR2/K0XyxhPLshj3xR3Qy/FgLnyBWfFiNrWg0CAwEAAaOBpjCBozA+BggrBgEFBQcBAQQyMDAwLgYIKwYBBQUHMAGGImh0dHA6Ly9jcGFjdC50ZXN0LnBraXNlcnZpY2VzLmNvbS8wEQYDVR0gBAowCDAGBgQqAwQFMA4GA1UdDwEB/wQEAwIGQDAdBgNVHQ4EFgQUzeeTGD+dzjHE0JDnIKUU3sMg5lYwHwYDVR0jBBgwFoAUKtf/ZyhdkGEJ1fuSyrzaCRAJc7owDQYJKoZIhvcNAQEFBQADggEBAHntbu8LKWyqmRjtSsgp0hXb0l3Jb18Ucpub35C2zIiwYsEZIg2FnNLRsn79TKjCgjXo0gLlTyhm+mWt2RL4NqVUJ48oYTYgj88oFx3g2/Q82ET/IhSfFynXS7rf4S4fbJOMq+IEqHAq91hrH4NF5IqjblPVZ8YiYgz74h2Bu1KDTSjN//0y8882JadgWQ+oPNQyAfG/KY0mzKJS0QrMxL6UlY9yCCz9hXQ3XXgQZoQQ3oD8ubQ4jg6zhh6JSNtD3zPZeoPoIUkFAbosJz53uN2HanqV6fPQSrEn0SKk10whs9WUNzVIcuEnOVPX/QK01YqL2k49ATTtuWrXsIn7s+I=</X509Certificate><X509Certificate>MIID4zCCAsugAwIBAgIRALRexXHQhgyanJhfjST8sc0wDQYJKoZIhvcNAQEFBQAwcDELMAkGA1UEBhMCU0UxHTAbBgNVBAoMFFRlc3RiYW5rIEEgQUIgKHB1YmwpMRUwEwYDVQQFEwwxMTExMTExMTExMTExKzApBgNVBAMMIlRlc3RiYW5rIEEgUm9vdCBDQSBmb3IgQmFua0lEIFRFU1QwHhcNMDUwODEwMDg0MDIxWhcNMTUxMjI1MTMxMDIxWjByMQswCQYDVQQGEwJTRTEdMBsGA1UECgwUVGVzdGJhbmsgQSBBQiAocHVibCkxFTATBgNVBAUTDDExMTExMTExMTExMTEtMCsGA1UEAwwkVGVzdGJhbmsgQSBlLUN1c3RvbWVyIENBMSBmb3IgQmFua0lEMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0vujokFQkWQ1WGs0edgFkySxEY4TY4t5AQTzJUQ0CvTw1jZqQM0c5isU882KgxOCa7fgbiER4yfptWeZ82FGXYyBq3OoD6g5cZCULzGqM7ofG6NLYvZVAkAjcEV759vDDXQ9X8yEeZCZWYEPLNNmKDmIGtL0yi/yjfWXOMClDPBSza13ywVaZD7RwM/Ma0+0BeWxffJOA32WIlwYcTvaOBNZvfcA1rSEitj1Gg6p4kH1RFlO4seQm6k1o6sMPjIePb0k8NIm/h24Gk/rl7DokJrIh7wjF3GjKHH4GY1A5RatD/Q8F3UsvPWMge2oDg7m+MSszcOWYk7046MinbboHwIDAQABo3YwdDAPBgNVHRMBAf8EBTADAQH/MBEGA1UdIAQKMAgwBgYEKgMEBTAOBgNVHQ8BAf8EBAMCAQYwHwYDVR0jBBgwFoAUH4pzW8rdg7jwRZJ2/scogHHHUbkwHQYDVR0OBBYEFCrX/2coXZBhCdX7ksq82gkQCXO6MA0GCSqGSIb3DQEBBQUAA4IBAQDWEieKNr2xK2WJ4hH9g0DnZ9nMJoC/pDsk+PSkx/zpei6P8Yh8x76JeE4+RkaNvUkBXAnRueMMhlgvRMONtV2IPx/ztOI9kXYj/DeSTmGmE+421uSMdFgIHebjDGtFPymGE88EqhtPzJeoIIk5SqQ1hZ2G1QAhVmFkIlUCfpe3eTR6p0kAGq7lOZXF2jX5L0kd8BOCeOWzKgv61ItjZ2pZYx9YXoXciBSCdvvsT2z2YRLuU9xP3n6ufik6s11l2gw2IPN5qwT3/hWE6tqvxonDQl8w/5F8SGF7vq0haYqIOQp45BUs2lSuYHVFXycWeIwKTtZ1Ji7aWr3xmxszOewO</X509Certificate><X509Certificate>MIID5jCCAs6gAwIBAgIQJ/Zz7DVJTGoHSGriMtOixzANBgkqhkiG9w0BAQUFADB2MQ0wCwYDVQQKDARCSURUMR0wGwYDVQQLDBRCSURUIE1lbWJlciBCYW5rcyBDQTEVMBMGA1UEBRMMMTExMTExMTExMTExMS8wLQYDVQQDDCZCSURUIFJvb3QgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkgVEVTVDAeFw0wNTA4MTAwODA1MDlaFw0xNTEyMzExMzA1MDlaMHAxCzAJBgNVBAYTAlNFMR0wGwYDVQQKDBRUZXN0YmFuayBBIEFCIChwdWJsKTEVMBMGA1UEBRMMMTExMTExMTExMTExMSswKQYDVQQDDCJUZXN0YmFuayBBIFJvb3QgQ0EgZm9yIEJhbmtJRCBURVNUMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA/Ncq+8F0ZkHJ7gLfm7qN3vlQpVlqs9bIbBUFs7ajPoYr/6SuHOda4lSGPy8mcYRw/z4EwIoxvQHOsX6qrLqN50SdkEFQQHn7RT8DLWjltAbrx+whgoKekC+CgTS3bUt6mq+6lO3yt2GNlSeLzCqHOTbRMsdW8PReRJ/wgg5B/5LgVjUVgjWTXSj3fXLGtxexJqRdQnTN7FjST8/Xd7zVEPrgXNxQGEpybqWsMMlKmK35FYoEDnSFpjLO7LiDzPYcfUe+Tl7dwoGIr1r6i3WM65wnepq34P+9/JO/J3JofHmffZno+Slqa1LyR8LESdbDRSdL9f52EFBv4iM3aizXhwIDAQABo3YwdDAPBgNVHRMBAf8EBTADAQH/MBEGA1UdIAQKMAgwBgYEKgMEBTAOBgNVHQ8BAf8EBAMCAQYwHwYDVR0jBBgwFoAUO2Se+IoWERRnl8nMp7NrBxej+E0wHQYDVR0OBBYEFB+Kc1vK3YO48EWSdv7HKIBxx1G5MA0GCSqGSIb3DQEBBQUAA4IBAQAh3zN6TkitneUtlhsWJzbyHTndNOAWyjYpTjuENPBbH2FgR+DxDL2zWgoxlPUcm32pHzVd0sSP2fK5mmqCbpN0gydoZ8NOuiKgGqsyO7mALzZXxApA/lAH5cLGcDKpWty98F7FIDjW1GX0rfbSfQqMGxQ3/pmjyD+qfYawKBc8+U9rfmhyGEduNHhWgqjiiQKaQP3dj33qOJ4erd8RR5jjTQ5odWRiERSAFL+6J9hpiHxT6p4anPufNRtsj2VPct2XUdEMe8ZsE17qxERCsfDqZA7DC9BEMUtWQRtOfw0u59JHHUlRyaK4j2N4UbEVourdUsoiGOVjToC8rXHlE+ZR</X509Certificate></X509Data></KeyInfo><Object><bankIdSignedData xmlns=\"http://www.bankid.com/signature/v1.0.0/types\" Id=\"bidSignedData\"><usrVisibleData charset=\"UTF-8\" visible=\"wysiwys\">SGVq</usrVisibleData><srvInfo><nonce>QVRRVHp3NU02alpxYWNpVUE4VWhmekU0ZTZTRzZKVWlxMXg0YVpIMkxCK1M=</nonce></srvInfo><clientInfo><funcId>Signing</funcId><host><fqdn>vgwb0046.vgregion.se</fqdn><ip>140.166.83.62</ip></host><version>UGVyc29uYWxfZXhlPTQuMTguMy4xMTAyNyZwZXJzaW5zdF9leGU9NC4xOC4zLjExMDI3JnRva2VuYXBpX2RsbD00LjE4LjMuMTEwMjcmcGVyc29uYWxfZGxsPTQuMTguMy4xMTAyMSZucF9wcnNubF9kbGw9NC4xOC4zLjExMDI3JmxuZ19zdnNlX2RsbD00LjE4LjMuMTEwMjcmbG5nX3BscGxfZGxsPTQuMTguMy4xMTAyNyZsbmdfbm9yX2RsbD00LjE4LjMuMTEwMjcmbG5nX2ZyZnJfZGxsPTQuMTguMy4xMTAyNyZsbmdfZmluX2RsbD00LjE4LjMuMTEwMjcmbG5nX2Rhbl9kbGw9NC4xOC4zLjExMDI3JmNyZHNpZW1fZGxsPTQuMTguMy4xMTAyNyZjcmRzZXRlY19kbGw9NC4xOC4zLjExMDI3JmNyZHByaXNtX2RsbD00LjE4LjMuMTEwMjcmY3JkZ3RvY2xzY19kbGw9NC4xOC4zLjExMDI3JmNyZGVkYl9kbGw9NC4xOC4zLjExMDI3JmJyX3N2c2VfZGxsPTQuMTguMy4xMTAyNyZicl9lbnVfZGxsPTQuMTguMy4xMTAyNyZicmFuZGluZ19kbGw9NC4xOC4zLjExMDI3JkNTUF9JTlNUQUxMRUQ9VFJVRSZQZXJzb25hbD00LjE4LjMuMTEwMjcmcGxhdGZvcm09d2luNjQmb3NfdmVyc2lvbj13aW43JmJlc3RfYmVmb3JlPTEzMjQ1NzA3NDcmZG9jU2lnbj0xJnVoaT15YWRBcTBGdFNlMjF1c0h5RlBTSHFSSGRBMTg9Jg==</version><env><ai><uhi>yadAq0FtSe21usHyFPSHqRHdA18=</uhi><utb>file</utb><rpr>7la0qSeqK5zQIj35LeJBN3OMQJg=</rpr><gbvv>RmuU7yVxt5D2rj0OTBJ8loQnFW0l08pJGA4NdxvY0YaPgn8U3wkRIb3wCuJfIAMnsjIF06iEu8ZDT+bqVDQlHA==</gbvv></ai></env></clientInfo></bankIdSignedData></Object></Signature>";

        //Create the SignatureVerificationRequest
        SignatureVerificationRequest request = new SignatureVerificationRequest();
        request.setSignature(new String(Base64.encode(signature.getBytes())));
        request.setSignatureFormat(SignatureFormat.XMLDIGSIG);

        //Marshal the SignatureVerificationRequest
        JAXBContext jc = JAXBContext.newInstance(SignatureVerificationRequest.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jc.createMarshaller().marshal(request, os);

        //Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<String>(os.toString(), headers);

        RestTemplate template = new RestTemplate();

        ResponseEntity<SignatureVerificationResponse> response = template.exchange(baseAddress + "/verifySignature",
                HttpMethod.POST, entity, SignatureVerificationResponse.class);

        SignatureVerificationResponse body = response.getBody();

        assertEquals(body.getStatus(), SignatureStatus.SUCCESS);

        //just to see the response in raw format
        ResponseEntity<String> stringResponse = template.exchange(baseAddress + "/verifySignature",
                HttpMethod.POST, entity, String.class);

        System.out.println(stringResponse.getBody());
    }

    @Test
    public void testVerifySignatureWithCms() throws IOException, JAXBException {

        String signature = "MIIKkAYJKoZIhvcNAQcCoIIKgTCCCn0CAQExCzAJBgUrDgMCGgUAMBMGCSqGSIb3DQEHAaAGBARTR1ZxoIIIbDCCBFQwggM8oAMCAQICEGtbXKTG/AZel6I+g2Yz/3UwDQYJKoZIhvcNAQEFBQAwRjELMAkGA1UEBhMCU0UxEzARBgNVBAoTClRlbGlhIFRlc3QxIjAgBgNVBAMTGVRlbGlhIGUtbGVnIFJvb3QgUFAgQ0EgdjEwHhcNMDkxMDIzMDY0NzIzWhcNMTUxMDEyMTI0NzIzWjBKMQswCQYDVQQGEwJTRTETMBEGA1UEChMKVGVsaWEgVGVzdDEmMCQGA1UEAxMdVGVsaWEgZS1sZWdpdGltYXRpb24gUFAgQ0EgdjIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDiJe/C+SAfGA0koWyrc4Mdldkozwh0ZSQSX+jugFUdm2ALX6vkdcmDvBzMMGiex3x5X1JMaeY7xrCjI1DE0uxAO+KJPTvM0+W3gBxCKXc6iPZniUzKR0FyQybZ44g1Nz5FKimeDfO83pi+yJMcmiD3wCPsEZjBB6uFQVaDcG87oJrlYI1QVlwECKdbv/B+mIg1a4UybUTe3IEg+dp0SvaHiU29yv0SyHEzT4EluUbziq50akt9VfTVH3SYsoJKVQiehXg7TUdWIBc8T1IEO+MyMHGJDqHkxwxpX/xbkjUM127Hy0odAxAeEiySfk4S3ysXoDUoatphmqZFda2xdJ1FAgMBAAGjggE4MIIBNDA/BgNVHSAEODA2MDQGBiqFcCNjAjAqMCgGCCsGAQUFBwIBFhxodHRwczovL3d3dy50cnVzdC50ZWxpYS5jb20vMIGPBgNVHR8EgYcwgYQwgYGgf6B9hntsZGFwOi8vbGRhcC5wcmVwcm9kLnRydXN0LnRlbGlhLmNvbS9jbj1UZWxpYSUyMGUtbGVnJTIwUm9vdCUyMFBQJTIwQ0ElMjB2MSxvPVRlbGlhJTIwVGVzdCxjPVNFP2F1dGhvcml0eXJldm9jYXRpb25saXN0P2Jhc2UwEgYDVR0TAQH/BAgwBgEB/wIBADALBgNVHQ8EBAMCAQYwHQYDVR0OBBYEFNhI3EgQc54kYzgfdPN9YmvoiP1fMB8GA1UdIwQYMBaAFNpo5D0VlQNYCZSAhfX9Oz5Bwe5pMA0GCSqGSIb3DQEBBQUAA4IBAQA7O7N0ABNu4MtUJHIvF7s6NOEWzHfgNmjjIN3IhR6kwq9v+Ypt6DWbt8T/cjLQAnF1tEp2k1koNz72LKb4YvJ6GnBHXJNUit2WJsfmS3wjwo4d7rWYyjZg0+Venuyr0JAypSaL73NczDcb0gJokBdXZqUy/fRoXs5gbrhA2+vQmVn4WwZQcWOmURh/EM/p2SuR/AM4Byf3HubWCJaPyteW04q/BATINOA/tMq82YrMpX8AAFCDL4OS6aeolcSWLECmCIvg1Ixb9leji5M2hzCwFYqPLQP+2//azK/yfTQdCRLgnfRIfi6KYao0Ag8O5SXXRJ7yFs9GskSyifgGUZ60MIIEEDCCAvigAwIBAgIQXlgXqKI34duOaprscsteYTANBgkqhkiG9w0BAQUFADBKMQswCQYDVQQGEwJTRTETMBEGA1UEChMKVGVsaWEgVGVzdDEmMCQGA1UEAxMdVGVsaWEgZS1sZWdpdGltYXRpb24gUFAgQ0EgdjIwHhcNMTEwMzE2MDkzNDM1WhcNMTUxMDEwMDkzNDM1WjBgMQswCQYDVQQGEwJTRTEXMBUGA1UEAwwOQWdkYSBBbmRlcnNzb24xEjAQBgNVBAQMCUFuZGVyc3NvbjENMAsGA1UEKgwEQWdkYTEVMBMGA1UEBRMMMTg4ODAzMDk5MzY4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjN80KrHreYIsudvRC/nTKK7CZPeMd1hNc82B+ZEkgOwvzZcCfjLpXh+5pyoTfBBFXOfHx3MPx2bj10sAc7Kwo08pnie2+gvRxbKT5h7GD1WtFeGmxlSkfufl4lx4pE1m1aNCrgR1UK5AJ2XoJYUQow1hPnoh7N1TpX13l5dpeqrYN1ZHgvaIh1hJQqb3IafcZZSlLxHOZvVd7pdGg9RC/W1onDtImq2PcrKGTTw+R6hPtX/KM30dv+jPZV4kGyXAvukNQQCpzs+d47VF7FlLd1FHgjSf8oPZLQw+lEVMR4ZydRTPfqKO7e30NoWA09sLVY3eFnZzxtXWTL0Z4ntjBwIDAQABo4HbMIHYMD8GCCsGAQUFBwEBBDMwMTAvBggrBgEFBQcwAYYjaHR0cDovL29jc3AucHJlcHJvZC50cnVzdC50ZWxpYS5jb20wRQYDVR0gBD4wPDA6BgYqhXAjYwIwMDAuBggrBgEFBQcCARYiaHR0cHM6Ly9yZXBvc2l0b3J5LnRydXN0LnRlbGlhLmNvbTAdBgNVHQ4EFgQU1XMqqdc1U+wYOD+m3KBI5JHLKQEwHwYDVR0jBBgwFoAU2EjcSBBzniRjOB90831ia+iI/V8wDgYDVR0PAQH/BAQDAgVAMA0GCSqGSIb3DQEBBQUAA4IBAQBOAs6zdJckaVzw6S9XuntlMaqmpeY8oqko+sfbjyJV6zgT6NvdLdSY+riCzBkvyQwRtio4z/cSacuaDbgkFMzYZxSh/wD0oTgffH7y6ztxMOjCh0sSLa6zeRQawvLi/IsDRlf0fVlflDyRvLw4fn2kGa9Jw4Az5cFwelUkyml3JoATr6yCTY88ePnvyuEqGQ9W8MGpUvPekrJwomdcwCZmRU+quwvXFk7+Ym1jbvARb4ajkg/bLwvqwIs+V1FpdX746v6aPtYL/bAbO833iJ6z3Jx5Nbo3qx8xMrbSmYw91qf8bl7gmpUfuqawj6CvKmZMX8qLLp0n1TaUYz/wDwwqMYIB5DCCAeACAQEwXjBKMQswCQYDVQQGEwJTRTETMBEGA1UEChMKVGVsaWEgVGVzdDEmMCQGA1UEAxMdVGVsaWEgZS1sZWdpdGltYXRpb24gUFAgQ0EgdjICEF5YF6iiN+Hbjmqa7HLLXmEwCQYFKw4DAhoFAKBdMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkFMQ8XDTExMTIwNzEzNDgyM1owIwYJKoZIhvcNAQkEMRYEFAWXD393tC3YXB1D/OTTtyluUD93MA0GCSqGSIb3DQEBAQUABIIBAAN6iJpoeQGXODMeFZSF3FeS6n+AeEwXnjHpXk0/eU8Cfja6a3dGPIswyc3vBcGLRjOVNzuecPDWYWbd6qRkkpmIhDem8TOKmUe2+QjXBpAbHo5jh/O8tCzLpc7ec3A8hPu5iHelRolzkfNT9tfXL0S3REoVHzes+zTyuKtlh1wKAAXAJdp6XP0rQwduuBhR8Yf4JxI3XN0LbiU5HswlnQ5qp944OInS8bUWOoR+kWAIXkWs3XFC1yD3nZAv+c/RVwC36dXmTZXOalJWAq1dzCkFzlPOS7TgX6UkkFHLbY8uIhVWPkx3v453uRI/uMreqppKe99Nys796yRmMFrkLjg=";

        //Create the SignatureVerificationRequest
        SignatureVerificationRequest request = new SignatureVerificationRequest();
        request.setSignature(signature);
        request.setSignatureFormat(SignatureFormat.CMS);

        //Marshal the SignatureVerificationRequest
        JAXBContext jc = JAXBContext.newInstance(SignatureVerificationRequest.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jc.createMarshaller().marshal(request, os);

        //Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<String>(os.toString(), headers);

        RestTemplate template = new RestTemplate();

        ResponseEntity<SignatureVerificationResponse> response = template.exchange(baseAddress + "/verifySignature",
                HttpMethod.POST, entity, SignatureVerificationResponse.class);

        SignatureVerificationResponse body = response.getBody();

        assertEquals(body.getStatus(), SignatureStatus.SUCCESS);

        //just to see the response in raw format
        ResponseEntity<String> stringResponse = template.exchange(baseAddress + "/verifySignature",
                HttpMethod.POST, entity, String.class);

        System.out.println(stringResponse.getBody());
    }

    @Test
    public void testSolveTicketWithWrongServiceId() throws IOException {
        URL url = new URL(baseAddress + "/solveTicket/nonExistingServiceId");
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

        int responseCode = httpURLConnection.getResponseCode();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), responseCode);
    }

    @Test
    public void testSolveTicketWithRightServiceId() throws IOException, TicketException {

        URL url = new URL(baseAddress + "/solveTicket/existingServiceId");
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

        int responseCode = httpURLConnection.getResponseCode();
        assertEquals(Response.Status.OK.getStatusCode(), responseCode);

        String contentType = httpURLConnection.getContentType();
        assertEquals("application/json", contentType);

        String response = extractBodyAsString(httpURLConnection).trim(); //remove the \r\n

        Ticket ticket = new TicketDto(response).toTicket();
        boolean valid = TicketManager.getInstance().verifyTicket(ticket);

        assertTrue(valid);

    }

    private String extractBodyAsString(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        int len = httpURLConnection.getContentLength();
        byte[] bytes = new byte[len];
        bis.read(bytes);
        return new String(bytes);
    }

}
