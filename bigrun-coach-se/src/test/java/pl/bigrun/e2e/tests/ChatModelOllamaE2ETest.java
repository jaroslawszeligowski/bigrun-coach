/**
 * Project: bigrun-coach-se
 * File: ChatModelOllamaE2ETest
 * ------------------------------------------------------------------------
 * Description:
 * Model selection tests — TC-04-03, TC-04-05, TC-04-06.
 * Verifies Fast button behaviour. Requires provider = Ollama.
 * Run with: mvn test -Dprovider=ollama
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
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import pl.bigrun.e2e.base.BaseE2ETest;
import pl.bigrun.e2e.pages.ChatPage;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfSystemProperty(named = "provider", matches = "ollama")
class ChatModelOllamaE2ETest extends BaseE2ETest {

    private ChatPage page;

    @BeforeEach
    void initPage() {
        page = new ChatPage(driver, wait);
    }

    @Test
    void fastButtonIsPresentForOllama() {
        assertThat(page.isFastButtonPresent()).isTrue();
    }

    @Test
    void clickingFastActivatesFast() {
        page.clickModelButton("fast");
        assertThat(page.isModelActive("fast")).isTrue();
        assertThat(page.isModelActive("smart")).isFalse();
    }

    @Test
    void returningToSmartActivatesSmart() {
        page.clickModelButton("fast");
        page.clickModelButton("smart");
        assertThat(page.isModelActive("smart")).isTrue();
        assertThat(page.isModelActive("fast")).isFalse();
    }
}
