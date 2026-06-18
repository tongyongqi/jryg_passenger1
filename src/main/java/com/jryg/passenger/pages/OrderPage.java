package com.jryg.passenger.pages;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class OrderPage extends BasePage {

    private static final String ORDER_LIST    = AppConfig.APP_PACKAGE + ":id/rv_order_list";
    private static final String ORDER_STATUS  = AppConfig.APP_PACKAGE + ":id/tv_order_status";
    private static final String BTN_CANCEL    = AppConfig.APP_PACKAGE + ":id/btn_cancel_order";
    private static final String BTN_CONFIRM   = "//android.widget.Button[@text='确认取消']";
    private static final String DRIVER_INFO   = AppConfig.APP_PACKAGE + ":id/tv_driver_name";
    private static final String ORDER_EMPTY   = AppConfig.APP_PACKAGE + ":id/tv_empty";
    private static final String TAB_HOME      = AppConfig.APP_PACKAGE + ":id/tab_home";

    public OrderPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean hasOrders() {
        return isDisplayed(By.id(ORDER_LIST));
    }

    public String getCurrentOrderStatus() {
        try {
            return getText(By.id(ORDER_STATUS));
        } catch (Exception e) {
            return "无进行中订单";
        }
    }

    public String getDriverName() {
        try {
            return getText(By.id(DRIVER_INFO));
        } catch (Exception e) {
            return "暂无司机";
        }
    }

    public OrderPage cancelOrder() {
        if (exists(By.id(BTN_CANCEL))) {
            click(By.id(BTN_CANCEL));
            log.info("点击取消订单");
            if (exists(By.xpath(BTN_CONFIRM))) {
                click(By.xpath(BTN_CONFIRM));
                log.info("确认取消订单");
            }
        } else {
            log.info("无取消按钮，无法取消订单");
        }
        return this;
    }

    public HomePage backToHome() {
        click(By.id(TAB_HOME));
        log.info("返回首页");
        return new HomePage(driver);
    }
}
