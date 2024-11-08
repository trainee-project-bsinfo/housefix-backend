package eu.bsinfo.db;

import eu.bsinfo.db.enums.Tables;
import eu.bsinfo.utils.UUIDUtils;
import eu.bsinfo.web.dto.Customer;
import eu.bsinfo.web.dto.Reading;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.rmi.NoSuchObjectException;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class SQLStatement {
    private final DatabaseConnection dbConn;

    public SQLStatement(DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    @Nullable
    public Customer getCustomer(UUID id) throws SQLException {
        String query = "SELECT * FROM "+Tables.CUSTOMERS+" WHERE id = ?;";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(query);
        stmt.setBytes(1, UUIDUtils.UUIDAsBytes(id));
        ResultSet rs = stmt.executeQuery();
        stmt.close();
        return ObjectMapper.getCustomer(rs);
    }

    public List<Customer> getCustomers() throws SQLException {
        String query = "SELECT * FROM "+Tables.CUSTOMERS+";";
        Statement stmt = dbConn.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        stmt.close();
        return ObjectMapper.getCustomers(rs);
    }

    public void createCustomer(Customer customer) throws SQLException {
        String mutation = "INSERT INTO "+Tables.CUSTOMERS+" (id, firstName, lastName, birthDate, gender) VALUES (?,?,?,?,?);";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(mutation);
        stmt.setBytes(1, UUIDUtils.UUIDAsBytes(customer.getid()));
        stmt.setString(2, customer.getFirstName());
        stmt.setString(3, customer.getLastName());
        stmt.setDate(4, Date.valueOf(customer.getBirthDate()));
        stmt.setString(5, customer.getGender().toString());

        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Customer creation failed");
        }

        stmt.close();
    }

    public int updateCustomer(Customer customer) throws SQLException {
        String mutation = "UPDATE "+Tables.CUSTOMERS+" SET firstName = ?, lastName = ?, birthDate = ?, gender = ? WHERE id = ?;";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(mutation);
        stmt.setString(1, customer.getFirstName());
        stmt.setString(2, customer.getLastName());
        stmt.setDate(3, Date.valueOf(customer.getBirthDate()));
        stmt.setString(4, customer.getGender().toString());
        stmt.setBytes(5, UUIDUtils.UUIDAsBytes(customer.getid()));

        int rowsAffected = stmt.executeUpdate();
        stmt.close();

        return rowsAffected;
    }

    public int deleteCustomer(UUID customerId) throws SQLException {
        String mutation = "DELETE FROM "+Tables.CUSTOMERS+" WHERE id = ?;";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(mutation);
        stmt.setBytes(1, UUIDUtils.UUIDAsBytes(customerId));

        int rowsAffected = stmt.executeUpdate();
        stmt.close();

        return rowsAffected;
    }

    @Nullable
    public Reading getReading(UUID id) throws SQLException {
        String query = "SELECT * FROM "+Tables.READINGS+" WHERE id = ?;";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(query);
        stmt.setBytes(1, UUIDUtils.UUIDAsBytes(id));
        ResultSet rs = stmt.executeQuery();
        stmt.close();
        return ObjectMapper.getReading(rs, this);
    }

    public List<Reading> getReadingsByCustomerId(UUID customerId) throws SQLException {
        String query = "SELECT * FROM "+Tables.READINGS+" WHERE customer_id = ?;";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(query);
        stmt.setBytes(1, UUIDUtils.UUIDAsBytes(customerId));
        ResultSet rs = stmt.executeQuery();
        stmt.close();
        return ObjectMapper.getReadings(rs, this);
    }

    public List<Reading> getReadings() throws SQLException {
        String query = "SELECT * FROM "+Tables.READINGS+";";
        Statement stmt = dbConn.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        stmt.close();
        return ObjectMapper.getReadings(rs, this);
    }

    public void createReading(Reading reading) throws SQLException, NoSuchObjectException {
        if (getCustomer(reading.getCustomer().getid()) == null) {
            throw new NoSuchObjectException("Customer not found");
        }

        String mutation = "INSERT INTO "+Tables.READINGS+" (id, comment, dateOfReading, kindOfMeter, meterCount, meterId, substitute, customer_id) VALUES (?,?,?,?,?,?,?,?);";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(mutation);
        stmt.setBytes(1, UUIDUtils.UUIDAsBytes(reading.getid()));
        stmt.setString(2, reading.getComment());
        stmt.setDate(3, Date.valueOf(reading.getDateOfReading()));
        stmt.setString(4, reading.getKindOfMeter().toString());
        stmt.setDouble(5, reading.getMeterCount());
        stmt.setString(6, reading.getMeterId());
        stmt.setBoolean(7, reading.getSubstitute());
        stmt.setBytes(8, UUIDUtils.UUIDAsBytes(reading.getCustomer().getid()));

        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Reading creation failed");
        }

        stmt.close();
    }

    public int updateReading(Reading reading) throws SQLException {
        String mutation = "UPDATE "+Tables.READINGS+" SET comment = ?, dateOfReading = ?, kindOfMeter = ?, meterCount = ?, meterId = ?, substitute = ?, customer_id = ? WHERE id = ?;";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(mutation);
        stmt.setString(1, reading.getComment());
        stmt.setDate(2, Date.valueOf(reading.getDateOfReading()));
        stmt.setString(3, reading.getKindOfMeter().toString());
        stmt.setDouble(4, reading.getMeterCount());
        stmt.setString(5, reading.getMeterId());
        stmt.setBoolean(6, reading.getSubstitute());

        byte[] customerId = null;
        if (reading.getCustomer() != null || reading.getCustomer().getid() != null) {
            customerId = UUIDUtils.UUIDAsBytes(reading.getCustomer().getid());
        }
        stmt.setBytes(7, customerId);
        stmt.setBytes(8, UUIDUtils.UUIDAsBytes(reading.getid()));

        int rowsAffected = stmt.executeUpdate();
        stmt.close();

        return rowsAffected;
    }

    public int deleteReading(UUID readingId) throws SQLException {
        String mutation = "DELETE FROM "+Tables.READINGS+" WHERE id = ?;";
        PreparedStatement stmt = dbConn.getConnection().prepareStatement(mutation);
        stmt.setBytes(1, UUIDUtils.UUIDAsBytes(readingId));

        int rowsAffected = stmt.executeUpdate();
        stmt.close();

        return rowsAffected;
    }
}
