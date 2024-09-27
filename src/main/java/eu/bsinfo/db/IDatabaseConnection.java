package eu.bsinfo.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseConnection {
    Connection openConnection() throws SQLException;
    void createAllTables();
    void truncateAllTables();
    void removeAllTables();
    void closeConnection();

}
