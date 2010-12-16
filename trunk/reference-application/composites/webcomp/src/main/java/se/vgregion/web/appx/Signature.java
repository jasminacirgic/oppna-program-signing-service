package se.vgregion.web.appx;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

public class Signature extends AbstractEntity<Integer> {
    byte[] signature;

    public Signature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public Integer getId() {
        return Arrays.hashCode(signature);
    }

    public String getDecoded() {
        return new String(Base64.decodeBase64(signature));
    }

    @Override
    public String toString() {
        return new String(signature);
    }
}