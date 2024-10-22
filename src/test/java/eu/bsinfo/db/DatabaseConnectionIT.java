package eu.bsinfo.db;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionIT {
    private static DatabaseConnection conn;

    @BeforeAll
    public static void loadConfigAndConnect() throws IOException, SQLException {
        Properties config = new Properties();
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        conn = new DatabaseConnection("test", config.getProperty("DB_USER"), config.getProperty("DB_PASSWORD"));
        conn.openConnection();
    }
    @AfterAll
    static void disconnect() {
        conn.closeConnection();
    }

    @BeforeEach
    void createTables() {
        conn.createAllTables();
    }
    @AfterEach
    void removeTables() {
        conn.removeAllTables();
    }

    void insertCustomer(Statement stmt) throws SQLException {
        String insertCustomer = "INSERT INTO "+Tables.CUSTOMERS+" (firstName, lastName, birthDate, gender) VALUES " +
                "('John', 'Doe', '1980-01-01', '"+Gender.M+"')";
        stmt.execute(insertCustomer);
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
    public void testInsertAndQueryData() throws SQLException {
        Statement stmt = conn.getConnection().createStatement();

        insertCustomer(stmt);

        ResultSet rs = stmt.executeQuery("SELECT * FROM "+Tables.CUSTOMERS+" WHERE id = 1");

        assertTrue(rs.next());
        assertEquals("John", rs.getString("firstName"));
        assertEquals("Doe", rs.getString("lastName"));
        assertEquals("1980-01-01", rs.getDate("birthDate").toString());
        assertEquals("MALE", rs.getString("gender"));

        stmt.close();
    }

    @Test
    public void testTruncateTables() throws SQLException {
        Statement stmt = conn.getConnection().createStatement();

        insertCustomer(stmt);

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
    }
}
