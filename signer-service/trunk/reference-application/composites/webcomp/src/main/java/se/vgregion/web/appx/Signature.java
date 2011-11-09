package se.vgregion.web.appx;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;
import se.vgregion.signera.signature._1.SignatureFormat;

public class Signature extends AbstractEntity<Integer> {
    private byte[] signature;
    private SignatureFormat format;

    public Signature(byte[] signature, SignatureFormat format) {
        this.signature = signature;
        this.format = format;
    }

    public Integer getId() {
        return Arrays.hashCode(signature);
    }

    public String getDecoded() {
        return new String(Base64.decodeBase64(signature));
    }

    public SignatureFormat getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return new String(signature);
    }
}