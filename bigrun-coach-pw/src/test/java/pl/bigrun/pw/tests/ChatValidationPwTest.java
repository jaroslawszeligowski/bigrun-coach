package pl.bigrun.pw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bigrun.pw.base.BasePwTest;
import pl.bigrun.pw.pages.ChatPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Project: bigrun-coach
 * File: ChatValidationPwTest
 * ------------------------------------------------------------------------
 * Description:
 * Input validation — send button state (TC-02) and example query buttons (TC-03).
 * Playwright counterpart of ChatValidationE2ETest (Selenium).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
class ChatValidationPwTest extends BasePwTest {

    private ChatPage chat;

    @BeforeEach
    void openChat() {
        chat = new ChatPage(page).open();
    }

    // TC-02 — walidacja przycisku Send

    @Test
    void sendIsDisabledWhenInputEmpty() {
        assertThat(chat.sendButton()).isDisabled();
    }

    @Test
    void sendIsEnabledAfterTyping() {
        chat.typeMessage("test");
        assertThat(chat.sendButton()).isEnabled();
    }

    @Test
    void sendIsDisabledAfterClearing() {
        chat.typeMessage("test");
        chat.clearMessage();
        assertThat(chat.sendButton()).isDisabled();
    }

    @Test
    void sendIsDisabledForWhitespaceOnly() {
        chat.typeMessage("   ");
        assertThat(chat.sendButton()).isDisabled();
    }

    // TC-03 — przyciski przykładowych zapytań

    @Test
    void toolCallingSectionIsVisible() {
        assertThat(chat.exampleQueriesSection("tool calling")).isVisible();
    }

    @Test
    void ragSectionIsVisible() {
        assertThat(chat.exampleQueriesSection("rag")).isVisible();
    }

    @Test
    void clickingToolCallingQueryFillsInput() {
        String expectedText = chat.getExampleQueryText("tool calling", 0);
        chat.clickExampleQuery("tool calling", 0);
        assertThat(chat.messageInput()).hasValue(expectedText);
    }

    @Test
    void clickingRagQueryFillsInput() {
        String expectedText = chat.getExampleQueryText("rag", 0);
        chat.clickExampleQuery("rag", 0);
        assertThat(chat.messageInput()).hasValue(expectedText);
    }

    @Test
    void clickingExampleQueryEnablesSendButton() {
        chat.clickExampleQuery("rag", 0);
        assertThat(chat.sendButton()).isEnabled();
    }

    @Test
    void longMessageIsNotTruncated() {
        String longMessage = "a".repeat(500);
        chat.typeMessage(longMessage);
        assertThat(chat.messageInput()).hasValue(longMessage);
    }
}
