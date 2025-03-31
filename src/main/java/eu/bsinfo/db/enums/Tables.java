package eu.bsinfo.db.enums;

public enum Tables {
    CUSTOMERS("customers"),
    READINGS("readings"),
    USERS("users");

    private final String name;
    Tables(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
