/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
 *
 * This package also includes various components that are not part of
 * Memristor-Discovery itself:
 *
 * * `Multibit`: Copyright 2011 multibit.org, MIT License
 * * `SteelCheckBox`: Copyright 2012 Gerrit, BSD license
 *
 * Knowm, Inc. holds copyright
 * and/or sufficient licenses to all components of the Memristor-Discovery
 * package, and therefore can grant, at its sole discretion, the ability
 * for companies, individuals, or organizations to create proprietary or
 * open source (even if not GPL) modules which may be dynamically linked at
 * runtime with the portions of Memristor-Discovery which fall under our
 * copyright/license umbrella, or are distributed under more flexible
 * licenses than GPL.
 *
 * The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * If you have any questions regarding our licensing policy, please
 * contact us at `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.gui.mvc.experiments.qc;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;

public class QCPreferencesPanel extends ExperimentPreferencesPanel {

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  private JLabel amplitudeLabel;
  private JTextField amplitudeTextField;

  private JLabel frequencyLabel;
  private JTextField frequencyTextField;

  private JLabel savePathLabel;
  private JTextField savePathTextField;

  /**
   * Constructor
   *
   * @param owner
   */
  public QCPreferencesPanel(JFrame owner) {

    super(owner);
  }

  @Override
  public void doCreateAndShowGUI(JPanel preferencesPanel) {

    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(10, 10, 10, 10);

    gc.gridy = 0;
    gc.gridx = 0;
    this.seriesResistorLabel = new JLabel("Series Resistor:");
    preferencesPanel.add(seriesResistorLabel, gc);

    gc.gridx = 1;
    this.seriesResistorTextField = new JTextField(12);
    this.seriesResistorTextField.setText(String.valueOf(experimentPreferences.getInteger(QCPreferences.SERIES_R_INIT_KEY, QCPreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.amplitudeLabel = new JLabel("Waveform Amplitude [V]:");
    preferencesPanel.add(amplitudeLabel, gc);

    gc.gridx = 1;
    this.amplitudeTextField = new JTextField(12);
    this.amplitudeTextField.setText(String.valueOf(experimentPreferences.getFloat(QCPreferences.AMPLITUDE_INIT_FLOAT_KEY, QCPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(amplitudeTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.frequencyLabel = new JLabel("Waveform Frequency [Hz]:");
    preferencesPanel.add(frequencyLabel, gc);

    gc.gridx = 1;
    this.frequencyTextField = new JTextField(12);
    this.frequencyTextField.setText(String.valueOf(experimentPreferences.getInteger(QCPreferences.FREQUENCY_INIT_KEY, QCPreferences.FREQUENCY_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(frequencyTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.savePathLabel = new JLabel("Report Save Path:");
    preferencesPanel.add(savePathLabel, gc);

    gc.gridx = 1;
    this.savePathTextField = new JTextField(20);
    this.savePathTextField.setText(String.valueOf(experimentPreferences.getString(QCPreferences.REPORT_DIRECTORY_PATH_KEY, QCPreferences.REPORT_DIRECTORY_PATH_DEFAULT_VALUE)));
    preferencesPanel.add(savePathTextField, gc);

  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setInteger(QCPreferences.SERIES_R_INIT_KEY, Integer.parseInt(seriesResistorTextField.getText()));
    experimentPreferences.setFloat(QCPreferences.AMPLITUDE_INIT_FLOAT_KEY, Float.parseFloat(amplitudeTextField.getText()));
    experimentPreferences.setInteger(QCPreferences.FREQUENCY_INIT_KEY, Integer.parseInt(frequencyTextField.getText()));
    experimentPreferences.setString(QCPreferences.REPORT_DIRECTORY_PATH_KEY, savePathTextField.getText());
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new QCPreferences();
  }

  @Override
  public String getAppName() {

    return "QC";
  }
}
