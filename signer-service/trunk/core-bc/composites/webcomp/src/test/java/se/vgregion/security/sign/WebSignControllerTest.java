package se.vgregion.security.sign;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

public class WebSignControllerTest {
    @Mock
    private SignatureService signatureService;
    @Mock
    private Repository<ELegType, String> eLegTypes;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Exception exception;

    private WebSignController signController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signController = new WebSignController(signatureService, eLegTypes);
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void shouldReturnUnmodifiableCollectionOfClientTypes() throws Exception {
        // Given
        final Collection<ELegType> allTypes = Arrays.asList(new ELegType("a", "a", PkiClient.NEXUS_PERSONAL_4),
                new ELegType("b", "b", PkiClient.NEXUS_PERSONAL_4));
        given(eLegTypes.findAll()).willReturn(allTypes);

        // When
        Collection<ELegType> actualTypes = signController.getClientTypes();

        // Then
        assertEquals(allTypes.size(), actualTypes.size());
        actualTypes.add(new ELegType("c", "c", PkiClient.NETMAKER_NETID_4));
    }

    @Test
    public final void shouldBindELegTypeToWebDataBinder() throws Exception {
        // Given
        final WebDataBinder binder = new WebDataBinder(null);
        // When
        signController.initBinder(binder);
        // Then
        assertEquals(ELegTypeEditor.class, binder.findCustomEditor(ELegType.class, "").getClass());
    }

    @Test
    public final void shouldPrepareSigningIfNoClientTypeIsSpecified() throws Exception {
        // Given
        final Model model = new ExtendedModelMap();
        final SignatureData signData = new SignatureData();
        given(request.getRemoteHost()).willReturn("example.com");
        // When
        String viewName = signController.prepareSignNoClientType(signData, model, request);
        // Then
        assertEquals("clientTypeSelection", viewName);
        assertTrue(model.asMap().containsKey("signData"));
        assertTrue(model.asMap().containsValue(signData));
    }

    @Test
    public final void shouldPrepareForSignClientRequest() throws Exception {
        // Given
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        final String nonce = "nonce";
        final Model model = new ExtendedModelMap();
        signData.setClientType(new ELegType("", "", clientType));

        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.generateNonce(any(PkiClient.class))).willReturn(nonce);
        given(request.isSecure()).willReturn(false);
        given(request.getServerName()).willReturn("example.com");
        given(request.getServerPort()).willReturn(7080);
        given(request.getContextPath()).willReturn("/ss");

        // When
        String viewName = signController.prepareSign(signData, model, request);

        // Then
        assertEquals(clientType.toString(), viewName);
        assertTrue(model.asMap().containsKey("signData"));
        assertTrue(model.asMap().containsValue(signData));
        assertTrue(model.asMap().containsKey("postbackUrl"));
        assertEquals("http://example.com:7080/ss/sign", model.asMap().get("postbackUrl"));
    }

    @Test
    public final void shouldPrepareForSecureSignClientRequest() throws Exception {
        // Given
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        final String nonce = "nonce";
        final Model model = new ExtendedModelMap();
        signData.setClientType(new ELegType("", "", clientType));

        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.generateNonce(any(PkiClient.class))).willReturn(nonce);
        given(request.isSecure()).willReturn(true);
        given(request.getServerName()).willReturn("example.com");
        given(request.getServerPort()).willReturn(7080);
        given(request.getContextPath()).willReturn("/ss");

        // When
        String viewName = signController.prepareSign(signData, model, request);

        // Then
        assertEquals(clientType.toString(), viewName);
        assertTrue(model.asMap().containsKey("signData"));
        assertTrue(model.asMap().containsValue(signData));
        assertTrue(model.asMap().containsKey("postbackUrl"));
        assertEquals("https://example.com:7080/ss/sign", model.asMap().get("postbackUrl"));
    }

    @Test
    public final void shouldVerifyAndSaveSignatureWithRedirect() throws Exception {
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        final String expectedRedirectUrl = "http://www.example.com";
        signData.setClientType(new ELegType("", "", clientType));

        // Given
        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.save(any(SignatureData.class))).willReturn(expectedRedirectUrl);

        // When
        String redirectUrl = signController.verifyAndSaveSignature(signData);

        // Then
        assertEquals("redirect:" + expectedRedirectUrl, redirectUrl);
    }

    @Test
    public final void shouldVerifyAndSaveSignatureWithNoRedirect() throws Exception {
        final SignatureData signData = new SignatureData();
        final PkiClient clientType = PkiClient.NETMAKER_NETID_4;
        final String encodedTbs = "encodedTbs";
        signData.setClientType(new ELegType("", "", clientType));

        // Given
        given(signatureService.encodeTbs(anyString(), any(PkiClient.class))).willReturn(encodedTbs);
        given(signatureService.save(any(SignatureData.class))).willReturn(null);

        // When
        String redirectUrl = signController.verifyAndSaveSignature(signData);

        // Then
        assertEquals("verified", redirectUrl);
    }

    @Test
    public final void shouldHandleAllKindOfErrors() {
        // When
        ModelAndView modelAndView = signController.handleException(exception, request);
        // Then
        assertEquals("errorHandling", modelAndView.getViewName());
        assertTrue(modelAndView.getModelMap().containsKey("class"));
        assertTrue(modelAndView.getModelMap().containsValue(ClassUtils.getShortName(exception.getClass())));
    }
}
