package com.framework.utils;

import com.framework.base.BasePage;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BrowserUtils extends BasePage {
    WebDriver driver;
    private String mainWindowHandle;

    public BrowserUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Captures the current window as the main window.
     * Call this right BEFORE clicking the 'Sign in with Google' button.
     */
    public void captureMainWindow() {
        this.mainWindowHandle = driver.getWindowHandle();
        System.out.println("Captured Main window is = "+this.mainWindowHandle);
    }

    /**
     * Switches focus to the newly opened child window.
     */
    public void switchToChildWindow() {
        Set<String> allWindowHandles = driver.getWindowHandles();
        for (String handle : allWindowHandles) {
            if (!handle.equals(mainWindowHandle)) {
                System.out.println("Switching focus to child window '"+handle+"'");
                driver.switchTo().window(handle);
                break; // Switches to the first found child window
            }
        }
    }

    /**
     * Switches focus to a specific window based on its page title.
     * @param targetTitle The partial or full title of the target window.
     */
    public void switchToWindowByTitle(String targetTitle) {
        Set<String> allWindowHandles = driver.getWindowHandles();
        for (String handle : allWindowHandles) {
            System.out.println("Switching focus to window with title '"+handle+"'");
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(targetTitle)) {
                break;
            }
        }
    }

    /**
     * Switches focus back to the originally captured main window.
     */
    public void switchToMainWindow() {
        if (mainWindowHandle != null) {
            System.out.println("Switching focus to main Window '"+mainWindowHandle+"'");
            driver.switchTo().window(mainWindowHandle);
        } else {
            System.out.println("Main window handle was never captured.");
        }
    }

    /**
     * Switches focus using the index of the tabs/windows.
     * 0 is the first window, 1 is the second, etc.
     */
    public void switchToWindowByIndex(int index) {
        Set<String> allWindowHandles = driver.getWindowHandles();
        List<String> windowList = new ArrayList<>(allWindowHandles);
        if (index < windowList.size()) {
            driver.switchTo().window(windowList.get(index));
        }
    }

    /**
     * Closes all child windows and returns focus to the main window.
     */
    public void closeAllChildWindows() {
        Set<String> allWindowHandles = driver.getWindowHandles();
        for (String handle : allWindowHandles) {
            if (!handle.equals(mainWindowHandle)) {
                System.out.println("Switching focus to '"+handle+"'");
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        switchToMainWindow();
    }
}