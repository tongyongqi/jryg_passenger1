package com.jryg.passenger.pages;

import com.jryg.passenger.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private static final String TAB_MY        = AppConfig.APP_PACKAGE + ":id/tab_my";
    private static final String INPUT_PHONE   = AppConfig.APP_PACKAGE + ":id/et_phone";
    private static final String INPUT_PWD     = AppConfig.APP_PACKAGE + ":id/et_password";
    private static final String BTN_LOGIN     = AppConfig.APP_PACKAGE + ":id/btn_login";
    private static final String TAB_PWD       = "//android.widget.TextView[@text='密码登录']";
    private static final String TAB_SMS       = "//android.widget.TextView[@text='验证码登录']";
    private static final String CB_AGREEMENT  = AppConfig.APP_PACKAGE + ":id/cb_agreement";
    private static final String INPUT_CODE    = AppConfig.APP_PACKAGE + ":id/et_sms_code";
    private static final String BTN_SEND_CODE = AppConfig.APP_PACKAGE + ":id/btn_send_code";

    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    public LoginPage navigateToMy() {
        click(By.id(TAB_MY));
        log.info("点击底部导航「我的」");
        return this;
    }

    public LoginPage switchToPasswordTab() {
        try {
            click(By.xpath(TAB_PWD));
            log.info("切换到密码登录");
        } catch (Exception e) {
            log.info("已是密码登录模式");
        }
        return this;
    }

    public LoginPage switchToSmsTab() {
        click(By.xpath(TAB_SMS));
        log.info("切换到验证码登录");
        return this;
    }

    public LoginPage enterPhone(String phone) {
        type(By.id(INPUT_PHONE), phone);
        log.info("输入手机号: " + phone);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(By.id(INPUT_PWD), password);
        log.info("输入密码");
        return this;
    }

    public LoginPage enterSmsCode(String code) {
        type(By.id(INPUT_CODE), code);
        log.info("输入验证码");
        return this;
    }

    public LoginPage sendSmsCode() {
        click(By.id(BTN_SEND_CODE));
        log.info("点击发送验证码");
        return this;
    }

    public LoginPage checkAgreement() {
        try {
            By cb = By.id(CB_AGREEMENT);
            if (exists(cb) && !find(cb).isSelected()) {
                click(cb);
                log.info("勾选用户协议");
            }
        } catch (Exception e) {
            log.fine("无协议勾选框");
        }
        return this;
    }

    public HomePage clickLogin() {
        click(By.id(BTN_LOGIN));
        log.info("点击登录按钮");
        return new HomePage(driver);
    }

    public HomePage loginByPassword(String phone, String password) {
        log.info("===== 密码登录流程 =====");
        return navigateToMy()
                .switchToPasswordTab()
                .enterPhone(phone)
                .enterPassword(password)
                .checkAgreement()
                .clickLogin();
    }

    public HomePage loginBySmsCode(String phone, String code) {
        log.info("===== 验证码登录流程 =====");
        return navigateToMy()
                .switchToSmsTab()
                .enterPhone(phone)
                .sendSmsCode()
                .enterSmsCode(code)
                .checkAgreement()
                .clickLogin();
    }
}
