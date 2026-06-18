package com.demo.tests;

import com.demo.base.BaseTest;
import com.microsoft.playwright.Locator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class GoogleHomeTest extends BaseTest {

    @Test
    public void testGoogleTitleAndSearch() {
        // 1. Navigate to Google
        page.navigate("https://www.google.com");

        // 2. Verify Title
        String title = page.title();
        Assert.assertEquals(title, "Google", "Title does not match!");

        // 3. Search for a term
        Locator searchBox = page.locator("textarea[name='q']"); // Google's current search input selector
        searchBox.fill("Playwright Java");
        searchBox.press("Enter");

        // 4. Wait for network to settle and verify search page title contains the term
        page.waitForURL("**/search*");
        Assert.assertTrue(page.title().contains("Playwright Java"), "Search page title missing query term.");

        // 5. Take a screenshot of the results for evidence (will be saved in GitHub artifact)
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/google-search-result.png")));
    }
}