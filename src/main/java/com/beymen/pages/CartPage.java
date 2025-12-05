package com.beymen.pages;

import com.beymen.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class CartPage extends BasePage {

    private final String[] priceSelectors = {
            ".m-productPrice__salePrice",
            ".m-basket__productPrice",
            "[class*='price']",
            "[class*='Price']"
    };

    private final String[] quantitySelectors = {
            "select[class*='quantity']",
            ".m-basket__quantity select",
            "[class*='quantity'] select",
            "select[name*='quantity']"
    };

    private final String[] removeSelectors = {
            ".m-basket__remove",
            "button[class*='remove']",
            "button[class*='Remove']",
            "[class*='basket'] button[class*='remove']",
            "[class*='delete']",
            "button[class*='sil']",
            "a[class*='remove']",
            ".o-basket__item button",
            "[data-testid*='remove']"
    };

    private final String[] cartItemSelectors = {
            "[class*='basketItem']",
            "[class*='basket__item']",
            "[class*='cartItem']",
            "[class*='productCard']"
    };

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartPageLoaded() {
        waitForPageLoad();
        sleep(3000);
        String url = driver.getCurrentUrl().toLowerCase();
        logger.info("Checking cart page URL: " + url);
        boolean isLoaded = url.contains("basket") || url.contains("sepet") || url.contains("cart");

        if (!isLoaded) {

            for (String selector : cartItemSelectors) {
                List<WebElement> items = driver.findElements(By.cssSelector(selector));
                if (!items.isEmpty()) {
                    logger.info("Found cart items with selector: " + selector);
                    isLoaded = true;
                    break;
                }
            }
        }

        logger.info("Cart page loaded: " + isLoaded);
        return isLoaded;
    }

    public String getCartPrice() {
        try {
            sleep(3000);

            String[] firstItemPriceSelectors = {
                    ".m-basket__item:first-child .m-productPrice__salePrice",
                    ".m-basket__item:first-child [class*='price']",
                    "[class*='basketItem']:first-child [class*='price']",
                    "[class*='basket__product']:first-child [class*='price']"
            };

            for (String selector : firstItemPriceSelectors) {
                List<WebElement> priceElements = driver.findElements(By.cssSelector(selector));
                for (WebElement el : priceElements) {
                    String text = el.getText().trim();
                    if (!text.isEmpty() && (text.contains("TL") || text.matches(".*\\d+[.,]?\\d*.*"))) {
                        logger.info("First item price found with selector '" + selector + "': " + text);
                        return text;
                    }
                }
            }

            String[] allPriceSelectors = {
                    ".m-productPrice__salePrice",
                    ".m-basket__productPrice",
                    "[class*='productPrice']",
                    "[class*='salePrice']"
            };

            for (String selector : allPriceSelectors) {
                List<WebElement> priceElements = driver.findElements(By.cssSelector(selector));
                if (!priceElements.isEmpty()) {
                    String text = priceElements.getFirst().getText().trim();
                    if (!text.isEmpty() && (text.contains("TL") || text.matches(".*\\d+[.,]?\\d*.*"))) {
                        logger.info("Cart price found with selector '" + selector + "': " + text);
                        return text;
                    }
                }
            }

            String[] xpathSelectors = {
                    "(//*[contains(@class, 'basket')]//*[contains(@class, 'price')])[1]",
                    "(//*[contains(text(), 'TL')])[1]"
            };

            for (String xpath : xpathSelectors) {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (!elements.isEmpty()) {
                    String text = elements.getFirst().getText().trim();
                    if (!text.isEmpty() && text.matches(".*\\d+.*")) {
                        logger.info("Cart price found with XPath: " + text);
                        return text;
                    }
                }
            }

            logger.warn("No cart price found, returning 0 TL");
            return "0 TL";
        } catch (Exception e) {
            logger.error("Error getting cart price: " + e.getMessage());
            return "0 TL";
        }
    }

    public double getCartPriceAsDouble() {
        String price = getCartPrice();
        return ProductPage.parsePrice(price);
    }

    public void increaseQuantity(int targetQuantity) {
        try {
            sleep(2000);
            for (String selector : quantitySelectors) {
                List<WebElement> selects = driver.findElements(By.cssSelector(selector));
                if (!selects.isEmpty()) {
                    Select select = new Select(selects.getFirst());
                    try {
                        select.selectByValue(String.valueOf(targetQuantity));
                    } catch (Exception e) {

                        select.selectByVisibleText(String.valueOf(targetQuantity));
                    }
                    logger.info("Quantity changed to: " + targetQuantity);
                    sleep(3000);
                    return;
                }
            }
            logger.warn("Quantity dropdown not found");
        } catch (Exception e) {
            logger.error("Error changing quantity: " + e.getMessage());
        }
    }

    public int getQuantity() {
        try {
            sleep(1000);
            for (String selector : quantitySelectors) {
                List<WebElement> selects = driver.findElements(By.cssSelector(selector));
                if (!selects.isEmpty()) {
                    Select select = new Select(selects.getFirst());
                    String value = select.getFirstSelectedOption().getText().trim();
                    logger.info("Current quantity text: " + value);

                    String numericValue = value.replaceAll("[^0-9]", "");
                    if (!numericValue.isEmpty()) {
                        int qty = Integer.parseInt(numericValue);
                        logger.info("Parsed quantity: " + qty);
                        return qty;
                    }
                }
            }
            return 1;
        } catch (Exception e) {
            logger.error("Error getting quantity: " + e.getMessage());
            return 1;
        }
    }

    public void removeProduct() {
        try {
            sleep(2000);

            for (String selector : removeSelectors) {
                List<WebElement> buttons = driver.findElements(By.cssSelector(selector));
                for (WebElement btn : buttons) {
                    if (btn.isDisplayed()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                        logger.info("Product removed from cart with selector: " + selector);
                        sleep(3000);
                        handleRemoveConfirmation();
                        return;
                    }
                }
            }

            String[] xpathSelectors = {
                    "//button[contains(@class, 'remove') or contains(@class, 'Remove')]",
                    "//button[contains(@class, 'delete') or contains(@class, 'Delete')]",
                    "//*[contains(@class, 'basket')]//*[contains(@class, 'remove')]",
                    "//span[contains(text(), 'Sil')]/ancestor::button",
                    "//button[contains(text(), 'Sil')]",
                    "//*[contains(@class, 'trash') or contains(@class, 'Trash')]",
                    "//i[contains(@class, 'trash')]/ancestor::button"
            };

            for (String xpath : xpathSelectors) {
                List<WebElement> buttons = driver.findElements(By.xpath(xpath));
                for (WebElement btn : buttons) {
                    if (btn.isDisplayed()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                        logger.info("Product removed with XPath: " + xpath);
                        sleep(3000);
                        handleRemoveConfirmation();
                        return;
                    }
                }
            }

            logger.warn("Could not find remove button");
        } catch (Exception e) {
            logger.error("Error removing product: " + e.getMessage());
        }
    }

    private void handleRemoveConfirmation() {
        try {
            sleep(1000);

            String[] confirmSelectors = {
                    "//button[contains(text(), 'Evet')]",
                    "//button[contains(text(), 'Onayla')]",
                    "//button[contains(text(), 'Tamam')]",
                    "//button[contains(@class, 'confirm')]",
                    ".m-modal__button--primary"
            };

            for (String selector : confirmSelectors) {
                try {
                    List<WebElement> buttons;
                    if (selector.startsWith("//")) {
                        buttons = driver.findElements(By.xpath(selector));
                    } else {
                        buttons = driver.findElements(By.cssSelector(selector));
                    }
                    for (WebElement btn : buttons) {
                        if (btn.isDisplayed()) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                            logger.info("Clicked confirmation button");
                            sleep(2000);
                            return;
                        }
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }
    }

    public void clearAllItems() {
        logger.info("Clearing all items from cart");
        int maxAttempts = 10;
        int attempts = 0;

        while (attempts < maxAttempts && !isCartEmpty()) {
            removeProduct();
            attempts++;
            sleep(2000);
        }

        logger.info("Cart cleared after " + attempts + " removals");
    }

    public boolean isCartEmpty() {
        try {
            sleep(2000);

            for (String selector : cartItemSelectors) {
                List<WebElement> items = driver.findElements(By.cssSelector(selector));
                if (!items.isEmpty()) {
                    logger.info("Cart has items");
                    return false;
                }
            }
            logger.info("Cart is empty");
            return true;
        } catch (Exception e) {
            logger.info("Cart appears to be empty");
            return true;
        }
    }

    public boolean comparePrices(double productPagePrice, double cartPrice) {
        boolean match = Math.abs(productPagePrice - cartPrice) < 1;
        logger.info(
                "Price comparison - Product page: " + productPagePrice + ", Cart: " + cartPrice + ", Match: " + match);
        return match;
    }
}
