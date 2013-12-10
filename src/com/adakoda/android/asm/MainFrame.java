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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private static final int DEFAULT_WIDTH = 320;
	private static final int DEFAULT_HEIGHT = 480;
	
	private static final String EXT_PNG = "png";
	
	private static final int FB_TYPE_XBGR = 0;
	private static final int FB_TYPE_RGBX = 1;
	private static final int FB_TYPE_XRGB = 2;

	private static int[][] FB_OFFSET_LIST = {
			{0, 1, 2, 3}, // XBGR : Normal
			{3, 2, 1, 0}, // RGBX : Xperia Arc
			{2, 1, 0, 3}  // XRGB : FireFox OS(B2G)
	};
	
	private MainPanel mPanel;
	private JPopupMenu mPopupMenu;

	private Preferences mPrefs;
	private int mRawImageWidth = DEFAULT_WIDTH;
	private int mRawImageHeight = DEFAULT_HEIGHT;
	private boolean mPortrait = true;
	private double mZoom = 1.0;
	private int mFbType = FB_TYPE_XBGR;

	private ADB mADB;
	private IDevice[] mDevices;
	private IDevice mDevice;

	private MonitorThread mMonitorThread;

	public MainFrame(String[] args) {
		initialize(args);
	}

	public void startMonitor() {
		mMonitorThread = new MonitorThread();
		mMonitorThread.start();
	}

	public void stopMonitor() {
		mMonitorThread = null;
	}

	public void selectDevice() {
		stopMonitor();

		mDevices = mADB.getDevices();
		if (mDevices != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < mDevices.length; i++) {
				list.add(mDevices[i].toString());
			}
			SelectDeviceDialog dialog = new SelectDeviceDialog(this, true, list);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
			if (dialog.isOK()) {
				int selectedIndex = dialog.getSelectedIndex();
				if (selectedIndex >= 0) {
					mDevice = mDevices[selectedIndex];
					setImage(null);
				}
			}
		}

		startMonitor();
	}

	public void setOrientation(boolean portrait) {
		if (mPortrait != portrait) {
			mPortrait = portrait;
			savePrefs();
			updateSize();
		}
	}

	public void setZoom(double zoom) {
		if (mZoom != zoom) {
			mZoom = zoom;
			savePrefs();
			updateSize();
		}
	}

	public void setFrameBuffer(int fbType) {
		if (mFbType != fbType) {
			mFbType = fbType;
			savePrefs();
		}
	}
	
	public void saveImage() {
		FBImage inImage = mPanel.getFBImage();
		if (inImage != null) {
			BufferedImage outImage = new BufferedImage((int) (inImage
					.getWidth() * mZoom), (int) (inImage.getHeight() * mZoom),
					inImage.getType());
			if (outImage != null) {
				AffineTransformOp op = new AffineTransformOp(AffineTransform
						.getScaleInstance(mZoom, mZoom),
						AffineTransformOp.TYPE_BILINEAR);
				op.filter(inImage, outImage);
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "*." + EXT_PNG;
					}

					@Override
					public boolean accept(File f) {
						String ext = f.getName().toLowerCase();
						return (ext.endsWith("." + EXT_PNG));
					}
				});
				if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					try {
						File file = fileChooser.getSelectedFile();
						String path = file.getAbsolutePath();
						if (!path.endsWith("." + EXT_PNG)) {
							file = new File(path + "." + EXT_PNG);
						}
						ImageIO.write(outImage, EXT_PNG, file);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(this,
								"Failed to save a image.", "Save Image",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	public void about() {
		AboutDialog dialog = new AboutDialog(this, true);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	public void updateSize() {
		int width;
		int height;
		if (mPortrait) {
			width = mRawImageWidth;
			height = mRawImageHeight;
		} else {
			width = mRawImageHeight;
			height = mRawImageWidth;
		}
		Insets insets = getInsets();
		int newWidth = (int) (width * mZoom) + insets.left + insets.right;
		int newHeight = (int) (height * mZoom) + insets.top + insets.bottom;

		if ((getWidth() != newWidth) || (getHeight() != newHeight)) {
			setSize(newWidth, newHeight);
		}
	}

	public void setImage(FBImage fbImage) {
		if (fbImage != null) {
			mRawImageWidth = fbImage.getRawWidth();
			mRawImageHeight = fbImage.getRawHeight();
		}
		mPanel.setFBImage(fbImage);
		updateSize();
	}

	private void initialize(String[] args) {
		mADB = new ADB();
		if (!mADB.initialize()) {
			JOptionPane.showMessageDialog(this,
				"Could not find adb, please install Android SDK and set path to adb.",
				"Error", JOptionPane.ERROR_MESSAGE);
		}

		parseArgs(args);
		
		initializePrefs();
		initializeFrame();
		initializePanel();
		initializeMenu();
		initializeActionMap();

		addMouseListener(mMouseListener);
		addWindowListener(mWindowListener);

		pack();
		setImage(null);
	}

	private void parseArgs(String[] args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.equals("-f1")) {
					mFbType = FB_TYPE_RGBX;
				} else if (arg.equals("-f2")) {
					mFbType = FB_TYPE_XRGB;
				}
			}
		}
	}
	
	private void savePrefs() {
		if (mPrefs != null) {
			mPrefs.putInt("PrefVer", 1);
			mPrefs.putBoolean("Portrait", mPortrait);
			mPrefs.putDouble("Zoom", mZoom);
			mPrefs.putInt("FbType", mFbType);
		}
	}
	
	private void initializePrefs() {
		mPrefs = Preferences.userNodeForPackage(this.getClass());
		if (mPrefs != null) {
			int prefVer = mPrefs.getInt("PrefVer", 1);
			if (prefVer == 1) {
				mPortrait = mPrefs.getBoolean("Portrait", true);
				mZoom = mPrefs.getDouble("Zoom", 1.0);
				mFbType = mPrefs.getInt("FbType", FB_TYPE_XBGR);
			}
		}
	}
	
	private void initializeFrame() {
		setTitle("Android Screen Monitor");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("icon.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}

	private void initializePanel() {
		mPanel = new MainPanel();
		add(mPanel);
	}

	private void initializeMenu() {
		mPopupMenu = new JPopupMenu();

		initializeSelectDeviceMenu();
		mPopupMenu.addSeparator();
		initializeOrientationMenu();
		initializeZoomMenu();
		initializeFrameBufferMenu();
		mPopupMenu.addSeparator();
		initializeSaveImageMenu();
		mPopupMenu.addSeparator();
		initializeAbout();

		mPopupMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
	}

	private void initializeSelectDeviceMenu() {
		JMenuItem menuItemSelectDevice = new JMenuItem("Select Device...");
		menuItemSelectDevice.setMnemonic(KeyEvent.VK_D);
		menuItemSelectDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectDevice();
			}
		});
		mPopupMenu.add(menuItemSelectDevice);
	}

	private void initializeOrientationMenu() {
		JMenu menuOrientation = new JMenu("Orientation");
		menuOrientation.setMnemonic(KeyEvent.VK_O);
		mPopupMenu.add(menuOrientation);

		ButtonGroup buttonGroup = new ButtonGroup();

		// Portrait
		JRadioButtonMenuItem radioButtonMenuItemPortrait = new JRadioButtonMenuItem(
				"Portrait");
		radioButtonMenuItemPortrait.setMnemonic(KeyEvent.VK_P);
		radioButtonMenuItemPortrait.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOrientation(true);
			}
		});
		if (mPortrait) {
			radioButtonMenuItemPortrait.setSelected(true);
		}
		buttonGroup.add(radioButtonMenuItemPortrait);
		menuOrientation.add(radioButtonMenuItemPortrait);

		// Landscape
		JRadioButtonMenuItem radioButtonMenuItemLandscape = new JRadioButtonMenuItem(
				"Landscape");
		radioButtonMenuItemLandscape.setMnemonic(KeyEvent.VK_L);
		radioButtonMenuItemLandscape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOrientation(false);
			}
		});
		if (!mPortrait) {
			radioButtonMenuItemLandscape.setSelected(true);
		}
		buttonGroup.add(radioButtonMenuItemLandscape);
		menuOrientation.add(radioButtonMenuItemLandscape);
	}

	private void initializeZoomMenu() {
		JMenu menuZoom = new JMenu("Zoom");
		menuZoom.setMnemonic(KeyEvent.VK_Z);
		mPopupMenu.add(menuZoom);

		ButtonGroup buttonGroup = new ButtonGroup();

		addRadioButtonMenuItemZoom(menuZoom, buttonGroup, 0.1, "10%", -1, mZoom);
		addRadioButtonMenuItemZoom(menuZoom, buttonGroup, 0.25, "25%", -1, mZoom);
		addRadioButtonMenuItemZoom(menuZoom, buttonGroup, 0.5, "50%", KeyEvent.VK_5, mZoom);
		addRadioButtonMenuItemZoom(menuZoom, buttonGroup, 0.75, "75%", KeyEvent.VK_7, mZoom);
		addRadioButtonMenuItemZoom(menuZoom, buttonGroup, 1.0, "100%", KeyEvent.VK_1, mZoom);
		addRadioButtonMenuItemZoom(menuZoom, buttonGroup, 1.5, "150%", KeyEvent.VK_0, mZoom);
		addRadioButtonMenuItemZoom(menuZoom, buttonGroup, 2.0, "200%", KeyEvent.VK_2, mZoom);
	}

	private void addRadioButtonMenuItemZoom(
			JMenu menuZoom, ButtonGroup buttonGroup,
			final double zoom, String caption, int nemonic,
			double currentZoom) {
		JRadioButtonMenuItem radioButtonMenuItemZoom = new JRadioButtonMenuItem(caption);
		if (nemonic != -1) {
			radioButtonMenuItemZoom.setMnemonic(nemonic);
		}
		radioButtonMenuItemZoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setZoom(zoom);
			}
		});
		if (currentZoom == zoom) {
			radioButtonMenuItemZoom.setSelected(true);
		}
		buttonGroup.add(radioButtonMenuItemZoom);
		menuZoom.add(radioButtonMenuItemZoom);
	}
	
	private void initializeFrameBufferMenu() {
		JMenu menuZoom = new JMenu("FrameBuffer");
		menuZoom.setMnemonic(KeyEvent.VK_F);
		mPopupMenu.add(menuZoom);

		ButtonGroup buttonGroup = new ButtonGroup();

		// XBGR
		JRadioButtonMenuItem radioButtonMenuItemFbXBGR = new JRadioButtonMenuItem(
				"XBGR");
		radioButtonMenuItemFbXBGR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFrameBuffer(FB_TYPE_XBGR);
			}
		});
		if (mFbType == FB_TYPE_XBGR) {
			radioButtonMenuItemFbXBGR.setSelected(true);
		}
		buttonGroup.add(radioButtonMenuItemFbXBGR);
		menuZoom.add(radioButtonMenuItemFbXBGR);

		// RGBX
		JRadioButtonMenuItem radioButtonMenuItemFbRGBX = new JRadioButtonMenuItem(
				"RGBX");
		radioButtonMenuItemFbRGBX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFrameBuffer(FB_TYPE_RGBX);
			}
		});
		if (mFbType == FB_TYPE_RGBX) {
			radioButtonMenuItemFbRGBX.setSelected(true);
		}
		buttonGroup.add(radioButtonMenuItemFbRGBX);
		menuZoom.add(radioButtonMenuItemFbRGBX);

		// XRGB
		JRadioButtonMenuItem radioButtonMenuItemFbXRGB = new JRadioButtonMenuItem(
				"XRGB");
		radioButtonMenuItemFbXRGB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFrameBuffer(FB_TYPE_XRGB);
			}
		});
		if (mFbType == FB_TYPE_XRGB) {
			radioButtonMenuItemFbXRGB.setSelected(true);
		}
		buttonGroup.add(radioButtonMenuItemFbXRGB);
		menuZoom.add(radioButtonMenuItemFbXRGB);
	}

	private void initializeSaveImageMenu() {
		JMenuItem menuItemSaveImage = new JMenuItem("Save Image...");
		menuItemSaveImage.setMnemonic(KeyEvent.VK_S);
		menuItemSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveImage();
			}
		});
		mPopupMenu.add(menuItemSaveImage);
	}

	private void initializeActionMap() {
		AbstractAction actionSelectDevice = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				selectDevice();
			}
		};
		AbstractAction actionPortrait = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setOrientation(true);
			}
		};
		AbstractAction actionLandscape = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setOrientation(false);
			}
		};
		AbstractAction actionZoom50 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setZoom(0.5);
			}
		};
		AbstractAction actionZoom75 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setZoom(0.75);
			}
		};
		AbstractAction actionZoom100 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setZoom(1.0);
			}
		};
		AbstractAction actionZoom150 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setZoom(1.5);
			}
		};
		AbstractAction actionZoom200 = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setZoom(2.0);
			}
		};
		AbstractAction actionSaveImage = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				saveImage();
			}
		};
		AbstractAction actionAbout = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				about();
			}
		};

		JComponent targetComponent = getRootPane();
		InputMap inputMap = targetComponent.getInputMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_DOWN_MASK), "Select Device");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_DOWN_MASK), "Portrait");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.CTRL_DOWN_MASK), "Landscape");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_5,
				InputEvent.CTRL_DOWN_MASK), "50%");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_7,
				InputEvent.CTRL_DOWN_MASK), "75%");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				InputEvent.CTRL_DOWN_MASK), "100%");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0,
				InputEvent.CTRL_DOWN_MASK), "150%");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				InputEvent.CTRL_DOWN_MASK), "200%");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK), "Save Image");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.CTRL_DOWN_MASK), "About ASM");

		targetComponent.setInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);

		targetComponent.getActionMap().put("Select Device", actionSelectDevice);
		targetComponent.getActionMap().put("Portrait", actionPortrait);
		targetComponent.getActionMap().put("Landscape", actionLandscape);
		targetComponent.getActionMap().put("Select Device", actionSelectDevice);
		targetComponent.getActionMap().put("50%", actionZoom50);
		targetComponent.getActionMap().put("75%", actionZoom75);
		targetComponent.getActionMap().put("100%", actionZoom100);
		targetComponent.getActionMap().put("150%", actionZoom150);
		targetComponent.getActionMap().put("200%", actionZoom200);
		targetComponent.getActionMap().put("Save Image", actionSaveImage);
		targetComponent.getActionMap().put("About ASM", actionAbout);
	}

	private void initializeAbout() {
		JMenuItem menuItemAbout = new JMenuItem("About ASM");
		menuItemAbout.setMnemonic(KeyEvent.VK_A);
		menuItemAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				about();
			}
		});
		mPopupMenu.add(menuItemAbout);
	}

	private MouseListener mMouseListener = new MouseListener() {
		public void mouseReleased(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				mPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	};

	private WindowListener mWindowListener = new WindowListener() {
		public void windowOpened(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowDeactivated(WindowEvent arg0) {
		}

		public void windowClosing(WindowEvent arg0) {
			if (mADB != null) {
				mADB.terminate();
			}
		}

		public void windowClosed(WindowEvent arg0) {
		}

		public void windowActivated(WindowEvent arg0) {
		}
	};

	public class MainPanel extends JPanel {
		private FBImage mFBImage;

		public MainPanel() {
			setBackground(Color.BLACK);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (mFBImage != null) {
				int srcWidth;
				int srcHeight;
				int dstWidth;
				int dstHeight;
				if (mPortrait) {
					srcWidth = mRawImageWidth;
					srcHeight = mRawImageHeight;
				} else {
					srcWidth = mRawImageHeight;
					srcHeight = mRawImageWidth;
				}
				dstWidth = (int) (srcWidth * mZoom);
				dstHeight = (int) (srcHeight * mZoom);
				if (mZoom == 1.0) {
					g.drawImage(mFBImage, 0, 0, dstWidth, dstHeight, 0, 0,
							srcWidth, srcHeight, null);
				} else {
					Image image = mFBImage.getScaledInstance(dstWidth,
							dstHeight, Image.SCALE_SMOOTH);
					if (image != null) {
						g.drawImage(image, 0, 0, dstWidth, dstHeight, 0, 0,
								dstWidth, dstHeight, null);
					}
				}
			}
		}

		public void setFBImage(FBImage fbImage) {
			mFBImage = fbImage;
			repaint();
		}

		public FBImage getFBImage() {
			return mFBImage;
		}
	}
	
	public class MonitorThread extends Thread {

		@Override
		public void run() {
			Thread thread = Thread.currentThread();
			if (mDevice != null) {
				try {
					while (mMonitorThread == thread) {
						final FBImage fbImage = getDeviceImage();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								setImage(fbImage);
							}
						});
					}
				} catch (IOException e) {
				}
			}
		}

		private FBImage getDeviceImage() throws IOException {
			boolean success = true;
			boolean debug = false;
			FBImage fbImage = null;
			RawImage tmpRawImage = null;
			RawImage rawImage = null;

			if (success) {
				try {
					tmpRawImage = mDevice.getScreenshot();

					if (tmpRawImage == null) {
						success = false;
					} else {
						if (debug == false) {
							rawImage = tmpRawImage;
						} else {
							rawImage = new RawImage();
							rawImage.version = 1;
							rawImage.bpp = 32;
							rawImage.size = tmpRawImage.width
									* tmpRawImage.height * 4;
							rawImage.width = tmpRawImage.width;
							rawImage.height = tmpRawImage.height;
							rawImage.red_offset = 0;
							rawImage.red_length = 8;
							rawImage.blue_offset = 16;
							rawImage.blue_length = 8;
							rawImage.green_offset = 8;
							rawImage.green_length = 8;
							rawImage.alpha_offset = 0;
							rawImage.alpha_length = 0;
							rawImage.data = new byte[rawImage.size];

							int index = 0;
							int dst = 0;
							for (int y = 0; y < rawImage.height; y++) {
								for (int x = 0; x < rawImage.width; x++) {
									int value = tmpRawImage.data[index++] & 0x00FF;
									value |= (tmpRawImage.data[index++] << 8) & 0xFF00;
									int r = ((value >> 11) & 0x01F) << 3;
									int g = ((value >> 5) & 0x03F) << 2;
									int b = ((value >> 0) & 0x01F) << 3;
									// little endian
									rawImage.data[dst++] = (byte) r;
									rawImage.data[dst++] = (byte) g;
									rawImage.data[dst++] = (byte) b;
									rawImage.data[dst++] = (byte) 0xFF;
//									// big endian
//									rawImage.data[dst++] = (byte) 0xFF;
//									rawImage.data[dst++] = (byte) b;
//									rawImage.data[dst++] = (byte) g;
//									rawImage.data[dst++] = (byte) r;
								}
							}
						}
					}
				} catch (IOException ioe) {
				} finally {
					if ((rawImage == null)
							|| ((rawImage.bpp != 16) && (rawImage.bpp != 32))) {
						success = false;
					}
				}
			}

			if (success) {
				final int imageWidth;
				final int imageHeight;

				if (mPortrait) {
					imageWidth = rawImage.width;
					imageHeight = rawImage.height;
				} else {
					imageWidth = rawImage.height;
					imageHeight = rawImage.width;
				}

				fbImage = new FBImage(imageWidth, imageHeight,
						BufferedImage.TYPE_INT_RGB, // BufferedImage.TYPE_INT_ARGB
						rawImage.width, rawImage.height);

				final byte[] buffer = rawImage.data;
				final int redOffset = rawImage.red_offset;
				final int greenOffset = rawImage.green_offset;
				final int blueOffset = rawImage.blue_offset;
				final int alphaOffset = rawImage.alpha_offset;
				final int redMask = getMask(rawImage.red_length);
				final int greenMask = getMask(rawImage.green_length);
				final int blueMask = getMask(rawImage.blue_length);
				final int alphaMask = getMask(rawImage.alpha_length);
				final int redShift = (8 - rawImage.red_length);
				final int greenShift = (8 - rawImage.green_length);
				final int blueShift = (8 - rawImage.blue_length);
				final int alphaShift = (8 - rawImage.alpha_length);

				int index = 0;

				final int offset0;
				final int offset1;
				final int offset2;
				final int offset3;
				
				if (rawImage.bpp == 16) {
					offset0 = 1;
					offset1 = 0;
					if (mPortrait) {
						for (int y = 0; y < rawImage.height; y++) {
							for (int x = 0; x < rawImage.width; x++) {
								int value = buffer[index + offset0] & 0x00FF;
								value |= (buffer[index + offset1] << 8) & 0xFF00;
								int r = ((value >>> redOffset) & redMask) << redShift;
								int g = ((value >>> greenOffset) & greenMask) << greenShift;
								int b = ((value >>> blueOffset) & blueMask) << blueShift;
								value = 255 << 24 | r << 16 | g << 8 | b;
								index += 2;
								fbImage.setRGB(x, y, value);
							}
						}
					} else {
						for (int y = 0; y < rawImage.height; y++) {
							for (int x = 0; x < rawImage.width; x++) {
								int value = buffer[index + offset0] & 0x00FF;
								value |= (buffer[index + offset1] << 8) & 0xFF00;
								int r = ((value >>> redOffset) & redMask) << redShift;
								int g = ((value >>> greenOffset) & greenMask) << greenShift;
								int b = ((value >>> blueOffset) & blueMask) << blueShift;
								value = 255 << 24 | r << 16 | g << 8 | b;
								index += 2;
								fbImage
										.setRGB(y, rawImage.width - x - 1,
												value);
							}
						}
					}
				} else if (rawImage.bpp == 32) {
					offset0 = FB_OFFSET_LIST[mFbType][0];
					offset1 = FB_OFFSET_LIST[mFbType][1];
					offset2 = FB_OFFSET_LIST[mFbType][2];
					offset3 = FB_OFFSET_LIST[mFbType][3];					
					if (mPortrait) {
						for (int y = 0; y < rawImage.height; y++) {
							for (int x = 0; x < rawImage.width; x++) {
								int value;
								value = buffer[index + offset0] & 0x00FF;
								value |= (buffer[index + offset1] & 0x00FF) << 8;
								value |= (buffer[index + offset2] & 0x00FF) << 16;
								value |= (buffer[index + offset3] & 0x00FF) << 24;
								final int r = ((value >>> redOffset) & redMask) << redShift;
								final int g = ((value >>> greenOffset) & greenMask) << greenShift;
								final int b = ((value >>> blueOffset) & blueMask) << blueShift;
								final int a;
								if (rawImage.alpha_length == 0) {
									a = 0xFF;
								} else {
									a = ((value >>> alphaOffset) & alphaMask) << alphaShift;
								}
								value = a << 24 | r << 16 | g << 8 | b;
								index += 4;
								fbImage.setRGB(x, y, value);
							}
						}
					} else {
						for (int y = 0; y < rawImage.height; y++) {
							for (int x = 0; x < rawImage.width; x++) {
								int value;
								value = buffer[index + offset0] & 0x00FF;
								value |= (buffer[index + offset1] & 0x00FF) << 8;
								value |= (buffer[index + offset2] & 0x00FF) << 16;
								value |= (buffer[index + offset3] & 0x00FF) << 24;
								final int r = ((value >>> redOffset) & redMask) << redShift;
								final int g = ((value >>> greenOffset) & greenMask) << greenShift;
								final int b = ((value >>> blueOffset) & blueMask) << blueShift;
								final int a;
								if (rawImage.alpha_length == 0) {
									a = 0xFF;
								} else {
									a = ((value >>> alphaOffset) & alphaMask) << alphaShift;
								}
								value = a << 24 | r << 16 | g << 8 | b;
								index += 4;
								fbImage
										.setRGB(y, rawImage.width - x - 1,
												value);
							}
						}
					}
				}
			}

			return fbImage;
		}
		
		public int getMask(int length) {
	        int res = 0;
	        for (int i = 0 ; i < length ; i++) {
	            res = (res << 1) + 1;
	        }

	        return res;
	    }
	}
}
