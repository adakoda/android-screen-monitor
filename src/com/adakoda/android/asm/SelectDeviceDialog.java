package com.adakoda.android.asm;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class SelectDeviceDialog extends JDialog {
	private JList mList;
	private JScrollPane mScrollPane;
	private JButton mOK;
	private JButton mCancel;

	private DefaultListModel mModel;
	private boolean mIsOK = false;
	private int mSelectedIndex = -1;

	public SelectDeviceDialog(Frame owner, boolean modal,
			ArrayList<String> initialList) {
		super(owner, modal);

		// Frame
		{
			setTitle("Select a Android Device");
			setBounds(0, 0, 240, 164);
			setResizable(false);
		}

		// List
		{
			// Model
			{
				mModel = new DefaultListModel();
				for (int i = 0; i < initialList.size(); i++) {
					mModel.addElement(initialList.get(i));
				}
			}

			mList = new JList(mModel);
			if (mModel.getSize() > 0) {
				mSelectedIndex = 0;
				mList.setSelectedIndex(mSelectedIndex);
			}
			mList.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {
						onOK();
					}
				}
			});

			// Scroll pane
			{
				mScrollPane = new JScrollPane(mList);
				mScrollPane
						.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			}
		}

		// OK
		{
			mOK = new JButton("OK");
			mOK.setEnabled((mModel.getSize() > 0));
			mOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onOK();
				}
			});
		}

		// Cancel
		{
			mCancel = new JButton("Cancel");
			mCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			});
		}

		// Container
		{
			Container container1 = new Container();
			GridLayout gridLayout = new GridLayout(1, 2, 0, 0);
			container1.setLayout(gridLayout);
			container1.add(mOK);
			container1.add(mCancel);

			Container containger = getContentPane();
			containger.add(mScrollPane, BorderLayout.CENTER);
			containger.add(container1, BorderLayout.SOUTH);
		}

		// Key
		{
			AbstractAction actionOK = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onOK();
				}
			};
			AbstractAction actionCancel = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			};

			JComponent targetComponent = getRootPane();
			InputMap inputMap = targetComponent.getInputMap();
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					"Cancel");
			targetComponent.setInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
			targetComponent.getActionMap().put("OK", actionOK);
			targetComponent.getActionMap().put("Cancel", actionCancel);
		}
	}

	public int getSelectedIndex() {
		return mSelectedIndex;
	}

	public boolean isOK() {
		return mIsOK;
	}

	private void onOK() {
		mSelectedIndex = mList.getSelectedIndex();
		mIsOK = true;
		dispose();
	}

	private void onCancel() {
		dispose();
	}
}
