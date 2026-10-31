/**
 * Project: bigrun-coach-se
 * File: BaseE2ETest
 * ------------------------------------------------------------------------
 * Description:
 * Base class for all Selenium E2E tests.
 * Manages WebDriver lifecycle — setup before each test, quit after each test.
 * Supports local execution and Selenium Grid (RemoteWebDriver).
 *
 * System properties:
 *   -Dbrowser=chrome|firefox|edge  (default: chrome)
 *   -Dgrid.url=http://host:4444    (when set, uses RemoteWebDriver)
 *   -Dbase.url=http://host:5173    (default: http://localhost:5173)
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.e2e.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.util.ResultsUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.Set;

@Tag("e2e")
public abstract class BaseE2ETest {

    protected static final String BASE_URL = System.getProperty("base.url", "http://localhost:5173");

    private static final String BROWSER  = System.getProperty("browser", "chrome");
    private static final String GRID_URL = System.getProperty("grid.url");
    private static final Set<String> SUITE_LABELS = Set.of("parentSuite", "suite", "subSuite");

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUp() throws Exception {
        labelAllureResult();
        driver = GRID_URL != null ? buildRemoteDriver() : buildLocalDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Allure: drzewo Suites „Selenium → klasa → przeglądarka” (wspólny raport z Playwright w Jenkinsie)
    // + historyId rozszerzony o przeglądarkę — Allure liczy go z ID testu JUnit, więc bez tego
    // ten sam test z Chrome i Firefox „zlałby się” w jeden wynik z powtórzeniami
    private void labelAllureResult() {
        Allure.parameter("browser", BROWSER);
        Allure.getLifecycle().updateTestCase(result -> {
            result.getLabels().removeIf(label -> SUITE_LABELS.contains(label.getName()));
            result.getLabels().add(ResultsUtils.createParentSuiteLabel("Selenium"));
            result.getLabels().add(ResultsUtils.createSuiteLabel(getClass().getSimpleName()));
            result.getLabels().add(ResultsUtils.createSubSuiteLabel(BROWSER));
            result.setHistoryId(result.getHistoryId() + "-" + BROWSER);
        });
    }

    private WebDriver buildLocalDriver() {
        return switch (BROWSER) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions opts = new FirefoxOptions();
//                opts.addArguments("-headless");
                yield new FirefoxDriver(opts);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions opts = new EdgeOptions();
//                opts.addArguments("--headless");
                opts.addArguments("--no-sandbox");
                opts.addArguments("--disable-dev-shm-usage");
                opts.addArguments("--window-size=1280,800");
                yield new EdgeDriver(opts);
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
//                opts.addArguments("--headless");
                opts.addArguments("--no-sandbox");
                opts.addArguments("--disable-dev-shm-usage");
                opts.addArguments("--window-size=1280,800");
                yield new ChromeDriver(opts);
            }
        };
    }

    private WebDriver buildRemoteDriver() throws Exception {
        return switch (BROWSER) {
            case "firefox" -> {
                FirefoxOptions opts = new FirefoxOptions();
                opts.addArguments("-headless");
                yield new RemoteWebDriver(URI.create(GRID_URL).toURL(), opts);
            }
            case "edge" -> {
                EdgeOptions opts = new EdgeOptions();
                opts.addArguments("--headless");
                opts.addArguments("--no-sandbox");
                opts.addArguments("--disable-dev-shm-usage");
                yield new RemoteWebDriver(URI.create(GRID_URL).toURL(), opts);
            }
            default -> {
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments("--headless");
                opts.addArguments("--no-sandbox");
                opts.addArguments("--disable-dev-shm-usage");
                yield new RemoteWebDriver(URI.create(GRID_URL).toURL(), opts);
            }
        };
    }
}
