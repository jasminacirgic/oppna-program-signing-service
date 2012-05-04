package se.vgregion.web.security.services;

import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.signera.signature._1.SignatureFormat;

/**
 * Factory class to create a {@link SignatureEnvelope}.
 * 
 * @author Anders Asplund - Callista Enterprise
 */
public final class SignatureEnvelopeFactory {

    private SignatureEnvelopeFactory() {
    }

    /**
     * Factory method to create an instance of {@link SignatureEnvelope}.
     * 
     * @param signatureName
     *            name of Signature
     * @param signatureFormat
     *            format of Signature (ie. CMS, XMLDIGSIG)
     * @param signature
     *            the signature to put in the envelope
     * @return an Envelope containing the signature and some metadata about it
     */
    public static SignatureEnvelope createSignatureEnvelope(String signatureName, SignatureFormat signatureFormat,
            String signature) {
        SignatureEnvelope result = new SignatureEnvelope();
        result.setErrorCode(0);
        result.setErrorMessage("OK");
        result.setSignatureName(signatureName);
        result.setSignatureFormat(signatureFormat);
        result.setSignature(signature);
        return result;
    }

    /**
     * Factory method to create an instance of {@link SignatureEnvelope}.
     * 
     * @param errorCode
     *            errorCode indicating why the signing wasn't completed
     * @param errorMessage
     *            errorMessage indicating why the signing wasn't completed
     * @return
     *            an Envelope containing the error information
     */
    public static SignatureEnvelope createSignatureEnvelope(int errorCode, String errorMessage) {
        SignatureEnvelope result = new SignatureEnvelope();
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }

}
