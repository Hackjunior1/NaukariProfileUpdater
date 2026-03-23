package com.framework.Hooks;

import com.framework.TestContext.TestContext;
import com.framework.base.BasePage;
import com.framework.utils.ConfigReader;
import com.framework.utils.ScreenShotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

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
    }

    /**
     * This hook runs before ANY scenario (@UI or @API).
     * Useful for setting up test data, database connections, or REST Assured base URIs.
     */
    @Before(order = 1)
    public void globalSetup(Scenario scenario) {
        System.out.println("Starting execution for scenario: " + scenario.getName());
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
//            if (scenario.isFailed()) {
//                // Capture screenshot as a byte array to attach it to the Extent/PDF report
//                final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//                scenario.attach(screenshot, "image/png", "Screenshot of failed step");
//                // 2. SAVE TO DISK: Clean the scenario name to be file-system safe, then use our utility
//                String safeTestName = scenario.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
//                ScreenShotUtils.takeScreenshot(driver, safeTestName);
//            }
            basePage.quitBrowser();
        }
    }

    /**
     * This hook runs after ANY scenario (@UI or @API).
     */
    @After(order = 1)
    public void globalTearDown(Scenario scenario) {
        System.out.println("Finished execution for scenario: " + scenario.getName() + " | Status: " + scenario.getStatus());
    }
}