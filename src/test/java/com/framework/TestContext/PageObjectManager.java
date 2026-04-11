package com.framework.TestContext;

import com.framework.base.BasePage;
import com.framework.pageObjectImplementation.HomePageImpl;
import com.framework.pageObjectImplementation.LoginPageImpl;
import com.framework.pageObjectImplementation.ProfilePageImpl;
import com.framework.utils.*;
import lombok.Getter;

public class PageObjectManager {
    // Declare all your page objects here
    private LoginPageImpl loginPage;
    private HomePageImpl homePage;
    private ProfilePageImpl profilePage;
    /**
     * -- GETTER --
     *  Lazy initialization: It only creates the object if it doesn't exist yet.
     *  It fetches the live driver from BasePage ONLY when the step actually requests the page.
     */
    @Getter
    private final BasePage basePage;
    private ScreenShotUtils screenShotUtils;
    private BrowserUtils browserUtils;
    private VerificationUtils verificationUtils;
    private WaitUtils waitUtils;
    private ActionsUtils actionsUtils;
    private CookieManagerUtils cookieManagerUtils;
    private EmailUtils emailUtils;

    /**
     * ✅ OPTIMIZED: Initialize BasePage ONCE in constructor.
     * No need to check null in every method call.
     */
    public PageObjectManager() {
        this.basePage = new BasePage();
    }

    public LoginPageImpl getLoginPage() {
        return (loginPage == null) ? loginPage = new LoginPageImpl(basePage.getDriver()) : loginPage;
    }

    public HomePageImpl getHomePage() {
        return (homePage == null) ? homePage = new HomePageImpl(basePage.getDriver()) : homePage;
    }

    public ProfilePageImpl getProfilePage() {
        return (profilePage == null) ? profilePage = new ProfilePageImpl(basePage.getDriver()) : profilePage;
    }

    public ScreenShotUtils getScreenShotUtils() {
        return (screenShotUtils == null) ? screenShotUtils = new ScreenShotUtils(basePage.getDriver()) : screenShotUtils;
    }

    public BrowserUtils getBrowserUtils() {
        return (browserUtils == null) ? browserUtils = new BrowserUtils(basePage.getDriver()) : browserUtils;
    }

    public VerificationUtils getVerificationUtils() {
        return (verificationUtils == null) ? verificationUtils = new VerificationUtils(basePage.getDriver(), getWaitUtils()) : verificationUtils;
    }

    public WaitUtils getWaitUtils() {
        return (waitUtils == null) ? waitUtils = new WaitUtils(basePage.getDriver()) : waitUtils;
    }

    public ActionsUtils getActionsUtils() {
        return (actionsUtils == null) ? actionsUtils = new ActionsUtils(basePage.getDriver()) : actionsUtils;
    }

    public CookieManagerUtils getCookieManagerUtils() {
        return (cookieManagerUtils == null) ? cookieManagerUtils = new CookieManagerUtils(basePage.getDriver()) : cookieManagerUtils;
    }

    public EmailUtils getEmailUtils() {
        return (emailUtils == null) ? emailUtils = new EmailUtils(basePage.getDriver()) : emailUtils;
    }
}
