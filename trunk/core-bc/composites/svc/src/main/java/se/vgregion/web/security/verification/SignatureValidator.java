package se.vgregion.web.security.verification;

import java.net.URL;
import java.security.SignatureException;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sll.wsdl.soap.osif.Osif;
import se.sll.wsdl.soap.osif.OsifService;
import se.sll.wsdl.soap.osif.VerifySignatureRequest;
import se.sll.wsdl.soap.osif.VerifySignatureResponse;
import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.web.security.OsifUtil;

public class SignatureValidator {

    private static Logger LOGGER = LoggerFactory.getLogger(SignatureValidator.class);

    public static void validate(String signature, String tbs, PkiClient pkiClient) throws SignatureException {
        URL wsdl = OsifService.class.getResource("/se/sll/wsdl/soap/osif/osif.wsdl");
        OsifService service = new OsifService(wsdl, new QName("urn:www.sll.se/wsdl/soap/osif", "OsifService"));
        Osif osif = service.getOsif();
        OsifUtil.setEndpointAddress(osif, "http://193.44.157.195:18899/osif/");

        VerifySignatureRequest request = new VerifySignatureRequest();
        request.setTbsText(tbs);
        request.setNonce("AFASDFASDF");
        request.setProvider(pkiClient.getId());
        request.setSignature(signature);
        request.setPolicy("logtest014");
        VerifySignatureResponse response = osif.verifySignature(request);

        if (response.getStatus().getErrorCode() != 0) {
            String errorMsg = response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription();
            LOGGER.error(errorMsg);

            throw new SignatureException(response.getStatus().getErrorGroupDescription());
        }

    }
}
