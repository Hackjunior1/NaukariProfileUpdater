package com.framework.utils;

import java.io.File;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listeners implements ITestListener {
    private static final Logger log = LogManager.getLogger(Listeners.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("========= Test Suite Started: " + context.getName() + " =========");
        System.out.println("extent.properties exists? " + 
        Paths.get("target/test-classes/extent.properties").toFile().exists());
        System.out.println("extent.properties loaded: " + (new File("target/test-classes/extent.properties").exists()));
    }

    @Override
    public void onTestStart(ITestResult result) {
        // High-level log for the console/log file
        log.info("Starting execution for scenario: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Scenario PASSED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("Scenario FAILED: " + result.getMethod().getMethodName());
        log.error("Error Detail: " + result.getThrowable().getMessage());

        // NOTE: We no longer take screenshots here.
        // The ExtentCucumberAdapter handles the screenshot attachment
        // directly at the failing Gherkin step.
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Scenario SKIPPED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("========= Test Suite Finished: " + context.getName() + " =========");
    }
}