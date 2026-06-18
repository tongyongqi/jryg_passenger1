package com.jryg.passenger.pages;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class BookingPage extends BasePage {

    private static final String TV_ESTIMATE_PRICE = AppConfig.APP_PACKAGE + ":id/tv_estimate_price";
    private static final String TV_COUPON         = AppConfig.APP_PACKAGE + ":id/tv_coupon";
    private static final String BTN_CONFIRM_CALL  = AppConfig.APP_PACKAGE + ":id/btn_confirm_call";

    public BookingPage(AndroidDriver driver) {
        super(driver);
    }

    public String getEstimatePrice() {
        String price = getText(By.id(TV_ESTIMATE_PRICE));
        log.info("获取预估价格 - 结果: " + price);
        return price;
    }

    public BookingPage selectCoupon() {
        log.info("查找优惠券入口并点击");
        click(By.id(TV_COUPON));
        log.info("优惠券选择成功");
        return this;
    }

    public CashierPage confirmCall() {
        log.info("点击确认呼叫按钮，准备吊起收银台");
        click(By.id(BTN_CONFIRM_CALL));
        log.info("确认呼叫成功，收银台已唤起");
        return new CashierPage(driver);
    }
}
