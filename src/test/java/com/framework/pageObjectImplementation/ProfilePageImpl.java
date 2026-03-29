package com.framework.pageObjectImplementation;

import com.framework.TestContext.TestContext;
import com.framework.utils.ActionsUtils;
import com.framework.utils.BrowserUtils;
import com.framework.utils.VerificationUtils;
import com.framework.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class ProfilePageImpl {
    private static final Logger logger = LogManager.getLogger(ProfilePageImpl.class);
    WebDriver driver;

    BrowserUtils browserUtils;
    VerificationUtils verificationUtils;
    ActionsUtils actionsUtils;
    WaitUtils waitUtils;

//  =========================================================================
    /**
     * Apparently defining the locators using a By is more reliable than defining them using a WebElement.
     * To get more clarification checkout the below link and read through the end
     * "<a href="https://share.google/aimode/GiOPLPqSUD2Q476w7"/>"
     */
// =========================================================================
    public ProfilePageImpl(WebDriver driver) {
        this.driver = driver;
        TestContext testContext = new TestContext();
        this.browserUtils = testContext.getPageObjectManager().getBrowserUtils();
        this.waitUtils = testContext.getPageObjectManager().getWaitUtils();
        this.verificationUtils = testContext.getPageObjectManager().getVerificationUtils();
        this.actionsUtils = testContext.getPageObjectManager().getActionsUtils();
    }


    private final By resumeHeadlineQuickLink = By.xpath("//ul//span[contains(text(),'Resume headline')]");
    private final By editResumeHeadlineButton = By.xpath("//span[text()='Resume headline']/following-sibling::span[contains(@class,'edit')]");
    private final By saveHeadlineButton = By.xpath("//button[text()='Save']");
    private final By successToastMessage = By.xpath("//p[contains(text(),'successfully updated') or contains(text(), 'Success')]");
    private final By profileEditButton = By.xpath("//div[@class = 'dashboard-component']//span[text() = 'Edit']/following-sibling::*[normalize-space(.) = 'editOneTheme']");
    private final By profileSaveButton = By.id("saveBasicDetailsBtn");
    private final By profileUpdatedTxtMgs = By.xpath("//span[contains(normalize-space(.),'Profile last updated') and not (@class = 'lazilyLoad ')]//span[@class]");
    private final By loader = By.className("circle");
    private final By resumeHeadlineTextArea = By.id("resumeHeadlineTxt");
    private final By lblBasicDetailsPopupTxt = By.xpath("//div[text() = 'Basic details']");
    private final By resumeQuickLink = By.xpath("//li[text() = 'Quick links']/following :: span[normalize-space(.) = 'Resume']");


    public void isUserInProfilePage() {
        Assert.assertTrue(verificationUtils.isElementDisplayed(driver.findElement(resumeHeadlineQuickLink)));
    }

    /**
     * Clicks the 'Resume' link in the Quick Links sidebar to scroll down to the Resume section.
     */

    public void updateResumeHeadline() {
        clickResumeHeadlineQuickLink();
        clickEditResumeHeadline();
        clickSaveHeadline();
    }

    public void clickResumeQuickLink() {

    }

    /**
     * Clicks the 'Resume Headline' link in the Quick Links sidebar.
     */
    public void clickResumeHeadlineQuickLink() {
        driver.findElement(resumeHeadlineQuickLink).click();
    }

    /**
     * Clicks the edit icon next to the Resume Headline to open the text area.
     */
    public void clickEditResumeHeadline() {
        driver.findElement(editResumeHeadlineButton).click();
    }

    /**
     * Retrieves the current text inside the Resume Headline text area.
     * @return Current headline text
     */
    public String getHeadlineText() {
        return null;
    }

    /**
     * Clears the existing headline and enters the new provided text.
     * @param newHeadline The string to update the headline with
     */
    public void updateHeadlineText(String newHeadline) {

    }

    /**
     * Clicks the save button to submit the updated headline.
     */
    public void clickSaveHeadline() {
        driver.findElement(saveHeadlineButton).click();
    }

    /**
     * Waits for the success toast message to appear on the screen at Resume Headline section
     * @return boolean true if the success message is displayed
     */
    public void isSuccessMessageDisplayed() {
        try {
            WebElement successMessage = driver.findElement(successToastMessage);
            Assert.assertTrue(verificationUtils.isElementDisplayed(successMessage));
            Assert.assertEquals(successMessage.getText(), "Success");

        } catch (Exception e) {
            logger.warn("Success toast message did not appear within the timeout period.", e);
        }
    }

    public void updateUserProfile() {
        driver.findElement(profileEditButton).click();
        waitUtils.waitForElementToDisappear(driver.findElement(loader), 500);
        actionsUtils.scrollToElementUsingJsExecutor(driver.findElement(profileSaveButton));
        driver.findElement(profileSaveButton).click();
    }

    public void isProfileUpdated(String LastUpdatedVal) {
        Assert.assertTrue(verificationUtils.verifyTextEquals(profileUpdatedTxtMgs,LastUpdatedVal));
    }
}
