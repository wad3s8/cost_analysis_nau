package ru.vladislav.cost_analysis_nau;

import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vladislav.cost_analysis_nau.entity.Role;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;

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

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CookieFilter cookieFilter;
    private static final String TEST_LOGIN = "restassured_test_user";
    private static final String TEST_PASSWORD = "testpassword123";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.proxy = null;

        // Создаём тестового пользователя если нет
        if (usersRepository.findByLogin(TEST_LOGIN) == null) {
            User user = new User();
            user.setLogin(TEST_LOGIN);
            user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            user.setRole(Role.USER);
            usersRepository.save(user);
        }

        cookieFilter = new CookieFilter();

        given()
                .filter(cookieFilter)
                .redirects().follow(true)
                .contentType(ContentType.URLENC)
                .formParam("username", TEST_LOGIN)
                .formParam("password", TEST_PASSWORD)
                .when()
                .post("/login")
                .then()
                .statusCode(anyOf(is(200), is(302)));
    }

    // ─── Защищённые страницы ─────────────────────────────────────────────────

    @Test
    @DisplayName("Главная: авторизованный запрос — возвращает 200")
    void dashboard_Authorized_Returns200() {
        given()
                .filter(cookieFilter)
                .redirects().follow(true)
                .when()
                .get("/")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Транзакции: авторизованный запрос — возвращает 200")
    void transactions_Authorized_Returns200() {
        given()
                .filter(cookieFilter)
                .redirects().follow(true)
                .when()
                .get("/transactions")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Счета: авторизованный запрос — возвращает 200")
    void accounts_Authorized_Returns200() {
        given()
                .filter(cookieFilter)
                .redirects().follow(true)
                .when()
                .get("/accounts")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Аналитика: авторизованный запрос — возвращает 200")
    void analytics_Authorized_Returns200() {
        given()
                .filter(cookieFilter)
                .redirects().follow(true)
                .when()
                .get("/analytics")
                .then()
                .statusCode(200);
    }

    // ─── Без авторизации ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Главная: без авторизации — редирект на /login")
    void dashboard_Unauthorized_RedirectsToLogin() {
        given()
                .redirects().follow(false)
                .when()
                .get("/")
                .then()
                .statusCode(anyOf(is(302), is(401)));
    }

    @Test
    @DisplayName("Транзакции: без авторизации — редирект на /login")
    void transactions_Unauthorized_RedirectsToLogin() {
        given()
                .redirects().follow(false)
                .when()
                .get("/transactions")
                .then()
                .statusCode(anyOf(is(302), is(401)));
    }
}
