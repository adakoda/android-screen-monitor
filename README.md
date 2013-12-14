# Android Screen Monitor

## Overview
The Android Screen Monitor (ASM) is a tool to monitor screen on the device or emulator.

ASM is an Android Debug Bridge (adb) client, When it starts monitoring screen,
ASM connects to adb on port 5037 and receives frame buffer continuously on the device or emulator and transfer image to your desktop window.

You can rotate and scaling monitor window and export a image into PNG file as screen shot tool.

## Download
 * [Android Screen Monitor](http://adakoda.github.io/android-screen-monitor/)

## System Requirements
 * Ver.3.00
 * Ver.2.00 - 2.50
  * JRE(JDK) 5 or 6 and must set path to java bin directory
  * Android 1.5-4.2 SDK and must set path to android sdk tools and platform-tools directory
 * Ver.1.00 - 1.50
  * Windows XP (32-bit) or Vista (32-bit)
  * Android 1.5-1.6 SDK and must set path to android sdk tools and platform-tools directory

## Install
 1. Install JRE(JDK) 5 or 6 and set path to java bin directory
 2. Install Android SDK and set path to android sdk *tools* and *platform-tools* directory
 3. Download Android Screen Monitor

## How to use
 * Ver.3.00
  1. Before launch ASM, ensure connecting android device and your PC with USB cable
  2. Launch ASM that file name is asm.jar by java command such as "java -jar asm.jar $ANDROID_HOME" on command prompt or terminal (set android SDK path to $ANDROID_HOME)
  3. Select android device which you want to monitor, when [Select a Android Device] window was shown
  4. ASM will show android device screen to your desktop window
  5. You can use several features by context menu (Right click on two button mouse environment or CTRL + Left click on one button mouse environment) and control target device from keyboard or mouse on your PC
 * Ver.2.00 - 2.50
  1. Before launch ASM, ensure connecting android device and your PC with USB cable
  2. Launch ASM that file name is asm.jar by java command such as "java -jar asm.jar" on command prompt or terminal
  3. Select android device which you want to monitor, when [Select a Android Device] window was shown
  4. ASM will show android device screen to your desktop window
  5. You can use several features by context menu (Right click on two button mouse environment or CTRL + Left click on one button mouse environment)
 * Ver.1.00 - 1.50
  1. Before launch ASM, ensure connecting android device and your PC with USB cable
  2. Launch ASM that file name is AndroidScreenMonitor.exe
  3. Select android device which you want to monitor, when [Select Android Device] window was shown
  4. ASM will show android device screen to your desktop window

## Notes
 * Ver.3.00
  * You can control target device from keyboard or mouse on your PC
  * You can set your android SDK path
 * Ver.2.5.0
  * Fixed 16bpp mode
 * Ver.2.4.0
  * You can select frame buffer type
 * Ver.2.00 - 2.30
  * You can select device by CTRL + D (or context menu)
  * You can switch window orientation to portrait or landscape by CTRL + P or CTRL + L key (or context menu)
  * You can scaling window by CTRL + 2 (200%), CTRL + 0 (150%), CTRL + 7 (75%), CTRL + 5 (50%) or CTRL + 1 (100%) key (or context menu)
  * You can export a image as PNG file by CTRL + S key (or context menu)
  * You can check application version by CTRL + A (or context menu)
 * Ver.1.00 - 1.50
  * You can rotate window by double clicking screen on ASM window or CTRL + R key
  * You can scaling window by CTRL + 2 (200%), CTRL + 7 (75%), CTRL + 5 (50%) or CTRL + 1 (100%) key
  * You can export a image as PNG file by CTRL + S key
  * You can check application version by clicking application icon on title bar, when it is running

## Known Problem
  * Ver.2.00
   * Can not use keyboard shotrcut such as CTRL + ... on Linux.
  * Ver.1.00
   * Because this adb client will be heavy for adb server, it might be down even if you use a few minutes under some environment. Unfortunately it was down, you must restart your computer to restart adb server, At that time adb server will not response "adb kill-server" or "adb start-server" while a few minutes.-> This problem was resolved on Ver.1.02.

## Release Notes
 * Ver.3.0.0 - 15 December 2013
  * Added remote control
  * Added android sdk path setting
   Ver.2.5.0 - 2 June 2013
  * Fixed 16bpp mode
 * Ver.2.40 - 28 January 2013
  * Added Firefox OS Alpha2 frame buffer support
  * Added some zoom ratio
  * Added remember last settings
 * Ver.2.30 - 27 March 2011
  * Added error message, when adb location was invalid.
  * Added "-a" command line parameter to enable Adjust Color.
  * Removed alpha channel from frame buffer like DDMS screen capture..
 * Ver.2.20 - 6 March 2011
  * Support adjust color which swap image data endian.
  * Improve save file extension.
 * Ver.2.10 - 24 January 2010
  * Support 32bpp devices such as Google Nexus One.
 * Ver.2.00 - 3 November 2009
  * Support Android 2.0 (Eclair) and application can run on Java Runtime Environment such as Windows, Macintosh and Linux.
 * Ver.1.50 - 27 June 2009
  * Support scaling monitor window and exporting a image as PNG file.
 * Ver.1.02 - 21 June 2009
  * Improve perfomance with keeping stability.
 * Ver.1.01 - 20 June 2009
  * Improve stability to realize this purpose frame rate is less than old version.
  * Bug fix: The message box will be shown 3 times in some condition, when device are not connected.
 * Ver.1.00 - 20 June 2009