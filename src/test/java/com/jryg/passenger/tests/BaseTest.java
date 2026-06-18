package com.jryg.passenger.tests;

import com.jryg.passenger.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class BaseTest {

    protected AndroidDriver driver;
    protected final Logger log = Logger.getLogger(getClass().getName());
    private FileHandler fileHandler;

    @BeforeEach
    void setUp() {
        initFileLogging();
        log.info("===== 初始化 Appium 驱动 =====");
        driver = DriverManager.createDriver();
    }

    @AfterEach
    void tearDown() {
        log.info("===== 释放 Appium 驱动 =====");
        DriverManager.quitDriver();
        if (fileHandler != null) {
            Logger.getLogger("").removeHandler(fileHandler);
            fileHandler.close();
        }
    }

    private void initFileLogging() {
        try {
            String projectRoot = System.getProperty("user.dir");
            File logDir = new File(projectRoot, "logs");
            logDir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String logFile = new File(logDir, "test-run-" + timestamp + ".log").getAbsolutePath();

            fileHandler = new FileHandler(logFile, false);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    String time = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(record.getMillis()));
                    String cls = record.getSourceClassName();
                    if (cls != null && cls.lastIndexOf('.') > 0) {
                        cls = cls.substring(cls.lastIndexOf('.') + 1);
                    }
                    return String.format("[%s] [%s] %s: %s%n",
                            time, record.getLevel(), cls, record.getMessage());
                }
            });

            Logger.getLogger("").addHandler(fileHandler);
            log.info("日志文件: " + logFile);
            log.info("工作目录: " + projectRoot);
        } catch (IOException e) {
            log.warning("无法创建日志文件: " + e.getMessage());
        }
    }
}
