package eu.bsinfo.web.api;

import eu.bsinfo.db.DatabaseConnection;
import eu.bsinfo.db.ObjectMapper;
import eu.bsinfo.db.SQLStatement;
import eu.bsinfo.db.enums.Gender;
import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.db.models.Customer;
import eu.bsinfo.db.models.Reading;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.ErrorDto;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadingsControllerIT {
    private static DatabaseConnection dbConn;
    private static SQLStatement stmt;

    @Mock
    private SQLStatement mockStmt;
    private final ReadingsController rc = new ReadingsController();

    @BeforeEach
    public void mockRC() {
        rc.setStmt(mockStmt);
    }

    @BeforeAll
    static void startServer() throws IOException {
        Properties config = new Properties();
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));

        dbConn = new DatabaseConnection(config.getProperty("DB_DBNAME"), config.getProperty("DB_USER"), config.getProperty("DB_PASSWORD"));
        Server.startServer("http://0.0.0.0:8080/", dbConn);
        stmt = new SQLStatement(dbConn);
        dbConn.truncateAllTables();
    }

    @AfterAll
    static void stopServer() {
        Server.stopServer();
    }

    @AfterEach
    void truncate() {
        dbConn.truncateAllTables();
    }

    Customer createCustomer() throws SQLException {
        Customer c = new Customer("John", "Doe", Gender.MALE, LocalDate.parse("1980-01-01"));
        stmt.createCustomer(c);
        return c;
    }

    Reading createReading(Customer c) throws SQLException, NoSuchObjectException {
        Reading r = new Reading(KindOfMeter.HEATER, LocalDate.parse("2024-11-08"), c, "No comment", 40000.10, "HEATER_1", false);
        stmt.createReading(r);
        return r;
    }

    Reading createReading(Customer c, String date) throws SQLException, NoSuchObjectException {
        Reading r = new Reading(KindOfMeter.HEATER, LocalDate.parse(date), c, "No comment", 40000.10, "HEATER_1", false);
        stmt.createReading(r);
        return r;
    }

    void createReading(Customer c, KindOfMeter kindOfMeter) throws SQLException, NoSuchObjectException {
        Reading r = new Reading(kindOfMeter, LocalDate.parse("2024-11-08"), c, "No comment", 40000.10, "fake_id", false);
        stmt.createReading(r);
    }

    @Test
    public void testGetReadings() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        createReading(c);

        RestAssured.get("/readings")
                .then().assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Readings.json")
                                )
                        )
                );
    }

    @Test
    public void testGetReadingsFilterByCustomerId() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        createReading(c);
        Customer c2 = createCustomer();
        createReading(c2);

        Response res = RestAssured.get("/readings?customer=" + c.getid().toString());
        res.then().assertThat().statusCode(200);

        List<Reading> readings = res.jsonPath().getList("readings", Reading.class);
        Assertions.assertFalse(readings.isEmpty());
        Assertions.assertTrue(readings.stream()
                .allMatch(rM -> rM.getCustomer().getid().toString().equals(c.getid().toString()))
        );
    }

    @Test
    public void testGetReadingsFilterByStartDateAndOrEndDate() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        Reading r = createReading(c, "2024-10-10");
        Reading r2 = createReading(c, "2024-11-10");
        Reading r3 = createReading(c, "2024-11-11");


        Response res = RestAssured.get("/readings?start=2024-10-10&end=2024-11-10");
        res.then().assertThat().statusCode(200);

        List<Reading> readings = res.jsonPath().getList("readings", Reading.class);
        Assertions.assertEquals(2, readings.size());
        Assertions.assertTrue(readings.stream()
                .allMatch(rM ->
                        rM.getDateOfReading().isBefore(LocalDate.of(2024, 11, 11)) &&
                                rM.getDateOfReading().isAfter(LocalDate.of(2024, 10, 9))
                )
        );

        Response res2 = RestAssured.get("/readings?start=2024-10-11");
        res2.then().assertThat().statusCode(200);

        List<Reading> readings2 = res2.jsonPath().getList("readings", Reading.class);
        Assertions.assertEquals(2, readings2.size());
        Assertions.assertTrue(readings2.stream()
                .allMatch(rM -> rM.getDateOfReading().isAfter(LocalDate.of(2024, 10, 10)))
        );

        Response res3 = RestAssured.get("/readings?end=2024-10-11");
        res3.then().assertThat().statusCode(200);

        List<Reading> readings3 = res3.jsonPath().getList("readings", Reading.class);
        Assertions.assertEquals(1, readings3.size());
        Assertions.assertTrue(readings3.stream()
                .allMatch(rM -> rM.getDateOfReading().isBefore(LocalDate.of(2024, 10, 12)))
        );
    }

    @Test
    public void testGetReadingsFilterByKindOfMeter() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        createReading(c);
        createReading(c, KindOfMeter.ELECTRICITY);

        Response res = RestAssured.get("/readings?kindOfMeter=" + KindOfMeter.ELECTRICITY);
        res.then().assertThat().statusCode(200);

        List<Reading> readings = res.jsonPath().getList("readings", Reading.class);
        Assertions.assertEquals(1, readings.size());
        Assertions.assertTrue(readings.stream()
                .allMatch(rM -> rM.getKindOfMeter() == KindOfMeter.ELECTRICITY)
        );
    }

    @Test
    public void testPostReadings() throws SQLException {
        Customer c = createCustomer();

        Map<String, Object> body = new HashMap<>();
        body.put("kindOfMeter", KindOfMeter.HEATER);
        body.put("dateOfReading", "2024-11-08");
        body.put("customer", c);
        body.put("comment", "No comment");
        body.put("meterCount", 40000.10);
        body.put("meterId", "HEATER_1");
        body.put("substitute", false);

        Response res = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/readings");

        res.then().assertThat()
                .statusCode(201)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Reading.json")
                                )
                        )
                );

        UUID id = res.jsonPath().getUUID("reading.id");
        assert stmt.getReading(id) != null;

        Customer notExistingC = new Customer("John", "Doe", Gender.MALE, LocalDate.parse("1980-01-01"));
        body.put("customer", notExistingC);

        res = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/readings");

        res.then().assertThat()
                .statusCode(201)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Reading.json")
                                )
                        )
                );
    }

    @Test
    public void testPostReadingsWithCustomerMissing() {
        Map<String, Object> body = new HashMap<>();
        body.put("kindOfMeter", KindOfMeter.HEATER);
        body.put("dateOfReading", "2024-11-08");
        body.put("comment", "No comment");
        body.put("meterCount", 40000.10);
        body.put("meterId", "HEATER_1");
        body.put("substitute", false);

        Response res = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/readings");

        res.then().assertThat()
                .statusCode(400)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Error.json")
                                )
                        )
                );
    }

    @Test
    public void testPutReadings() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        Reading r = createReading(c);

        Map<String, Object> body = new HashMap<>();
        body.put("id", r.getid().toString());
        body.put("kindOfMeter", KindOfMeter.ELECTRICITY);
        body.put("dateOfReading", "2024-11-08");
        body.put("customer", c);
        body.put("comment", "My comment");
        body.put("meterCount", 40000.10);
        body.put("meterId", "ELECTRICITY_1");
        body.put("substitute", false);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .put("/readings")
                .then().assertThat()
                .statusCode(200);

        Reading updatedReading = stmt.getReading(r.getid());
        Assertions.assertEquals(KindOfMeter.ELECTRICITY, updatedReading.getKindOfMeter());
        Assertions.assertEquals(r.getDateOfReading(), updatedReading.getDateOfReading());
        Assertions.assertEquals(r.getCustomer().getid().toString(), updatedReading.getCustomer().getid().toString());
        Assertions.assertEquals("My comment", updatedReading.getComment());
        Assertions.assertEquals(r.getMeterCount(), updatedReading.getMeterCount());
        Assertions.assertEquals("ELECTRICITY_1", updatedReading.getMeterId());
        Assertions.assertEquals(r.getSubstitute(), updatedReading.getSubstitute());

        Customer notExistingC = new Customer("John", "Doe", Gender.MALE, LocalDate.parse("1980-01-01"));
        body.put("customer", notExistingC);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .put("/readings")
                .then().assertThat()
                .statusCode(404)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Error.json")
                                )
                        )
                );
    }

    @Test
    public void testPutReadingsWithCustomerMissing() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        Reading r = createReading(c);
        stmt.deleteCustomer(c.getid());

        Map<String, Object> body = new HashMap<>();
        body.put("id", r.getid().toString());
        body.put("kindOfMeter", KindOfMeter.ELECTRICITY);
        body.put("dateOfReading", "2024-11-08");
        body.put("comment", "My comment");
        body.put("meterCount", 40000.10);
        body.put("meterId", "ELECTRICITY_1");
        body.put("substitute", false);

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .put("/readings")
                .then().assertThat()
                .statusCode(400)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Error.json")
                                )
                        )
                );
    }

    @Test
    public void testGetReading() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        Reading r = createReading(c);

        RestAssured.get("/readings/" + r.getid().toString())
                .then().assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Reading.json")
                                )
                        )
                );
    }

    @Test
    public void testDeleteReading() throws SQLException, NoSuchObjectException {
        Customer c = createCustomer();
        Reading r = createReading(c);

        RestAssured.delete("/readings/" + r.getid().toString())
                .then().assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Reading.json")
                                )
                        )
                );

        assert stmt.getReading(r.getid()) == null;
    }

    @Test
    public void testExceptions() throws SQLException, NoSuchObjectException {
        String errMsg = "fake error";
        Customer c = createCustomer();
        Reading r = createReading(c);

        when(mockStmt.getCustomer(any())).thenReturn(c);

        doThrow(new SQLException(errMsg)).when(mockStmt).createReading(any());
        when(mockStmt.updateReading(any())).thenReturn(0).thenThrow(new SQLException(errMsg));
        when(mockStmt.getReading(any())).thenReturn(null).thenThrow(new SQLException(errMsg)).thenReturn(r);
        when(mockStmt.deleteReading(any())).thenReturn(0).thenThrow(new SQLException(errMsg));

        jakarta.ws.rs.core.Response res = rc.createReading(r);

        Assertions.assertEquals(500, res.getStatus());
        ErrorDto errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertTrue(errorDto.getMessage().equals(errMsg));

        res = rc.updateReading(r);

        Assertions.assertEquals(404, res.getStatus());

        res = rc.updateReading(r);

        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertTrue(errorDto.getMessage().equals(errMsg));

        res = rc.getReading(any());

        Assertions.assertEquals(404, res.getStatus());

        res = rc.getReading(any());

        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertTrue(errorDto.getMessage().equals(errMsg));

        res = rc.deleteReading(any());

        Assertions.assertEquals(404, res.getStatus());

        res = rc.deleteReading(any());

        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertTrue(errorDto.getMessage().equals(errMsg));

        MockedStatic<ObjectMapper> mockOM = Mockito.mockStatic(ObjectMapper.class);
        mockOM.when(() -> ObjectMapper.getReadings(any(), any())).thenThrow(new SQLException(errMsg));

        res = rc.getReadings(null,null, null,null);
        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertTrue(errorDto.getMessage().equals(errMsg));

        mockOM.close();
    }
}
