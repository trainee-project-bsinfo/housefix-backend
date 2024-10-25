package eu.bsinfo.db;

import eu.bsinfo.db.dto.Customer;
import eu.bsinfo.db.dto.Reading;
import eu.bsinfo.db.enums.Gender;
import eu.bsinfo.db.enums.KindOfMeter;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class SQLStatementIT {
    private static SQLStatement sqlStmt;
    private static DatabaseConnection conn;

    @BeforeAll
    static void init() throws SQLException, IOException {
        Properties config = new Properties();
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        conn = new DatabaseConnection(config.getProperty("DB_DBNAME"), config.getProperty("DB_USER"), config.getProperty("DB_PASSWORD"))
                .openConnection();
        conn.createAllTables();
        conn.truncateAllTables();
        sqlStmt = new SQLStatement(conn);
    }
    @AfterAll
    static void close() {
        conn.closeConnection();
    }

    @AfterEach
    void clearTables() {
        conn.truncateAllTables();
    }

    Customer createCustomer() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("1980-01-01", formatter);
        Customer c = new Customer("John", "Doe", Gender.MALE, date);

        sqlStmt.createCustomer(c);

        return c;
    }

    Reading createReading() throws SQLException {
        Customer c = createCustomer();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("2024-08-20", formatter);
        Reading r = new Reading(KindOfMeter.ELECTRICITY, date, c.getid(), "No comment", 12000.23, "ELECTRICITY_1", false);

        sqlStmt.createReading(r);

        return r;
    }


    @Test
    public void testCreateCustomer() throws SQLException {
        Customer c = createCustomer();

        Customer createdC = sqlStmt.getCustomer(c.getid());

        assertThat(createdC).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(c);
    }

    @Test
    public void testUpdateCustomer() throws SQLException {
        Customer c = createCustomer();

        c.setFirstName("Gandalf");
        int affectedRows = sqlStmt.updateCustomer(c);
        Assertions.assertEquals(1, affectedRows);

        Customer updatedC = sqlStmt.getCustomer(c.getid());
        assertThat(updatedC).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(c);
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        Customer c = createCustomer();

        int affectedRows = sqlStmt.deleteCustomer(c);
        Assertions.assertEquals(1, affectedRows);

        Customer deletedC = sqlStmt.getCustomer(c.getid());
        Assertions.assertNull(deletedC);
    }

    @Test
    public void testGetCustomers() throws SQLException {
        Customer c1 = createCustomer();
        Customer c2 = createCustomer();

        List<Customer> customers = sqlStmt.getCustomers();

        Assertions.assertEquals(2, customers.size());
        assertThat(customers)
                .hasSize(2)
                .extracting(Customer::getid, Customer::getFirstName, Customer::getLastName, Customer::getGender, Customer::getBirthDate)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(c1.getid(), c1.getFirstName(), c1.getLastName(), c1.getGender(), c1.getBirthDate()),
                        Tuple.tuple(c2.getid(), c2.getFirstName(), c2.getLastName(), c2.getGender(), c2.getBirthDate())
                );
    }

    @Test
    public void testCreateReading() throws SQLException {
        Reading r = createReading();

        Reading createdR = sqlStmt.getReading(r.getid());
        assertThat(createdR).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(r);
    }

    @Test
    public void testUpdateReading() throws SQLException {
        Reading r = createReading();

        r.setKindOfMeter(KindOfMeter.WATER);
        r.setMeterId("WATER_1");
        int affectedRows = sqlStmt.updateReading(r);
        Assertions.assertEquals(1, affectedRows);

        Reading updatedR = sqlStmt.getReading(r.getid());
        assertThat(updatedR).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(r);
    }

    @Test
    public void testDeleteReading() throws SQLException {
        Reading r = createReading();

        int affectedRows = sqlStmt.deleteReading(r);
        Assertions.assertEquals(1, affectedRows);

        Reading deletedR = sqlStmt.getReading(r.getid());
        Assertions.assertNull(deletedR);
    }

    @Test
    public void testGetReadings() throws SQLException {
        Reading r1 = createReading();
        Reading r2 = createReading();

        List<Reading> readings = sqlStmt.getReadings();

        Assertions.assertEquals(2, readings.size());
        assertThat(readings)
                .hasSize(2)
                .extracting(Reading::getid, Reading::getKindOfMeter, Reading::getDateOfReading, Reading::getCustomerId, Reading::getComment, Reading::getMeterCount, Reading::getMeterId, Reading::getSubstitute)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(r1.getid(), r1.getKindOfMeter(), r1.getDateOfReading(), r1.getCustomerId(), r1.getComment(), r1.getMeterCount(), r1.getMeterId(), r1.getSubstitute()),
                        Tuple.tuple(r2.getid(), r2.getKindOfMeter(), r2.getDateOfReading(), r2.getCustomerId(), r2.getComment(), r2.getMeterCount(), r2.getMeterId(), r2.getSubstitute())
                );
    }
}