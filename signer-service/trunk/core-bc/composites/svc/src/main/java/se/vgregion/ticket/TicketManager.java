package se.vgregion.ticket;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.web.security.services.ServiceIdService;

/**
 * Class for managing tasks associated with {@link Ticket}s, like creation and verification. To ensure that a
 * {@link Ticket} is valid it is created with a signature which can verify only if the timestamp of the
 * {@link Ticket} is not modified. Therefore it is impossible to tamper with the timestamp.
 * <p/>
 * The class is singleton since verification of a {@link Ticket} can only be made by the same instance which
 * signed it.
 *
 * @author Anders Asplund
 * @author Patrik Bergström
 */
public final class TicketManager {

    private static final Logger LOG = LoggerFactory.getLogger(TicketManager.class);
    private static final Long MILLIS_IN_A_MINUTE = 1000L * 60;
    private static final Long KEEP_ALIVE = 5 * MILLIS_IN_A_MINUTE; // 5 minutes;
    private static final String KEY_ALGORITHM = "DSA";
    private static final int KEY_SIZE = 1024;
    private static final String SIGNATURE_ALGORITHM = "SHA512withDSA";
    private static final String PROVIDER_NAME = "BC";

    //This is a Spring bean but created with a factory method so it's still a pure singleton.
    private static TicketManager instance = null;

    private ServiceIdService serviceIdService;

    private final KeyPair keyPair;
    private final Signature signature;

    private TicketManager() {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            kpg.initialize(KEY_SIZE, new SecureRandom());
            keyPair = kpg.generateKeyPair();
            signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Factory method to get the singleton instance.
     *
     * @return the instance
     */
    public static TicketManager getInstance() {
        if (instance == null) {
            TicketManager ticketManager = new TicketManager();
            instance = ticketManager;
        }
        return instance;
    }

    @Autowired
    public void setServiceIdService(ServiceIdService serviceIdService) {
        this.serviceIdService = serviceIdService;
    }

    /**
     * Solves a {@link Ticket}, i.e. creates a new {@link Ticket} which is signed. To solve a {@link Ticket} a
     * serviceId must be provided to authenticate the requester.
     *
     * @param serviceId serviceId
     * @return the new {@link Ticket}
     * @throws TicketException if the serviceId is not valid
     */
    public Ticket solveTicket(String serviceId) throws TicketException {
        boolean exists = serviceIdService.containsServiceId(serviceId);
        if (!exists) {
            throw new TicketException(String.format("Service-id %s cannot be found.", serviceId));
        }

        long due = System.currentTimeMillis() + KEEP_ALIVE;
        final int thousand = 1000;
        due = due / thousand * thousand; //round to whole seconds

        byte[] signatureBytes = createSignature(due);
        Ticket ticket = new Ticket(due, signatureBytes);
        return ticket;
    }

    private byte[] createSignature(Long due) {
        try {
            PrivateKey privateKey = keyPair.getPrivate();
            signature.initSign(privateKey);
            signature.update(dueToBytes(due));
            return signature.sign();
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verifySignature(Ticket ticket) {
        try {
            signature.initVerify(keyPair.getPublic());
            signature.update(dueToBytes(ticket.getDue()));
            return signature.verify(ticket.getSignature());
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies the {@link Ticket} according to its date and signature.
     *
     * @param ticket the {@link Ticket}
     * @return <code>true</code> if the {@link Ticket} is valid or <code>false</code> otherwise
     */
    public boolean verifyTicket(Ticket ticket) {
        return verifyDue(ticket) && verifySignature(ticket);
    }

    private boolean verifyDue(Ticket ticket) {
        final long now = System.currentTimeMillis();
        Long due = ticket.getDue();
        return (!isNull(due) && due >= now);
    }

    private static boolean isNull(Object o) {
        return o == null;
    }

    private byte[] dueToBytes(Long due) {
        return due.toString().getBytes();
    }

}
