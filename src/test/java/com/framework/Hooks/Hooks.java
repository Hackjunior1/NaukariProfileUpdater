package com.framework.Hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.framework.TestContext.TestContext;
import com.framework.base.BasePage;
import com.framework.utils.ConfigReader;
import com.framework.utils.ExtentReportUtils;
import com.framework.utils.ScreenShotUtils;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;


public class Hooks {
    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private static final ExtentReports extentReports = ExtentReportUtils.reportsInitializer();
    private static ExtentTest extentTest;
    TestContext testContext;
    WebDriver driver;
    BasePage basePage;
    ScreenShotUtils screenShotUtils;

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
        this.driver = basePage.getDriver();
        this.screenShotUtils = testContext.getPageObjectManager().getScreenShotUtils();
        
        Reporter.getCurrentTestResult().setAttribute("driver", this.driver);
        logger.info("Browser initialized and ScreenShotUtils ready");
    }

    /**
     * This hook runs before ANY scenario (@UI or @API).
     * Useful for setting up test data, database connections, or REST Assured base URIs.
     */
    @Before(order = 1)
    public void globalSetup(Scenario scenario) {
        logger.info("Starting execution for scenario: {}", scenario.getName());
    }

    @AfterStep("@UI")
    public void addScreenshotAfterStep(Scenario scenario) {
        // Get the driver from your BasePage or TestContext
        WebDriver driver = basePage.getDriver();
        if (driver != null) {
            try {
                // Save screenshot to disk - Extent will find it via screenshot.dir config
              String screenShotPath =  screenShotUtils.takeScreenshot();
                extentTest = extentReports.createTest(scenario.getName());
                extentTest.addScreenCaptureFromPath(screenShotPath,"Failed case ScreenShot");
            } catch (Exception e) {
                logger.error("Failed to capture screenshot", e);
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
                    String screenShotPath = screenShotUtils.takeScreenshot();
                    extentTest = extentReports.createTest(scenario.getName());
                    extentTest.addScreenCaptureFromPath(screenShotPath,"Failed case ScreenShot");
                } catch (Exception e) {
                    logger.error("Failed to capture screenshot on failure", e);
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
        logger.info("Finished execution for scenario: {} | Status: {}", scenario.getName(), scenario.getStatus());
         if (extentReports != null) {
            extentReports.flush();
        }
    }
}