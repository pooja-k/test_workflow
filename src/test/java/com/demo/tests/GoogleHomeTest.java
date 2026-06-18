package com.demo.tests;

import com.demo.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ColorScheme;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;

public class GoogleHomeTest extends BaseTest {

    @Test(priority = 1)
    public void testNavigationToDocs() {
        // Test 1: Verify clicking "Get Started" redirects to the correct documentation URL
        page.navigate("https://playwright.dev/java/");
        
        Locator getStartedBtn = page.locator("text=Get started");
        Assert.assertTrue(getStartedBtn.isVisible(), "Get Started button should be visible");
        
        getStartedBtn.click();
        
        // Playwright handles auto-waiting for the transition
        Assert.assertTrue(page.url().contains("/docs/intro"), "URL did not transition to the intro docs page.");
    }

    @Test(priority = 2)
    public void testDarkModeEmulation() {
        // Test 2: Emulate user browser system preferences (Dark Mode) and verify page adjustment
        context.setColorScheme(ColorScheme.DARK);
        page.navigate("https://playwright.dev/java/");
        
        // Check if the HTML root element possesses the dark theme attribute preferred by Tailwind/Docusaurus
        Locator htmlTag = page.locator("html");
        String dataTheme = htmlTag.getAttribute("data-theme");
        
        Assert.assertEquals(dataTheme, "dark", "The website did not honor the dark mode browser emulation preference.");
        
        // Capture evidence of dark mode rendering
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/surefire-reports/dark-mode-check.png")));
    }

    @Test(priority = 3)
    public void testSearchModalAndInput() {
        // Test 3: Interact with complex elements (Modals and Dynamic Key Presses)
        page.navigate("https://playwright.dev/java/");
        
        // Click the search button triggers a modal shortcut dialog
        Locator searchTrigger = page.locator(".DocSearch-Button");
        searchTrigger.click();
        
        // Wait for the modal input field to pop open
        Locator searchInput = page.locator("#docsearch-input");
        Assert.assertTrue(searchInput.isVisible(), "Search dialog input modal did not display.");
        
        // Type a query and look for dynamic suggestions
        searchInput.fill("Assertions");
        
        Locator firstResult = page.locator(".DocSearch-Hit-title").first();
        page.waitForSelector(".DocSearch-Hit-title");
        
        Assert.assertTrue(firstResult.innerText().toLowerCase().contains("assertion"), 
                "Search drop-down suggestions did not surface relevant content queries.");
    }
}