package com.framework.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BasePage {
    private static final Logger logger = LogManager.getLogger(BasePage.class);

    // ThreadLocal ensures thread safety for parallel test execution
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

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

    public ChromeOptions setChromeDriverHeadLessOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080"); // Set a fixed size so screenshots aren't tiny mobile-sized captures
        options.addArguments("--force-device-scale-factor=0.7");
        options.addArguments("--high-dpi-support=0.9");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox"); // Crucial for Jenkins/Docker
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        options.addArguments("--incognito");

//      --- Hides the "Chrome is being controlled by automated software" notification
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        String actualVersion = getDriverVersion(null);
        String customUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/" + actualVersion + " Safari/537.36";

        options.addArguments("user-agent=" + customUA);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        return options;
    }

    public void getSessionCookies(){
        Set<Cookie> cookies = getDriver().manage().getCookies();
// 3. Print them or save to a file to copy for GitLab
        for (Cookie ck : cookies) {
            if (ck.getName().equalsIgnoreCase("bm_sv"))
                System.out.println(ck.getName() + ":" + ck.getValue());
        }

    }

    /**
     * Initializes the WebDriver based on the browser name passed.
     * @param browserName e.g., "chrome", "firefox", "edge"
     */
    public void initializeBrowser(String browserName) {
        WebDriver localDriver;
        Map<String, Object> params = new HashMap<>();
        switch (browserName.toLowerCase()) {
            case "chrome" -> {
//                WebDriverManager.chromedriver().browserVersion("auto").setup();
//                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions;
                if (Boolean.parseBoolean(ConfigReader.getProperty("headless"))) {
                    chromeOptions = setChromeDriverHeadLessOptions();
                } else {
                    chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--disable-notifications");
                }
                localDriver = new ChromeDriver(chromeOptions);
                params.put("source", "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
                ((ChromeDriver) localDriver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);

            }
            case "firefox" ->{
                WebDriverManager.firefoxdriver().setup();
                localDriver = new FirefoxDriver();
            }
            case "edge" -> {
//               [Fatal Error] showing while using the edge driver. Execute once and see the issue once.
                System.setProperty(SimpleLogger.DEFAULT_FLOW_MESSAGE_FACTORY_CLASS.toString(), "warn");
                System.setProperty("wdm.edgeDriverUrl", "https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/");
                WebDriverManager.edgedriver().setup();
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
        getDriver().manage().window().maximize();
        getDriver().manage().deleteAllCookies();

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

//      Log versions for debugging version mismatch issues
        logBrowserAndDriverVersion(getDriver(),ConfigReader.getProperty("browser"));
    }

    /**
     *
     * @return web driver capabilities using which you can extract the browser like chrome/edge/firefox capabilities.
     */
    private Capabilities getBrowserCapabilities(WebDriver webDriver){
        return ((RemoteWebDriver) webDriver).getCapabilities();
    }

    /*****
     logBrowserAndDriverVersion using to print the browser version using in local system and the driver version
     trying to find solution of "SessionNotCreated" error in Ci Cd run
     @para browserDriverName expects the browser name like chrome/edge/firefox
     */
    private void logBrowserAndDriverVersion(WebDriver webDriver, String browserDriverName) {
        Capabilities caps = getBrowserCapabilities(getDriver());
        String browserName = caps.getBrowserName();
        String browserVersion = caps.getBrowserVersion();
        Object chromeOptionsObj = caps.getCapability(browserDriverName);
        String driverVersion = getDriverVersion(chromeOptionsObj);

//      Clean Logging for both Console and Logger
        String logMessage = String.format("Browser: %s | Version: %s | Driver Version: %s",
                browserName, browserVersion, driverVersion);
        System.out.println("From the driver is initialized\n"+logMessage);
        if (logger != null) {
            logger.info(logMessage);
        }
    }

    private String getDriverVersion(Object chromeOptionsObj) {
        String driverVersion = "138.0.0.0"; // Default fallback
        if ( chromeOptionsObj != null) {
            if (chromeOptionsObj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> chromeOptions = (Map<String, Object>) chromeOptionsObj;
                Object versionObj = chromeOptions.get("chromedriverVersion");
                if (versionObj != null)
                    driverVersion = versionObj.toString();
            }
        }
        else {
            try {
                ProcessBuilder pb;
//              Check if running on Windows or Linux (CI)
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    pb = new ProcessBuilder("powershell.exe", "-command",
                            "(Get-Item (Get-ItemProperty 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\chrome.exe').'(Default)').VersionInfo.ProductVersion");
                } else {
                    pb = new ProcessBuilder("google-chrome", "--version");
                }

                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = reader.readLine();

                if (output != null && !output.isEmpty()) {
                    // Regex to extract only the version number (e.g., 138.0.6613.84)
                    Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
                    Matcher matcher = pattern.matcher(output);
                    if (matcher.find()) {
                        driverVersion = matcher.group();
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not detect browser version, using default: " + driverVersion);
            }
            return driverVersion;
        }
        System.out.println("From else block in getDriverVersion\n"+"Version: "+driverVersion);
        return driverVersion;
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
        return mapper.readValue(jsonData, new TypeReference<>() {
        });
    }
}