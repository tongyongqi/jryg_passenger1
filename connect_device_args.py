"""Common argparse configuration for Android Appium scripts."""

import argparse
from connect_device_settings import (
    DEFAULT_SERVER,
    DEFAULT_APP,
    DEFAULT_APP_PACKAGE,
    DEFAULT_APP_ACTIVITY,
    DEFAULT_DEVICE_NAME,
    DEFAULT_REINSTALL,
)


def build_common_parser(description="Android Appium helper"):
    parser = argparse.ArgumentParser(description=description)
    parser.add_argument("--app", default=DEFAULT_APP, help="APK path or URL to install/start")
    parser.add_argument("--appPackage", default=DEFAULT_APP_PACKAGE, help="App package name")
    parser.add_argument("--appActivity", default=DEFAULT_APP_ACTIVITY, help="App activity name")
    parser.add_argument("--udid", help="Device udid from adb devices")
    parser.add_argument("--deviceName", default=DEFAULT_DEVICE_NAME, help="Device name (optional)")
    parser.add_argument("--reinstall", action="store_true", default=DEFAULT_REINSTALL, help="Reinstall the APK each run when --app is provided")
    parser.add_argument("--server", default=DEFAULT_SERVER, help="Appium server URL")
    return parser
