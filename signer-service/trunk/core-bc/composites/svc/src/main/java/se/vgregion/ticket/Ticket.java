package se.vgregion.ticket;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;


public final class Ticket {

    private Long due;
    private byte[] signature;

    public Ticket(Long due, byte[] signature) {
        this.due = due;
        this.signature = signature;
    }

    public Long getDue() {
        return due;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getSignatureAsBase64() {
        return Base64.encodeBase64String(signature);
    }
}
