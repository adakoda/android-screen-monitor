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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	public AboutDialog(Frame owner, boolean modal) {
		super(owner, modal);

		// Frame
		setTitle("About Android Screen Monitor");
		setBounds(0, 0, 320, 140);
		setResizable(false);

		// Label
		JLabel labelApp = new JLabel("Android Screen Monitor Version 2.30");
		JLabel labelCopyright = new JLabel(
				"Copyright (C) 2009-2011 adakoda Al rights reserved.");
		JTextField labelUrl = new JTextField(
				"http://www.adakoda.com/adakoda/android/asm/");
		labelUrl.setEditable(false);
		labelUrl.setBorder(new EmptyBorder(0, 0, 0, 0));
		labelUrl.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {

			}

			public void mouseClicked(MouseEvent arg0) {
				JTextField textField = (JTextField) arg0.getSource();
				textField.selectAll();
			}
		});

		// OK
		JButton buttonOK = new JButton("OK");
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		// Container
		Container container1 = new Container();
		FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 5, 5);
		container1.setLayout(flowLayout);
		container1.add(labelApp);
		container1.add(labelCopyright);
		container1.add(labelUrl);
		container1.add(buttonOK);

		Container containger = getContentPane();
		containger.add(container1, BorderLayout.CENTER);
		containger.add(buttonOK, BorderLayout.SOUTH);

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

	private void onOK() {
		dispose();
	}

	private void onCancel() {
		dispose();
	}
}
