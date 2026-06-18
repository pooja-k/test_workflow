package com.demo.tests;

import com.demo.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SauceDemoTest extends BaseTest {

    private static final String SAUCE_URL = "https://www.saucedemo.com/";

    private void login(String username, String password) {
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Navigating to " + SAUCE_URL + "</i>");
        page.navigate(SAUCE_URL);
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Entering username: '" + username + "' and password</i>");
        page.locator("[data-test='username']").fill(username);
        page.locator("[data-test='password']").fill(password);
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Clicking Login button</i>");
        page.locator("[data-test='login-button']").click();
    }

    // 1. UI elements on Login Page
    @Test(priority = 1, description = "Verify that standard UI elements (username input, password input, and login button) are visible on the login landing page.")
    public void testLoginPageUIElements() {
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Navigating to SauceDemo</i>");
        page.navigate(SAUCE_URL);
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Checking visibility of input fields and login button</i>");
        Assert.assertTrue(page.locator("[data-test='username']").isVisible(), "Username field should be visible");
        Assert.assertTrue(page.locator("[data-test='password']").isVisible(), "Password field should be visible");
        Assert.assertTrue(page.locator("[data-test='login-button']").isVisible(), "Login button should be visible");
    }

    // 2. Successful Login with Standard User
    @Test(priority = 2, description = "Verify that standard_user can log in successfully and is redirected to the products inventory page.")
    public void testSuccessfulLogin() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Verifying successful redirect to inventory</i>");
        Assert.assertTrue(page.url().contains("/inventory.html"), "Should navigate to inventory page after login");
        Assert.assertEquals(page.locator(".title").innerText(), "Products", "Title should be 'Products'");
    }

    // 3. Login Failure with empty username
    @Test(priority = 3, description = "Verify validation error prompt occurs when attempting login with empty username.")
    public void testLoginWithEmptyUsername() {
        login("", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Verifying Username field validation warning</i>");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Username is required"), "Error message should mention Username is required");
    }

    // 4. Login Failure with empty password
    @Test(priority = 4, description = "Verify validation error prompt occurs when attempting login with empty password.")
    public void testLoginWithEmptyPassword() {
        login("standard_user", "");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Verifying Password field validation warning</i>");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Password is required"), "Error message should mention Password is required");
    }

    // 5. Login Failure with invalid credentials
    @Test(priority = 5, description = "Verify matching credentials validation warning when invalid credentials are provided.")
    public void testLoginWithInvalidCredentials() {
        login("wrong_user", "wrong_password");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Verifying invalid credentials matching warning</i>");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Username and password do not match"), "Error message should indicate invalid credentials");
    }

    // 6. Login Failure with locked out user
    @Test(priority = 6, description = "Verify account lockout warning matches for locked_out_user.")
    public void testLoginWithLockedOutUser() {
        login("locked_out_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Verifying lockout notification matches account status</i>");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Sorry, this user has been locked out"), "Error message should indicate locked out state");
    }

    // 7. Successful Logout
    @Test(priority = 7, description = "Verify navigation redirect sequence and session teardown on logout action.")
    public void testLogout() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Opening navigation side drawer menu</i>");
        page.locator("#react-burger-menu-btn").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Clicking logout sidebar item link</i>");
        page.locator("#logout_sidebar_link").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Asserting returned state of login page</i>");
        Assert.assertEquals(page.url(), SAUCE_URL, "Logout should redirect to login landing page");
        Assert.assertTrue(page.locator("[data-test='login-button']").isVisible(), "Login button should be visible again");
    }

    // 8. Count of Items on Inventory Page
    @Test(priority = 8, description = "Verify quantity check on inventory items display catalog counts.")
    public void testInventoryItemCount() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Counting display catalog items</i>");
        int itemCount = page.locator("[data-test='inventory-item']").count();
        Assert.assertEquals(itemCount, 6, "There should be exactly 6 inventory items displayed");
    }

    // 9. Navigation to Product Details Page
    @Test(priority = 9, description = "Verify catalog link routing redirect details display match for selected item.")
    public void testProductDetailsNavigation() {
        login("standard_user", "secret_sauce");
        String productName = page.locator("[data-test='item-4-title-link']").innerText();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Clicking title link for product: '" + productName + "'</i>");
        page.locator("[data-test='item-4-title-link']").click();
        
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Asserting redirect and detail name match</i>");
        Assert.assertTrue(page.url().contains("/inventory-item.html"), "Should navigate to detail page");
        String detailName = page.locator(".inventory_details_name").innerText();
        Assert.assertEquals(detailName, productName, "Product name in detail page should match inventory page");
    }

    // 10. Product Sorting: Name (A to Z)
    @Test(priority = 10, description = "Verify sorting order sequence works alphabetically A to Z.")
    public void testSortNameAtoZ() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Selecting sorting option A to Z</i>");
        page.locator("[data-test='product-sort-container']").selectOption("az");

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Extracting name arrays and verifying sequence sorting matches</i>");
        List<String> names = page.locator("[data-test='inventory-item-name']").allInnerTexts();
        List<String> sortedNames = new ArrayList<>(names);
        Collections.sort(sortedNames);

        Assert.assertEquals(names, sortedNames, "Products should be sorted alphabetically from A to Z");
    }

    // 11. Product Sorting: Name (Z to A)
    @Test(priority = 11, description = "Verify sorting order sequence works alphabetically Z to A.")
    public void testSortNameZtoA() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Selecting sorting option Z to A</i>");
        page.locator("[data-test='product-sort-container']").selectOption("za");

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Extracting name arrays and verifying sequence sorting matches</i>");
        List<String> names = page.locator("[data-test='inventory-item-name']").allInnerTexts();
        List<String> sortedNames = new ArrayList<>(names);
        sortedNames.sort(Collections.reverseOrder());

        Assert.assertEquals(names, sortedNames, "Products should be sorted alphabetically from Z to A");
    }

    // 12. Product Sorting: Price (Low to High)
    @Test(priority = 12, description = "Verify sorting sequence works numerically low to high based on price values.")
    public void testSortPriceLowToHigh() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Selecting sorting option Low to High</i>");
        page.locator("[data-test='product-sort-container']").selectOption("lohi");

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Extracting price value lists and verifying sorting order</i>");
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
    @Test(priority = 13, description = "Verify sorting sequence works numerically high to low based on price values.")
    public void testSortPriceHighToLow() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Selecting sorting option High to Low</i>");
        page.locator("[data-test='product-sort-container']").selectOption("hilo");

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Extracting price value lists and verifying sorting order</i>");
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
    @Test(priority = 14, description = "Verify item count increment state and dynamic remove toggle switch on single item additions.")
    public void testAddSingleProductToCart() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Clicking Add to Cart for Sauce Labs Backpack</i>");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Verifying cart badge is updated to '1' and button flips to 'Remove'</i>");
        String badgeCount = page.locator("[data-test='shopping-cart-badge']").innerText();
        Assert.assertEquals(badgeCount, "1", "Cart badge count should display '1'");
        Assert.assertTrue(page.locator("[data-test='remove-sauce-labs-backpack']").isVisible(), "Add to Cart button should toggle to 'Remove'");
    }

    // 15. Add Multiple Products to Cart
    @Test(priority = 15, description = "Verify item count updates dynamically for multiple product additions.")
    public void testAddMultipleProductsToCart() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Adding 3 separate items to the shopping cart</i>");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='add-to-cart-sauce-labs-bike-light']").click();
        page.locator("[data-test='add-to-cart-sauce-labs-bolt-t-shirt']").click();

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Verifying cart badge is updated to '3'</i>");
        String badgeCount = page.locator("[data-test='shopping-cart-badge']").innerText();
        Assert.assertEquals(badgeCount, "3", "Cart badge count should display '3'");
    }

    // 16. Remove Product from Inventory Page
    @Test(priority = 16, description = "Verify catalog remove operations teardown items and clear cart badge elements.")
    public void testRemoveProductFromInventory() {
        login("standard_user", "secret_sauce");
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Adding item and confirming cart badge is present</i>");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        Assert.assertEquals(page.locator("[data-test='shopping-cart-badge']").innerText(), "1");

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Clicking remove button from inventory page</i>");
        page.locator("[data-test='remove-sauce-labs-backpack']").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Asserting cart badge element clears</i>");
        Assert.assertEquals(page.locator("[data-test='shopping-cart-badge']").count(), 0, "Cart badge should disappear after removing the item");
    }

    // 17. Remove Product from Cart Page
    @Test(priority = 17, description = "Verify item teardown from within cart detail page screen updates line elements.")
    public void testRemoveProductFromCartPage() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Navigating to shopping cart detail page</i>");
        page.locator("[data-test='shopping-cart-link']").click();
        
        Assert.assertTrue(page.url().contains("/cart.html"), "Should navigate to cart page");
        Assert.assertEquals(page.locator("[data-test='inventory-item-name']").innerText(), "Sauce Labs Backpack");
        
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Removing item via cart line interface</i>");
        page.locator("[data-test='remove-sauce-labs-backpack']").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Asserting element is removed from checkout view</i>");
        Assert.assertEquals(page.locator("[data-test='inventory-item-name']").count(), 0, "Item should be removed from the cart view");
    }

    // 18. Checkout Validation: First Name Required
    @Test(priority = 18, description = "Verify validation constraint trigger on empty First Name during checkout sequence submission.")
    public void testCheckoutMissingFirstName() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='shopping-cart-link']").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Proceeding to checkout form page</i>");
        page.locator("[data-test='checkout']").click();

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Leaving first name empty and filling other parameters</i>");
        page.locator("[data-test='lastName']").fill("Doe");
        page.locator("[data-test='postalCode']").fill("12345");
        page.locator("[data-test='continue']").click();

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Asserting First Name is required warning prompt</i>");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("First Name is required"), "Error message should complain about missing first name");
    }

    // 19. Checkout Validation: Last Name Required
    @Test(priority = 19, description = "Verify validation constraint trigger on empty Last Name during checkout sequence submission.")
    public void testCheckoutMissingLastName() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='shopping-cart-link']").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Proceeding to checkout form page</i>");
        page.locator("[data-test='checkout']").click();

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Leaving last name empty and filling other parameters</i>");
        page.locator("[data-test='firstName']").fill("John");
        page.locator("[data-test='postalCode']").fill("12345");
        page.locator("[data-test='continue']").click();

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Asserting Last Name is required warning prompt</i>");
        String errorMsg = page.locator("[data-test='error']").innerText();
        Assert.assertTrue(errorMsg.contains("Last Name is required"), "Error message should complain about missing last name");
    }

    // 20. Complete Checkout Flow
    @Test(priority = 20, description = "Verify overall end-to-end checkout execution transaction pipeline finishes successfully with correct tax details and final confirmations.")
    public void testCheckoutCompleteFlow() {
        login("standard_user", "secret_sauce");
        page.locator("[data-test='add-to-cart-sauce-labs-backpack']").click();
        page.locator("[data-test='shopping-cart-link']").click();
        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Entering checkout flow form</i>");
        page.locator("[data-test='checkout']").click();

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Submitting complete user information forms</i>");
        page.locator("[data-test='firstName']").fill("John");
        page.locator("[data-test='lastName']").fill("Doe");
        page.locator("[data-test='postalCode']").fill("12345");
        page.locator("[data-test='continue']").click();

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Reviewing calculation summary matching tax items</i>");
        Assert.assertTrue(page.url().contains("/checkout-step-two.html"), "Should navigate to step two checkout preview");
        String totalText = page.locator(".summary_total_label").innerText();
        Assert.assertTrue(totalText.contains("$32.39"), "Total cost should display correct tax-inclusive sum");

        Reporter.log("<br>&nbsp;&nbsp;↳ <i>Finishing order and validating completion banner message</i>");
        page.locator("[data-test='finish']").click();
        Assert.assertTrue(page.url().contains("/checkout-complete.html"), "Should navigate to complete checkout confirmation");
        Assert.assertEquals(page.locator(".complete-header").innerText(), "Thank you for your order!", "Should show successful order prompt");
    }
}
