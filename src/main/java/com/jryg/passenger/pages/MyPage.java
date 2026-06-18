package com.jryg.passenger.pages;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class MyPage extends BasePage {

    private static final String AVATAR       = AppConfig.APP_PACKAGE + ":id/iv_avatar";
    private static final String USER_NAME    = AppConfig.APP_PACKAGE + ":id/tv_user_name";
    private static final String MY_ORDER     = AppConfig.APP_PACKAGE + ":id/tv_my_order";
    private static final String SETTINGS     = AppConfig.APP_PACKAGE + ":id/iv_settings";
    private static final String BTN_LOGOUT   = AppConfig.APP_PACKAGE + ":id/btn_logout";

    public MyPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoggedIn() {
        return isDisplayed(By.id(AVATAR));
    }

    public String getUserName() {
        return getText(By.id(USER_NAME));
    }

    public MyPage openMyOrders() {
        click(By.id(MY_ORDER));
        log.info("查看我的订单");
        return this;
    }

    public MyPage openSettings() {
        click(By.id(SETTINGS));
        log.info("打开设置");
        return this;
    }

    public MyPage logout() {
        click(By.id(BTN_LOGOUT));
        log.info("退出登录");
        return this;
    }
}
