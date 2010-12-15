package se.vgregion.security.sign;

import java.beans.PropertyEditorSupport;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.web.exceptions.IllegalWebArgumentException;

public class ELegTypeEditor extends PropertyEditorSupport {
    private ELegTypeRepository eLegTypes;

    public ELegTypeEditor(ELegTypeRepository eLegTypes) {
        this.eLegTypes = eLegTypes;
    }

    @Override
    public void setAsText(String clientType) {
        ELegType eLegType = eLegTypes.find(clientType);
        if (eLegType == null) {
            throw new IllegalWebArgumentException();
        }
        setValue(eLegType);
    }
}
