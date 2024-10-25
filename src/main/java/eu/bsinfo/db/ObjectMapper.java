package eu.bsinfo.db;

import eu.bsinfo.db.dto.Customer;
import eu.bsinfo.db.dto.Reading;
import eu.bsinfo.db.enums.Gender;
import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.utils.UUIDUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObjectMapper {
    public static Customer getCustomer(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return null;
        }
        return new Customer(
                UUIDUtils.bytesAsUUID(rs.getBytes("id")),
                rs.getString("firstName"),
                rs.getString("lastName"),
                Gender.valueOf(rs.getString("gender")),
                rs.getDate("birthDate").toLocalDate()
        );
    }

    public static List<Customer> getCustomers(ResultSet rs) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        while (rs.next()) {
            customers.add(new Customer(
                    UUIDUtils.bytesAsUUID(rs.getBytes("id")),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    Gender.valueOf(rs.getString("gender")),
                    rs.getDate("birthDate").toLocalDate()
            ));
        }
        return customers;
    }

    public static Reading getReading(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return null;
        }
        return new Reading(
                UUIDUtils.bytesAsUUID(rs.getBytes("id")),
                KindOfMeter.valueOf(rs.getString("kindOfMeter")),
                rs.getDate("dateOfReading").toLocalDate(),
                UUIDUtils.bytesAsUUID(rs.getBytes("customer_id")),
                rs.getString("comment"),
                rs.getDouble("meterCount"),
                rs.getString("meterId"),
                rs.getBoolean("substitute")
        );
    }

    public static List<Reading> getReadings(ResultSet rs) throws SQLException {
        List<Reading> readings = new ArrayList<>();
        while (rs.next()) {
            readings.add(new Reading(
                    UUIDUtils.bytesAsUUID(rs.getBytes("id")),
                    KindOfMeter.valueOf(rs.getString("kindOfMeter")),
                    rs.getDate("dateOfReading").toLocalDate(),
                    UUIDUtils.bytesAsUUID(rs.getBytes("customer_id")),
                    rs.getString("comment"),
                    rs.getDouble("meterCount"),
                    rs.getString("meterId"),
                    rs.getBoolean("substitute")
            ));
        }
        return readings;
    }
}
