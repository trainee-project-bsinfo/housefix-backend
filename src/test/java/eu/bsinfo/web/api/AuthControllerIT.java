package eu.bsinfo.web.api;

import eu.bsinfo.db.DatabaseConnection;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.LoginDto;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class AuthControllerIT {
    private static DatabaseConnection dbConn;
    private static String token;

    @BeforeAll
    static void startServer() throws IOException {
        Properties config = new Properties();
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));

        dbConn = new DatabaseConnection(config.getProperty("DB_DBNAME"), config.getProperty("DB_USER"), config.getProperty("DB_PASSWORD"));
        try {
            dbConn.openConnection().removeAllTables();
        } catch (SQLException ignored) {}
        Server.startServer("http://0.0.0.0:8080/", dbConn);
    }

    @AfterAll
    static void stopServer() {
        Server.stopServer();
    }

    @AfterEach
    public void clearSession() {
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .delete("/auth");
        token = null;
    }

    private String login() {
        LoginDto loginDto = new LoginDto("admin", "admin");

        return RestAssured.given()
                .contentType("application/json")
                .body(loginDto)
                .post("/auth")
                .then().assertThat()
                .statusCode(200)
                .extract()
                .path("token");
    }

    private void logout(String token) {
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .delete("/auth")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testIsAuthorizedWithValidToken() {
        token = login();

        RestAssured.given().header("Authorization", "Bearer " + token)
                .get("/auth")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testIsAuthorizedWithNoToken() {
        RestAssured.get("/auth")
                .then().assertThat()
                .statusCode(401);
    }

    @Test
    public void testIsAuthorizedWithInvalidToken() {
        RestAssured.given().header("Authorization", "Bearer fake")
                .get("/auth")
                .then().assertThat()
                .statusCode(401);
    }

    @Test
    public void testIsAuthorizedWithExpiredSession() {
        token = login();
        logout(token);

        System.out.println(token);

        RestAssured.given().header("Authorization", "Bearer "+token)
                .get("/auth")
                .then().assertThat()
                .statusCode(401);
    }

    @Test
    public void testLoginWithCorrectCredentials() {
        login();
    }

    @Test
    public void testLoginWithIncorrectCredentials() {
        LoginDto loginDto = new LoginDto("admin", "wrong");

        RestAssured.given()
                .contentType("application/json")
                .body(loginDto)
                .post("/auth")
                .then().assertThat()
                .statusCode(401);
    }

    @Test
    public void testLoginWithNotExistingUser() {
        LoginDto loginDto = new LoginDto("wrong", "wrong");

        RestAssured.given()
                .contentType("application/json")
                .body(loginDto)
                .post("/auth")
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    public void testLogoutWithValidToken() {
        token = login();
        logout(token);
    }

    @Test
    public void testLogoutWithNoToken() {
        RestAssured.delete("/auth")
                .then().assertThat()
                .statusCode(401);
    }

    @Test
    public void testLogoutWithInvalidToken() {
        RestAssured.given().header("Authorization", "Bearer fake")
                .delete("/auth")
                .then().assertThat()
                .statusCode(401);
    }
}
