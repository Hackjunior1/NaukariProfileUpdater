package com.framework.Hooks;

import com.framework.TestContext.TestContext;
import com.framework.base.BasePage;
import com.framework.utils.ConfigReader;
import com.framework.utils.ScreenShotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;


public class Hooks {
    TestContext testContext;
    WebDriver driver;
    BasePage basePage;
    // The TestContext is injected automatically by PicoContainer
    public Hooks(TestContext testContext) {
        this.testContext = testContext;
        this.basePage = testContext.getPageObjectManager().getBasePage();
    }
    /**
     * This hook only runs for scenarios tagged with @UI.
     * It prevents the browser from launching during API test execution.
     */
    @Before("@UI")
    public void setupUI() {
        basePage.initializeBrowser(ConfigReader.getProperty("browser"));
        Reporter.getCurrentTestResult().setAttribute("driver", basePage.getDriver());

    }

    /**
     * This hook runs before ANY scenario (@UI or @API).
     * Useful for setting up test data, database connections, or REST Assured base URIs.
     */
    @Before(order = 1)
    public void globalSetup(Scenario scenario) {
        System.out.println("Starting execution for scenario: " + scenario.getName());
    }

    @AfterStep("@UI")
    public void addScreenshotAfterStep(Scenario scenario) {
        // Get the driver from your BasePage or TestContext
        WebDriver driver = basePage.getDriver();
        if (driver != null) {
            try {
                // Save screenshot to disk - Extent will find it via screenshot.dir config
                ScreenShotUtils.takeScreenshot(scenario.getName(), driver);
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    /**
     * This hook only runs for scenarios tagged with @UI.
     * It captures a screenshot if the scenario fails and attaches it to the Extent/PDF report,
     * then gracefully tears down the WebDriver instance.
     */
    @After("@UI")
    public void tearDownUI(Scenario scenario) {
        WebDriver driver = basePage.getDriver();

        if (driver != null) {
            if (scenario.isFailed()) {
                try {
                    // Save failed screenshot to disk
                    String safeTestName = scenario.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
                    ScreenShotUtils.takeScreenshot(safeTestName, driver);
                } catch (Exception e) {
                    System.err.println("Failed to capture screenshot on failure: " + e.getMessage());
                }
            }
            basePage.quitBrowser();
        }
    }

/**                                     Do not delete this method*/
//    public void tearDownUI(Scenario scenario) {
//        WebDriver driver = basePage.getDriver();
//
//        if (driver != null) {
//            // 1. SAVE TO DISK: Clean the scenario name to be file-system safe, then use our utility
//            String safeTestName = scenario.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
//            String screenshotPath = ScreenShotUtils.takeScreenshot(safeTestName,driver);
//
//            if (scenario.isFailed()) {
//                if (screenshotPath != null) {
//                    try {
//                        // Capture screenshot as a byte array to attach it to the Extent/PDF report
//                        final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//                        scenario.attach(screenshot, "image/png", "Screenshot of failed step");
//                    } catch (Exception e) {
//                        System.err.println("Failed to attach to Cucumber report: " + e.getMessage());
//                    }
//                    Reporter.getCurrentTestResult().setAttribute("finalScreenshotPath", screenshotPath);
//                }
//            }
//            basePage.quitBrowser();
//        }
//    }
    /**
     * This hook runs after ANY scenario (@UI or @API).
     */
    @After(order = 1)
    public void globalTearDown(Scenario scenario) {
        System.out.println("Finished execution for scenario: " + scenario.getName() + " | Status: " + scenario.getStatus());
    }
}