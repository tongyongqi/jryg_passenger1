"""Connect to an Android device via Appium and print basic session info.

Usage examples:
    python connect_device.py --app /path/to/app.apk --udid 0123456789abcdef
    python connect_device.py --appPackage com.example.app --appActivity .MainActivity --udid 0123

Requirements: Appium server running (default http://127.0.0.1:4723/), adb available.

此脚本的目的：
- 连接到 Android 设备（通过 udid 或 deviceName）
- 安装或启动指定 APK（通过 --app 或 appPackage/appActivity）
- 打印一些会话和设备信息，方便调试
"""
import argparse
import subprocess
import time
import os
import re
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from connect_device_args import build_common_parser
from connect_device_settings import DEFAULT_PASSENGER_LOGON_PATH

# 说明：本文件使用 Appium Python 客户端的新式 API（使用 Options 对象），
# 因此在创建 driver 时应通过 `options=` 传入 UiAutomator2Options 实例。


def make_caps(args):
    """构建并返回 UiAutomator2Options 对象。

    使用 Options 对象比直接传 dict 更兼容新版 Appium-Python-Client。
    将命令行参数映射到对应的 options 属性。
    """
    opts = UiAutomator2Options()
    # 基础属性：平台与自动化后端
    opts.platform_name = "Android"
    opts.automation_name = "UiAutomator2"
    # 保持应用状态，避免每次都清理数据（可根据需要调整）
    opts.no_reset = True
    # 如果超过该超时时间没有新命令，会话会被服务端回收
    opts.new_command_timeout = 300

    # 将用户传入的参数映射到 options
    if args.udid:
        opts.udid = args.udid
    # 如果已提供 appPackage，则优先通过脚本手动安装/启动应用，避免 Appium session 自动启动错误 activity
    if args.app and not args.appPackage:
        # 仅在未指定包名时让 Appium 直接处理 APK 安装/启动
        opts.app = args.app
    if args.appPackage and args.appActivity:
        # 只有在明确提供 activity 时才传递包名和 activity
        opts.app_package = args.appPackage
        opts.app_activity = args.appActivity
    if args.deviceName:
        opts.device_name = args.deviceName

    return opts


def detect_single_adb_device():
    try:
        result = subprocess.run(["adb", "devices"], capture_output=True, text=True, check=True)
        lines = [line.strip() for line in result.stdout.splitlines() if line.strip()]
        devices = [line.split()[0] for line in lines if "device" in line and not line.startswith("List of devices")]
        if len(devices) == 1:
            return devices[0]
    except Exception:
        return None
    return None


def is_app_installed(udid, package_name):
    try:
        result = subprocess.run(
            ["adb", "-s", udid, "shell", "pm", "list", "packages", package_name],
            capture_output=True,
            text=True,
            check=True,
        )
        return any(line.strip().endswith(package_name) for line in result.stdout.splitlines())
    except Exception:
        return False


def adb_install_app(udid, app):
    if not app:
        return False
    if app.startswith("http://") or app.startswith("https://"):
        return False
    try:
        subprocess.run(
            ["adb", "-s", udid, "install", "-r", "-g", app],
            check=True,
            capture_output=True,
            text=True,
        )
        return True
    except Exception as e:
        print("adb install failed:", e)
        return False


def adb_uninstall_app(udid, package_name):
    try:
        subprocess.run(
            ["adb", "-s", udid, "uninstall", package_name],
            check=True,
            capture_output=True,
            text=True,
        )
        return True
    except Exception as e:
        print("adb uninstall failed:", e)
        return False


def install_app(driver, udid, app):
    if not app:
        return False
    if hasattr(driver, "install_app"):
        try:
            driver.install_app(app)
            return True
        except Exception as e:
            print("driver.install_app failed:", e)
            accept_install_risk_prompt(driver)
            try:
                driver.install_app(app)
                return True
            except Exception as e2:
                print("driver.install_app retry failed:", e2)
    return adb_install_app(udid, app)


