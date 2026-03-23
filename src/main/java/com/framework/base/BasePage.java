package com.framework.base;

import com.framework.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

public class BasePage {

    // ThreadLocal ensures thread safety for parallel test execution
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    /**
     * Initializes the WebDriver based on the browser name passed.
     * @param browserName e.g., "chrome", "firefox", "edge"
     */
    public void initializeBrowser(String browserName) {
        WebDriver localDriver;

        switch (browserName.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions chromeOptions;
                if (Boolean.parseBoolean(ConfigReader.getProperty("headless"))) {
                    chromeOptions = setChromeDriverHeadLessOptions();
                } else{
                    chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--disable-notifications");
                }
                localDriver = new ChromeDriver(chromeOptions);
            }
            case "firefox" -> localDriver = new FirefoxDriver();
            case "edge" -> localDriver = new EdgeDriver();
            default -> {
                System.out.println("Browser not explicitly supported. Defaulting to Chrome.");
                localDriver = new ChromeDriver();
            }
        }

        // Assign the local driver to the ThreadLocal map
        driver.set(localDriver);

        // Global WebDriver configurations
        getDriver().manage().window().maximize();
        getDriver().manage().deleteAllCookies();

        // Dynamic waits
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    /**
     * Retrieves the thread-safe WebDriver instance.
     * @return WebDriver
     */
    public WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Quits the browser and cleans up the thread to prevent memory leaks.
     */
    public void quitBrowser() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
    public static ChromeOptions setupUserProfileForChromeDriver() {
        ChromeOptions options = new ChromeOptions();

        // 1. Point to the local directory where Chrome stores your user data
        // Replace <YourUsername> with your actual OS username
        options.addArguments("user-data-dir=C:\\Users\\Suresh.Patibandla\\AppData\\Local\\Google\\Chrome\\User Data");

        // 2. Point to the specific profile folder (e.g., "Default", "Profile 1", "Profile 2")
        options.addArguments("profile-directory=Default");
        options.addArguments("--disable-notifications");

        return options;
    }

    public static ChromeOptions setChromeDriverHeadLessOptions(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080"); // Set a fixed size so screenshots aren't tiny mobile-sized captures
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox"); // Crucial for Jenkins/Docker
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        return options;
    }
}