/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2020 Knowm Inc. www.knowm.org
 *
 * <p>This package also includes various components that are not part of Memristor-Discovery itself:
 *
 * <p>* `Multibit`: Copyright 2011 multibit.org, MIT License * `SteelCheckBox`: Copyright 2012
 * Gerrit, BSD license
 *
 * <p>Knowm, Inc. holds copyright and/or sufficient licenses to all components of the
 * Memristor-Discovery package, and therefore can grant, at its sole discretion, the ability for
 * companies, individuals, or organizations to create proprietary or open source (even if not GPL)
 * modules which may be dynamically linked at runtime with the portions of Memristor-Discovery which
 * fall under our copyright/license umbrella, or are distributed under more flexible licenses than
 * GPL.
 *
 * <p>The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * <p>If you have any questions regarding our licensing policy, please contact us at
 * `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.core.FileUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control.ControlController;

public class kTBitSatSolverPreferencesPanel extends ExperimentPreferencesPanel {

  private String filePath = "";
  private JLabel filePathLabel;
  private JButton filePathButton;
  private JTextField filePathTextField;

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  private JLabel amplitudeLabel;
  private JTextField amplitudeTextField;

  private JLabel amplitudeReverseLabel;
  private JTextField amplitudeReverseTextField;

  private JLabel pulseWidthLabel;
  private JTextField pulseWidthTextField;

  /**
   * Constructor
   *
   * @param owner
   */
  public kTBitSatSolverPreferencesPanel(JFrame owner, String experimentName) {

    super(owner, experimentName);
  }

  @Override
  public void doCreateAndShowGUI(JPanel preferencesPanel) {

    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(10, 10, 10, 10);

    gc.gridy = 0;
    gc.gridx = 0;

    this.filePathLabel = new JLabel("File Path");
    preferencesPanel.add(filePathLabel, gc);

    filePathButton = new JButton("Choose");
    filePathButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    gc.gridx = 1;
    preferencesPanel.add(filePathButton, gc);

    filePathButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            try {
              filePath =
                  FileUtils.showPickFileDialog(
                      preferencesPanel,
                      filePathLabel.getText(),
                      ControlController.getSATFileFilter());
              filePathTextField.setText(filePath);
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        });
    gc.gridy++;
    gc.gridx = 0;
    gc.gridwidth = 2;

    this.filePathTextField = new JTextField(24);
    this.filePathTextField.setText(
        String.valueOf(
            experimentPreferences.getString(
                kTBitSatSolverPreferences.FILE_PATH_INIT_KEY,
                kTBitSatSolverPreferences.FILE_PATH_INIT_DEFAULT_VALUE)));

    preferencesPanel.add(filePathTextField, gc);

    gc.gridwidth = 1;
    gc.gridy++;
    gc.gridx = 0;

    this.seriesResistorLabel = new JLabel("Series Resistor:");
    preferencesPanel.add(seriesResistorLabel, gc);

    gc.gridx = 1;
    this.seriesResistorTextField = new JTextField(12);
    this.seriesResistorTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                kTBitSatSolverPreferences.SERIES_R_INIT_KEY,
                kTBitSatSolverPreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    gc.gridy++;
    gc.gridx = 0;
    this.amplitudeLabel = new JLabel("Forward Amplitude [V]:");
    preferencesPanel.add(amplitudeLabel, gc);

    gc.gridx = 1;
    this.amplitudeTextField = new JTextField(12);
    this.amplitudeTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                kTBitSatSolverPreferences.AMPLITUDE_INIT_FLOAT_KEY,
                kTBitSatSolverPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(amplitudeTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.amplitudeReverseLabel = new JLabel("Reverse Amplitude [V]:");
    preferencesPanel.add(amplitudeReverseLabel, gc);

    gc.gridx = 1;
    this.amplitudeReverseTextField = new JTextField(12);
    this.amplitudeReverseTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                kTBitSatSolverPreferences.AMPLITUDE_REVERSE_INIT_FLOAT_KEY,
                kTBitSatSolverPreferences.AMPLITUDE_REVERSE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(amplitudeReverseTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.pulseWidthLabel = new JLabel("Pulse Width [ns]:");
    preferencesPanel.add(pulseWidthLabel, gc);

    gc.gridx = 1;
    this.pulseWidthTextField = new JTextField(12);
    this.pulseWidthTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                kTBitSatSolverPreferences.PULSE_WIDTH_INIT_KEY,
                kTBitSatSolverPreferences.PULSE_WIDTH_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(pulseWidthTextField, gc);

    gc.gridy++;
  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setString(
        kTBitSatSolverPreferences.FILE_PATH_INIT_KEY, filePathTextField.getText().trim());
    experimentPreferences.setInteger(
        kTBitSatSolverPreferences.SERIES_R_INIT_KEY,
        Integer.parseInt(seriesResistorTextField.getText()));
    experimentPreferences.setFloat(
        kTBitSatSolverPreferences.AMPLITUDE_INIT_FLOAT_KEY,
        Float.parseFloat(amplitudeTextField.getText()));
    experimentPreferences.setInteger(
        kTBitSatSolverPreferences.PULSE_WIDTH_INIT_KEY,
        Integer.parseInt(pulseWidthTextField.getText()));
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new kTBitSatSolverPreferences();
  }
}
