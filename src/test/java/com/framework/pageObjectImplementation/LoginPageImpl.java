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
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final By otpFieldContainer = By.xpath("//div[contains(@class,'otp-fields')]");
    private final By otpInputFields = By.xpath("//div[contains(@class,'otp-fields')]//input[@type='tel']");

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

        Map<String,String> userCred = new HashMap<>();
        userCred.put("emailProvider","GMAIL");
        userCred.put("username",username);
        userCred.put("password",password);
        String otp = emailUtils.getOTPFromEmail(userCred);
        System.out.println("OTP = "+otp);
       enterOtp(otp);


    }

    private void enterOtp(String otp) {
        String normalizedOtp = otp == null ? "" : otp.replaceAll("\\D", "").trim();
        if (normalizedOtp.length() != 6) {
            throw new IllegalArgumentException("OTP must contain exactly 6 digits. Received: '" + otp + "'");
        }

        waitForOtpFieldsToLoad();

        for (int i = 0; i < normalizedOtp.length(); i++) {
            typeOtpDigit(i, normalizedOtp.charAt(i));
        }
    }

    private void waitForOtpFieldsToLoad() {
        waitUtils.waitForElementVisible(otpFieldContainer, 30, 250);

        long endTime = System.currentTimeMillis() + 30000;
        while (System.currentTimeMillis() < endTime) {
            List<WebElement> fields = driver.findElements(otpInputFields);
            if (fields.size() >= 6) {
                return;
            }
            waitUtils.threadSleepWait(250);
        }

        throw new TimeoutException("Timed out waiting for 6 OTP input fields to appear on the page.");
    }

    private void typeOtpDigit(int index, char digit) {
        long endTime = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < endTime) {
            List<WebElement> fields = driver.findElements(otpInputFields);
            if (fields.size() <= index) {
                waitUtils.threadSleepWait(200);
                continue;
            }

            WebElement field = fields.get(index);
            try {
                waitUtils.waitForClickable(field);
                field.click();
                field.sendKeys(String.valueOf(digit));
                logger.info("Entered OTP digit {} into box {}", digit, index + 1);
                return;
            } catch (Exception e) {
                waitUtils.threadSleepWait(200);
            }
        }

        throw new TimeoutException("Unable to enter OTP digit at position " + (index + 1));
    }
}
