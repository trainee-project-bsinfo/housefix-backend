package eu.bsinfo.web;

import eu.bsinfo.db.DatabaseConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.Properties;

public class ServerIT {
    @Test
    public void testIfItStartsStopsAndReturnsDbConn() throws IOException, SQLException {
        Properties config = new Properties();
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        DatabaseConnection dbConn = new DatabaseConnection(config.getProperty("DB_DBNAME"), config.getProperty("DB_USER"), config.getProperty("DB_PASSWORD"))
                .openConnection();

        Server.startServer("http://0.0.0.0:8080/", dbConn);
        Assertions.assertEquals(dbConn, Server.getDbConn());
        Server.stopServer();
    }

    @Test
    public void coverage() {
        new Server();
        Assertions.assertThrows(ServerException.class, () -> Server.startServer("", null));
        Server.stopServer();
    }
}
