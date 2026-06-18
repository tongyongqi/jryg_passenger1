package com.jryg.passenger;

import com.jryg.passenger.config.AppConfig;
import com.jryg.passenger.driver.DriverManager;
import com.jryg.passenger.pages.HomePage;
import com.jryg.passenger.pages.LoginPage;
import io.appium.java_client.android.AndroidDriver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        AndroidDriver driver = null;
        try {
            driver = DriverManager.createDriver();

            new LoginPage(driver)
                    .loginByPassword(AppConfig.TEST_PHONE, AppConfig.TEST_PASSWORD);

            HomePage home = new HomePage(driver);

            if (home.isMapDisplayed()) {
                LOG.info("===== 自动化测试通过 =====");
            } else {
                LOG.severe("===== 自动化测试失败 =====");
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "运行异常", e);
        } finally {
            DriverManager.quitDriver();
        }
    }
}
