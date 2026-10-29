package pl.bigrun.pw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bigrun.pw.base.BasePwTest;
import pl.bigrun.pw.pages.ChatPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Project: bigrun-coach
 * File: ChatModelGenericPwTest
 * ------------------------------------------------------------------------
 * Description:
 * Model selector — provider-independent checks (smart button visible and active by default).
 * Playwright counterpart of ChatModelGenericE2ETest (Selenium).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
class ChatModelGenericPwTest extends BasePwTest {

    private ChatPage chat;

    @BeforeEach
    void openChat() {
        chat = new ChatPage(page).open();
    }

    @Test
    void smartButtonIsAlwaysVisible() {
        assertThat(chat.modelButton("smart")).isVisible();
    }

    @Test
    void smartIsActiveByDefault() {
        assertThat(chat.modelButton("smart")).hasAttribute("aria-pressed", "true");
    }
}
