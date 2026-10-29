package pl.bigrun.pw;

import org.junit.jupiter.api.Test;
import pl.bigrun.pw.base.BasePwTest;

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
class HelloPwTest extends BasePwTest {

    @Test
    void shouldLaunchBrowserAndReadTitle() {
        page.setContent("<html><head><title>BigRun Coach</title></head><body></body></html>");
        assertEquals("BigRun Coach", page.title());
    }

    @Test
    void shouldStartEachTestWithBlankPage() {
        // nowy BrowserContext → pusta strona, bez śladu po poprzednim teście
        assertEquals("about:blank", page.url());
    }
}
