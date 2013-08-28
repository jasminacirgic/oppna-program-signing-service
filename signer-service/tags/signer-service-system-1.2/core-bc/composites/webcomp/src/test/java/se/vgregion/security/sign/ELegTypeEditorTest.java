package se.vgregion.security.sign;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.web.exceptions.IllegalWebArgumentException;

public class ELegTypeEditorTest {
    @Mock
    private Repository<ELegType, String> eLegTypes;
    @Mock
    private ELegType eLegType;
    private ELegTypeEditor editor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        editor = new ELegTypeEditor(eLegTypes);
    }

    @Test
    public final void shouldSetClientTypeAsText() throws Exception {
        // Given
        given(eLegTypes.find(anyString())).willReturn(eLegType);
        // When
        editor.setAsText("valid client type");
        // Then
        assertSame(eLegType, editor.getValue());
    }

    @Test(expected = IllegalWebArgumentException.class)
    public final void shouldThrowExceptionIfInvalidClientTypeString() throws Exception {
        // Given
        given(eLegTypes.find(anyString())).willReturn(null);
        // When
        editor.setAsText("invalid client type");
    }
}
