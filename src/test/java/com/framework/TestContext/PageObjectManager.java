package com.framework.TestContext;

import com.framework.base.BasePage;
import com.framework.pageObjectImplementation.HomePageImpl;
import com.framework.pageObjectImplementation.LoginPageImpl;
import com.framework.pageObjectImplementation.ProfilePageImpl;

public class PageObjectManager {
    // Declare all your page objects here
    private LoginPageImpl loginPage;
    private HomePageImpl homePage;
    private ProfilePageImpl profilePage;
    private BasePage basePage;

    // Default constructor
    public PageObjectManager() {
    }

    /**
     * Lazy initialization: It only creates the object if it doesn't exist yet.
     * It fetches the live driver from BasePage ONLY when the step actually requests the page.
     */
    public LoginPageImpl getLoginPage() {
        return (loginPage == null) ? loginPage = new LoginPageImpl(basePage.getDriver()) : loginPage;
    }

    public HomePageImpl getHomePage() {
        return (homePage == null) ? homePage = new HomePageImpl(basePage.getDriver()) : homePage;
    }

    public ProfilePageImpl getProfilePage() {
        return (profilePage == null) ? profilePage = new ProfilePageImpl(basePage.getDriver()) : profilePage;
    }

    public BasePage getBasePage() {
        return (basePage == null) ? basePage = new BasePage() : basePage;
    }
}
