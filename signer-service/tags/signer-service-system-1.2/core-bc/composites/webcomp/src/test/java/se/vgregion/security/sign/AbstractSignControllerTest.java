package se.vgregion.security.sign;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

public class AbstractSignControllerTest {
    @Mock
    private SignatureService signatureService;
    @Mock
    private Repository<ELegType, String> eLegTypes;
    private AbstractSignController signController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signController = new AbstractSignController(signatureService, eLegTypes, TicketManager.getInstance()) {
        };
    }

    @Test
    public final void sholudReturnSignatureService() throws Exception {
        assertSame(signatureService, signController.getSignatureService());
    }

    @Test
    public final void shouldReturnLegTypes() throws Exception {
        assertSame(eLegTypes, signController.geteLegTypes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void shouldReturnUnmodifiableCollectionOfClientTypes() throws Exception {
        // Given
        Collection<ELegType> allTypes = Arrays.asList(new ELegType("a", "a", "a", PkiClient.NEXUS_PERSONAL_4),
                new ELegType("b", "b", "b", PkiClient.NEXUS_PERSONAL_4));
        given(eLegTypes.findAll()).willReturn(allTypes);

        // When
        Collection<ELegType> actualTypes = signController.getClientTypes();

        // Then
        assertEquals(allTypes.size(), actualTypes.size());
        actualTypes.add(new ELegType("c", "c", "c", PkiClient.NETMAKER_NETID_4));
    }

    @Test
    public final void shouldPrepareSignResponse() throws Exception {
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        final String nonce = "nonce";
        signData.setClientType(new ELegType("", "", "", clientType));

        // Given
        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.generateNonce(any(PkiClient.class))).willReturn(nonce);

        // When
        String clientTypeString = signController.prepareSign(signData);

        // Then
        assertEquals(encodedTbs, signData.getEncodedTbs());
        assertEquals(nonce, signData.getNonce());
        assertEquals(clientType.toString(), clientTypeString);
    }

    @Test
    public final void shouldVerifySignature() throws Exception {
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        signData.setClientType(new ELegType("", "", "", clientType));

        // Given
        // given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);

        // When
        boolean result = signController.verifySignature(signData);

        // Then
        assertTrue(result);
    }

    @Test(expected = SignatureException.class)
    public final void shouldThrowSignatureExceptionIfVerificationFails() throws Exception {
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        signData.setClientType(new ELegType("", "", "", clientType));

        // Given
        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        doThrow(new SignatureException()).when(signatureService).verifySignature(any(SignatureData.class));

        // When
        boolean result = signController.verifySignature(signData);

        // Then
        assertTrue(result);
        assertEquals(encodedTbs, signData.getEncodedTbs());
    }
}
