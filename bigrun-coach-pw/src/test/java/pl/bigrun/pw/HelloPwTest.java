package pl.bigrun.pw;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Project: bigrun-coach
 * File: HelloPwTest
 * ------------------------------------------------------------------------
 * Description:
 * Setup check — Playwright launches Chromium, loads an in-memory page
 * and reads its title. Runs offline (no app stack required).
 * Created just for initial testing.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
class HelloPwTest {

    @Test
    void shouldLaunchBrowserAndReadTitle() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch()) {
            Page page = browser.newPage();
            page.setContent("<html><head><title>BigRun Coach</title></head><body></body></html>");
            assertEquals("BigRun Coach", page.title());
        }
    }
}
