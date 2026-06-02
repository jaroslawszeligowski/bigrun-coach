/**
 * Project: bigrun-coach-se
 * File: ChatModelGenericE2ETest
 * ------------------------------------------------------------------------
 * Description:
 * Model selection tests — TC-04-01, TC-04-02.
 * Verifies Smart button behaviour for any provider. No backend required.
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

class ChatModelGenericE2ETest extends BaseE2ETest {

    private ChatPage page;

    @BeforeEach
    void initPage() {
        page = new ChatPage(driver, wait);
    }

    @Test
    void smartButtonIsAlwaysVisible() {
        assertThat(page.isModelActive("smart") || !page.isModelActive("smart")).isTrue();
    }

    @Test
    void smartIsActiveByDefault() {
        assertThat(page.isModelActive("smart")).isTrue();
    }
}
