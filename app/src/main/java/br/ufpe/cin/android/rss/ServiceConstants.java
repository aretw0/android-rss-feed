package br.ufpe.cin.android.rss;

public enum ServiceConstants {
    DATA_UPDATE("DATA_UPDATE"),
    DATA_ERROR("DATA_ERROR"),
    DATA_REFRESH("DATA_REFRESH"),
    XML_ERROR("XML_ERROR");

    private String flag;

    ServiceConstants(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }
}
