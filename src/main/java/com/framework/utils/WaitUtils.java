package com.framework.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.util.NoSuchElementException;


public class WaitUtils {
    WebDriver driver;
    private WebDriverWait wait;

    final Duration defaultTimeout = Duration.ofSeconds(30);
    final Duration milliSec = Duration.ofMillis(500);

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
    }

// Refer gemini chat Thread "https://gemini.google.com/share/074978d0ab75"
    /**
     * Fluent Wait implementation for Element Visibility.
     * Handles NoSuchElement and StaleElement exceptions during the polling period.
     */
    public WebElement waitForVisible(WebElement element) {
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(defaultTimeout)
                .pollingEvery(milliSec)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Timeout: Element was not visible after " + defaultTimeout + " seconds.");

        try {
            return fluentWait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            // Log specific error details or take a screenshot here
            throw new TimeoutException("Failed to find element visibility: " + element.toString(), e);
        }
    }

    /**
     * Fluent Wait implementation for Element Clickability.
     * Specifically ignores StaleElementReferenceException which often occurs during JS refreshes.
     */
    public WebElement waitForClickable(WebElement element) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(defaultTimeout)
                .pollingEvery(milliSec)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class)
                .withMessage("Timeout: Element was not clickable after " + defaultTimeout + " seconds.");

        try {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            throw new TimeoutException("Element remained non-clickable: " + element.toString(), e);
        }
    }
    /**
     * Sets the Implicit Wait for the driver instance.
     * Note: Use cautiously as it can conflict with Explicit Waits.
     */
    public void ImplicitWebDriverWait(int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }


    /**
     * Returns a standard WebDriverWait instance for custom conditions with default wait used in this.
     *
     */
    public WebDriverWait explicitWait(int milliSeconds){
        return new WebDriverWait(driver, Duration.ofMillis(milliSeconds)) ;
    }


    /**
     * Configures a FluentWait with custom polling and ignored exceptions.
     */
    public Wait<WebDriver> getFluentWait(int timeoutSec, int pollingMs) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSec))
                .pollingEvery(Duration.ofMillis(pollingMs))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

}
