package eu.bsinfo;

import eu.bsinfo.db.DatabaseConnection;
import eu.bsinfo.web.Server;

public class Main {
    public static void main(String[] args) {
        String db = System.getenv("MYSQL_DATABASE");
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        DatabaseConnection dbConn = new DatabaseConnection(db, user, password);
        Server.startServer("http://0.0.0.0:8080/", dbConn);
    }
}