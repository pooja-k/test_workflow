package com.demo.tests;

import com.demo.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;

public class GoogleHomeTest extends BaseTest {

    @Test
    public void testGoogleTitleAndSearch() {
        // 1. Navigate to Google
        page.navigate("https://www.google.com");

        // 2. Verify Homepage Title
        String title = page.title();
        Assert.assertEquals(title, "Google", "Title does not match!");

        // 3. Find search box and fill it
        Locator searchBox = page.locator("textarea[name='q']");
        searchBox.fill("Playwright Java");
        searchBox.press("Enter");

        // 4. Wait for the search results container to appear on screen
        page.waitForSelector("#search");

        // 5. Take a screenshot of the actual state for debugging
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/surefire-reports/google-search-result.png")));

        // 6. Robust Assertion: Verify the input field on the results page still holds our query
        String inputValue = page.locator("textarea[name='q']").inputValue();
        Assert.assertEquals(inputValue, "Playwright Java", "The search query was not preserved in the input box.");
    }
}