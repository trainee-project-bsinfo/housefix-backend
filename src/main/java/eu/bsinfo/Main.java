package eu.bsinfo;

import eu.bsinfo.db.DatabaseConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        try {
            DatabaseConnection conn = new DatabaseConnection("housefix", user, password);
            conn.openConnection();
            conn.createAllTables();
            conn.truncateAllTables();
            conn.removeAllTables();
            conn.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while(true) {}
    }
}