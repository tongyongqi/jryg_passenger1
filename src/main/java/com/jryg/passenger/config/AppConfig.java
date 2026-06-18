package com.jryg.passenger.config;

import java.time.Duration;

public class AppConfig {

    private AppConfig() {}

    public static final String APP_PACKAGE = "com.jryg.client";
    public static final String APP_ACTIVITY = ".SplashActivity";
    public static final String PLATFORM_NAME = "Android";
    public static final String PLATFORM_VERSION = "10";
    public static final String DEVICE_NAME = "Android Emulator";
    public static final String APPIUM_URL = "http://127.0.0.1:4723";
    public static final String APPIUM_PATH = "/wd/hub";

    public static final Duration WAIT_TIMEOUT = Duration.ofSeconds(20);
    public static final Duration IMPLICIT_WAIT = Duration.ofSeconds(5);
    public static final Duration POLL_INTERVAL = Duration.ofMillis(500);

    public static final String TEST_PHONE = "13800138000";
    public static final String TEST_PASSWORD = "123456";
    public static final String TEST_SMS_CODE = "123456";
}
