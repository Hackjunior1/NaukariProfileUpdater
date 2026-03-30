package com.framework.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class BasePage {
    private static final Logger logger = LogManager.getLogger(BasePage.class);

    // ThreadLocal ensures thread safety for parallel test execution
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static ChromeOptions setupUserProfileForChromeDriver() {
        ChromeOptions options = new ChromeOptions();

        // 1. Point to the local directory where Chrome stores your user data
        // Replace <YourUsername> with your actual OS username
        options.addArguments("user-data-dir=C:\\Users\\<user.dir>\\AppData\\Local\\Google\\Chrome\\User Data");

        // 2. Point to the specific profile folder (e.g., "Default", "Profile 1", "Profile 2")
        options.addArguments("profile-directory=Default");
        options.addArguments("--disable-notifications");

        return options;
    }

    public static ChromeOptions setChromeDriverHeadLessOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080"); // Set a fixed size so screenshots aren't tiny mobile-sized captures
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox"); // Crucial for Jenkins/Docker
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        return options;
    }

    /**
     * Initializes the WebDriver based on the browser name passed.
     *
     * @param browserName e.g., "chrome", "firefox", "edge"
     */
    public void initializeBrowser(String browserName) {
        WebDriver localDriver;

        switch (browserName.toLowerCase()) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions;
                if (Boolean.parseBoolean(ConfigReader.getProperty("headless"))) {
                    chromeOptions = setChromeDriverHeadLessOptions();
                } else {
                    chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--disable-notifications");
                }
                localDriver = new ChromeDriver(chromeOptions);
            }
            case "firefox" ->{
                WebDriverManager.firefoxdriver().setup();
                localDriver = new FirefoxDriver();
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                localDriver = new EdgeDriver();
            }
            default -> {
                logger.warn("Browser '{}' not explicitly supported. Defaulting to Chrome.", browserName);
                WebDriverManager.chromedriver().setup();
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
     *
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

    public List<HashMap<String, String>> readJsonDataToMap(String filePath) throws IOException {
        //reading json data and adding it to a string variable
        String jsonData = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);

        //converting string to HashMap using jackson databind
        ObjectMapper mapper = new ObjectMapper();
        // In this scenario we only have one data set inside the json data file. in case we have multiple data entries
        // in json data file sheet all of them will be return in form of List. that is why we have used hashMap inside a list.
        // and returning that list
        return mapper.readValue(jsonData, new TypeReference<List<HashMap<String, String>>>() {
        });
    }
}