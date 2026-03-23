package com.framework.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;


public class ScreenShotUtils {

    private  Robot robot;

    // Initialize the Robot instance lazily
    private Robot getRobot() throws AWTException {
        if (robot == null) {
            robot = new Robot();
        }
        return robot;
    }

    /**
     * Captures the entire primary screen.
     *
     * @return A BufferedImage representing the screen capture.
     * @throws AWTException If the platform configuration does not allow low-level input control.
     */
    public BufferedImage captureFullScreen() throws AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return getRobot().createScreenCapture(screenRect);
    }

    /**
     * Captures a specific region of the screen.
     *
     * @param region The Rectangle area to capture (x, y, width, height).
     * @return A BufferedImage representing the captured area.
     * @throws AWTException If the platform configuration does not allow low-level input control.
     */
    public BufferedImage captureRegion(Rectangle region) throws AWTException {
        return getRobot().createScreenCapture(region);
    }

    /**
     * Saves a BufferedImage to a file.
     *
     * @param image    The BufferedImage to save.
     * @param format   The image format (e.g., "png", "jpg").
     * @param filePath The destination file path including the file name and extension.
     * @throws IOException If an error occurs during writing.
     */
    public void saveImage(BufferedImage image, String format, String filePath) throws IOException {
        File outputFile = new File(filePath);

        // Ensure the parent directories exist
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        ImageIO.write(image, format, outputFile);
    }

    /**
     * Convenience method: Captures the full screen and immediately saves it to a file.
     *
     * @param filePath The destination file path.
     * @param format   The image format (e.g., "png").
     * @return true if successful, false otherwise.
     */
    public boolean captureAndSaveFullScreen(String filePath, String format) {
        try {
            BufferedImage screen = captureFullScreen();
            saveImage(screen, format, filePath);
            return true;
        } catch (AWTException | IOException e) {
            System.err.println("Failed to capture and save screenshot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

//    // --- Usage Example ---
//    public static void main(String[] args) {
//        String desktopPath = System.getProperty("user.home") + "/Desktop/fullscreen_capture.png";
//
//        System.out.println("Taking screenshot...");
//        boolean success = captureAndSaveFullScreen(desktopPath, "png");
//
//        if (success) {
//            System.out.println("Screenshot saved successfully to: " + desktopPath);
//        }
//    }

    /**
     * Captures a screenshot and saves it to the Reports/screenshot directory.
     *
     * @param scenarioName The name of the scenario or test.
     * @return The absolute path to the saved screenshot, or null if it failed.
     */
    public static String takeScreenshot(String scenarioName,WebDriver driver) {
        // 1. Define the base directory relative to the project root
        String projectRoot = System.getProperty("user.dir");
        Path screenshotDir = Paths.get(projectRoot, "Reports", "ScreenShots");

        // 2. Sanitize the scenario name and generate a timestamped file name
        String safeName = scenarioName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String timestamp = DateTimeFormatter.ofPattern("MMM_dd_yyyy_hh-mm a").format(LocalDateTime.now());
        String fileName = safeName + "_" + timestamp + ".png";

        Path destinationPath = screenshotDir.resolve(fileName);

        try {
            // 3. Ensure the target directories exist (Crucial for CI/CD)
            Files.createDirectories(screenshotDir);

            // 4. Capture the screenshot using Selenium
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // 5. Copy the file to the target destination
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("screenshot path '"+destinationPath.toAbsolutePath().toString()+"'");
            return "../ScreenShots/" + fileName;

        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during screenshot capture: " + e.getMessage());
        }

        return null;
    }
}
//Reference Code
//    public String takeScreenshot(String testCaseName, WebDriver driver){
//        File ScreenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//        String time = DateTimeFormatter.ofPattern("MMM_dd_yyyy_hh-mm a").format(LocalDateTime.now());
//        String filePath = System.getProperty("user.dir")+"/reports/ScreenShots/"+testCaseName+"-"+time+".png";
//        try {
//            FileUtils.copyFile(ScreenShot,new File(filePath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return filePath;
//    }