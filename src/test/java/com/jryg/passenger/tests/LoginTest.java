package com.jryg.passenger.tests;

import com.jryg.passenger.config.AppConfig;
import com.jryg.passenger.pages.HomePage;
import com.jryg.passenger.pages.LoginPage;
import com.jryg.passenger.pages.MyPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTest {

    @Test
    void testLoginByPassword() {
        HomePage home = new LoginPage(driver)
                .loginByPassword(AppConfig.TEST_PHONE, AppConfig.TEST_PASSWORD);
        assertTrue(home.isMapDisplayed(), "首页地图应显示 — 密码登录成功");
    }

    @Test
    void testLoginBySmsCode() {
        HomePage home = new LoginPage(driver)
                .loginBySmsCode(AppConfig.TEST_PHONE, AppConfig.TEST_SMS_CODE);
        assertTrue(home.isMapDisplayed(), "首页地图应显示 — 验证码登录成功");
    }

    @Test
    void testVerifyLoggedInState() {
        new LoginPage(driver)
                .navigateToMy();
        MyPage myPage = new MyPage(driver);
        assertTrue(myPage.isLoggedIn(), "用户应处于登录状态");
    }
}
