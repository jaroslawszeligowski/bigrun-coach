package pl.bigrun.pw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.bigrun.pw.base.BasePwTest;
import pl.bigrun.pw.pages.ChatPage;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Project: bigrun-coach
 * File: ChatExampleQueriesPwTest
 * ------------------------------------------------------------------------
 * Description:
 * Parametrized — each example query fills the input and returns a response.
 * Fast model cases are skipped when the provider has no fast model (non-Ollama).
 * Playwright counterpart of ChatExampleQueriesE2ETest (Selenium).
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
class ChatExampleQueriesPwTest extends BasePwTest {

    private ChatPage chat;

    @BeforeEach
    void openChat() {
        chat = new ChatPage(page).open();
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
            assumeTrue(chat.isFastButtonPresent(), "Fast model not available — skipping");
            chat.clickModelButton("fast");
        }

        String expectedQuery = chat.getExampleQueryText(group.trim(), index);
        chat.clickExampleQuery(group.trim(), index);
        assertThat(chat.messageInput()).hasValue(expectedQuery);

        chat.clickSend();
        chat.waitForResponse();
        assertThat(chat.responseOutput()).hasText(Pattern.compile("\\S"));
    }
}
