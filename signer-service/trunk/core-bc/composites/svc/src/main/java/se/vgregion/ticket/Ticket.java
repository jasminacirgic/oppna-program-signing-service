package se.vgregion.ticket;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;


public final class Ticket {

    private Long due;
    private byte[] signature;

    /**
     * Constructor.
     *
     * @param due a {@link Long} denoting the validity date
     * @param signature a byte array with the signature of the due
     */
    public Ticket(Long due, byte[] signature) {
        this.due = due;
        this.signature = signature.clone();
    }

    public Long getDue() {
        return due;
    }

    public byte[] getSignature() {
        return signature.clone();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getSignatureAsBase64() {
        return Base64.encodeBase64String(signature);
    }
}
