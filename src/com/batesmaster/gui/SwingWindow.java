// Copyright 2011 OpenThinking Systems, LLC
// Available under the GPLv3 Open Source license

package com.batesmaster.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import com.batesmaster.batesStamper;

/**
 * Java Swing GUI wrapper for the batesmaster command line.
 * 
 * 
 * <p>
 * Copyright 2011 OpenThinking Systems, LLC
 * 
 */

public class SwingWindow {

	private JFrame frame;
	private final JLabel lblStampRotation = new JLabel(
			"Stamp rotation (degrees)");
	private JTextField tfStartingNumber;
	private JTextField txtd;
	private JTextField tfOffsetLeft;
	private JTextField tfOffsetBottom;
	private JTextField tfRotation;

	static boolean isValid(Component... xs) {
		for (Component x : xs)
			if (Color.RED.equals(x.getBackground()))
				return false;
		return true;
	}

	static class ConstrainToInt extends FocusAdapter {

		Integer max;

		Integer min;

		ConstrainToInt(Integer min, Integer max) {
			this.min = min;
			this.max = max;
		}

		@Override
		public void focusLost(FocusEvent e) {
			Component c = e.getComponent();
			if (c instanceof JTextField) {
				JTextField tf = (JTextField) c;
				String t = tf.getText();
				try {
					Integer x = Integer.valueOf(t);
					if (max != null && x > max)
						throw new IllegalArgumentException("max = " + max);
					if (min != null && x < min)
						throw new IllegalArgumentException("min = " + min);
					tf.setBackground(Color.WHITE);
				} catch (Exception ex) {
					tf.setBackground(Color.RED);
				}
			}
		}

	}

