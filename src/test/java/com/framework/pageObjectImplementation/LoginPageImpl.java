package com.framework.pageObjectImplementation;

import com.framework.base.BasePage;

import com.framework.utils.BrowserUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPageImpl extends BasePage{

    BrowserUtils browserUtils;
    WebDriver driver;

    @FindBy(id = "usernameField")
    private WebElement txtEmailOrUsername;

    @FindBy(id = "passwordField")
    private WebElement txtPassword;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement btnLogin;

    @FindBy(linkText = "Forgot Password?")
    private WebElement lnkForgotPassword;

    @FindBy(xpath = "//span[@class='err-msg']")
    private WebElement lblErrorMessage;

    @FindBy(xpath = "//span[text()='Sign in with Google']/..")
    private WebElement btnGoogleLogin;

    @FindBy(xpath = "//a[text()='Register for free']")
    private WebElement lnkRegister;

    @FindBy(xpath = "//input[@aria-label='Email or phone']")
    private WebElement googleUserName;

    @FindBy(xpath = "//div[@data-primary-action-label='Next']//span[normalize-space(.)='Next']/parent::button")
    private WebElement googleNextButton;

    @FindBy(xpath = "//input[@aria-label='Enter your password']")
    private WebElement googleUserPassword;

    public LoginPageImpl(WebDriver driver) {
        this.driver = driver;
        browserUtils = new BrowserUtils(driver);
        PageFactory.initElements(this.driver, this);
    }

    /**
     * Navigates to the provided base URL.
     * @param url The environment URL fetched from config.properties
     */
    public void navigateToLoginPage(String url) {
        driver.get(url);
    }

    /**
     * Completes the login flow by opening the modal and submitting credentials.
     * @param username Fetched from .env
     * @param password Fetched from .env
     */
    public void performLogin(String username, String password) {
        txtEmailOrUsername.sendKeys(username);
        txtPassword.sendKeys(password);
        btnLogin.click();
        System.out.println("login button clicked and user login action started");
    }
}
