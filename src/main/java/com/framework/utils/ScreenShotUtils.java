package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ScreenShotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenShotUtils.class);

    private final WebDriver driver;

    public ScreenShotUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Captures a screenshot and saves it to the Reports/screenshot directory.
     */
    public String takeScreenshot() {
        if (driver == null) {
            logger.error("WebDriver is null. Cannot capture screenshot. Ensure browser is initialized.");
            throw new IllegalStateException("WebDriver is not initialized");
        }

        Path screenshotDir = Paths.get(System.getProperty("user.dir"), "Reports", "ScreenShots");
        String timestamp = DateTimeFormatter.ofPattern("MMM_dd_yyyy_hh-mm_ss").format(LocalDateTime.now());
        String fileName = timestamp + ".png";
        Path destinationPath = screenshotDir.resolve(fileName);

        try {
            Files.createDirectories(screenshotDir);
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            if (sourceFile == null) {
                logger.error("Screenshot capture returned null. Driver may not be responsive.");
                throw new IllegalStateException("Screenshot capture returned null");
            }

            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Screenshot saved at: {}", destinationPath.toAbsolutePath());
            return destinationPath.toString();
        } catch (IOException e) {
            logger.error("Failed to save screenshot at {}", destinationPath.toAbsolutePath(), e);
            throw new RuntimeException("Failed to save screenshot", e);
        } catch (NullPointerException e) {
            logger.error("NullPointerException during screenshot: {}", e.getMessage(), e);
            throw new RuntimeException("NullPointerException during screenshot", e);
        } catch (Exception e) {
            logger.error("Unexpected error during screenshot capture", e);
            throw new RuntimeException("Unexpected error during screenshot capture", e);
        }
    }
}