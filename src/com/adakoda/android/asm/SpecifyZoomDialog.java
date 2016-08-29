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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class SpecifyZoomDialog extends JDialog {
	private JSpinner mSpinner;
	private JLabel mLabel;
	private JPanel mPanel;
	private JButton mOK;
	private JButton mCancel;

	private boolean mIsOK = false;
	
	private double mSpecifiedZoom = 0.0;

	public SpecifyZoomDialog(Frame owner, boolean modal,
			double currentZoom) {
		super(owner, modal);

		// Frame
		{
			setTitle("Specify Zoom");
			setBounds(0, 0, 180, 84);
			setResizable(false);
		}

		// Spinner
		{
			mPanel = new JPanel();
			SpinnerNumberModel model = new SpinnerNumberModel((int)(currentZoom*100.0), 1, 300, 1);
			mSpinner = new JSpinner(model);
			mSpinner.setPreferredSize(new Dimension(140, 25));
			mPanel.add(mSpinner);
			
			mLabel = new JLabel("%");
			mPanel.add(mLabel);
		}

		// OK
		{
			mOK = new JButton("OK");
			mOK.setEnabled(true);
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
			containger.add(mPanel, BorderLayout.CENTER);
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

	public double getSpecifiedZoom() {
		return mSpecifiedZoom;
	}

	public boolean isOK() {
		return mIsOK;
	}

	private void onOK() {
		mSpecifiedZoom = ((Integer)mSpinner.getValue())/100.0;
		mIsOK = true;
		dispose();
	}

	private void onCancel() {
		dispose();
	}
}
