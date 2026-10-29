package pl.bigrun.pw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bigrun.pw.base.BasePwTest;
import pl.bigrun.pw.pages.ChatPage;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Project: bigrun-coach
 * File: ChatFlowPwTest
 * ------------------------------------------------------------------------
 * Description:
 * Send flow — spinner, disabled button, response; Enter sends, Shift+Enter does not.
 * Requires a running stack with an LLM provider (real responses).
 * Playwright counterpart of ChatFlowE2ETest (Selenium).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
class ChatFlowPwTest extends BasePwTest {

    private static final Pattern NOT_BLANK = Pattern.compile("\\S");

    private ChatPage chat;

    @BeforeEach
    void openChat() {
        chat = new ChatPage(page).open();
    }

    @Test
    void fullSendFlow() {
        chat.typeMessage("Pace for 3h marathon?");
        chat.clickSend();

        assertThat(chat.loadingSpinner()).isVisible();
        assertThat(chat.sendButton()).isDisabled();

        chat.waitForResponse();
        assertThat(chat.responseOutput()).hasText(NOT_BLANK);
        assertThat(chat.loadingSpinner()).isHidden();
    }

    @Test
    void enterKeySendsMessage() {
        chat.typeMessage("What is threshold heart rate?");
        chat.pressEnter();

        assertThat(chat.loadingSpinner()).isVisible();
        chat.waitForResponse();
        assertThat(chat.responseOutput()).hasText(NOT_BLANK);
    }

    @Test
    void shiftEnterDoesNotSendMessage() {
        chat.typeMessage("test");
        chat.pressShiftEnter();

        assertThat(chat.loadingSpinner()).isHidden();
        assertThat(chat.sendButton()).isEnabled();
    }
}
