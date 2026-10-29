package pl.bigrun.pw.tests;

import com.microsoft.playwright.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bigrun.pw.base.BasePwTest;
import pl.bigrun.pw.pages.ChatPage;

import java.util.concurrent.atomic.AtomicReference;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Project: bigrun-coach
 * File: ChatMockedApiPwTest
 * ------------------------------------------------------------------------
 * Description:
 * UI tests with the backend API mocked in the browser (page.route) —
 * no backend, no database, no LLM. Only the frontend must be served.
 * Covers answer rendering, request body, HTTP error and network failure.
 * No Selenium counterpart — showcases Playwright network interception.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
class ChatMockedApiPwTest extends BasePwTest {

    private static final String CONFIG_URL = "**/api/v1/config";
    private static final String CHAT_URL   = "**/api/v1/chat";

    private static final String OLLAMA_CONFIG = """
            {"provider":"ollama","smartModel":"qwen2.5:14b","fastModel":"llama3.2:3b","bielikModel":null}""";

    private ChatPage chat;

    @BeforeEach
    void mockConfigAndOpenChat() {
        page.route(CONFIG_URL, route -> route.fulfill(json(200, OLLAMA_CONFIG)));
        chat = new ChatPage(page).open();
    }

    @Test
    void providerTagShowsMockedProvider() {
        assertThat(chat.providerTag()).hasText("Ollama");
    }

    @Test
    void responseShowsMockedAnswer() {
        page.route(CHAT_URL, route -> route.fulfill(json(200, """
                {"answer":"Mocked: 4:16 min/km"}""")));

        chat.sendMessage("Pace for 3h marathon?");

        assertThat(chat.responseOutput()).hasText("Mocked: 4:16 min/km");
    }

    @Test
    void requestBodyContainsMessageAndSelectedModel() {
        AtomicReference<String> requestBody = new AtomicReference<>();
        page.route(CHAT_URL, route -> {
            requestBody.set(route.request().postData());
            route.fulfill(json(200, """
                    {"answer":"ok"}"""));
        });

        chat.clickModelButton("fast");
        chat.sendMessage("test");
        chat.waitForResponse();

        assertEquals("{\"message\":\"test\",\"model\":\"fast\"}", requestBody.get());
    }

    @Test
    void backendErrorIsShownToUser() {
        page.route(CHAT_URL, route -> route.fulfill(json(500, """
                {"status":500,"message":"LLM unavailable","timestamp":"2026-10-28T10:00:00Z"}""")));

        chat.sendMessage("test");

        // element błędu zawiera ikonę "✕" przed treścią — sprawdzamy samą treść
        assertThat(chat.errorMessage()).containsText("Error 500: LLM unavailable");
    }

    @Test
    void networkFailureShowsConnectionError() {
        page.route(CHAT_URL, Route::abort);

        chat.sendMessage("test");

        assertThat(chat.errorMessage()).containsText("Cannot connect to server");
    }

    private static Route.FulfillOptions json(int status, String body) {
        return new Route.FulfillOptions()
                .setStatus(status)
                .setContentType("application/json")
                .setBody(body);
    }
}
