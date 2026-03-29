package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.time.Duration;

public class VerificationUtils {
    private static final Logger logger = LogManager.getLogger(VerificationUtils.class);

    private final WebDriver driver;
    private final Duration defaultTimeout = Duration.ofSeconds(10);
    private final WaitUtils waitUtils;
    private final SoftAssert softAssert;

    /**
     * ✅ Constructor now accepts WaitUtils from PageObjectManager
     * Eliminates the need to create objects with new keyword
     */
    public VerificationUtils(WebDriver driver, WaitUtils waitUtils) {
        this.driver = driver;
        this.softAssert = new SoftAssert();
        this.waitUtils = waitUtils;
    }

    private void waitFor(int timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private WebDriverWait getWait() {
        return waitUtils.explicitWait(driver, defaultTimeout);
    }

    // =========================================================================
    // ELEMENT VISIBILITY & STATE VALIDATIONS
    // =========================================================================

    public boolean isElementDisplayed(WebElement element) {
        try {
            if (element == null) {
                logger.warn("isElementDisplayed: element is null");
                return false;
            }
            getWait().until(ExpectedConditions.visibilityOf(element));
            return element.isDisplayed();
        } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException e) {
            logger.warn("isElementDisplayed failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in isElementDisplayed", e);
            return false;
        }
    }

    public boolean isElementDisplayed(By locator) {
        try {
            if (driver.findElement(locator) == null) {
                logger.warn("isElementDisplayed: element is null");
                return false;
            }
            getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
            return driver.findElement(locator).isDisplayed();
        } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException e) {
            logger.warn("isElementDisplayed failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in isElementDisplayed", e);
            return false;
        }
    }

    public boolean isElementInvisible(By locator) {
        try {
            getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("isElementInvisible(By) failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in isElementInvisible(By)", e);
            return false;
        }
    }

    public boolean isElementInvisible(WebElement element) {
        try {
            getWait().until(ExpectedConditions.invisibilityOf(element));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("isElementInvisible(WebElement) failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in isElementInvisible(WebElement)", e);
            return false;
        }
    }

    public boolean isElementEnabled(By locator) {
        try {
            WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            return element.isEnabled();
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("isElementEnabled failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in isElementEnabled", e);
            return false;
        }
    }

    public boolean isElementSelected(By locator) {
        try {
            WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            return element.isSelected();
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("isElementSelected failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in isElementSelected", e);
            return false;
        }
    }

    // =========================================================================
    // TEXT & ATTRIBUTE VALIDATIONS RETURNS A BOOLEAN VALUE.
    // =========================================================================

    public boolean verifyTextEquals(By locator, String expectedText) {
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
            return getWait().until(ExpectedConditions.textToBe(locator, expectedText));
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("verifyTextEquals failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyTextEquals", e);
            return false;
        }
    }

    public boolean verifyTextContains(By locator, String partialText) {
        try {
            WebElement element = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
            String actualText = element.getText();
            return actualText != null && actualText.contains(partialText);
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("verifyTextContains failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyTextContains", e);
            return false;
        }
    }

    public boolean verifyAttributeEquals(By locator, String attributeName, String expectedValue) {
        try {
            getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            return getWait().until(ExpectedConditions.attributeToBe(locator, attributeName, expectedValue));
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("verifyAttributeEquals failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyAttributeEquals", e);
            return false;
        }
    }

    // =========================================================================
    // PAGE & BROWSER VALIDATIONS RETURNS A BOOLEAN VALUE.
    // =========================================================================

    public boolean verifyPageTitleEquals(String expectedTitle) {
        try {
            return getWait().until(ExpectedConditions.titleIs(expectedTitle));
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("verifyPageTitleEquals failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyPageTitleEquals", e);
            return false;
        }
    }

    public boolean verifyPageTitleContains(String partialTitle) {
        try {
            return getWait().until(ExpectedConditions.titleContains(partialTitle));
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("verifyPageTitleContains failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyPageTitleContains", e);
            return false;
        }
    }

    public boolean verifyCurrentUrlEquals(String expectedUrl) {
        try {
            return getWait().until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("verifyCurrentUrlEquals failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyCurrentUrlEquals", e);
            return false;
        }
    }

    public boolean verifyCurrentUrlContains(String partialUrl) {
        try {
            return getWait().until(ExpectedConditions.urlContains(partialUrl));
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("verifyCurrentUrlContains failed", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyCurrentUrlContains", e);
            return false;
        }
    }

    // =========================================================================
    // STRING VALIDATIONS (HARD ASSERTIONS).
    // =========================================================================

    // Hard Assertion: Use for critical flow blockers
    public void verifyStrEqual(String actual, String expected, String message) {
        logger.info("Validating: {}", message);
        Assert.assertEquals(actual, expected, message);
    }

    // =========================================================================
    // STRING VALIDATIONS (SOFT ASSERTIONS).
    // =========================================================================

    // Soft Assertion: Use for non-critical UI checks (e.g., footer text)
    public void softAssertStrEqual(String actual, String expected, String message) {
        softAssert.assertEquals(actual, expected, message);
    }

    // Call this at the end of the test to report soft failures
    public void finalizeValidations() {
        softAssert.assertAll();
    }
}
