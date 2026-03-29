package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ActionsUtils {
    private static final Logger logger = LogManager.getLogger(ActionsUtils.class);
    private final WebDriver driver;
    Actions actions;
    WaitUtils waitUtils;

    public ActionsUtils(WebDriver driver) {
        this.driver = driver;
        actions = new Actions(driver);

    }

    /**
     * Helper method to check if the element is displayed and enabled (interactable).
     */
    private static boolean isInteractable(WebElement element) {
        try {
            boolean idDisplayed = element.isDisplayed();
            boolean isEnabled = element.isEnabled();
            return !element.isDisplayed() || !element.isEnabled();
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Scrolls down (Clockwise wheel rotation) until the element is visible and interactable.
     */
    public void scrollDownToElement(WebElement element) {
        long lastHeight = 0;
        while (!isInteractable(element)) {
            actions.scrollByAmount(0, 300).perform();

            // Check if we've hit the bottom of the page to prevent infinite looping
            long currentHeight = (long) ((JavascriptExecutor) driver).executeScript("return window.pageYOffset;");
            if (currentHeight == lastHeight) {
                logger.warn("Reached end of page while scrolling; Element not found.");
                break;
            }
            lastHeight = currentHeight;
        }
    }

    /**
     * Scrolls up (Anti-clockwise wheel rotation) until the element is visible and interactable.
     */
    public void scrollUpToElement(WebElement element) {
        long lastHeight = -1;
        while (!isInteractable(element)) {
            actions.scrollByAmount(0, -300).perform();

            // Check if we've hit the top of the page
            long currentHeight = (long) ((JavascriptExecutor) driver).executeScript("return window.pageYOffset;");
            if (currentHeight == 0 || currentHeight == lastHeight) {
                logger.warn("Reached top of page while scrolling; Element not found.");
                break;
            }
            lastHeight = currentHeight;
        }
    }

    public void moveToElement(WebElement element) {
        actions.moveToElement(element);
    }

    /**
     * Scrolls the element into the center of the viewport and waits for it to be visually present.
     * Useful for ensuring elements are visible in screenshots.
     */
    public void scrollToElementUsingJsExecutor(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. Scroll the element to the center of the viewport (smoother for popups)
        js.executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center'});", element);

        // 2. Wait until the element is actually within the visual viewport boundaries
        new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(d -> (Boolean) js.executeScript(
                        "var rect = arguments[0].getBoundingClientRect();" +
                                "return (rect.top >= 0 && rect.bottom <= (window.innerHeight || document.documentElement.clientHeight));",
                        element));
    }
}
