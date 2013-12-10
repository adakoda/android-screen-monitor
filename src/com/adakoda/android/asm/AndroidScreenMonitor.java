/*
 * Copyright (C) 2009-2011 adakoda
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

import javax.swing.SwingUtilities;

public class AndroidScreenMonitor {
	
	private MainFrame mMainFrame;
	private static String[] mArgs;

	public AndroidScreenMonitor() {
	}
	
	public void initialize() {
		mMainFrame = new MainFrame(mArgs);
		mMainFrame.setLocationRelativeTo(null);
		mMainFrame.setVisible(true);
		mMainFrame.selectDevice();
	}
	
	public static void main(String[] args) {
		mArgs = args;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AndroidScreenMonitor().initialize();
            }
        });
	}
}
