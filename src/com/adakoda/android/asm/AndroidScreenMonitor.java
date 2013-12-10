package com.adakoda.android.asm;

import javax.swing.SwingUtilities;

public class AndroidScreenMonitor {
	
	private MainFrame mMainFrame;

	public AndroidScreenMonitor() {
		mMainFrame = new MainFrame();
		mMainFrame.setLocationRelativeTo(null);
	}
	
	public void initialize() {
		mMainFrame.setVisible(true);
		mMainFrame.selectDevice();
	}
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AndroidScreenMonitor().initialize();
            }
        });
	}
}
