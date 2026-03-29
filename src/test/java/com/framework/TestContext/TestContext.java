package com.framework.TestContext;

import lombok.Getter;

@Getter
public class TestContext {

    private final PageObjectManager pageObjectManager;

    public TestContext() {
        // Initialize the manager once per scenario
        pageObjectManager = new PageObjectManager();
    }
}
