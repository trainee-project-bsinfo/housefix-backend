package eu.bsinfo.db;

import eu.bsinfo.manager.ConfigManager;
import eu.bsinfo.manager.ConfigProperties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection implements IDatabaseConnection {
    private final String db;
    private final String user;
    private final String password;

    private Connection conn;

    public DatabaseConnection(String db, String user, String password) {
        this.db = db;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection openConnection() throws SQLException {
        String baseUrl;
        try {
            baseUrl = ConfigManager.getProperty(ConfigProperties.DB_BASE_URI);
        } catch (IOException e) {
            throw new SQLException(e);
        }
        conn = DriverManager.getConnection(baseUrl + db, user, password);
        return conn;
    }

    public Connection getConnection() {
        return conn;
    }

    @Override
    public void createAllTables() {
        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "firstName VARCHAR(255) NOT NULL, " +
                "lastName VARCHAR(255) NOT NULL, " +
                "birthDate DATE, " +
                "gender ENUM('MALE', 'FEMALE', 'OTHER')" +
                ");";

        String createReadingTable = "CREATE TABLE IF NOT EXISTS reading (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "comment TEXT, " +
                "dateOfReading DATE NOT NULL, " +
                "kindOfMeter ENUM('ELECTRICITY', 'WATER', 'GAS', 'HEATING'), " +
                "meterCount DOUBLE NOT NULL, " +
                "meterId VARCHAR(255), " +
                "substitute BOOLEAN, " +
                "customer_id INT, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createCustomersTable);
            stmt.execute(createReadingTable);
            System.out.println("Tabellen 'ICustomer' und 'IReading' erfolgreich erstellt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tabellen: " + e.getMessage());
        }

    }

    @Override
    public void truncateAllTables() {
        // SQL-Befehle zum Leeren der Tabellen ICustomer und IReading
        String truncateReadingTable = "TRUNCATE TABLE IReading;";
        String truncateCustomersTable = "TRUNCATE TABLE ICustomer;";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(truncateReadingTable);
            stmt.execute(truncateCustomersTable);
            System.out.println("Tabellen 'ICustomer' und 'IReading' erfolgreich geleert.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Leeren der Tabellen: " + e.getMessage());
        }
    }

    @Override
    public void removeAllTables() {
        // SQL-Befehle zum Löschen der Tabellen ICustomer und IReading
        String dropReadingTable = "DROP TABLE IF EXISTS IReading;";
        String dropCustomersTable = "DROP TABLE IF EXISTS ICustomer;";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(dropReadingTable);
            stmt.execute(dropCustomersTable);
            System.out.println("Tabellen 'ICustomer' und 'IReading' erfolgreich entfernt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Entfernen der Tabellen: " + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Fehler beim Schließen der Datenbankverbindung: " + e.getMessage());
            }
        }
    }
}