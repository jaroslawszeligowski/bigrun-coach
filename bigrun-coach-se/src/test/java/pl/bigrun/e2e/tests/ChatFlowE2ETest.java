/**
 * Project: bigrun-coach-se
 * File: ChatFlowE2ETest
 * ------------------------------------------------------------------------
 * Description:
 * Full flow tests — TC-05.
 * Verifies complete send → response cycle and keyboard behaviour.
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

import static org.assertj.core.api.Assertions.assertThat;

class ChatFlowE2ETest extends BaseE2ETest {

    private ChatPage page;

    @BeforeEach
    void initPage() {
        page = new ChatPage(driver, wait);
    }

    @Test
    void fullSendFlow() {
        page.typeMessage("Pace for 3h marathon?");
        page.clickSend();

        assertThat(page.isSpinnerVisible()).isTrue();
        assertThat(page.isSendButtonDisabled()).isTrue();

        page.waitForResponse();

        assertThat(page.isResponseVisible()).isTrue();
        assertThat(page.getResponseText()).isNotBlank();
        assertThat(page.isSpinnerVisible()).isFalse();
    }

    @Test
    void enterKeySendsMessage() {
        page.typeMessage("What is threshold heart rate?");
        page.pressEnter();

        assertThat(page.isSpinnerVisible()).isTrue();

        page.waitForResponse();

        assertThat(page.getResponseText()).isNotBlank();
    }

    @Test
    void shiftEnterDoesNotSendMessage() {
        page.typeMessage("test");
        page.pressShiftEnter();

        assertThat(page.isSpinnerVisible()).isFalse();
        assertThat(page.isSendButtonEnabled()).isTrue();
    }
}
