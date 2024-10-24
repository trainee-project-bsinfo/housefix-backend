package eu.bsinfo.db;

public enum Gender {
    D("DIVERS"),
    F("FEMALE"),
    M("MALE"),
    U("UNSPECIFIED");

    private final String name;
    Gender(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
