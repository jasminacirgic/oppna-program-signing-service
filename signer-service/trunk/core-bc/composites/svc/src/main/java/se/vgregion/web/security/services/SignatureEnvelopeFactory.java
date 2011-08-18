package se.vgregion.web.security.services;


import se.vgregion.proxy.signera.signature.SignatureEnvelope;
import se.vgregion.proxy.signera.signature.SignatureFormat;

public class SignatureEnvelopeFactory {

    public static SignatureEnvelope createSignatureEnvelope(String signatureName, SignatureFormat signatureFormat, String signature) {
        SignatureEnvelope result = new SignatureEnvelope();
        result.setErrorCode(0);
        result.setErrorMessage("OK");
        result.setSignatureName(signatureName);
        result.setSignatureFormat(signatureFormat);
        result.setSignature(signature);
        return result;
    }

    public static SignatureEnvelope createSignatureEnvelope(int errorCode, String errorMessage) {
        SignatureEnvelope result = new SignatureEnvelope();
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }


}
