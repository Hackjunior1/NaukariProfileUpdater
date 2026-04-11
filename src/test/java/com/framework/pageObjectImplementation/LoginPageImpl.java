package com.framework.pageObjectImplementation;

import com.framework.TestContext.TestContext;
import com.framework.base.BasePage;
import com.framework.utils.BrowserUtils;
import com.framework.utils.CookieManagerUtils;
import com.framework.utils.EmailUtils;
import com.framework.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPageImpl{
    private static final Logger logger = LogManager.getLogger(LoginPageImpl.class);

    BrowserUtils browserUtils;
    WebDriver driver;
    WaitUtils waitUtils;
    BasePage basePage;
    CookieManagerUtils cookieManagerUtils;
    EmailUtils emailUtils;

//     =========================================================================
    /**
     * Apparently defining the locators using a By is more reliable than defining them using a WebElement.
     * To get more clarification checkout the below link and read through the end
     * "<a href="https://share.google/aimode/GiOPLPqSUD2Q476w7"/>"
     */
//     =========================================================================

    public LoginPageImpl(WebDriver driver) {
        this.driver = driver;
        TestContext testContext = new TestContext();

        this.browserUtils = testContext.getPageObjectManager().getBrowserUtils();
        this.waitUtils = testContext.getPageObjectManager().getWaitUtils();
        this.basePage = testContext.getPageObjectManager().getBasePage();
        this.cookieManagerUtils = testContext.getPageObjectManager().getCookieManagerUtils();
        this.emailUtils = testContext.getPageObjectManager().getEmailUtils();
    }

    private final By txtEmailOrUsername = By.id("usernameField");
    private final By txtPassword = By.id("passwordField");
    private final By btnLogin = By.xpath("//button[@type='submit']");
    private final By lnkForgotPassword = By.linkText("Forgot Password?");
    private final By lblErrorMessage = By.xpath("//span[@class='err-msg']");
    private final By btnGoogleLogin = By.xpath("//span[text()='Sign in with Google']/..");
    private final By lnkRegister = By.xpath("//a[text()='Register for free']");
    private final By googleUserName = By.xpath("//input[@aria-label='Email or phone']");
    private final By googleNextButton = By.xpath(
            "//div[@data-primary-action-label='Next']//span[normalize-space(.)='Next']/parent::button");
    private final By googleUserPassword = By.xpath("//input[@aria-label='Enter your password']");

    /**
     * Navigates to the provided base URL.
     * @param url The environment URL fetched from config.properties
     */
    public void navigateToLoginPage(String url) {
        driver.get(url);


//        String cookieValue = System.getenv("SESSION_COOKIE_VALUE");
//        Cookie ck = new Cookie("NtToken", cookieValue, ".naukri.com", "/", null);
//        driver.manage().addCookie(ck);

         // Now you are logged in

    }

    /**
     * Completes the login flow by opening the modal and submitting credentials.
     * @param username Fetched from .env
     * @param password Fetched from .env
     */
    public void performLogin(String username, String password) {
        WebElement waitingEle = waitUtils.waitForElementVisible(txtEmailOrUsername,5,200);
        waitUtils.waitForClickable(waitingEle);
        driver.findElement(txtEmailOrUsername).sendKeys(username);
        driver.findElement(txtPassword).sendKeys(password);
        driver.findElement(btnLogin).click();
        logger.info("login button clicked and user login action started");

//      --- BYPASSING THE OTP SCREEN USING COOKIE SESSION
//        waitUtils.threadSleepWait(3000);
//        cookieManagerUtils.saveCookies();
//        cookieManagerUtils.loadCookies();
//        driver.navigate().refresh();
        emailUtils.getOTPFromEmail();
    }
}