def accept_install_risk_prompt(driver, timeout=5):
    selectors = [
        (AppiumBy.XPATH, "//android.widget.CheckBox[contains(@text,'已了解应用的风险检测结果') or contains(@content-desc,'已了解应用的风险检测结果') or contains(@resource-id,'checkbox') or contains(@resource-id,'agree') or contains(@resource-id,'risk') or contains(@resource-id,'protocol') ]"),
        (AppiumBy.XPATH, "//android.widget.TextView[contains(@text,'已了解应用的风险检测结果') or contains(@content-desc,'已了解应用的风险检测结果') ]"),
        (AppiumBy.XPATH, "//*[contains(@text,'已了解应用的风险检测结果') or contains(@content-desc,'已了解应用的风险检测结果') ]"),
        (AppiumBy.XPATH, "//*[contains(@text,'继续安装') or contains(@content-desc,'继续安装') or contains(@text,'继续使用') or contains(@content-desc,'继续使用') or contains(@text,'继续') or contains(@content-desc,'继续') ]"),
        (AppiumBy.XPATH, "//*[contains(@text,'安装') and (contains(@resource-id,'button') or contains(@resource-id,'install') or contains(@content-desc,'install')) ]"),
        (AppiumBy.XPATH, "//android.widget.Button[contains(@text,'继续') or contains(@text,'安装') or contains(@content-desc,'继续') or contains(@content-desc,'安装') or contains(@content-desc,'install') ]"),
    ]
    clicked_any = False
    for by, locator in selectors:
        try:
            el = WebDriverWait(driver, timeout).until(EC.element_to_be_clickable((by, locator)))
            el.click()
            print('Clicked install risk prompt:', locator)
            clicked_any = True
            time.sleep(1)
        except Exception:
            pass
    if not clicked_any:
        extra_selectors = [
            (AppiumBy.XPATH, "//*[contains(@text,'已了解应用的风险检测结果')]/following-sibling::*"),
            (AppiumBy.XPATH, "//*[contains(@text,'已了解应用的风险检测结果')]/preceding-sibling::*"),
            (AppiumBy.XPATH, "//*[contains(@text,'已了解应用的风险检测结果')]/parent::*"),
            (AppiumBy.XPATH, "//*[contains(@text,'已了解应用的风险检测结果')]/ancestor::*"),
            (AppiumBy.XPATH, "//*[contains(@text,'已了解应用的风险检测结果')]/../android.widget.CheckBox"),
        ]
        for by, locator in extra_selectors:
            try:
                el = WebDriverWait(driver, timeout).until(EC.element_to_be_clickable((by, locator)))
                el.click()
                print('Clicked extra install risk prompt:', locator)
                clicked_any = True
                time.sleep(1)
                break
            except Exception:
                pass
    if clicked_any:
        try:
            el = WebDriverWait(driver, timeout).until(
                EC.element_to_be_clickable(
                    (AppiumBy.XPATH, "//*[contains(@text,'继续安装') or contains(@content-desc,'继续安装') or contains(@text,'安装') or contains(@content-desc,'安装') or contains(@text,'继续') or contains(@content-desc,'继续') or contains(@text,'确认') or contains(@content-desc,'确认')]"),
                )
            )
            el.click()
            print('Clicked final install button')
            time.sleep(1)
        except Exception:
            pass
    return clicked_any


def dismiss_startup_popups(driver, timeout=2):
    selectors = [
        (AppiumBy.ID, "com.android.packageinstaller:id/permission_allow_button"),
        (AppiumBy.XPATH, "//*[contains(@text,'允许') or contains(@text,'允许一次') or contains(@text,'在使用期间允许') or contains(@text,'使用时允许') or contains(@text,'始终允许') or contains(@text,'使应用时允许')]"),
        (AppiumBy.XPATH, "//*[contains(@text,'同意') or contains(@text,'我知道了') or contains(@text,'知道了') or contains(@text,'我已阅读并同意') or contains(@text,'已阅读并同意') or contains(@text,'继续使用') or contains(@text,'继续安装') or contains(@text,'继续') ]"),
        (AppiumBy.XPATH, "//*[contains(@text,'关闭') or contains(@text,'取消') or contains(@text,'跳过') or contains(@text,'立即体验') or contains(@text,'稍后再说')]"),
        (AppiumBy.XPATH, "//*[@content-desc='关闭' or @content-desc='close' or contains(@content-desc,'继续') or contains(@content-desc,'同意') or contains(@content-desc,'允许') ]"),
    ]
    clicked_any = False
    for by, locator in selectors:
        try:
            el = WebDriverWait(driver, timeout).until(EC.element_to_be_clickable((by, locator)))
            el.click()
            print('Dismissed popup with', by, locator)
            clicked_any = True
            time.sleep(1)
        except Exception:
            pass
    return clicked_any


def close_all_popups(driver, timeout=2, max_attempts=6):
    for _ in range(max_attempts):
        if not dismiss_startup_popups(driver, timeout):
            break
        time.sleep(0.5)


def launch_installed_app(driver, udid, package_name, activity=None):
    if activity:
        try:
            driver.start_activity(package_name, activity)
            return True
        except Exception:
            pass
    try:
        cmd = ["adb", "-s", udid, "shell", "monkey", "-p", package_name, "-c", "android.intent.category.LAUNCHER", "1"]
        subprocess.run(cmd, check=True, capture_output=True, text=True)
        return True
    except Exception as e:
        print("launch_installed_app failed:", e)
    return False


def open_app(driver, udid, package_name=None, activity=None):
    if package_name and activity:
        try:
            driver.start_activity(package_name, activity)
            return True
        except Exception:
            pass
    if package_name:
        return launch_installed_app(driver, udid, package_name, activity)
    try:
        if hasattr(driver, 'launch_app'):
            driver.launch_app()
            return True
    except Exception:
        pass
    return False


