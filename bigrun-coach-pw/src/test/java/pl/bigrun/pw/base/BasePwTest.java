package pl.bigrun.pw.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import io.qameta.allure.util.ResultsUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Project: bigrun-coach
 * File: BasePwTest
 * ------------------------------------------------------------------------
 * Description:
 * Base class for Playwright E2E tests — one browser per test class,
 * fresh BrowserContext and Page per test (full isolation, fast start).
 * Supports local and remote (Playwright Server) execution and saves
 * a trace (target/traces/*.zip) for every failed test, attached to the Allure result.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
public abstract class BasePwTest {

    protected static final String BASE_URL = System.getProperty("base.url", "http://localhost");
    private static final String BROWSER    = System.getProperty("browser", "chromium");
    private static final boolean HEADLESS  = Boolean.parseBoolean(System.getProperty("headless", "true"));
    private static final String WS_URL     = System.getProperty("pw.ws.url");
    private static final Path TRACES_DIR   = Paths.get("target", "traces");
    private static final Set<String> SUITE_LABELS = Set.of("parentSuite", "suite", "subSuite");

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    protected Page page;

    // Uruchamia się po teście, a przed @AfterEach — kontekst jest jeszcze otwarty.
    // Trace zapisywany tylko dla nieudanych testów (odpowiednik "retain-on-failure")
    // i dołączany do wyniku w Allure — do pobrania prosto z raportu.
    @RegisterExtension
    final AfterTestExecutionCallback traceOnFailure = extensionContext -> {
        if (context == null) {
            return;
        }
        if (extensionContext.getExecutionException().isEmpty()) {
            context.tracing().stop();
            return;
        }
        String fileName = extensionContext.getRequiredTestClass().getSimpleName() + "_"
                + extensionContext.getDisplayName()
                        .replaceAll("\\(\\)$", "")
                        .replaceAll("[^A-Za-z0-9._-]", "_") + ".zip";
        Path tracePath = TRACES_DIR.resolve(fileName);
        context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        attachTraceToAllure(tracePath);
    };

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        BrowserType type = browserType(playwright);
        browser = WS_URL != null
                ? type.connect(WS_URL)
                : type.launch(new BrowserType.LaunchOptions().setHeadless(HEADLESS));
    }

    @BeforeEach
    void createContextAndPage() {
        labelAllureResult();
        context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(BASE_URL)
                .setViewportSize(1280, 800));
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    private static void attachTraceToAllure(Path tracePath) {
        try (InputStream trace = Files.newInputStream(tracePath)) {
            Allure.addAttachment("Playwright trace (trace.playwright.dev)",
                    "application/zip", trace, "zip");
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot attach trace " + tracePath, e);
        }
    }

    // Allure: drzewo Suites „Playwright → klasa → przeglądarka” (wspólny raport z Selenium w Jenkinsie)
    // + historyId rozszerzony o przeglądarkę — Allure liczy go z ID testu JUnit, więc bez tego
    // ten sam test z 3 przeglądarek „zlałby się” w jeden wynik z powtórzeniami
    private void labelAllureResult() {
        Allure.parameter("browser", BROWSER);
        Allure.getLifecycle().updateTestCase(result -> {
            result.getLabels().removeIf(label -> SUITE_LABELS.contains(label.getName()));
            result.getLabels().add(ResultsUtils.createParentSuiteLabel("Playwright"));
            result.getLabels().add(ResultsUtils.createSuiteLabel(getClass().getSimpleName()));
            result.getLabels().add(ResultsUtils.createSubSuiteLabel(BROWSER));
            result.setHistoryId(result.getHistoryId() + "-" + BROWSER);
        });
    }

    private static BrowserType browserType(Playwright playwright) {
        return switch (BROWSER.toLowerCase()) {
            case "chromium" -> playwright.chromium();
            case "firefox"  -> playwright.firefox();
            case "webkit"   -> playwright.webkit();
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + BROWSER + " (use chromium | firefox | webkit)");
        };
    }
}
