package eu.bsinfo.db.enums;

public enum Tables {
    CUSTOMERS("customers"),
    READING("readings");

    private final String name;
    Tables(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