	static class ConstrainToFormat extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			Component c = e.getComponent();
			if (c instanceof JTextField) {
				JTextField tf = (JTextField) c;
				String t = tf.getText();
				try {
					String.format(t, 1);
					tf.setBackground(Color.WHITE);
				} catch (Exception ex) {
					tf.setBackground(Color.RED);
				}
			}
		}
	}

	static class StampPDF implements Runnable {

		final batesStamper stamper;

		StampPDF(SwingWindow window, File inPdf) {
			int startNumber = Integer.parseInt(window.tfStartingNumber
					.getText());
			File outPdf = new File(inPdf.getAbsolutePath() + ".out.pdf");

			stamper = new batesStamper(inPdf.getAbsolutePath(), outPdf
					.getAbsolutePath(), startNumber);

			stamper.setOffsetx(Integer.parseInt(window.tfOffsetLeft.getText()));
			stamper.setOffsety(Integer
					.parseInt(window.tfOffsetBottom.getText()));
			stamper.setRotation(Integer.parseInt(window.tfRotation.getText()));
			stamper.setFormat(window.txtd.getText());

		}

		@Override
		public void run() {

			if (!stamper.ProcessDoc()) {
				JOptionPane.showMessageDialog(null,
						"Batesmaster reported failure. \nPlease check "
								+ stamper.outputFileName, "Failure",
						JOptionPane.ERROR_MESSAGE);
			}

		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SwingWindow window = new SwingWindow();
					window.frame.setTransferHandler(new TransferHandler() {

						private static final long serialVersionUID = 1L;

						@Override
						public boolean canImport(TransferSupport support) {
							// check if all settings are valid
							if (!isValid(window.tfStartingNumber, window.txtd,
									window.tfOffsetLeft, window.tfOffsetBottom,
									window.tfRotation))
								return false;

							if (support
									.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

								support.setDropAction(COPY);
								return true;

							}
							return false;
						}

						@SuppressWarnings("unchecked")
						@Override
						public boolean importData(TransferSupport support) {
							ExecutorService thread = Executors
									.newSingleThreadExecutor();
							try {
								List<File> f = (List<File>) support
										.getTransferable().getTransferData(
												DataFlavor.javaFileListFlavor);
								boolean result = false;
								for (File fi : f) {
									if (fi.getName().toLowerCase().endsWith(
											".pdf")) {
										thread.submit(new StampPDF(window, fi));
										result = true;
									} else {
										System.err.println(f + " is not a PDF");
									}
								}
								return result;
							} catch (Exception e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(null,
										"An internal error has occured: " + e);
							} finally {
								thread.shutdown();
							}
							return false;
						}

					});
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SwingWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 387, 401);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblPdfBatesmaster = new JLabel(
				"Batesmaster - pdf bates stamper\n");
		lblPdfBatesmaster.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblPdfBatesmaster.setBounds(6, 6, 438, 16);
		frame.getContentPane().add(lblPdfBatesmaster);

		JLabel lblBatesmasterIsOpen = new JLabel(
				"Batesmaster is Open Source software licensed under GPLv3\n");
		lblBatesmasterIsOpen
				.setToolTipText("batesmaster was so far written by Mark Manoukian and Gregory Pruden\nCopyright for the GUI wrapper: 2011, OpenThinking Systems, LLC");
		lblBatesmasterIsOpen.setHorizontalAlignment(SwingConstants.TRAILING);
		lblBatesmasterIsOpen.setBounds(6, 329, 375, 26);
		frame.getContentPane().add(lblBatesmasterIsOpen);

		JLabel lblHttpwwwbatesmastercom = new JLabel(
				"http://www.batesmaster.com/");
		lblHttpwwwbatesmastercom
				.setHorizontalAlignment(SwingConstants.TRAILING);
		lblHttpwwwbatesmastercom.setBounds(134, 351, 247, 22);
		frame.getContentPane().add(lblHttpwwwbatesmastercom);

		JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setBounds(37, 136, 324, 181);
		frame.getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 250, 70, 0 };
		gbl_panel.rowHeights = new int[] { 30, 30, 30, 28, 28, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblStartingNumber = new JLabel("Starting number");
		GridBagConstraints gbc_lblStartingNumber = new GridBagConstraints();
		gbc_lblStartingNumber.fill = GridBagConstraints.BOTH;
		gbc_lblStartingNumber.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartingNumber.gridx = 0;
		gbc_lblStartingNumber.gridy = 0;
		panel.add(lblStartingNumber, gbc_lblStartingNumber);

		tfStartingNumber = new JTextField();
		tfStartingNumber.addFocusListener(new ConstrainToInt(0, null));
		tfStartingNumber.setHorizontalAlignment(SwingConstants.TRAILING);
		tfStartingNumber.setText("1");
		GridBagConstraints gbc_tfStartingNumber = new GridBagConstraints();
		gbc_tfStartingNumber.fill = GridBagConstraints.BOTH;
		gbc_tfStartingNumber.insets = new Insets(0, 0, 5, 0);
		gbc_tfStartingNumber.gridx = 1;
		gbc_tfStartingNumber.gridy = 0;
		panel.add(tfStartingNumber, gbc_tfStartingNumber);
		tfStartingNumber.setColumns(10);

		JLabel lblFormat = new JLabel("Number format");
		GridBagConstraints gbc_lblFormat = new GridBagConstraints();
		gbc_lblFormat.fill = GridBagConstraints.BOTH;
		gbc_lblFormat.insets = new Insets(0, 0, 5, 5);
		gbc_lblFormat.gridx = 0;
		gbc_lblFormat.gridy = 1;
		panel.add(lblFormat, gbc_lblFormat);

		txtd = new JTextField();
		txtd.addFocusListener(new ConstrainToFormat());
		txtd.setText("%05d");
		txtd.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_txtd = new GridBagConstraints();
		gbc_txtd.fill = GridBagConstraints.BOTH;
		gbc_txtd.insets = new Insets(0, 0, 5, 0);
		gbc_txtd.gridx = 1;
		gbc_txtd.gridy = 1;
		panel.add(txtd, gbc_txtd);
		txtd.setColumns(10);

		JLabel lblPixelOffsetFrom = new JLabel("Pixel offset from left of page");
		GridBagConstraints gbc_lblPixelOffsetFrom = new GridBagConstraints();
		gbc_lblPixelOffsetFrom.fill = GridBagConstraints.BOTH;
		gbc_lblPixelOffsetFrom.insets = new Insets(0, 0, 5, 5);
		gbc_lblPixelOffsetFrom.gridx = 0;
		gbc_lblPixelOffsetFrom.gridy = 2;
		panel.add(lblPixelOffsetFrom, gbc_lblPixelOffsetFrom);

		tfOffsetLeft = new JTextField();
		tfOffsetLeft.addFocusListener(new ConstrainToInt(0, 10000));
		tfOffsetLeft.setHorizontalAlignment(SwingConstants.TRAILING);
		tfOffsetLeft.setText("10");
		GridBagConstraints gbc_tfOffsetLeft = new GridBagConstraints();
		gbc_tfOffsetLeft.fill = GridBagConstraints.BOTH;
		gbc_tfOffsetLeft.insets = new Insets(0, 0, 5, 0);
		gbc_tfOffsetLeft.gridx = 1;
		gbc_tfOffsetLeft.gridy = 2;
		panel.add(tfOffsetLeft, gbc_tfOffsetLeft);
		tfOffsetLeft.setColumns(10);

		JLabel lblPixelOffsetFrom_1 = new JLabel(
				"Pixel offset from bottom of page");
		GridBagConstraints gbc_lblPixelOffsetFrom_1 = new GridBagConstraints();
		gbc_lblPixelOffsetFrom_1.fill = GridBagConstraints.BOTH;
		gbc_lblPixelOffsetFrom_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblPixelOffsetFrom_1.gridx = 0;
		gbc_lblPixelOffsetFrom_1.gridy = 3;
		panel.add(lblPixelOffsetFrom_1, gbc_lblPixelOffsetFrom_1);

		tfOffsetBottom = new JTextField();
		tfOffsetBottom.setText("10");
		tfOffsetBottom.addFocusListener(new ConstrainToInt(0, 10000));
		tfOffsetBottom.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_tfOffsetBottom = new GridBagConstraints();
		gbc_tfOffsetBottom.fill = GridBagConstraints.BOTH;
		gbc_tfOffsetBottom.insets = new Insets(0, 0, 5, 0);
		gbc_tfOffsetBottom.gridx = 1;
		gbc_tfOffsetBottom.gridy = 3;
		panel.add(tfOffsetBottom, gbc_tfOffsetBottom);
		tfOffsetBottom.setColumns(10);
		GridBagConstraints gbc_lblStampRotation = new GridBagConstraints();
		gbc_lblStampRotation.anchor = GridBagConstraints.WEST;
		gbc_lblStampRotation.insets = new Insets(0, 0, 0, 5);
		gbc_lblStampRotation.gridx = 0;
		gbc_lblStampRotation.gridy = 4;
		panel.add(lblStampRotation, gbc_lblStampRotation);

		tfRotation = new JTextField();
		tfRotation.setText("0");
		tfRotation.addFocusListener(new ConstrainToInt(0, 360));
		tfRotation.setHorizontalAlignment(SwingConstants.TRAILING);
		tfRotation.setColumns(10);
		GridBagConstraints gbc_tfRotation = new GridBagConstraints();
		gbc_tfRotation.anchor = GridBagConstraints.NORTH;
		gbc_tfRotation.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfRotation.gridx = 1;
		gbc_tfRotation.gridy = 4;
		panel.add(tfRotation, gbc_tfRotation);

		JLabel lblDropPdfFiles = new JLabel(
				"Drop PDF files on this window to stamp them");
		lblDropPdfFiles.setHorizontalAlignment(SwingConstants.CENTER);
		lblDropPdfFiles.setBounds(16, 46, 345, 16);
		frame.getContentPane().add(lblDropPdfFiles);

		JTextPane txtpnTheOutputFiles = new JTextPane();
		txtpnTheOutputFiles.setBackground(UIManager
				.getColor("Label.background"));
		txtpnTheOutputFiles.setEditable(false);
		txtpnTheOutputFiles
				.setText("The stamped output files will be created next to the originals. No existing files will be overwritten without warning.");
		txtpnTheOutputFiles.setBounds(47, 74, 285, 63);
		frame.getContentPane().add(txtpnTheOutputFiles);
	}
}