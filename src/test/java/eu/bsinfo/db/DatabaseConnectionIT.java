package eu.bsinfo.db;

import eu.bsinfo.db.enums.Gender;
import eu.bsinfo.db.enums.Tables;
import eu.bsinfo.utils.UUIDUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionIT {
    private static DatabaseConnection conn;
    private static final UUID uuid = UUID.fromString("fb50b5c8-d100-44ad-b2c5-aad47016f564");

    @BeforeAll
    public static void loadConfigAndConnect() throws IOException, SQLException {
        Properties config = new Properties();
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        conn = new DatabaseConnection(config.getProperty("DB_DBNAME"), config.getProperty("DB_USER"), config.getProperty("DB_PASSWORD"))
                .openConnection();
    }
    @AfterAll
    static void disconnect() {
        conn.closeConnection();
    }

    @BeforeEach
    void createTables() {
        conn.createAllTables();
        conn.truncateAllTables();
    }
    @AfterEach
    void removeTables() {
        conn.removeAllTables();
    }

    @Test
    public void testCreateNecessaryTables() throws SQLException {
        Statement stmt = conn.getConnection().createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM information_schema.tables WHERE table_name = '"+Tables.READING+"';");
        assertTrue(rs.next());

        rs = stmt.executeQuery("SELECT * FROM information_schema.tables WHERE table_name = '"+Tables.CUSTOMERS+"';");
        assertTrue(rs.next());

        stmt.close();
    }

    @Test
    public void testTruncateTables() throws SQLException {
        String mutation = "INSERT INTO "+Tables.CUSTOMERS+" (id, firstName, lastName, birthDate, gender) VALUES (?,?,?,?,?);";
        PreparedStatement prepStmt = conn.getConnection().prepareStatement(mutation);
        prepStmt.setBytes(1, UUIDUtils.UUIDAsBytes(uuid));
        prepStmt.setString(2, "John");
        prepStmt.setString(3, "Doe");
        prepStmt.setDate(4, Date.valueOf("1980-01-01"));
        prepStmt.setString(5, Gender.MALE.toString());
        prepStmt.executeUpdate();
        prepStmt.close();

        Statement stmt = conn.getConnection().createStatement();

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM "+Tables.CUSTOMERS);
        assertTrue(rs.next());
        assertEquals(1, rs.getInt("count"));

        conn.truncateAllTables();

        rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM "+Tables.CUSTOMERS);
        assertTrue(rs.next());
        assertEquals(0, rs.getInt("count"));

        stmt.close();
    }

    @Test
    public void testRemoveTables() throws SQLException {
        conn.removeAllTables();
        Statement stmt = conn.getConnection().createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM information_schema.tables WHERE table_name = '"+Tables.CUSTOMERS+"';");
        assertFalse(rs.next());

        rs = stmt.executeQuery("SELECT * FROM information_schema.tables WHERE table_name = '"+Tables.READING+"';");
        assertFalse(rs.next());

        stmt.close();

        conn.createAllTables();
    }
}
