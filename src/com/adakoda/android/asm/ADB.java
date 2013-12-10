/*
 * Copyright (C) 2009-2013 adakoda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

		// for debugging (follwing line is a example)
//		adbLocation = "C:\\ ... \\android-sdk-windows\\platform-tools"; // Windows
//		adbLocation = "/ ... /adt-bundle-mac-x86_64/sdk/platform-tools"; // MacOS X
		
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
