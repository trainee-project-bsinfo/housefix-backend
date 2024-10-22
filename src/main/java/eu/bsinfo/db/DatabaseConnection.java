package eu.bsinfo.db;

import eu.bsinfo.manager.ConfigManager;
import eu.bsinfo.manager.ConfigProperties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

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
        if (conn != null) {
            return conn;
        }
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
        StringBuilder genderEnums = new StringBuilder();
        String[] genderNames = Arrays.stream(Gender.values()).map(Gender::toString).toArray(String[]::new);
        for (int i = 0; i < genderNames.length; i++) {
            String genderName = genderNames[i];
            genderEnums.append("'").append(genderName).append("'");
            if (i < genderNames.length - 1) {
                genderEnums.append(", ");
            }
        }

        String createCustomersTable = "CREATE TABLE IF NOT EXISTS "+Tables.CUSTOMERS+" (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "firstName VARCHAR(255) NOT NULL, " +
                "lastName VARCHAR(255) NOT NULL, " +
                "birthDate DATE, " +
                "gender ENUM("+genderEnums+")" +
                ");";

        StringBuilder kindOfMeterEnums = new StringBuilder();
        String[] kindOfMeterNames = Arrays.stream(KindOfMeter.values()).map(KindOfMeter::toString).toArray(String[]::new);
        for (int i = 0; i < kindOfMeterNames.length; i++) {
            String kindOfMeterName = kindOfMeterNames[i];
            kindOfMeterEnums.append("'").append(kindOfMeterName).append("'");
            if (i < kindOfMeterNames.length - 1) {
                kindOfMeterEnums.append(", ");
            }
        }

        String createReadingTable = "CREATE TABLE IF NOT EXISTS "+Tables.READING+" (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "comment TEXT, " +
                "dateOfReading DATE NOT NULL, " +
                "kindOfMeter ENUM("+kindOfMeterEnums+")," +
                "meterCount DOUBLE NOT NULL, " +
                "meterId VARCHAR(255), " +
                "substitute BOOLEAN, " +
                "customer_id INT, " +
                "CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES "+Tables.CUSTOMERS+"(id) ON DELETE SET NULL" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createCustomersTable);
            stmt.execute(createReadingTable);
            System.out.println("Tabellen erfolgreich erstellt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tabellen: " + e.getMessage());
        }
    }

    @Override
    public void truncateAllTables() {
        String disableFKChecks = "SET FOREIGN_KEY_CHECKS = 0;";
        String enableFKChecks = "SET FOREIGN_KEY_CHECKS = 1;";
        String truncateReadingTable = "TRUNCATE TABLE "+Tables.CUSTOMERS+";";
        String truncateCustomersTable = "TRUNCATE TABLE "+Tables.READING+";";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(disableFKChecks);
            stmt.execute(truncateReadingTable);
            stmt.execute(truncateCustomersTable);
            stmt.execute(enableFKChecks);
            System.out.println("Tabellen erfolgreich geleert.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Leeren der Tabellen: " + e.getMessage());
        }
    }

    @Override
    public void removeAllTables() {
        String dropFK = "ALTER TABLE "+Tables.READING+" DROP FOREIGN KEY fk_customer;";
        String dropTables = "DROP TABLE IF EXISTS "+Tables.CUSTOMERS+","+Tables.READING+";";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(dropFK);
            stmt.execute(dropTables);
            System.out.println("Tabellen erfolgreich entfernt.");
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