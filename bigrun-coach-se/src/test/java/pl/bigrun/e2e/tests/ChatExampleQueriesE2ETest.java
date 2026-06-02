/**
 * Project: bigrun-coach-se
 * File: ChatExampleQueriesE2ETest
 * ------------------------------------------------------------------------
 * Description:
 * Parameterized tests for all 10 example query buttons.
 * Each query is tested with an assigned model (smart or fast).
 * Verifies: query fills input, response is received.
 * Fast model tests are skipped when provider is not Ollama.
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.bigrun.e2e.base.BaseE2ETest;
import pl.bigrun.e2e.pages.ChatPage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ChatExampleQueriesE2ETest extends BaseE2ETest {

    private ChatPage page;

    @BeforeEach
    void initPage() {
        page = new ChatPage(driver, wait);
    }

    @ParameterizedTest(name = "[{index}] group={0} query={1} model={2}")
    @CsvSource({
        "tool calling, 0, smart",
        "tool calling, 1, smart",
        "tool calling, 2, smart",
        "tool calling, 3, fast",
        "tool calling, 4, fast",
        "tool calling, 5, fast",
        "rag,          0, smart",
        "rag,          1, smart",
        "rag,          2, fast",
        "rag,          3, fast"
    })
    void exampleQueryFillsInputAndReturnsResponse(String group, int index, String model) {
        if ("fast".equals(model)) {
            assumeTrue(page.isFastButtonPresent(), "Fast model not available — skipping");
            page.clickModelButton("fast");
        }

        String expectedQuery = page.getExampleQueryText(group.trim(), index);
        page.clickExampleQuery(group.trim(), index);

        assertThat(page.getMessageInputValue()).isEqualTo(expectedQuery);

        page.clickSend();
        page.waitForResponse();

        assertThat(page.getResponseText()).isNotBlank();
    }
}
