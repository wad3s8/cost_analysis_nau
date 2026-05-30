package ru.vladislav.cost_analysis_nau;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LoginUITest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String HOME_URL  = BASE_URL + "/home";

    private static final String VALID_LOGIN    = "testuser";
    private static final String VALID_PASSWORD = "testpassword";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ─── Тест 1: Успешный вход ─────────────────────────────────────────────

    @Test
    @DisplayName("Успешный вход с корректными учётными данными")
    void login_ValidCredentials_RedirectsToHomePage() {
        driver.get(LOGIN_URL);

        WebElement loginField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement submitButton  = driver.findElement(By.cssSelector("button[type='submit']"));

        loginField.clear();
        loginField.sendKeys(VALID_LOGIN);
        passwordField.clear();
        passwordField.sendKeys(VALID_PASSWORD);
        submitButton.click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login")));

        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.contains("/login"),
                "После входа не должно быть /login, но текущий URL: " + currentUrl);
    }

    // ─── Тест 2: Неверные учётные данные ──────────────────────────────────

    @Test
    @DisplayName("Вход с неверным паролем — остаётся на странице входа с ошибкой")
    void login_InvalidCredentials_StaysOnLoginPage() {
        driver.get(LOGIN_URL);

        WebElement loginField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement submitButton  = driver.findElement(By.cssSelector("button[type='submit']"));

        loginField.sendKeys(VALID_LOGIN);
        passwordField.sendKeys("wrongpassword");
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "При неверном пароле должны остаться на /login, но текущий URL: " + currentUrl);
        assertTrue(currentUrl.contains("error"),
                "URL должен содержать ?error, но текущий URL: " + currentUrl);
    }

    // ─── Тест 3: Успешный выход ────────────────────────────────────────────

    @Test
    @DisplayName("Успешный выход из системы — редирект на страницу входа")
    void logout_AuthorizedUser_RedirectsToLoginPage() {
        driver.get(LOGIN_URL);

        WebElement loginField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement submitButton  = driver.findElement(By.cssSelector("button[type='submit']"));

        loginField.sendKeys(VALID_LOGIN);
        passwordField.sendKeys(VALID_PASSWORD);
        submitButton.click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login")));

        driver.get(BASE_URL + "/logout");

        wait.until(ExpectedConditions.urlContains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "После выхода должен быть редирект на /login, но текущий URL: " + currentUrl);

        boolean loginFormVisible = !driver.findElements(By.name("username")).isEmpty();
        assertTrue(loginFormVisible, "После выхода должна отображаться форма входа");
    }

    // ─── Тест 4: Доступ к защищённой странице без авторизации ────────────

    @Test
    @DisplayName("Переход на защищённую страницу без авторизации — редирект на вход")
    void accessProtectedPage_NotAuthorized_RedirectsToLogin() {
        driver.get(HOME_URL);

        wait.until(ExpectedConditions.urlContains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "Без авторизации должен быть редирект на /login, но текущий URL: " + currentUrl);
    }
}