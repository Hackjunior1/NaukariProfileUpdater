package com.framework.pageObjectImplementation;

import com.framework.TestContext.TestContext;
import com.framework.base.BasePage;
import com.framework.utils.BrowserUtils;
import com.framework.utils.VerificationUtils;
import com.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;


public class HomePageImpl{

    WebDriver driver;
    BrowserUtils browserUtils;
    VerificationUtils verificationUtils;
    WaitUtils waitUtils;

    //     =========================================================================
    /**
     * Apparently defining the locators using a By is more reliable than defining them using a WebElement.
     * To get more clarification checkout the below link and read through the end
     * "<a href="https://share.google/aimode/GiOPLPqSUD2Q476w7"/>"
     */
//     =========================================================================

    public HomePageImpl(WebDriver driver) {
        TestContext testContext = new TestContext();
        this.browserUtils = testContext.getPageObjectManager().getBrowserUtils();
        this.verificationUtils = testContext.getPageObjectManager().getVerificationUtils();
        this.waitUtils = testContext.getPageObjectManager().getWaitUtils();
        this.driver = driver;
    }

    private final By txtProfileName = By.cssSelector(".info__heading");
    private final By btnViewProfile = By.xpath("//div[@class='view-profile-wrapper']/a");

    private final By lblRecommendedJobsHeading = By.cssSelector(".reco-title");
    private final By homePageSidePanel = By.className("chatbot_DrawerContentWrapper");
    private final By btnHomePageSidePaneCross = By.cssSelector(".crossIcon.chatBot.chatBot-ic-cross");

    public void isUserLoggedIn(String userName) {
        WebElement visibleElement;

        try {
            visibleElement = waitUtils.waitForElementVisible(txtProfileName, 5, 500);
        } catch (TimeoutException ex) {
//            isSidePanelDisplayed();
            visibleElement = waitUtils.waitForElementVisible(txtProfileName, 10, 500);
        }

        Assert.assertTrue(verificationUtils.isElementDisplayed(visibleElement));
        String usrName = visibleElement.getText();
        Assert.assertEquals(userName, usrName, "Selected user Successfully logged in");
        Assert.assertTrue(verificationUtils.isElementDisplayed(driver.findElement(btnViewProfile)));
        Assert.assertTrue(verificationUtils.isElementDisplayed(driver.findElement(lblRecommendedJobsHeading)));
    }

    public void isSidePanelDisplayed() {
        try {
            WebElement ele = waitUtils.waitForElementVisible(homePageSidePanel, 5, 250);
            boolean isVisible = verificationUtils.isElementDisplayed(ele);
            if (isVisible) {
                driver.findElement(homePageSidePanel).click();
            }
        } catch (TimeoutException e) {
            // Side panel did not appear within the timeout; nothing to close, safely ignore.
        }
    }

    public void navigateToProfile() {
        driver.findElement(btnViewProfile).click();
    }

}

