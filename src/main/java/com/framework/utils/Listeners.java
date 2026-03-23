package com.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listeners implements ITestListener {
    private static final Logger log = LogManager.getLogger(Listeners.class);
    //ExtentTest is what create an entry about a test case in the Html report.
    ExtentTest extentTest;
    ExtentReports reports = extentReportUtils.reportsInitializer();
    WebDriver driver;

    //to get an understanding of why I have used this here watch udemy session 179.
    ThreadLocal<ExtentTest> testExecutionSync = new ThreadLocal<>();
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        extentTest = reports.createTest(testName);
        testExecutionSync.set(extentTest);
        log.info("Test Execution started : {}", extentTest);
        testExecutionSync.get().log(Status.INFO, "Test execution started: " + testName);
    }
    @Override
    public void onTestSuccess(ITestResult result) {
        testExecutionSync.get().log(Status.PASS,result.getTestName());
        String screenShotPath = new ScreenShotUtils(driver).takeScreenshot(result.getMethod().getMethodName(),driver);
        testExecutionSync.get().addScreenCaptureFromPath(screenShotPath);
    }
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " failed!"));
        log.info("Failed to Execute Method : "+result.getMethod().getMethodName());
        //getThrowable() method contains the error message of a failed test case, and we are passing it to ExtentTest object to print the error message on to html reports.
        testExecutionSync.get().fail(result.getThrowable());
        try {
            driver = (WebDriver) result.getTestClass().getRealClass().getField("driver").get(result.getInstance());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        String screenShotPath = new ScreenShotUtils(driver).takeScreenshot(result.getMethod().getMethodName(),driver);
        testExecutionSync.get().addScreenCaptureFromPath(screenShotPath);

    }
    @Override
    public void onTestSkipped(ITestResult result) {
        testExecutionSync.get().log(Status.SKIP,result.getTestName());
    }
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        this.onTestFailure(result);
    }

    @Override
    public void onFinish(ITestContext context) {
        reports.flush();
    }

    /**
     * Helper method to safely grab the driver and attach a Base64 screenshot to the report.
     */
//    private void attachScreenshotToReport(ITestResult result, String screenshotName) {
//        // Retrieve the driver from the ITestResult (Thread-safe for parallel execution)
//        WebDriver driver = (WebDriver) result.getAttribute("WebDriver");
//
//        if (driver != null) {
//            // Using Base64 is perfect for HTML reports because the image is embedded directly in the HTML file.
//            // This means no broken image links if you share the HTML file over email or in Jenkins!
//            String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
//
//            extentTestThreadSafe.get().info(
//                    screenshotName,
//                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
//            );
//        } else {
//            extentTestThreadSafe.get().log(Status.WARNING, "WebDriver was null, could not attach screenshot.");
//        }
//    }
}