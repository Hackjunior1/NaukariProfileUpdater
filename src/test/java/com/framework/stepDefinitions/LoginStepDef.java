package com.framework.stepDefinitions;

import com.framework.TestContext.TestContext;
import com.framework.pageObjectImplementation.LoginPageImpl;
import com.framework.utils.ConfigReader;
import com.framework.utils.EnvReaderUtility;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class LoginStepDef {
    TestContext testContext;
    LoginPageImpl loginPage;

    String userName = null;
    String password = null;

    public LoginStepDef(TestContext testContext) {
        this.testContext = testContext;
        this.loginPage = testContext.getPageObjectManager().getLoginPage();
    }

    @Given("the user navigates to the Naukri login page")
    public void navigateToNaukriLogin() {
        loginPage.navigateToLoginPage(ConfigReader.getProperty("ui.base.url"));
    }

    @When("the user logs into Naukri profile")
    public void theUserLogsIntoNaukriProfile() {
        userName = EnvReaderUtility.getCredential("ADMIN_USERNAME");
        password = EnvReaderUtility.getCredential("ADMIN_PASSWORD");
        loginPage.performLogin(userName, password);

    }


}
