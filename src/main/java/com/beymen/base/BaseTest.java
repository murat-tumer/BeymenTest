package com.beymen.base;

import com.beymen.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.time.Duration;

public class BaseTest {
    protected static WebDriver driver;
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    public static void setUp() {
        String browser = ConfigReader.getProperty("browser", "chrome").toLowerCase();
        logger.info("Starting browser: " + browser);

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--start-maximized");
                driver = new ChromeDriver(options);
                break;
        }

        int implicitWait = ConfigReader.getIntProperty("implicit.wait");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().window().maximize();

        String baseUrl = ConfigReader.getProperty("base.url");
        driver.get(baseUrl);
        logger.info("Navigated to: " + baseUrl);
    }

    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed");
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }
}
