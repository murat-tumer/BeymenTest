package com.beymen.pages;

import com.beymen.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class HomePage extends BasePage {

    private final By cookieAcceptButton = By.id("onetrust-accept-btn-handler");
    private final By genderCloseButton = By.cssSelector(".o-modal__closeButton");
    private final By searchBoxTrigger = By.cssSelector("input.o-header__search--input");
    private final By searchBoxInput = By.id("o-searchSuggestion__input");
    private final By logo = By.cssSelector(".o-header__logo");
    private final By searchCloseButton = By.cssSelector(".o-searchSuggestion__close");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isPageLoaded() {
        try {
            waitForPageLoad();
            sleep(2000);
            boolean isLoaded = isElementVisible(logo);
            logger.info("Homepage loaded: " + isLoaded);
            return isLoaded;
        } catch (Exception e) {
            logger.error("Error checking if homepage is loaded: " + e.getMessage());
            return false;
        }
    }

    public void acceptCookies() {
        try {
            sleep(2000);
            if (isElementPresent(cookieAcceptButton)) {
                click(cookieAcceptButton);
                logger.info("Cookies accepted");
                sleep(1000);
            }
        } catch (Exception e) {
            logger.info("No cookie popup found or already accepted");
        }
    }

    public void closeGenderPopup() {
        try {
            sleep(1000);
            if (isElementPresent(genderCloseButton)) {
                click(genderCloseButton);
                logger.info("Gender popup closed");
                sleep(500);
            }
        } catch (Exception e) {
            logger.info("No gender popup found");
        }
    }

    private void openSearchBox() {
        try {
            WebElement trigger = waitForClickable(searchBoxTrigger);
            trigger.click();
            sleep(1000);
            logger.info("Search box opened");
        } catch (Exception e) {
            logger.warn("Could not click search trigger: " + e.getMessage());
        }
    }

    public void search(String keyword) {
        openSearchBox();
        WebElement searchInput = waitForElement(searchBoxInput);
        searchInput.clear();
        searchInput.sendKeys(keyword);
        logger.info("Searched for: " + keyword);
        sleep(500);
    }

    public void searchAndSubmit(String keyword) {
        openSearchBox();
        WebElement searchInput = waitForElement(searchBoxInput);
        searchInput.clear();
        searchInput.sendKeys(keyword);
        searchInput.sendKeys(Keys.ENTER);
        logger.info("Searched and submitted: " + keyword);
        sleep(500);
    }

    public void clearSearchBox() {
        try {

            if (isElementPresent(searchCloseButton)) {
                click(searchCloseButton);
                sleep(500);
            }

            openSearchBox();

            WebElement searchInput = waitForElement(searchBoxInput);
            searchInput.click();

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) {
                searchInput.sendKeys(Keys.COMMAND + "a");
            } else {
                searchInput.sendKeys(Keys.CONTROL + "a");
            }
            searchInput.sendKeys(Keys.BACK_SPACE);
            sleep(500);
            logger.info("Search box cleared");
        } catch (Exception e) {
            logger.warn("Error clearing search box: " + e.getMessage());

            if (isElementPresent(searchCloseButton)) {
                click(searchCloseButton);
                sleep(500);
            }
        }
    }

    public String getSearchBoxText() {
        try {
            WebElement searchInput = driver.findElement(searchBoxInput);
            return searchInput.getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }
}
