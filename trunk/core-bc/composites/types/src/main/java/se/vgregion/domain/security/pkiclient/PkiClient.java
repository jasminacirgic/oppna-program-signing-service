package se.vgregion.domain.security.pkiclient;

public enum PkiClient {
    IBM_CBT_25(1), SMARTTRUST_PERSONAL_30(2), NEXUS_PERSONAL_4(4), NETMAKER_NETID_4(5), NEXUS_PERSONAL_4X(6);
    private int id;

    private PkiClient(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}