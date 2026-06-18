package com.jryg.passenger.tests;

import com.jryg.passenger.config.AppConfig;
import com.jryg.passenger.pages.HomePage;
import com.jryg.passenger.pages.LoginPage;
import com.jryg.passenger.pages.MyPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileTest extends BaseTest {

    @Test
    void testUserProfileDisplayed() {
        HomePage home = new LoginPage(driver)
                .loginByPassword(AppConfig.TEST_PHONE, AppConfig.TEST_PASSWORD);
        MyPage myPage = home.navigateToMy();
        assertTrue(myPage.isLoggedIn(), "用户头像应显示");
        String name = myPage.getUserName();
        assertNotNull(name, "用户名不应为空");
        log.info("当前用户: " + name);
    }

    @Test
    void testOpenMyOrders() {
        HomePage home = new LoginPage(driver)
                .loginByPassword(AppConfig.TEST_PHONE, AppConfig.TEST_PASSWORD);
        home.navigateToMy()
                .openMyOrders();
    }
}
