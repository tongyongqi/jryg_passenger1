package com.jryg.passenger.pages;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.logging.Logger;

public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log;

    protected BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, AppConfig.WAIT_TIMEOUT, AppConfig.POLL_INTERVAL);
        this.log = Logger.getLogger(getClass().getName());
    }

    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean exists(By locator) {
        try {
            return driver.findElements(locator).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