def uninstall_app(driver, udid, package_name):
    if hasattr(driver, "remove_app"):
        try:
            driver.remove_app(package_name)
            return True
        except Exception as e:
            print("driver.remove_app failed:", e)
    return adb_uninstall_app(udid, package_name)


def install_and_open_app(driver, udid, app, package_name=None, activity=None, reinstall=False):
    if package_name:
        app_installed = is_app_installed(udid, package_name)
        if app_installed and reinstall:
            print(f"Package {package_name} is installed and reinstall requested; uninstalling first")
            if uninstall_app(driver, udid, package_name):
                print(f"Uninstalled {package_name}")
                app_installed = False
            else:
                print(f"Failed to uninstall {package_name}; continuing with install attempt")
        if not app_installed:
            if not app:
                print(f"Package {package_name} not installed and no APK provided; cannot install")
                return False
            print(f"Installing {package_name} from {app}")
            if not install_app(driver, udid, app):
                print("Install failed")
                return False
        print(f"Opening package {package_name}")
        return open_app(driver, udid, package_name, activity)
    if app:
        print("Installing app from", app)
        if not install_app(driver, udid, app):
            print("Install failed")
            return False
        try:
            if hasattr(driver, 'launch_app'):
                driver.launch_app()
                return True
        except Exception:
            pass
    print("No package or app provided; cannot open application")
    return False

def main():
    parser = build_common_parser(description="Connect to Android device via Appium")
    args = parser.parse_args()

    if not args.udid:
        args.udid = detect_single_adb_device()
        if args.udid:
            print(f"Detected connected device udid: {args.udid}")
        else:
            print("No single adb device detected. Please provide --udid.")
            return

    # Try to load defaults from passenger-logon.py if present
    def load_defaults_from_passenger(path=DEFAULT_PASSENGER_LOGON_PATH):
        defaults = {}
        if not os.path.exists(path):
            return defaults
        text = open(path, "r", encoding="utf-8").read()
        # match patterns like: parser.add_argument("--udid", default="VALUE",
        # 正则匹配 passenger-logon.py 中的 add_argument(... default=...)
        # 目的是提取脚本中定义的默认值，减少重复配置
        pattern = re.compile(r'parser\.add_argument\(\s*"(?P<arg>--[a-zA-Z0-9_]+)"[^\n]*?default\s*=\s*(?P<val>"[^"]*"|\'[^\']*\'|[^,\)\n]+)')
        for m in pattern.finditer(text):
            arg = m.group('arg').lstrip('-')
            val = m.group('val').strip()
            # strip quotes
            if (val.startswith('"') and val.endswith('"')) or (val.startswith("'") and val.endswith("'")):
                val = val[1:-1]
            defaults[arg] = val
        return defaults

    passenger_defaults = load_defaults_from_passenger()
    if passenger_defaults:
        print("Loaded defaults from passenger-logon.py:")
        for k, v in passenger_defaults.items():
            print(f"  {k}: {v}")
        # apply defaults when args not provided
        for key, val in passenger_defaults.items():
            if hasattr(args, key) and getattr(args, key) is None:
                setattr(args, key, val)

    options = make_caps(args)
    # 确保 W3C required capability `automationName` 以非 namespaced 形式存在，
    # 有些服务端在解析时要求 plain 'automationName' 键
    try:
        options.set_capability("automationName", "UiAutomator2")
        options.set_capability("platformName", "Android")
    except Exception:
        pass
    # 打印 capabilities 预览，帮助在日志中确认传递给 Appium 的内容
    try:
        caps_preview = options.to_capabilities()
    except Exception:
        # 某些版本的 Options 对象可能不支持直接导出，兜底打印属性字典
        caps_preview = getattr(options, "__dict__", str(options))
    print("Desired capabilities:", caps_preview)

    driver = None
    try:
        driver = webdriver.Remote(args.server, options=options)
        print("Session id:", driver.session_id)

        try:
            pkg = getattr(args, "appPackage", None) or passenger_defaults.get("appPackage")
            opened = install_and_open_app(driver, args.udid, args.app, pkg, args.appActivity, args.reinstall)
            if opened:
                print("App installed/opened successfully")
                close_all_popups(driver, timeout=2, max_attempts=6)
            else:
                print("Failed to install or open app")
        except Exception as e:
            print("Failed to install/open app:", e)

        try:
            print("Current package:", driver.current_package)
        except Exception:
            print("Could not read current package")
        try:
            print("Current activity:", driver.current_activity)
        except Exception:
            print("Could not read current activity")

        # small probe: get device time and page source length
        try:
            print("Device time:", driver.device_time)
        except Exception:
            pass
        try:
            src = driver.page_source
            print("Page source length:", len(src) if src else 0)
        except Exception:
            print("Could not get page source")

        # keep session alive briefly so user can inspect on device
        print("Session established — sleeping 5s then quitting")
        time.sleep(5)

    except Exception as e:
        print("Failed to start Appium session:", e)
    finally:
        if driver:
            driver.quit()


if __name__ == "__main__":
    main()
