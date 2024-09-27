package eu.bsinfo.manager;

public enum ConfigProperties {
    DB_BASE_URI("DB_BASE_URI");

    private final String value;

    ConfigProperties(String v) {
        value = v;
    }

    @Override
    public String toString() {
        return value;
    }
}
