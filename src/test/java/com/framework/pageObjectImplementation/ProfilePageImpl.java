package com.framework.pageObjectImplementation;

import com.framework.utils.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

public class ProfilePageImpl {
    WebDriver driver;

    BrowserUtils browserUtils;
    VerificationUtils verificationUtils;
    ActionsUtils actionsUtils;
    WaitUtils waitUtils;

    public ProfilePageImpl(WebDriver driver) {
        this.driver = driver;
        browserUtils = new BrowserUtils(driver);
        waitUtils = new WaitUtils(driver);
        verificationUtils = new VerificationUtils(driver);
        actionsUtils = new ActionsUtils(driver);
        PageFactory.initElements(driver, this);
    }
    // Quick Links section - "Resume" link
    @FindBy(xpath = "//li[text() = 'Quick links']/following :: span[normalize-space(.) = 'Resume']")
    private WebElement resumeQuickLink;

    // Quick Links section - "Resume Headline" link
    @FindBy(xpath = "//ul//span[contains(text(),'Resume headline')]")
    private WebElement resumeHeadlineQuickLink;

    // The 'Edit' pen icon next to the Resume Headline section
    @FindBy(xpath = "//span[text()='Resume headline']/following-sibling::span[contains(@class,'edit')]")
    private WebElement editResumeHeadlineButton;

    // The text area where the headline is typed
    @FindBy(id = "resumeHeadlineTxt")
    private WebElement resumeHeadlineTextArea;

    // The Save button inside the headline edit modal
    @FindBy(xpath = "//button[text()='Save']")
    private WebElement saveHeadlineButton;

    // The success message that pops up after saving
    @FindBy(xpath = "//p[contains(text(),'successfully updated') or contains(text(), 'Success')]")
    private WebElement successToastMessage;

    @FindBy(xpath = "//div[@class = 'dashboard-component']//span[text() = 'Edit']/following-sibling::*[normalize-space(.) = 'editOneTheme']")
    private WebElement profileEditButton;

    @FindBy(xpath = "//div[text() = 'Basic details']")
    private WebElement lblBasicDetailsPopupTxt;

    @FindBy(id = "saveBasicDetailsBtn")
    private WebElement profileSaveButton;

    @FindBy(xpath = "//span[contains(normalize-space(.),'Profile last updated') and not (@class = 'lazilyLoad ')]//span[@class]")
    private WebElement profileUpdatedTxtMgs;

    @FindBy(className = "circle")
    private WebElement loader;

    public void isUserInProfilePage(){
        Assert.assertTrue(verificationUtils.isElementDisplayed(resumeHeadlineQuickLink));
    }
    /**
     * Clicks the 'Resume' link in the Quick Links sidebar to scroll down to the Resume section.
     */

    public void updateResumeHeadline(){
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
        resumeHeadlineQuickLink.click();
    }

    /**
     * Clicks the edit icon next to the Resume Headline to open the text area.
     */
    public void clickEditResumeHeadline() {
        editResumeHeadlineButton.click();
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
        saveHeadlineButton.click();
    }

    /**
     * Waits for the success toast message to appear on the screen at Resume Headline section
     * @return boolean true if the success message is displayed
     */
    public void isSuccessMessageDisplayed() {
        try {
            Assert.assertTrue(verificationUtils.isElementDisplayed(successToastMessage));
            Assert.assertEquals(successToastMessage.getText(), "Success");

        } catch (Exception e) {
            System.out.println("Success toast message did not appear within the timeout period.");

        }
    }

    public void updateUserProfile(){
       profileEditButton.click();
       actionsUtils.waitForElementToDisappear(loader,500);
       actionsUtils.scrollToElementUsingJsExecutor(profileSaveButton);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
       profileSaveButton.getText();
    }
    public void isProfileUpdated(String LastUpdatedVal){
        String profileUpdatedText = profileUpdatedTxtMgs.getText();
        Assert.assertEquals(profileUpdatedText,LastUpdatedVal,"Profile last updated - "+profileUpdatedText);
    }

}
