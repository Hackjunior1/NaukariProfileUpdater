package com.framework.TestContext;

import lombok.Getter;
import org.openqa.selenium.WebDriver;

@Getter
public class TestContext {

        private final PageObjectManager pageObjectManager;

        public TestContext() {
            // Initialize the manager once per scenario
            pageObjectManager = new PageObjectManager();
        }

        public WebDriver getDriver(){
            return pageObjectManager.getBasePage().getDriver();
        }
}
