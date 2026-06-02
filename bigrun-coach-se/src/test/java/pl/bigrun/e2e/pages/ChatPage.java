/**
 * Project: bigrun-coach-se
 * File: ChatPage
 * ------------------------------------------------------------------------
 * Description:
 * Page Object Model for BigRun Coach chat UI.
 * Single source of truth for all selectors and UI interactions.
 * Tests must not use By.* directly — only through this class.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ChatPage {

    private static final By MESSAGE_INPUT    = By.cssSelector("[data-testid=message-input]");
    private static final By SEND_BUTTON      = By.cssSelector("[data-testid=send-button]");
    private static final By LOADING_SPINNER  = By.cssSelector("[data-testid=loading-spinner]");
    private static final By ERROR_MESSAGE    = By.cssSelector("[data-testid=error-message]");
    private static final By RESPONSE_OUTPUT  = By.cssSelector("[data-testid=response-output]");
    private static final By APP_HEADER       = By.cssSelector("[data-testid=app-header]");
    private static final By APP_TITLE        = By.cssSelector("[data-testid=app-title]");
    private static final By PROVIDER_TAG     = By.cssSelector("[data-testid=provider-tag]");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final WebDriverWait longWait;

    public ChatPage(WebDriver driver, WebDriverWait wait) {
        this.driver   = driver;
        this.wait     = wait;
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // --- Akcje ---

    public void typeMessage(String text) {
        driver.findElement(MESSAGE_INPUT).sendKeys(text);
    }

    public void clearMessage() {
        WebElement input = driver.findElement(MESSAGE_INPUT);
        Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.COMMAND : Keys.CONTROL;
        input.sendKeys(Keys.chord(modifier, "a"));
        input.sendKeys(Keys.BACK_SPACE);
    }

    public void clickSend() {
        driver.findElement(SEND_BUTTON).click();
    }

    public void sendMessage(String text) {
        typeMessage(text);
        clickSend();
    }

    public void pressEnter() {
        driver.findElement(MESSAGE_INPUT).sendKeys(Keys.RETURN);
    }

    public void pressShiftEnter() {
        driver.findElement(MESSAGE_INPUT).sendKeys(Keys.chord(Keys.SHIFT, Keys.RETURN));
    }

    public void clickExampleQuery(String group, int index) {
        driver.findElement(By.cssSelector(
            "[data-testid='example-query-" + group + "-" + index + "']"
        )).click();
    }

    public void clickModelButton(String model) {
        driver.findElement(By.cssSelector("[data-testid=model-button-" + model + "]")).click();
    }

    // --- Odczyty ---

    public String getMessageInputValue() {
        return driver.findElement(MESSAGE_INPUT).getAttribute("value");
    }

    public String getAppTitle() {
        return driver.findElement(APP_TITLE).getText();
    }

    public String getProviderTagText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PROVIDER_TAG)).getText();
    }

    public String getResponseText() {
        return driver.findElement(RESPONSE_OUTPUT).getText();
    }

    public String getErrorText() {
        return driver.findElement(ERROR_MESSAGE).getText();
    }

    public String getExampleQueryText(String group, int index) {
        return driver.findElement(By.cssSelector(
            "[data-testid='example-query-" + group + "-" + index + "']"
        )).getText();
    }

    // --- Stany boolean ---

    public boolean isHeaderVisible() {
        return !driver.findElements(APP_HEADER).isEmpty()
            && driver.findElement(APP_HEADER).isDisplayed();
    }

    public boolean isSendButtonDisabled() {
        return driver.findElement(SEND_BUTTON).getAttribute("disabled") != null;
    }

    public boolean isSendButtonEnabled() {
        return !isSendButtonDisabled();
    }

    public boolean isSpinnerVisible() {
        return !driver.findElements(LOADING_SPINNER).isEmpty()
            && driver.findElement(LOADING_SPINNER).isDisplayed();
    }

    public boolean isResponseVisible() {
        return !driver.findElements(RESPONSE_OUTPUT).isEmpty();
    }

    public boolean isErrorVisible() {
        return !driver.findElements(ERROR_MESSAGE).isEmpty();
    }

    public boolean isFastButtonPresent() {
        return !driver.findElements(By.cssSelector("[data-testid=model-button-fast]")).isEmpty();
    }

    public boolean isModelActive(String model) {
        String ariaPressed = driver.findElement(
            By.cssSelector("[data-testid=model-button-" + model + "]")
        ).getAttribute("aria-pressed");
        return Boolean.parseBoolean(ariaPressed);
    }

    public boolean isSectionVisible(String group) {
        return !driver.findElements(By.cssSelector(
            "[data-testid='example-queries-" + group + "']"
        )).isEmpty();
    }

    // --- Waity ---

    public void waitForResponse() {
        longWait.until(ExpectedConditions.visibilityOfElementLocated(RESPONSE_OUTPUT));
    }

    public void waitForSpinnerToDisappear() {
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_SPINNER));
    }

    public void waitForError() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
    }
}
