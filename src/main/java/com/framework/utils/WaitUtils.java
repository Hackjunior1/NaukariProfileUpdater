package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;


public class WaitUtils {
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    final Duration defaultTimeout = Duration.ofSeconds(30);
    final Duration milliSec = Duration.ofMillis(500);
    private final WebDriver webDriver;

    public WaitUtils(WebDriver webDriver) {
        this.webDriver = webDriver;
    }


// Refer gemini chat Thread "https://gemini.google.com/share/074978d0ab75"

    /**
     * Fluent Wait implementation for Element Visibility.
     * Handles NoSuchElement and StaleElement exceptions during the polling period.
     */
    public WebElement waitForElementVisible(By locator, int waitTIme, int pollingEvery) {
        Wait<WebDriver> fluentWait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(waitTIme))
                .pollingEvery(Duration.ofMillis(pollingEvery))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Timeout: Element was not visible after " + defaultTimeout + " seconds.");

        try {
           return fluentWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            // Log specific error details or take a screenshot here
            throw new TimeoutException("Failed to find element visibility: " + locator.toString(), e);
        }
    }

    /**
     * Fluent Wait implementation for Element Clickability.
     * Specifically ignores StaleElementReferenceException which often occurs during JS refreshes.
     */
    public void waitForClickable(WebElement element) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(defaultTimeout)
                .pollingEvery(milliSec)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class)
                .withMessage("Timeout: Element was not clickable after " + defaultTimeout + " seconds.");

        try {
             wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            throw new TimeoutException("Element remained non-clickable: " + element.toString(), e);
        }
    }

    /**
     * Waits until the element is located in the DOM, irrespective of whether it is visible in the UI.
     * presenceOfElementLocated() will only look for element in DOM and check the availability of element.
     * Once the element is located in DOM the wait will complete.
     */

    public void waitForElementPresence(int timeoutSec, int pollingMs, By locator) {
        Wait<WebDriver> wait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(timeoutSec))
                .pollingEvery(Duration.ofMillis(pollingMs))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException("Element remained non-visible: " + locator.toString(), e);
        }
    }

    /**
     * Waits for a loader or spinner to disappear from the UI/DOM.
     * Use 'By' locator to avoid StaleElementReferenceException.
     */
    public void waitForElementToDisappear(WebElement locator, int timeoutInMilliSeconds) {
        try {
            explicitWait(webDriver, Duration.ofMillis(timeoutInMilliSeconds))
                    .until(ExpectedConditions.invisibilityOf(locator));
            logger.info("waitForElementToDisappear: element is now invisible: {}", locator);

        } catch (TimeoutException e) {
            logger.warn("waitForElementToDisappear timeout after {} ms: {}", timeoutInMilliSeconds, locator);

        } catch (Exception e) {
            logger.error("waitForElementToDisappear unexpected error", e);

        }
    }

    /**
     * Sets the Implicit Wait for the driver instance.
     * Note: Use cautiously as it can conflict with Explicit Waits.
     */
    public void ImplicitWebDriverWait(WebDriver driver, int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }


    /**
     * Convenience wrapper around {@link Thread#sleep(long)} with proper interruption handling.
     *
     * @param millis the time to sleep in milliseconds; non-positive values are ignored.
     */
    public void threadSleepWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Thread interrupted while sleeping for {} ms", millis, e);
        }
    }


    /**
     * Returns a standard WebDriverWait instance for custom conditions with default wait used in this.
     *
     */
    public WebDriverWait explicitWait(WebDriver driver, Duration milliSeconds) {
        return new WebDriverWait(driver, milliSeconds);
    }


    /**
     * Configures a FluentWait with custom polling and ignored exceptions.
     */
    public Wait<WebDriver> getFluentWait(WebDriver driver, int timeoutSec, int pollingMs) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSec))
                .pollingEvery(Duration.ofMillis(pollingMs))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

}
