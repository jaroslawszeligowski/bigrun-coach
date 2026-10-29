package pl.bigrun.pw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bigrun.pw.base.BasePwTest;
import pl.bigrun.pw.pages.ChatPage;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Project: bigrun-coach
 * File: ChatSmokePwTest
 * ------------------------------------------------------------------------
 * Description:
 * Smoke tests — page loads, header, title, provider tag, input and send button.
 * Playwright counterpart of ChatSmokeE2ETest (Selenium).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
class ChatSmokePwTest extends BasePwTest {

    private ChatPage chat;

    @BeforeEach
    void openChat() {
        chat = new ChatPage(page).open();
    }

    @Test
    void headerIsVisible() {
        assertThat(chat.appHeader()).isVisible();
    }

    @Test
    void titleIsCorrect() {
        assertThat(chat.appTitle()).hasText("BigRun Coach");
    }

    @Test
    void providerTagShowsKnownProvider() {
        assertThat(chat.providerTag()).hasText(Pattern.compile("^(Ollama|Gemini|OpenAI)$"));
    }

    @Test
    void messageInputIsEnabled() {
        assertThat(chat.messageInput()).isEnabled();
    }

    @Test
    void sendButtonIsVisible() {
        assertThat(chat.sendButton()).isVisible();
    }
}
