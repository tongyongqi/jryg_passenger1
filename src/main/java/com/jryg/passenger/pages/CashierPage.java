package com.jryg.passenger.pages;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class CashierPage extends BasePage {

    private static final String LL_WECHAT_PAY    = AppConfig.APP_PACKAGE + ":id/ll_wechat";
    private static final String LL_ALIPAY         = AppConfig.APP_PACKAGE + ":id/ll_alipay";
    private static final String TV_PAYMENT_AMOUNT = AppConfig.APP_PACKAGE + ":id/tv_payment_amount";

    public CashierPage(AndroidDriver driver) {
        super(driver);
    }

    public CashierPage selectWechatPay() {
        log.info("选择支付方式 - 微信支付");
        click(By.id(LL_WECHAT_PAY));
        log.info("微信支付已选中");
        return this;
    }

    public CashierPage selectAlipay() {
        log.info("选择支付方式 - 支付宝支付");
        click(By.id(LL_ALIPAY));
        log.info("支付宝支付已选中");
        return this;
    }

    public String getPaymentAmount() {
        String amount = getText(By.id(TV_PAYMENT_AMOUNT));
        log.info("获取支付金额 - 结果: " + amount);
        return amount;
    }
}
