/**
 * Project: bigrun-coach-se
 * File: ChatValidationE2ETest
 * ------------------------------------------------------------------------
 * Description:
 * Validation tests — TC-02, TC-03.
 * Verifies UI validation and example query buttons. No backend required.
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

import static org.assertj.core.api.Assertions.assertThat;

class ChatValidationE2ETest extends BaseE2ETest {

    private ChatPage page;

    @BeforeEach
    void initPage() {
        page = new ChatPage(driver, wait);
    }

    // TC-02 — walidacja przycisku Send

    @Test
    void sendIsDisabledWhenInputEmpty() {
        assertThat(page.isSendButtonDisabled()).isTrue();
    }

    @Test
    void sendIsEnabledAfterTyping() {
        page.typeMessage("test");
        assertThat(page.isSendButtonEnabled()).isTrue();
    }

    @Test
    void sendIsDisabledAfterClearing() {
        page.typeMessage("test");
        page.clearMessage();
        assertThat(page.isSendButtonDisabled()).isTrue();
    }

    @Test
    void sendIsDisabledForWhitespaceOnly() {
        page.typeMessage("   ");
        assertThat(page.isSendButtonDisabled()).isTrue();
    }

    // TC-03 — przyciski przykładowych zapytań

    @Test
    void toolCallingSectionIsVisible() {
        assertThat(page.isSectionVisible("tool calling")).isTrue();
    }

    @Test
    void ragSectionIsVisible() {
        assertThat(page.isSectionVisible("rag")).isTrue();
    }

    @Test
    void clickingToolCallingQueryFillsInput() {
        String expectedText = page.getExampleQueryText("tool calling", 0);
        page.clickExampleQuery("tool calling", 0);
        assertThat(page.getMessageInputValue()).isEqualTo(expectedText);
    }

    @Test
    void clickingRagQueryFillsInput() {
        String expectedText = page.getExampleQueryText("rag", 0);
        page.clickExampleQuery("rag", 0);
        assertThat(page.getMessageInputValue()).isEqualTo(expectedText);
    }

    @Test
    void clickingExampleQueryEnablesSendButton() {
        page.clickExampleQuery("rag", 0);
        assertThat(page.isSendButtonEnabled()).isTrue();
    }

    @Test
    void longMessageIsNotTruncated() {
        String longMessage = "a".repeat(500);
        page.typeMessage(longMessage);
        assertThat(page.getMessageInputValue()).hasSize(500);
    }
}
