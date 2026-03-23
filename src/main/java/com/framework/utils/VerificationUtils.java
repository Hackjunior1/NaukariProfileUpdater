package com.framework.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Supplier;

public class VerificationUtils {

    private final WebDriver driver;
    private final Duration defaultTimeout = Duration.ofSeconds(10);

    public VerificationUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * CORE RETRY MECHANISM: Wraps validation logic in a retry loop to handle
     * transient state issues like Stale Elements in dynamic DOMs.
     */
    private boolean executeWithRetry(Supplier<Boolean> validationLogic, String actionDescription) {
        int attempts = 0;
        int maxRetries = 3;
        while (attempts < maxRetries) {
            try {
                return validationLogic.get();
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                System.out.println("⚠️ DOM state changed during '" + actionDescription + "'. Retrying... (" + (attempts + 1) + "/" + maxRetries + ")");
                waitFor(); // Brief pause before retrying
            } catch (TimeoutException e) {
                System.out.println("❌ Timeout occurred during: " + actionDescription);
                return false;
            } catch (Exception e) {
                System.out.println("❌ Unexpected error during '" + actionDescription + "': " + e.getMessage());
                return false;
            }
            attempts++;
        }
        return false;
    }

    private void waitFor() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, defaultTimeout);
    }

    // =========================================================================
    // ELEMENT VISIBILITY & STATE VALIDATIONS
    // =========================================================================

    public boolean isElementDisplayed(WebElement element) {
        return executeWithRetry(() -> {
            getWait().until(ExpectedConditions.visibilityOf(element));
            return element.isDisplayed();
        }, "verified element is displayed: " + element);
    }
    public boolean isElementInvisible(By locator) {
        return executeWithRetry(() ->
                        getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator))
                , "verify element is invisible: " + locator);
    }

    public boolean isElementEnabled(By locator) {
        return executeWithRetry(() -> {
            WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            return element.isEnabled();
        }, "verify element is enabled: " + locator);
    }

    public boolean isElementSelected(By locator) {
        return executeWithRetry(() -> {
            WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            return element.isSelected();
        }, "verify element is selected: " + locator);
    }

    // =========================================================================
    // TEXT & ATTRIBUTE VALIDATIONS
    // =========================================================================

    public boolean verifyTextEquals(By locator, String expectedText) {
        return executeWithRetry(() -> {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
            return getWait().until(ExpectedConditions.textToBe(locator, expectedText));
        }, "verify text equals '" + expectedText + "' for: " + locator);
    }

    public boolean verifyTextContains(By locator, String partialText) {
        return executeWithRetry(() -> {
            WebElement element = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
            String actualText = element.getText();
            return actualText != null && actualText.contains(partialText);
        }, "verify text contains '" + partialText + "' for: " + locator);
    }

    public boolean verifyAttributeEquals(By locator, String attributeName, String expectedValue) {
        return executeWithRetry(() -> {
            getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            return getWait().until(ExpectedConditions.attributeToBe(locator, attributeName, expectedValue));
        }, "verify attribute '" + attributeName + "' equals '" + expectedValue + "' for: " + locator);
    }

    // =========================================================================
    // PAGE & BROWSER VALIDATIONS
    // =========================================================================

    public boolean verifyPageTitleEquals(String expectedTitle) {
        return executeWithRetry(() ->
                        getWait().until(ExpectedConditions.titleIs(expectedTitle))
                , "verify page title is: " + expectedTitle);
    }

    public boolean verifyPageTitleContains(String partialTitle) {
        return executeWithRetry(() ->
                        getWait().until(ExpectedConditions.titleContains(partialTitle))
                , "verify page title contains: " + partialTitle);
    }

    public boolean verifyCurrentUrlEquals(String expectedUrl) {
        return executeWithRetry(() ->
                        getWait().until(ExpectedConditions.urlToBe(expectedUrl))
                , "verify current URL is: " + expectedUrl);
    }

    public boolean verifyCurrentUrlContains(String partialUrl) {
        return executeWithRetry(() ->
                        getWait().until(ExpectedConditions.urlContains(partialUrl))
                , "verify current URL contains: " + partialUrl);
    }
}
