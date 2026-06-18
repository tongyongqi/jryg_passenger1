package com.jryg.passenger.driver;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverManager {

    private static final ThreadLocal<AndroidDriver> driverThread = new ThreadLocal<>();

    private DriverManager() {}

    public static AndroidDriver createDriver() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(AppConfig.PLATFORM_NAME)
                .setPlatformVersion(AppConfig.PLATFORM_VERSION)
                .setDeviceName(AppConfig.DEVICE_NAME)
                .setAppPackage(AppConfig.APP_PACKAGE)
                .setAppActivity(AppConfig.APP_ACTIVITY)
                .setAutoGrantPermissions(true)
                .setNoReset(true);

        try {
            AndroidDriver driver = new AndroidDriver(
                    new URL(AppConfig.APPIUM_URL + AppConfig.APPIUM_PATH), options);
            driver.manage().timeouts().implicitlyWait(AppConfig.IMPLICIT_WAIT);
            driverThread.set(driver);
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Appium URL 格式错误: " + AppConfig.APPIUM_URL, e);
        }
    }

    public static AndroidDriver getDriver() {
        return driverThread.get();
    }

    public static void quitDriver() {
        AndroidDriver driver = driverThread.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            } finally {
                driverThread.remove();
            }
        }
    }
}
