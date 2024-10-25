package eu.bsinfo.db.enums;

public enum Gender {
    DIVERS("DIVERS"),
    FEMALE("FEMALE"),
    MALE("MALE"),
    UNSPECIFIED("UNSPECIFIED");

    private final String name;
    Gender(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
