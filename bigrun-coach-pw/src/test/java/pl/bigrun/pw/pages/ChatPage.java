package pl.bigrun.pw.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Project: bigrun-coach
 * File: ChatPage
 * ------------------------------------------------------------------------
 * Description:
 * Page Object Model for BigRun Coach chat UI (Playwright).
 * Single source of truth for all selectors and UI interactions —
 * the same data-testid attributes as the Selenium ChatPage.
 * Tests must not call page.getByTestId(...) directly — only through this class.
 * Locators are exposed for web-first assertions (PlaywrightAssertions.assertThat),
 * which wait automatically — no explicit waits needed.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 28/10/2026
 */
public class ChatPage {

    // Odpowiedź LLM trwa kilka–kilkanaście sekund — jak longWait (30 s) w Selenium
    public static final double RESPONSE_TIMEOUT_MS = 30_000;

    private final Page page;

    public ChatPage(Page page) {
        this.page = page;
    }

    public ChatPage open() {
        page.navigate("/");
        return this;
    }

    // --- Lokatory ---

    public Locator messageInput() {
        return page.getByTestId("message-input");
    }

    public Locator sendButton() {
        return page.getByTestId("send-button");
    }

    public Locator loadingSpinner() {
        return page.getByTestId("loading-spinner");
    }

    public Locator errorMessage() {
        return page.getByTestId("error-message");
    }

    public Locator responseOutput() {
        return page.getByTestId("response-output");
    }

    public Locator appHeader() {
        return page.getByTestId("app-header");
    }

    public Locator appTitle() {
        return page.getByTestId("app-title");
    }

    public Locator providerTag() {
        return page.getByTestId("provider-tag");
    }

    public Locator modelButton(String model) {
        return page.getByTestId("model-button-" + model);
    }

    public Locator exampleQuery(String group, int index) {
        return page.getByTestId("example-query-" + group + "-" + index);
    }

    public Locator exampleQueriesSection(String group) {
        return page.getByTestId("example-queries-" + group);
    }

    // --- Akcje ---

    public void typeMessage(String text) {
        messageInput().fill(text);
    }

    public void clearMessage() {
        messageInput().clear();
    }

    public void clickSend() {
        sendButton().click();
    }

    public void sendMessage(String text) {
        typeMessage(text);
        clickSend();
    }

    public void pressEnter() {
        messageInput().press("Enter");
    }

    public void pressShiftEnter() {
        messageInput().press("Shift+Enter");
    }

    public void clickExampleQuery(String group, int index) {
        exampleQuery(group, index).click();
    }

    public void clickModelButton(String model) {
        modelButton(model).click();
    }

    // --- Odczyty ---

    public String getExampleQueryText(String group, int index) {
        return exampleQuery(group, index).innerText();
    }

    // Config z backendu ładuje się asynchronicznie — najpierw czekamy na tag providera,
    // dopiero potem sprawdzamy przycisk (w Selenium sprawdzenie było natychmiastowe)
    public boolean isFastButtonPresent() {
        providerTag().waitFor();
        return modelButton("fast").count() > 0;
    }

    // --- Waity ---

    public void waitForResponse() {
        responseOutput().waitFor(new Locator.WaitForOptions().setTimeout(RESPONSE_TIMEOUT_MS));
    }
}
