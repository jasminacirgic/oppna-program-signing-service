package se.vgregion.security.sign;

import java.beans.PropertyEditorSupport;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.web.exceptions.IllegalWebArgumentException;

/**
 * A {@link java.beans.PropertyEditor.PropertyEditor} which converts a {@link String} into a {@link ELegType}.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class ELegTypeEditor extends PropertyEditorSupport {
    private Repository<ELegType, String> eLegTypes;

    /**
     * Constructs an {@link ELegTypeEditor}.
     * 
     * @param eLegTypes
     *            repository containing all types of e-legitimation.
     */
    public ELegTypeEditor(Repository<ELegType, String> eLegTypes) {
        this.eLegTypes = eLegTypes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String clientType) {
        ELegType eLegType = eLegTypes.find(clientType);
        if (eLegType == null) {
            throw new IllegalWebArgumentException();
        }
        setValue(eLegType);
    }
}
