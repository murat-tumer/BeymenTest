package com.beymen.pages;

import com.beymen.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductPage extends BasePage {

    private final String[] nameSelectors = {
            "h1[class*='title']",
            ".o-productDetail__title",
            "[class*='productName']",
            "h1",
            ".product-title"
    };

    private final String[] priceSelectors = {
            "[class*='price'] span",
            ".m-price__current",
            "[class*='salePrice']",
            "[class*='Price']",
            ".price"
    };

    private final String[] sizeSelectors = {
            ".m-variation__item:not(.-disabled):not(.m-variation__item--disabled)",
            "[class*='size']:not([class*='disabled'])",
            ".size-option:not(.disabled)",
            "button[class*='variation']:not([disabled])"
    };

    private final String[] addToCartSelectors = {
            "#addBasket",
            "button[class*='addToCart']",
            "button[class*='addBasket']",
            "[class*='addToCart'] button",
            "button[class*='sepet']",
            ".o-productDetail__addToCart",
            "#productAddToCart"
    };

    private final String[] cartIconSelectors = {
            "a[href*='basket']",
            "a[href*='sepet']",
            "[class*='basket']",
            "[class*='cart']",
            ".o-header__userInfo--basket"
    };

    private String selectedProductName;
    private String selectedProductPrice;

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    private WebElement findElementBySelectors(String[] selectors) {
        for (String selector : selectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    logger.info("Found element with selector: " + selector);
                    return elements.get(0);
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    public String getProductName() {
        try {
            waitForPageLoad();
            sleep(500);

            WebElement nameElement = findElementBySelectors(nameSelectors);
            if (nameElement != null) {
                selectedProductName = nameElement.getText().trim();
                logger.info("Product name: " + selectedProductName);
                return selectedProductName;
            }

            selectedProductName = driver.getTitle().split("\\|")[0].trim();
            logger.info("Product name from title: " + selectedProductName);
            return selectedProductName;
        } catch (Exception e) {
            logger.error("Error getting product name: " + e.getMessage());
            return "Unknown Product";
        }
    }

    public String getProductPrice() {

        if (selectedProductPrice != null && !selectedProductPrice.isEmpty()) {
            logger.info("Returning cached product price: " + selectedProductPrice);
            return selectedProductPrice;
        }

        try {
            sleep(300);

            String[] xpathSelectors = {
                    "//span[contains(@class, 'salePrice') or contains(@class, 'current')]",
                    "//div[contains(@class, 'price')]//span[contains(text(), 'TL')]",
                    "//*[contains(@class, 'productDetail')]//*[contains(text(), 'TL')]"
            };

            for (String xpath : xpathSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(xpath));
                    for (WebElement el : elements) {
                        String text = el.getText().trim();
                        if (!text.isEmpty() && text.matches(".*\\d+.*")) {
                            selectedProductPrice = text;
                            logger.info("Product price found with XPath: " + selectedProductPrice);
                            return selectedProductPrice;
                        }
                    }
                } catch (Exception e) {

                }
            }

            String[] specificPriceSelectors = {
                    ".m-price__current",
                    ".m-productPrice__salePrice",
                    "[class*='salePrice']"
            };

            for (String selector : specificPriceSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    for (WebElement el : elements) {
                        String text = el.getText().trim();
                        if (!text.isEmpty() && (text.contains("TL") || text.matches(".*\\d+[.,]\\d+.*"))) {
                            selectedProductPrice = text;
                            logger.info("Product price found with CSS: " + selectedProductPrice);
                            return selectedProductPrice;
                        }
                    }
                } catch (Exception e) {

                }
            }

            return "0 TL";
        } catch (Exception e) {
            logger.error("Error getting product price: " + e.getMessage());
            return "0 TL";
        }
    }

    public void selectAvailableSize() {
        try {
            sleep(500);

            for (String selector : sizeSelectors) {
                List<WebElement> sizes = driver.findElements(By.cssSelector(selector));
                if (!sizes.isEmpty()) {
                    for (WebElement size : sizes) {
                        if (size.isDisplayed() && size.isEnabled()) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", size);
                            logger.info("Size selected with selector: " + selector);
                            sleep(500);
                            return;
                        }
                    }
                }
            }

            logger.info("No size selection required or available");
        } catch (Exception e) {
            logger.info("Size selection not available: " + e.getMessage());
        }
    }

    public void addToCart() {
        try {
            selectAvailableSize();
            sleep(500);

            WebElement addButton = null;

            for (String selector : addToCartSelectors) {
                List<WebElement> buttons = driver.findElements(By.cssSelector(selector));
                if (!buttons.isEmpty()) {
                    for (WebElement btn : buttons) {
                        if (btn.isDisplayed() && btn.isEnabled()) {
                            addButton = btn;
                            logger.info("Found add to cart button with selector: " + selector);
                            break;
                        }
                    }
                }
                if (addButton != null)
                    break;
            }

            if (addButton == null) {

                List<WebElement> xpathButtons = driver.findElements(
                        By.xpath(
                                "//button[contains(text(), 'SEPET') or contains(text(), 'Sepet') or contains(text(), 'sepet') or contains(@class, 'add')]"));
                if (!xpathButtons.isEmpty()) {
                    addButton = xpathButtons.get(0);
                    logger.info("Found add to cart button with XPath");
                }
            }

            if (addButton != null) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
                sleep(300);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
                logger.info("Product added to cart");
                sleep(1500);
            } else {
                throw new RuntimeException("Add to cart button not found");
            }
        } catch (Exception e) {
            logger.error("Error adding to cart: " + e.getMessage());
            throw new RuntimeException("Failed to add product to cart", e);
        }
    }

    public CartPage goToCart() {
        sleep(1000);

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        sleep(500);

        WebElement cartLink = null;

        String[] xpathSelectors = {
                "//span[contains(text(), 'Sepetim')]/ancestor::a",
                "//a[.//span[contains(text(), 'Sepetim')]]",
                "//div[contains(@class, 'header')]//a[contains(@href, 'sepet')]",
                "//header//a[contains(@href, 'sepet')]",
                "//a[contains(text(), 'Sepetim')]",
                "//a[contains(text(), 'SEPETİM')]",
                "//*[contains(@class, 'basket') or contains(@class, 'Basket')]//a",
                "//a[@href='/sepet']"
        };

        for (String xpath : xpathSelectors) {
            try {
                List<WebElement> links = driver.findElements(By.xpath(xpath));
                for (WebElement link : links) {
                    if (link.isDisplayed()) {
                        cartLink = link;
                        logger.info("Found Sepetim link with XPath: " + xpath);
                        break;
                    }
                }
                if (cartLink != null)
                    break;
            } catch (Exception e) {
            }
        }

        if (cartLink == null) {
            String[] cssSelectors = {
                    ".o-header__userInfo--basket",
                    "a[href='/sepet']",
                    "a[href*='/sepet']",
                    "[class*='header'] [class*='basket']",
                    "[class*='header'] [class*='cart']",
                    ".o-header__user a[href*='sepet']"
            };

            for (String selector : cssSelectors) {
                try {
                    List<WebElement> links = driver.findElements(By.cssSelector(selector));
                    for (WebElement link : links) {
                        if (link.isDisplayed()) {
                            cartLink = link;
                            logger.info("Found Sepetim link with CSS: " + selector);
                            break;
                        }
                    }
                    if (cartLink != null)
                        break;
                } catch (Exception e) {

                }
            }
        }

        if (cartLink != null) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cartLink);
                sleep(500);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartLink);
                logger.info("Clicked on Sepetim link");
            } catch (Exception e) {
                logger.warn("JS click failed, trying direct click");
                cartLink.click();
            }
        } else {

            logger.info("Sepetim link not found, navigating directly to /sepet");
            driver.get("https://www.beymen.com/sepet");
        }

        waitForPageLoad();
        sleep(3000);
        logger.info("Cart page URL: " + driver.getCurrentUrl());
        return new CartPage(driver);
    }

    public String[] getProductInfo() {
        String name = getProductName();
        String price = getProductPrice();
        return new String[] { name, price };
    }

    public double getPriceAsDouble() {
        String price = getProductPrice();
        return parsePrice(price);
    }

    public static double parsePrice(String priceText) {
        String cleanPrice = priceText
                .replaceAll("[^0-9,.]", "")
                .replace(".", "")
                .replace(",", ".");
        try {
            return Double.parseDouble(cleanPrice);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
