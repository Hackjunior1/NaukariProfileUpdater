package com.framework.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;

public class PopupScrollUtils {

//    For future reference checkout gemini chat Thread : https://gemini.google.com/share/6dd3ad8e47db

    WebDriver driver;

    public PopupScrollUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Scrolls down inside a specific container (like a popup).
     * @param container The scrollable element/div of the popup.
     * @param target The element you are looking for inside the popup.
     */
    public void scrollDownInPopup(WebElement container, WebElement target) {
        Actions actions = new Actions(driver);
        // This tells Selenium the "mouse" is over the popup container
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement(container);

        while (!isInteractable(target)) {
            // Scroll down 200px relative to the popup container
            actions.scrollFromOrigin(scrollOrigin, 0, 200).perform();

            // Safety: If you want to check if you hit the bottom of the popup,
            // you'd compare container.getAttribute("scrollTop") logic here.
        }
    }

    /**
     * Scrolls up inside a specific container (like a popup).
     */
    public void scrollUpInPopup(WebElement container, WebElement target) {
        Actions actions = new Actions(driver);
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement(container);

        while (!isInteractable(target)) {
            actions.scrollFromOrigin(scrollOrigin, 0, -200).perform();
        }
    }

    private boolean isInteractable(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
