package com.framework.stepDefinitions;

import com.framework.TestContext.TestContext;
import com.framework.pageObjectImplementation.HomePageImpl;
import com.framework.utils.ConfigReader;
import io.cucumber.java.en.And;

public class HomePageStepDef {
    TestContext testContext;
    HomePageImpl homePage;

    public HomePageStepDef(TestContext testContext) {
        this.testContext = testContext;
        this.homePage = testContext.getPageObjectManager().getHomePage();
    }

    @And("Verify the user has successfully logged in and landed in Home page")
    public void verifyTheUserHasSuccessfullyLoggedIn() {
        homePage.isUserLoggedIn(ConfigReader.getProperty("ui.username"));
    }

    @And("the user navigated to profile page")
    public void theUserNavigatedToProfilePage() {
        homePage.navigateToProfile();
    }

}
