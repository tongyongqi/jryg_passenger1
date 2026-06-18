package com.jryg.passenger.tests;

import com.jryg.passenger.config.AppConfig;
import com.jryg.passenger.pages.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RideBookingTest extends BaseTest {

    @Test
    void testFullRideBookingFlow() {
        log.info("========== 步骤1: 短信验证码登录 ==========");
        HomePage home = new LoginPage(driver)
                .loginBySmsCode(AppConfig.TEST_PHONE, AppConfig.TEST_SMS_CODE);
        boolean mapShown = home.isMapDisplayed();
        log.info("登录结果 - 地图显示: " + mapShown);
        assertTrue(mapShown, "登录成功后首页地图应显示");

        log.info("========== 步骤2: 关闭首页弹框 ==========");
        home.closePopup();

        log.info("========== 步骤3: 搜索起点 - 时代之光名苑 ==========");
        home.enterStartAddress("时代之光名苑")
            .selectFirstLocation();
        log.info("起点设置完成");

        log.info("========== 步骤4: 搜索终点 - 西直门地铁 ==========");
        home.enterEndAddress("西直门地铁")
            .selectFirstLocation();
        log.info("终点设置完成");

        log.info("========== 步骤5: 进入询价页面 ==========");
        BookingPage bookingPage = home.bookRide();
        String estimatePrice = bookingPage.getEstimatePrice();
        log.info("获取预估价格成功 - 预估价格: " + estimatePrice);

        log.info("========== 步骤6: 选择优惠券 ==========");
        bookingPage.selectCoupon();
        log.info("优惠券选择完成");

        log.info("========== 步骤7: 确认呼叫 - 吊起收银台 ==========");
        CashierPage cashierPage = bookingPage.confirmCall();
        String paymentAmount = cashierPage.getPaymentAmount();
        log.info("收银台唤起成功 - 支付金额: " + paymentAmount);

        log.info("========== 步骤8: 选择微信支付 ==========");
        cashierPage.selectWechatPay();
        log.info("微信支付选择完成");

        log.info("========== 全流程执行完毕 ==========");
        log.info("叫车全流程：登录 → 关闭弹框 → 起点搜索(时代之光名苑) → 终点搜索(西直门地铁) → 询价 → 选券 → 确认呼叫 → 微信支付");
    }
}
