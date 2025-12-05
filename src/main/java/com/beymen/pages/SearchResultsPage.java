package com.beymen.pages;

import com.beymen.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchResultsPage extends BasePage {

    // Beymen product IMAGE selectors - click on image, not brand link
    private final String[] productImageSelectors = {
        ".m-productCard__image a",
        ".o-productList__item .m-productCard__image a",
        "[class*='productCard'] img",
        ".m-productCard__imageContainer a",
        "a[class*='image'] img",
        ".product-image a"
    };

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    private List<WebElement> findProductImages() {
        List<WebElement> products = new ArrayList<>();

        for (String selector : productImageSelectors) {
            try {
                products = driver.findElements(By.cssSelector(selector));
                if (!products.isEmpty()) {
                    logger.info("Found " + products.size() + " product images with selector: " + selector);
                    return products;
                }
            } catch (Exception e) {
                logger.debug("Selector failed: " + selector);
            }
        }

        // Try XPath for product images
        try {
            products = driver.findElements(By.xpath("//div[contains(@class, 'productCard')]//a[contains(@href, '/p/')]//img/.."));
            if (!products.isEmpty()) {
                logger.info("Found " + products.size() + " products with XPath");
                return products;
            }
        } catch (Exception e) {
            logger.debug("XPath selector failed");
        }

        // Fallback: any link with image inside product list
        try {
            products = driver.findElements(By.cssSelector(".o-productList a:has(img)"));
            if (!products.isEmpty()) {
                logger.info("Found " + products.size() + " products with fallback selector");
                return products;
            }
        } catch (Exception e) {
            logger.debug("Fallback selector failed");
        }

        return products;
    }

    public boolean hasProducts() {
        try {
            waitForPageLoad();
            sleep(1000);

            // Scroll to trigger lazy loading
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 300)");
            sleep(500);

            List<WebElement> products = findProductImages();
            boolean hasProducts = !products.isEmpty();
            logger.info("Products found: " + products.size());

            // Log current URL for debugging
            logger.info("Current URL: " + driver.getCurrentUrl());

            return hasProducts;
        } catch (Exception e) {
            logger.error("Error checking products: " + e.getMessage());
            return false;
        }
    }

    public int getProductCount() {
        return findProductImages().size();
    }

    public ProductPage selectRandomProduct() {
        waitForPageLoad();
        sleep(1000);

        // Scroll down to load products
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500)");
        sleep(500);

        List<WebElement> productImages = findProductImages();

        if (productImages.isEmpty()) {
            logger.error("No product images found! Current URL: " + driver.getCurrentUrl());
            logger.error("Page title: " + driver.getTitle());
            throw new RuntimeException("No products found in search results");
        }

        logger.info("Found " + productImages.size() + " product images");

        // Select random product from first 10
        Random random = new Random();
        int maxIndex = Math.min(productImages.size(), 10);
        int randomIndex = random.nextInt(maxIndex);

        WebElement selectedProduct = productImages.get(randomIndex);

        logger.info("Selecting random product at index: " + randomIndex);

        // Scroll to element and click
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", selectedProduct);
        sleep(500);

        // Click on the product image
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", selectedProduct);
            logger.info("Clicked on product image");
        } catch (Exception e) {
            // If click fails, try clicking parent link
            try {
                WebElement parent = selectedProduct.findElement(By.xpath("./ancestor::a"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", parent);
                logger.info("Clicked on parent link");
            } catch (Exception ex) {
                selectedProduct.click();
                logger.info("Direct click on element");
            }
        }

        waitForPageLoad();
        sleep(1000);

        logger.info("Navigated to product page: " + driver.getCurrentUrl());

        return new ProductPage(driver);
    }
}
