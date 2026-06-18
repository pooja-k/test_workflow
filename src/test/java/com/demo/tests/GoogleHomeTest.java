package com.demo.tests;

import com.demo.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ColorScheme; // This one is fine!
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
        
        Assert.assertTrue(page.url().contains("/docs/intro"), "URL did not transition to the intro docs page.");
    }

    @Test(priority = 2)
    public void testDarkModeEmulation() {
        // Test 2: Emulate user browser system preferences (Dark Mode) safely via page options
        page.navigate("https://playwright.dev/java/");
        
        // No separate import needed; Java resolves Page.EmulateMediaOptions automatically
        page.emulateMedia(new Page.EmulateMediaOptions().setColorScheme(ColorScheme.DARK));
        
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
        
        Locator searchTrigger = page.locator(".DocSearch-Button");
        searchTrigger.click();
        
        Locator searchInput = page.locator("#docsearch-input");
        Assert.assertTrue(searchInput.isVisible(), "Search dialog input modal did not display.");
        
        searchInput.fill("Assertions");
        
        Locator firstResult = page.locator(".DocSearch-Hit-title").first();
        page.waitForSelector(".DocSearch-Hit-title");
        
        Assert.assertTrue(firstResult.innerText().toLowerCase().contains("assertion"), 
                "Search drop-down suggestions did not surface relevant content queries.");
    }
}