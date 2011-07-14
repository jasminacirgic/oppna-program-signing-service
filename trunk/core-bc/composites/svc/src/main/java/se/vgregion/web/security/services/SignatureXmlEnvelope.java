/**
 * 
 */
package se.vgregion.web.security.services;

import se.vgregion.domain.security.pkiclient.PkiClient.SignatureFormat;

/**
 * @author anders
 * 
 */
public final class SignatureXmlEnvelope {
    private String signature = "";
    private String signatureName = "";
    private SignatureFormat signatureFormat = null;
    private int errorCode = 0;
    private String errorMessage = "OK";

    public SignatureXmlEnvelope(String signatureName, SignatureFormat signatureFormat, String signature) {
        this.signature = signature;
        this.signatureName = signatureName;
        this.signatureFormat = signatureFormat;
    }
    public SignatureXmlEnvelope(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getSignatureName() {
        return signatureName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<signatureenvelope>\n");
        sb.append("<errorcode>").append(errorCode).append("</errorcode>\n");
        sb.append("<errormessage>").append(errorMessage).append("</errormessage>\n");
        sb.append("<signaturename>").append(signatureName).append("</signaturename>\n");
        sb.append("<signatureformat>").append(signatureFormat).append("</signatureformat>\n");
        sb.append("<signature>").append(signature).append("</signature>\n");
        sb.append("</signatureenvelope>\n");
        return sb.toString();
    }
}
