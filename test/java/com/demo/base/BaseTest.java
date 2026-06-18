package com.demo.base;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeClass
    public void launchBrowser() {
        playwright = Playwright.create();
        // Headless is true by default, perfect for CI/CD environments like GitHub Actions
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeMethod
    public void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod
    public void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterClass
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}