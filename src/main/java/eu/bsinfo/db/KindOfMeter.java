package eu.bsinfo.db;

public enum KindOfMeter {
    HEATER("HEATER"),
    ELECTRICITY("ELECTRICITY"),
    WATER("WATER"),
    UNKNOWN("UNKNOWN");

    private final String name;
    KindOfMeter(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
