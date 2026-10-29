package pl.bigrun.pw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import pl.bigrun.pw.base.BasePwTest;
import pl.bigrun.pw.pages.ChatPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Project: bigrun-coach
 * File: ChatModelOllamaPwTest
 * ------------------------------------------------------------------------
 * Description:
 * Model selector — Ollama-only checks (fast button, switching smart/fast).
 * Runs only with -Dprovider=ollama, like ChatModelOllamaE2ETest (Selenium).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
@EnabledIfSystemProperty(named = "provider", matches = "ollama")
class ChatModelOllamaPwTest extends BasePwTest {

    private ChatPage chat;

    @BeforeEach
    void openChat() {
        chat = new ChatPage(page).open();
    }

    @Test
    void fastButtonIsPresentForOllama() {
        assertThat(chat.modelButton("fast")).isVisible();
    }

    @Test
    void clickingFastActivatesFast() {
        chat.clickModelButton("fast");
        assertThat(chat.modelButton("fast")).hasAttribute("aria-pressed", "true");
        assertThat(chat.modelButton("smart")).hasAttribute("aria-pressed", "false");
    }

    @Test
    void returningToSmartActivatesSmart() {
        chat.clickModelButton("fast");
        chat.clickModelButton("smart");
        assertThat(chat.modelButton("smart")).hasAttribute("aria-pressed", "true");
        assertThat(chat.modelButton("fast")).hasAttribute("aria-pressed", "false");
    }
}
