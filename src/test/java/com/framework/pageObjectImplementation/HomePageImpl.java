package com.framework.pageObjectImplementation;

import com.framework.base.BasePage;
import com.framework.utils.BrowserUtils;
import com.framework.utils.VerificationUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;


public class HomePageImpl extends BasePage {

    WebDriver driver;
    BrowserUtils browserUtils;
    VerificationUtils verificationUtils;

    @FindBy(css = ".info__heading")
    private WebElement profileNameText;


    @FindBy(xpath = "//div[@class='view-profile-wrapper']/a")
    private WebElement viewProfileButton;

    @FindBy(css = ".reco-title")
    private WebElement recommendedJobsHeading;

    @FindBy(className = "chatbot_DrawerContentWrapper")
    private WebElement homePageSidePanel;

    @FindBy(css = ".crossIcon.chatBot.chatBot-ic-cross")
    private WebElement btnHomePageSidePaneCross;

    public HomePageImpl(WebDriver driver) {
        this.driver = driver;
        browserUtils = new BrowserUtils(driver);
        PageFactory.initElements(driver, this);
        verificationUtils = new VerificationUtils(driver);
    }

    public void isUserLoggedIn(String userName) {
//        isSidePanelDisplayed();
        Assert.assertTrue(verificationUtils.isElementDisplayed(profileNameText));
        String usrName = profileNameText.getText();
        Assert.assertEquals(userName,usrName,"Selected user Successfully logged in");
        Assert.assertTrue(verificationUtils.isElementDisplayed(viewProfileButton));
        Assert.assertTrue(verificationUtils.isElementDisplayed(recommendedJobsHeading));
    }

    public void isSidePanelDisplayed(){
        boolean isVisible = verificationUtils.isElementDisplayed(homePageSidePanel);
       if (isVisible){
           btnHomePageSidePaneCross.click();
        }
    }

    public void navigateToProfile() {
        viewProfileButton.click();
    }

}

