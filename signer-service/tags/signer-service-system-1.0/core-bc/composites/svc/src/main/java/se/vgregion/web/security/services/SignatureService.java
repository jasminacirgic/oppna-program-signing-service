package se.vgregion.web.security.services;

import java.security.SignatureException;

import se.vgregion.domain.security.pkiclient.PkiClient;

public interface SignatureService {

    /**
     * Verifies message from the client when signing data.
     * 
     * @param signData
     *            the data to sign.
     * @throws SignatureException
     *             if verification of the signature is invalid in some way.
     */
    void verifySignature(SignatureData signData) throws SignatureException;

    /**
     * Encodes the tbs - To Be Signed according to the pki clients requirement.
     * 
     * @param tbs
     *            data To Be Signed.
     * @param provider
     *            the pki client used when signing the tbs
     * @return returns a base64-encoded string of tbs
     * @throws SignatureException
     *             if something went wrong when encoding the tbs
     */
    String encodeTbs(String tbs, PkiClient provider) throws SignatureException;

    /**
     * Generates a random string used to prevent replay attacks. The number is generated according to the pki
     * clients requirement.
     * 
     * @param provider
     *            the pki client used for signing
     * @return returns a random string
     * @throws SignatureException
     *             if the generation of the nonce faild
     */
    String generateNonce(PkiClient provider) throws SignatureException;

    /**
     * Saves the signature using the information supplied in signData. Where and how the signature is saved is
     * supplied in signData and if the signature is saved as a file a random name is created. To set you own name
     * use {@link SignatureServiceOsif#save(SignatureData, String)} instead. The method can return a string
     * containing a callback url, the method client should redirect to the callback url.
     * 
     * @param signData
     *            information about the signature
     * @return a callback url
     * @throws SignatureException
     *             if the save was unsuccessful
     */
    String save(SignatureData signData) throws SignatureException;

    /**
     * Saves the signature using the information supplied in signData ie. where and how the signature is saved. The
     * method can return a string containing a callback url, the method client should redirect to the callback url.
     * 
     * @param signData
     *            information about the signature
     * @param signatureName
     *            the name under which the signature is saved
     * @return a callback url
     * @throws SignatureException
     *             if the save was unsuccessful
     */
    String save(SignatureData signData, String signatureName) throws SignatureException;

    /**
     * Aborts the signing with error message supplied in the {@link SignatureData}. The
     * method can return a string containing a callback url, the method client should redirect to the callback url.
     * @param signData
     *            information about why the signing was aborted
     * @return a callback url
     * @throws SignatureException
     *             if the abort was unsuccessful
     */
    String abort(SignatureData signData) throws SignatureException;

}