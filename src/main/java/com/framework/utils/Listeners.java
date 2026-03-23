package com.framework.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Listeners implements ITestListener {
    private static final Logger log = LogManager.getLogger(Listeners.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("========= Test Suite Started: " + context.getName() + " =========");
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