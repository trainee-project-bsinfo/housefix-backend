package eu.bsinfo.web.api;

import eu.bsinfo.db.DatabaseConnection;
import eu.bsinfo.db.SQLStatement;
import eu.bsinfo.db.enums.Gender;
import eu.bsinfo.db.models.Customer;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.ErrorDto;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomersControllerIT {
    private static DatabaseConnection dbConn;
    private static SQLStatement stmt;

    @Mock
    private SQLStatement mockStmt;
    private static final CustomersController cc = new CustomersController();

    @BeforeEach
    void mockCC() {
        cc.setStmt(mockStmt);
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

    @Test
    public void testGetCustomers() throws SQLException {
        createCustomer();

        RestAssured.get("/customers")
                .then().assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Customers.json")
                                )
                        )
                );
    }

    @Test
    public void testPostCustomers() throws SQLException {
        Map<String, String> body = new HashMap<>();
        body.put("firstName", "John");
        body.put("lastName", "Doe");
        body.put("gender", Gender.MALE.toString());
        body.put("birthDate", "1980-01-01");

        Response res = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/customers");

        res.then().assertThat()
                .statusCode(201)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Customer.json")
                                )
                        )
                );

        UUID id = res.jsonPath().getUUID("customer.id");
        assert stmt.getCustomer(id) != null;
    }

    @Test
    public void testPutCustomers() throws SQLException {
        Customer c = createCustomer();

        Map<String, String> body = new HashMap<>();
        body.put("id", c.getid().toString());
        body.put("firstName", c.getFirstName());
        body.put("lastName", "Done");
        body.put("gender", Gender.MALE.toString());
        body.put("birthDate", "1980-01-30");

        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .put("/customers")
                .then().assertThat()
                .statusCode(200);

        Customer updatedCustomer = stmt.getCustomer(c.getid());
        Assertions.assertEquals(c.getFirstName(), updatedCustomer.getFirstName());
        Assertions.assertEquals("Done", updatedCustomer.getLastName());
        Assertions.assertEquals(c.getGender(), updatedCustomer.getGender());
        Assertions.assertEquals(LocalDate.parse("1980-01-30"), updatedCustomer.getBirthDate());
    }

    @Test
    public void testGetCustomer() throws SQLException {
        Customer c = createCustomer();

        RestAssured.get("/customers/" + c.getid().toString())
                .then().assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_Customer.json")
                                )
                        )
                );
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        Customer c = createCustomer();

        RestAssured.delete("/customers/" + c.getid().toString())
                .then().assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator
                        .matchesJsonSchema(
                                Objects.requireNonNull(
                                        Thread.currentThread().getContextClassLoader()
                                                .getResourceAsStream("schema_CustomerWithReadings.json")
                                )
                        )
                );

        assert stmt.getCustomer(c.getid()) == null;
    }

    @Test
    public void testExceptions() throws SQLException {
        String errMsg = "fake error";
        Customer c = createCustomer();

        doThrow(new SQLException(errMsg)).when(mockStmt).getCustomers();
        doThrow(new SQLException(errMsg)).when(mockStmt).createCustomer(any());
        when(mockStmt.updateCustomer(any())).thenReturn(0).thenThrow(new SQLException(errMsg));
        when(mockStmt.getCustomer(any())).thenReturn(null).thenThrow(new SQLException(errMsg)).thenReturn(c);
        when(mockStmt.getReadingsByCustomerId(any())).thenReturn(new ArrayList<>());
        when(mockStmt.deleteCustomer(any())).thenReturn(0).thenThrow(new SQLException(errMsg));

        jakarta.ws.rs.core.Response res = cc.getCustomers();

        Assertions.assertEquals(500, res.getStatus());
        ErrorDto errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertEquals(errorDto.getMessage(), errMsg);

        res.close();

        res = cc.createCustomer(any());

        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertEquals(errorDto.getMessage(), errMsg);

        res.close();

        res = cc.updateCustomer(any());

        Assertions.assertEquals(404, res.getStatus());

        res.close();

        res = cc.updateCustomer(any());

        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertEquals(errorDto.getMessage(), errMsg);

        res.close();

        res = cc.getCustomer(any());

        Assertions.assertEquals(404, res.getStatus());

        res.close();

        res = cc.getCustomer(any());

        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertEquals(errorDto.getMessage(), errMsg);

        res.close();

        res = cc.deleteCustomer(any());

        Assertions.assertEquals(404, res.getStatus());

        res.close();

        res = cc.deleteCustomer(any());

        Assertions.assertEquals(500, res.getStatus());
        errorDto = (ErrorDto) res.getEntity();
        Assertions.assertNotNull(errorDto);
        Assertions.assertEquals(errorDto.getMessage(), errMsg);

        res.close();
    }
}
