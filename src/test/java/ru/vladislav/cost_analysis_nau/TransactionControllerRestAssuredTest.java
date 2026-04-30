package ru.vladislav.cost_analysis_nau;

import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:postgresql://localhost:5432/analysis-nau",
                "spring.datasource.username=analysis-nau",
                "spring.datasource.password=12345",
                "spring.datasource.driver-class-name=org.postgresql.Driver",
                "spring.jpa.hibernate.ddl-auto=update",
                "spring.data.rest.base-path=/data-rest",
                "app.name=cost_analysis_nau",
                "app.version=1.0"
        }
)
class TransactionControllerRestAssuredTest {

    static {
        System.clearProperty("http.proxyHost");
        System.clearProperty("https.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyPort");
    }

    @LocalServerPort
    private int port;

    private CookieFilter cookieFilter;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.proxy = null;

        cookieFilter = new CookieFilter();

        given()
                .filter(cookieFilter)
                .redirects().follow(true)
                .contentType(ContentType.URLENC)
                .formParam("username", "testuser")
                .formParam("password", "testpassword")
                .when()
                .post("/login")
                .then()
                .statusCode(anyOf(is(200), is(302)));
    }

    // ─── GET /{accountId}/byAccount ──────────────────────────────────────────

    @Test
    @DisplayName("byAccount: авторизованный запрос — возвращает 200 и массив")
    void byAccount_Authorized_Returns200() {
        given()
                .filter(cookieFilter)
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .when()
                .get("/transaction-custom/1/byAccount")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    @DisplayName("byAccount: несуществующий счёт — возвращает 404")
    void byAccount_AccountNotFound_Returns404() {
        given()
                .filter(cookieFilter)
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .when()
                .get("/transaction-custom/99999/byAccount")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("byAccount: без авторизации — редирект на /login (302 или 401)")
    void byAccount_Unauthorized_Returns302Or401() {
        given()
                .redirects().follow(false)
                .when()
                .get("/transaction-custom/1/byAccount")
                .then()
                .statusCode(anyOf(is(302), is(401)));
    }

    // ─── GET /{accountId}/byCategory ─────────────────────────────────────────

    @Test
    @DisplayName("byCategory: существующая категория — возвращает 200 и массив")
    void byCategory_ValidCategory_Returns200() {
        given()
                .filter(cookieFilter)
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .queryParam("nameCategory", "Food")
                .when()
                .get("/transaction-custom/1/byCategory")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    @DisplayName("byCategory: несуществующая категория — возвращает 404")
    void byCategory_CategoryNotFound_Returns404() {
        given()
                .filter(cookieFilter)
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .queryParam("nameCategory", "НесуществующаяКатегория_xyz")
                .when()
                .get("/transaction-custom/1/byCategory")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("byCategory: несуществующий счёт — возвращает 404")
    void byCategory_AccountNotFound_Returns404() {
        given()
                .filter(cookieFilter)
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .queryParam("nameCategory", "Food")
                .when()
                .get("/transaction-custom/99999/byCategory")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("byCategory: без авторизации — редирект на /login (302 или 401)")
    void byCategory_Unauthorized_Returns302Or401() {
        given()
                .redirects().follow(false)
                .queryParam("nameCategory", "Food")
                .when()
                .get("/transaction-custom/1/byCategory")
                .then()
                .statusCode(anyOf(is(302), is(401)));
    }
}