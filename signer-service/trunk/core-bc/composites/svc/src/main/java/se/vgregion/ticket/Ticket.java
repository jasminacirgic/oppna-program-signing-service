package se.vgregion.ticket;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;


public final class Ticket {

    private Long due;
    private byte[] signature;

    // Default access constructor
    Ticket(Long due, byte[] signature) {
        this.due = due;
        this.signature = signature;
    }

    public boolean isValid() {
        TicketManager.INSTANCE.verifySignature(this);
        final long now = System.currentTimeMillis();
        return (!isNull(due) && due >= now);
    }

    public long getDue() {
        return due;
    }

    public byte[] getSignature() {
        return signature;
    }

    private static boolean isNull(Object o) {
        return o == null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getSignatureAsBase64() {
        return Base64.encodeBase64String(signature);
    }
}
