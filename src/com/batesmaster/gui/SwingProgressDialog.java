// Copyright 2011 OpenThinking Systems, LLC
// Available under the GPLv3 Open Source license
package com.batesmaster.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
class SwingProgressDialog extends JFrame {

	private final JPanel contentPane;
	private final JProgressBar progressBar;
	private final JLabel lblProcessingFile;

	void completeOneFile() {
		progressBar.setValue(progressBar.getValue() + 1);
	}

	void startNewFile(String name) {
		if (progressBar.isIndeterminate()) {
			lblProcessingFile.setText(String.format("Processing file '%s'",
					name));
		} else {
			lblProcessingFile.setText(String.format(
					"[%d/%d] Processing file '%s'", progressBar.getValue() + 1,
					progressBar.getMaximum(), name));
		}
	}

	/**
	 * Create the frame.
	 */
	SwingProgressDialog(int numberOfFiles) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 477, 118);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(16, 46, 440, 44);
		progressBar.setMaximum(numberOfFiles);
		if (numberOfFiles == 1) {
			progressBar.setIndeterminate(true);
		}
		contentPane.add(progressBar);

		lblProcessingFile = new JLabel("Processing file");
		lblProcessingFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblProcessingFile.setBounds(16, 18, 440, 16);
		contentPane.add(lblProcessingFile);
	}
}
