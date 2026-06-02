/**
 * Project: bigrun-coach-se
 * File: ChatSmokeE2ETest
 * ------------------------------------------------------------------------
 * Description:
 * Smoke tests — TC-01. Verifies page loads correctly.
 * Requires: backend running on :8081, frontend on :5173.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.e2e.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bigrun.e2e.base.BaseE2ETest;
import pl.bigrun.e2e.pages.ChatPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatSmokeE2ETest extends BaseE2ETest {

    private ChatPage page;

    @BeforeEach
    void initPage() {
        page = new ChatPage(driver, wait);
    }

    @Test
    void headerIsVisible() {
        assertThat(page.isHeaderVisible()).isTrue();
    }

    @Test
    void titleIsCorrect() {
        assertThat(page.getAppTitle()).isEqualTo("BigRun Coach");
    }

    @Test
    void providerTagShowsKnownProvider() {
        assertThat(page.getProviderTagText()).isIn("Ollama", "Gemini", "OpenAI");
    }

    @Test
    void messageInputIsEnabled() {
        assertThat(page.isSendButtonDisabled()).isTrue();
    }

    @Test
    void sendButtonIsVisible() {
        assertThat(page.isSendButtonDisabled()).isTrue();
    }
}
