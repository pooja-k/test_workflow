package com.demo.tests;

import com.demo.base.BaseTest;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;

public class GoogleHomeTest extends BaseTest {

    @Test
    public void testE2ELandingPage() {
        // 1. Navigate to a fast, CI-friendly developer page
        page.navigate("https://playwright.dev/java/");

        // 2. Verify Title matches expectations
        String title = page.title();
        Assert.assertTrue(title.contains("Playwright"), "Title does not contain framework name!");

        // 3. Locate a core element (Get Started Button) and verify visibility
        boolean isButtonVisible = page.locator("text=Get started").isVisible();
        Assert.assertTrue(isButtonVisible, "Get Started button is missing from the DOM.");

        // 4. Save a clean screenshot to your artifacts directory
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/surefire-reports/landing-page-success.png")));
    }
}