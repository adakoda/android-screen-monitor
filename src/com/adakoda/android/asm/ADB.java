package com.adakoda.android.asm;

import java.io.File;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class ADB {
	private AndroidDebugBridge mAndroidDebugBridge;

	public boolean initialize() {
		boolean success = true;

		String adbLocation = System
				.getProperty("com.android.screenshot.bindir");

		if (success) {
			if ((adbLocation != null) && (adbLocation.length() != 0)) {
				adbLocation += File.separator + "adb";
			} else {
				adbLocation = "adb";
			}
			AndroidDebugBridge.init(false);
			mAndroidDebugBridge = AndroidDebugBridge.createBridge(adbLocation,
					true);
			if (mAndroidDebugBridge == null) {
				success = false;
			}
		}

		if (success) {
			int count = 0;
			while (mAndroidDebugBridge.hasInitialDeviceList() == false) {
				try {
					Thread.sleep(100);
					count++;
				} catch (InterruptedException e) {
				}
				if (count > 100) {
					success = false;
					break;
				}
			}
		}

		if (!success) {
			terminate();
		}

		return success;
	}

	public void terminate() {
		AndroidDebugBridge.terminate();
	}

	public IDevice[] getDevices() {
		IDevice[] devices = null;
		if (mAndroidDebugBridge != null) {
			devices = mAndroidDebugBridge.getDevices();
		}
		return devices;
	}
}
