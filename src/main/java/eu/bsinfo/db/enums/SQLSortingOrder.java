package eu.bsinfo.db.enums;

public enum SQLSortingOrder {
    ASC("ASC"),
    DESC("DESC");

    private final String name;
    SQLSortingOrder(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
