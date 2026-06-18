package com.jryg.passenger.pages;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private static final String MAP_VIEW      = AppConfig.APP_PACKAGE + ":id/map_view";
    private static final String INPUT_START   = AppConfig.APP_PACKAGE + ":id/et_start";
    private static final String INPUT_END     = AppConfig.APP_PACKAGE + ":id/et_end";
    private static final String BTN_CALL_CAR  = AppConfig.APP_PACKAGE + ":id/btn_call_car";
    private static final String TAB_HOME      = AppConfig.APP_PACKAGE + ":id/tab_home";
    private static final String TAB_ORDER     = AppConfig.APP_PACKAGE + ":id/tab_order";
    private static final String TAB_MY        = AppConfig.APP_PACKAGE + ":id/tab_my";
    private static final String LOCATION_LIST = AppConfig.APP_PACKAGE + ":id/rv_location";
    private static final String POPUP_CLOSE   = AppConfig.APP_PACKAGE + ":id/iv_close";

    public HomePage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isMapDisplayed() {
        return isDisplayed(By.id(MAP_VIEW));
    }

    public HomePage enterStartAddress(String address) {
        type(By.id(INPUT_START), address);
        log.info("输入起点: " + address);
        return this;
    }

    public HomePage enterEndAddress(String address) {
        type(By.id(INPUT_END), address);
        log.info("输入终点: " + address);
        return this;
    }

    public HomePage selectFirstLocation() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        click(By.id(LOCATION_LIST + ":id/tv_name"));
        log.info("选择第一个地点");
        return this;
    }

    public OrderPage callCar() {
        click(By.id(BTN_CALL_CAR));
        log.info("点击呼叫车辆");
        return new OrderPage(driver);
    }

    public OrderPage navigateToOrder() {
        click(By.id(TAB_ORDER));
        log.info("切换到订单页面");
        return new OrderPage(driver);
    }

    public MyPage navigateToMy() {
        click(By.id(TAB_MY));
        log.info("切换到我的页面");
        return new MyPage(driver);
    }

    public HomePage closePopup() {
        try {
            By closeBtn = By.id(POPUP_CLOSE);
            boolean popupExists = exists(closeBtn);
            log.info("检测首页弹框 - 是否存在: " + popupExists);
            if (popupExists) {
                click(closeBtn);
                log.info("成功关闭首页弹框");
            } else {
                log.info("首页无弹框，无需关闭");
            }
        } catch (Exception e) {
            log.info("关闭弹框异常（可能无弹框）: " + e.getMessage());
        }
        return this;
    }

    public BookingPage bookRide() {
        click(By.id(BTN_CALL_CAR));
        log.info("点击呼叫车辆进入询价页面");
        return new BookingPage(driver);
    }
}
