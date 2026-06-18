package com.demo.tests;

import com.demo.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SauceDemoTest extends BaseTest {

    private static final String SAUCE_URL = "https://www.saucedemo.com/";

    private void login(String username, String password) {
        page.navigate(SAUCE_URL);
        page.locator("[data-test='username']").fill(username);
        page.locator("[data-test='password']").fill(password);
        page.locator("[data-test='login-button']").click();
    }

    // 1. UI elements on Login Page
    @Test(priority = 1)
    public void testLoginPageUIElements() {
        page.navigate(SAUCE_URL);
        Assert.assertTrue(page.locator("[data-test='username']").isVisible(), "Username field should be visible");
        Assert.assertTrue(page.locator("[data-test='password']").isVisible(), "Password field should be visible");
        Assert.assertTrue(page.locator("[data-test='login-button']").isVisible(), "Login button should be visible");
    }

    // 2. Successful Login with Standard User
    @Test(priority = 2)
    public void testSuccessfulLogin() {
        login("standard_user", "secret_sauce");
        Assert.assertTrue(page.url().contains("/inventory.html"), "Should navigate to inventory page after login");
        Assert.assertEquals(page.locator(".title").innerText(), "Products", "Title should be 'Products'");
    }

    // 3. Login Failure with empty username
    @Test(priority = 3)
    public void testLoginWithEmptyUsername() {
        login("", "secret_sauce");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Username is required"), "Error message should mention Username is required");
    }

    // 4. Login Failure with empty password
    @Test(priority = 4)
    public void testLoginWithEmptyPassword() {
        login("standard_user", "");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Password is required"), "Error message should mention Password is required");
    }

    // 5. Login Failure with invalid credentials
    @Test(priority = 5)
    public void testLoginWithInvalidCredentials() {
        login("wrong_user", "wrong_password");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Username and password do not match"), "Error message should indicate invalid credentials");
    }

    // 6. Login Failure with locked out user
    @Test(priority = 6)
    public void testLoginWithLockedOutUser() {
        login("locked_out_user", "secret_sauce");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Sorry, this user has been locked out"), "Error message should indicate locked out state");
    }

    // 7. Successful Logout
    @Test(priority = 7)
    public void testLogout() {
        login("standard_user", "secret_sauce");
        page.locator("#react-burger-menu-btn").click();
        page.locator("#logout_sidebar_link").click();
        Assert.assertEquals(page.url(), SAUCE_URL, "Logout should redirect to login landing page");
        Assert.assertTrue(page.locator("[data-test='login-button']").isVisible(), "Login button should be visible again");
    }

    // 8. Count of Items on Inventory Page
    @Test(priority = 8)
    public void testInventoryItemCount() {
        login("standard_user", "secret_sauce");
        int itemCount = page.locator("[data-test='inventory-item']").count();
        Assert.assertEquals(itemCount, 6, "There should be exactly 6 inventory items displayed");
    }

    // 9. Navigation to Product Details Page
    @Test(priority = 9)
    public void testProductDetailsNavigation() {
        login("standard_user", "secret_sauce");
        String productName = page.locator("[data-test='item-4-title-link']").innerText();
        page.locator("[data-test='item-4-title-link']").click();
        
        Assert.assertTrue(page.url().contains("/inventory-item.html"), "Should navigate to detail page");
        String detailName = page.locator(".inventory_details_name").innerText();
        Assert.assertEquals(detailName, productName, "Product name in detail page should match inventory page");
    }

    // 10. Product Sorting: Name (A to Z)
    @Test(priority = 10)
    public void testSortNameAtoZ() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='product-sort-container']").selectOption("az");

        List<String> names = page.locator("[data-test='inventory-item-name']").allInnerTexts();
        List<String> sortedNames = new ArrayList<>(names);
        Collections.sort(sortedNames);

        Assert.assertEquals(names, sortedNames, "Products should be sorted alphabetically from A to Z");
    }

    // 11. Product Sorting: Name (Z to A)
    @Test(priority = 11)
    public void testSortNameZtoA() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='product-sort-container']").selectOption("za");

        List<String> names = page.locator("[data-test='inventory-item-name']").allInnerTexts();
        List<String> sortedNames = new ArrayList<>(names);
        sortedNames.sort(Collections.reverseOrder());

        Assert.assertEquals(names, sortedNames, "Products should be sorted alphabetically from Z to A");
    }

    // 12. Product Sorting: Price (Low to High)
    @Test(priority = 12)
    public void testSortPriceLowToHigh() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='product-sort-container']").selectOption("lohi");

        List<String> priceTexts = page.locator("[data-test='inventory-item-price']").allInnerTexts();
        List<Double> prices = new ArrayList<>();
        for (String text : priceTexts) {
            prices.add(Double.parseDouble(text.replace("$", "")));
        }

        List<Double> sortedPrices = new ArrayList<>(prices);
        Collections.sort(sortedPrices);

        Assert.assertEquals(prices, sortedPrices, "Products should be sorted by price ascending");
    }

    // 13. Product Sorting: Price (High to Low)
    @Test(priority = 13)
    public void testSortPriceHighToLow() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='product-sort-container']").selectOption("hilo");

        List<String> priceTexts = page.locator("[data-test='inventory-item-price']").allInnerTexts();
        List<Double> prices = new ArrayList<>();
        for (String text : priceTexts) {
            prices.add(Double.parseDouble(text.replace("$", "")));
        }

        List<Double> sortedPrices = new ArrayList<>(prices);
        sortedPrices.sort(Collections.reverseOrder());

        Assert.assertEquals(prices, sortedPrices, "Products should be sorted by price descending");
    }

    // 14. Add Single Product to Cart
    @Test(priority = 14)
    public void testAddSingleProductToCart() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        
        String badgeCount = page.locator("[data-test='shopping-cart-badge']").innerText();
        Assert.assertEquals(badgeCount, "1", "Cart badge count should display '1'");
        Assert.assertTrue(page.locator("[data-test='remove-sauce-labs-backpack']").isVisible(), "Add to Cart button should toggle to 'Remove'");
    }

    // 15. Add Multiple Products to Cart
    @Test(priority = 15)
    public void testAddMultipleProductsToCart() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='add-to-cart-sauce-labs-bike-light']").click();
        page.locator("[data-test='add-to-cart-sauce-labs-bolt-t-shirt']").click();

        String badgeCount = page.locator("[data-test='shopping-cart-badge']").innerText();
        Assert.assertEquals(badgeCount, "3", "Cart badge count should display '3'");
    }

    // 16. Remove Product from Inventory Page
    @Test(priority = 16)
    public void testRemoveProductFromInventory() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        Assert.assertEquals(page.locator("[data-test='shopping-cart-badge']").innerText(), "1");

        page.locator("[data-test='remove-sauce-labs-backpack']").click();
        Assert.assertEquals(page.locator("[data-test='shopping-cart-badge']").count(), 0, "Cart badge should disappear after removing the item");
    }

    // 17. Remove Product from Cart Page
    @Test(priority = 17)
    public void testRemoveProductFromCartPage() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='shopping-cart-link']").click();
        
        Assert.assertTrue(page.url().contains("/cart.html"), "Should navigate to cart page");
        Assert.assertEquals(page.locator("[data-test='inventory-item-name']").innerText(), "Sauce Labs Backpack");
        
        page.locator("[data-test='remove-sauce-labs-backpack']").click();
        Assert.assertEquals(page.locator("[data-test='inventory-item-name']").count(), 0, "Item should be removed from the cart view");
    }

    // 18. Checkout Validation: First Name Required
    @Test(priority = 18)
    public void testCheckoutMissingFirstName() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='shopping-cart-link']").click();
        page.locator("[data-test='checkout']").click();

        page.locator("[data-test='lastName']").fill("Doe");
        page.locator("[data-test='postalCode']").fill("12345");
        page.locator("[data-test='continue']").click();

        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("First Name is required"), "Error message should complain about missing first name");
    }

    // 19. Checkout Validation: Last Name Required
    @Test(priority = 19)
    public void testCheckoutMissingLastName() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='shopping-cart-link']").click();
        page.locator("[data-test='checkout']").click();

        page.locator("[data-test='firstName']").fill("John");
        page.locator("[data-test='postalCode']").fill("12345");
        page.locator("[data-test='continue']").click();

        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Last Name is required"), "Error message should complain about missing last name");
    }

    // 20. Complete Checkout Flow
    @Test(priority = 20)
    public void testCheckoutCompleteFlow() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='shopping-cart-link']").click();
        page.locator("[data-test='checkout']").click();

        page.locator("[data-test='firstName']").fill("John");
        page.locator("[data-test='lastName']").fill("Doe");
        page.locator("[data-test='postalCode']").fill("12345");
        page.locator("[data-test='continue']").click();

        Assert.assertTrue(page.url().contains("/checkout-step-two.html"), "Should navigate to step two checkout preview");
        
        // Assert correct total calculation is present
        String totalText = page.locator(".summary_total_label").innerText();
        Assert.assertTrue(totalText.contains("$32.39"), "Total cost should display correct tax-inclusive sum");

        page.locator("[data-test='finish']").click();
        Assert.assertTrue(page.url().contains("/checkout-complete.html"), "Should navigate to complete checkout confirmation");
        Assert.assertEquals(page.locator(".complete-header").innerText(), "Thank you for your order!", "Should show successful order prompt");
    }
}
