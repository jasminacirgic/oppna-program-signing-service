package se.vgregion.ticket;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton package private ticket manager, responsible to manage all solved tickets in a store. It keeps the
 * tickets in a concurrent map since both {@link Ticket} and {@link TicketCleanup} writes and reads to the store.
 * The class is an internal for the Ticket process thus its declared package private.
 *
 * @author Anders Asplund
 */
public enum TicketManager {
    INSTANCE; // Singleton

    private static final Logger LOG = LoggerFactory.getLogger(TicketManager.class);
    private static Map<String, Long> solvedTickets = new ConcurrentHashMap<String, Long>();
    private static final Long MILLIS_IN_A_MINUTE = 1000L * 60;
    private static final Long KEEP_ALIVE = 5 * MILLIS_IN_A_MINUTE; // 5 minutes;

    private KeyPair keyPair;
    private final String algorithm = "DSA";

    TicketManager() {
        try {
            KeyPairGenerator kg = KeyPairGenerator.getInstance(algorithm);
            final int keysize = 1024;
            kg.initialize(keysize);
            keyPair = kg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Ticket solveTicket(String serviceId) {
        //todo verify serviceId
        long due = System.currentTimeMillis() + KEEP_ALIVE;
        byte[] signature = createSignature(due);
        Ticket ticket = new Ticket(due, signature);
        return ticket;
    }

    void removeOldTickets() {
        LOG.info("Number of valid tickets in store: {}", solvedTickets.size());
        final Long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> ticketEntry : solvedTickets.entrySet()) {
            if (ticketEntry.getValue() < now) {
                LOG.info("Removing ticket: {}", ticketEntry);
                solvedTickets.remove(ticketEntry.getKey());
            }
        }
        LOG.info("Number of valid tickets in store: {}", solvedTickets.size());
    }

    public byte[] createSignature(Long due) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            PrivateKey privateKey = keyPair.getPrivate();
            signature.initSign(privateKey);
            signature.update(dueToBytes(due));
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifySignature(Ticket ticket) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(keyPair.getPublic());
            signature.update(dueToBytes(ticket.getDue()));
            return signature.verify(ticket.getSignature());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] dueToBytes(Long due) {
        return due.toString().getBytes();
    }
}
