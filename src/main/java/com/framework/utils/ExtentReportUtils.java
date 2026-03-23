package com.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportUtils {
    public static ExtentReports reportsInitializer() {
        // to create extent reports we need to use two classes.
        // they are ExtentReporter and  ExtentSparkReporter.
        // ExtentSparkReporter class responsible to hold all the metadata about an executing test
        // in the form of object.
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/reports/ExtentReports/Report.html");
        sparkReporter.config().setReportName("Naukri Profile Update Reporter");
        sparkReporter.config().setDocumentTitle("Naukri Profile Execution Report");

        //ExtentReports class is responsible to put the result of an executed test case into the reports.
        // this will only work when we pass the ExtentSparkReporter object to ExtentReports object
        // just like the line below the object creation
        ExtentReports reports = new ExtentReports();
        reports.setSystemInfo("Tester", "suresh P.");
        reports.attachReporter(sparkReporter);
        return reports;
    }
}
