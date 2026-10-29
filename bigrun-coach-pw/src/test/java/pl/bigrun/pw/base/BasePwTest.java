package pl.bigrun.pw.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Project: bigrun-coach
 * File: BasePwTest
 * ------------------------------------------------------------------------
 * Description:
 * Base class for Playwright E2E tests — one browser per test class,
 * fresh BrowserContext and Page per test (full isolation, fast start).
 * Supports local and remote (Playwright Server) execution and saves
 * a trace (target/traces/*.zip) for every failed test.
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

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    protected Page page;

    // Uruchamia się po teście, a przed @AfterEach — kontekst jest jeszcze otwarty.
    // Trace zapisywany tylko dla nieudanych testów (odpowiednik "retain-on-failure").
    @RegisterExtension
    final AfterTestExecutionCallback traceOnFailure = extensionContext -> {
        if (context == null) {
            return;
        }
        Tracing.StopOptions stopOptions = new Tracing.StopOptions();
        if (extensionContext.getExecutionException().isPresent()) {
            String fileName = extensionContext.getRequiredTestClass().getSimpleName() + "_"
                    + extensionContext.getDisplayName()
                            .replaceAll("\\(\\)$", "")
                            .replaceAll("[^A-Za-z0-9._-]", "_") + ".zip";
            stopOptions.setPath(TRACES_DIR.resolve(fileName));
        }
        context.tracing().stop(stopOptions);
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
