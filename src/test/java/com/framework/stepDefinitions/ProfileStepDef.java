package com.framework.stepDefinitions;


import com.framework.TestContext.TestContext;
import com.framework.pageObjectImplementation.ProfilePageImpl;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class ProfileStepDef {
    TestContext testContext;
    ProfilePageImpl profilePageImpl;

    public ProfileStepDef(TestContext testContext) {
        this.testContext = testContext;
        this.profilePageImpl = testContext.getPageObjectManager().getProfilePage();
    }

    @And("the user Edit profile")
    public void theUserEditProfile() {
        profilePageImpl.updateUserProfile();
    }

    @Then("Verify profile last updated displayed with {string}")
    public void verifyAPopupWithSuccessMessageDisplayed(String LastUpdatedVal) {
        profilePageImpl.isProfileUpdated(LastUpdatedVal);
    }
}
