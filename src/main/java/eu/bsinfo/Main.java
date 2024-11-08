package eu.bsinfo;

import eu.bsinfo.db.DatabaseConnection;
import eu.bsinfo.web.Server;

import java.rmi.ServerException;

public class Main {
    public static void main(String[] args) throws ServerException {
        String db = System.getenv("MYSQL_DATABASE");
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        DatabaseConnection dbConn = new DatabaseConnection(db, user, password);
        Server.startServer("http://0.0.0.0:8080/", dbConn);
    }

    public static boolean isInTestMode() {
        return Boolean.parseBoolean(System.getProperty("testing", "false"));
    }
    public static UnsupportedOperationException getOnlyForTestingException() {
        return new UnsupportedOperationException("This method is for testing purposes only: activate test mode with -Dtesting");
    }
}