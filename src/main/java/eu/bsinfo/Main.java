package eu.bsinfo;

import eu.bsinfo.db.DatabaseConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        try {
            new DatabaseConnection("housefix", user, password)
                    .openConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while(true) {}
    }
}