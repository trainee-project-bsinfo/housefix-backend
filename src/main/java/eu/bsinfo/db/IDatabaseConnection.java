package eu.bsinfo.db;

import java.sql.SQLException;

public interface IDatabaseConnection {
    IDatabaseConnection openConnection() throws SQLException;
    void createAllTables();
    void truncateAllTables();
    void removeAllTables();
    void closeConnection();
}
