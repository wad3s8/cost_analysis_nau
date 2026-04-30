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

/**
 * UI-тесты для проверки входа и выхода из приложения.
 *
 * ВАЖНО: Перед запуском убедитесь, что приложение запущено на localhost:8080.
 * Селекторы (loginInput, passwordInput и т.д.) нужно скорректировать
 * под реальную HTML-разметку страницы входа вашего приложения.
 */
class LoginUITest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String HOME_URL  = BASE_URL + "/home";      // URL после успешного входа

    // Учётные данные тестового пользователя (должны существовать в БД)
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
        options.addArguments("--headless");          // Запуск без окна браузера
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
        // Шаг 1: открыть страницу входа
        driver.get(LOGIN_URL);

        // Шаг 2: ввести логин и пароль
        // ⚠️ Замените значения By.id(...) на реальные id/name атрибуты
        //    полей вашей страницы входа
        WebElement loginField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement submitButton  = driver.findElement(By.id("login-button"));

        loginField.clear();
        loginField.sendKeys(VALID_LOGIN);

        passwordField.clear();
        passwordField.sendKeys(VALID_PASSWORD);

        // Шаг 3: нажать кнопку входа
        submitButton.click();

        // Шаг 4: убедиться, что произошёл редирект на домашнюю страницу
        wait.until(ExpectedConditions.urlContains("/home"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/home"),
                "После входа должен быть редирект на /home, но текущий URL: " + currentUrl);

        // Дополнительно: проверить, что на странице есть признак авторизации
        // Например, кнопка выхода или приветствие пользователя
        boolean logoutVisible = !driver.findElements(By.id("logout-button")).isEmpty();
        assertTrue(logoutVisible, "Кнопка выхода должна быть видна после авторизации");
    }

    // ─── Тест 2: Неверные учётные данные ──────────────────────────────────

    @Test
    @DisplayName("Вход с неверным паролем — остаётся на странице входа с ошибкой")
    void login_InvalidCredentials_StaysOnLoginPage() {
        driver.get(LOGIN_URL);

        WebElement loginField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement submitButton  = driver.findElement(By.id("login-button"));

        loginField.sendKeys(VALID_LOGIN);
        passwordField.sendKeys("wrongpassword");
        submitButton.click();

        // Должны остаться на странице входа
        wait.until(ExpectedConditions.urlContains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "При неверном пароле должны остаться на /login, но текущий URL: " + currentUrl);

        // Проверяем наличие сообщения об ошибке
        // ⚠️ Замените селектор на реальный из вашей разметки
        boolean errorMessageVisible = !driver.findElements(By.className("error-message")).isEmpty();
        assertTrue(errorMessageVisible, "Должно отображаться сообщение об ошибке");
    }

    // ─── Тест 3: Успешный выход ────────────────────────────────────────────

    @Test
    @DisplayName("Успешный выход из системы — редирект на страницу входа")
    void logout_AuthorizedUser_RedirectsToLoginPage() {
        // Сначала выполняем вход
        driver.get(LOGIN_URL);

        WebElement loginField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement submitButton  = driver.findElement(By.id("login-button"));

        loginField.sendKeys(VALID_LOGIN);
        passwordField.sendKeys(VALID_PASSWORD);
        submitButton.click();

        // Ждём успешного входа
        wait.until(ExpectedConditions.urlContains("/home"));

        // Нажимаем кнопку выхода
        // ⚠️ Замените By.id("logout-button") на реальный селектор
        WebElement logoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("logout-button")));
        logoutButton.click();

        // Проверяем, что произошёл редирект на страницу входа
        wait.until(ExpectedConditions.urlContains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "После выхода должен быть редирект на /login, но текущий URL: " + currentUrl);

        // Проверяем, что форма входа снова отображается
        boolean loginFormVisible = !driver.findElements(By.id("username")).isEmpty();
        assertTrue(loginFormVisible, "После выхода должна отображаться форма входа");
    }

    // ─── Тест 4: Доступ к защищённой странице без авторизации ────────────

    @Test
    @DisplayName("Переход на защищённую страницу без авторизации — редирект на вход")
    void accessProtectedPage_NotAuthorized_RedirectsToLogin() {
        driver.get(HOME_URL);

        // Неавторизованный пользователь должен быть перенаправлен на /login
        wait.until(ExpectedConditions.urlContains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "Без авторизации должен быть редирект на /login, но текущий URL: " + currentUrl);
    }
}