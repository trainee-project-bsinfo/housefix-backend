package eu.bsinfo.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection implements IDatabaseConnection{
    Connection connection;

    @Override
    public Connection openConnection() {
        return connection;
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

        String createReadingTable = "CREATE TABLE IF NOT EXISTS IReading (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "comment TEXT, " +
                "dateOfReading DATE NOT NULL, " +
                "kindOfMeter ENUM('ELECTRICITY', 'WATER', 'GAS', 'HEATING'), " +
                "meterCount DOUBLE NOT NULL, " +
                "meterId VARCHAR(255), " +
                "substitute BOOLEAN, " +
                "customer_id INT, " +
                "FOREIGN KEY (customer_id) REFERENCES ICustomer(customer_id) ON DELETE SET NULL" +
                ");";

        try (Statement stmt = connection.createStatement()) {
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

        try (Statement stmt = connection.createStatement()) {
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

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropReadingTable);
            stmt.execute(dropCustomersTable);
            System.out.println("Tabellen 'ICustomer' und 'IReading' erfolgreich entfernt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Entfernen der Tabellen: " + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Fehler beim Schließen der Datenbankverbindung: " + e.getMessage());
            }
        }
    }
}