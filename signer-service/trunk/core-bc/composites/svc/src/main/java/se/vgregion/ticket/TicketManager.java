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
 * Singleton package private ticket manager, responsible to manage all solved tickets in a store. It keeps the
 * tickets in a concurrent map since both {@link Ticket} and {@link TicketCleanup} writes and reads to the store.
 * The class is an internal for the Ticket process thus its declared package private.
 *
 * @author Anders Asplund
 */
public class TicketManager {

    private static final Logger LOG = LoggerFactory.getLogger(TicketManager.class);
    private static final Long MILLIS_IN_A_MINUTE = 1000L * 60;
    private static final Long KEEP_ALIVE = 5 * MILLIS_IN_A_MINUTE; // 5 minutes;
    private static final String keyAlgorithm = "DSA";
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
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyAlgorithm);
            kpg.initialize(KEY_SIZE, new SecureRandom());
            keyPair = kpg.generateKeyPair();
            signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

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

    public Ticket solveTicket(String serviceId) throws TicketException {
        boolean exists = serviceIdService.containsServiceId(serviceId);
        if (!exists) {
            throw new TicketException(String.format("Service-id %s cannot be found.", serviceId));
        }

        long due = System.currentTimeMillis() + KEEP_ALIVE;
        due = due / 1000 * 1000; //round to whole seconds

        byte[] signature = createSignature(due);
        Ticket ticket = new Ticket(due, signature);
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
            Signature signature = this.signature;
            signature.initVerify(keyPair.getPublic());
            signature.update(dueToBytes(ticket.getDue()));
            return signature.verify(ticket.getSignature());
        }catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

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
