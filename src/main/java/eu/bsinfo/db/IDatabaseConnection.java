package eu.bsinfo.db;

import java.util.Properties;

public interface IDatabaseConnection {
    IDatabaseConnection openConnerction(Properties properties);
    void createAllTables();
    void truncateAllTables();
    void removeAllTables();
    void closeConnection();

}
