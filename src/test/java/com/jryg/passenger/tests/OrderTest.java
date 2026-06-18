package com.jryg.passenger.tests;

import com.jryg.passenger.config.AppConfig;
import com.jryg.passenger.pages.HomePage;
import com.jryg.passenger.pages.LoginPage;
import com.jryg.passenger.pages.OrderPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest extends BaseTest {

    @Test
    void testViewOrderHistory() {
        HomePage home = new LoginPage(driver)
                .loginByPassword(AppConfig.TEST_PHONE, AppConfig.TEST_PASSWORD);
        OrderPage orderPage = home.navigateToOrder();
        log.info("当前订单状态: " + orderPage.getCurrentOrderStatus());
    }

    @Test
    void testCancelOngoingOrder() {
        HomePage home = new LoginPage(driver)
                .loginByPassword(AppConfig.TEST_PHONE, AppConfig.TEST_PASSWORD);
        OrderPage orderPage = home.navigateToOrder();
        orderPage.cancelOrder();
        assertFalse(orderPage.hasOrders(), "取消后应无进行中订单");
    }

    @Test
    void testOrderPageNavigation() {
        HomePage home = new LoginPage(driver)
                .loginByPassword(AppConfig.TEST_PHONE, AppConfig.TEST_PASSWORD);
        OrderPage orderPage = home.navigateToOrder();
        home = orderPage.backToHome();
        assertTrue(home.isMapDisplayed(), "应成功返回首页");
    }
}
