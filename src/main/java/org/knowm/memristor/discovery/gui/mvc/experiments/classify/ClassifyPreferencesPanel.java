/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;

public class ClassifyPreferencesPanel extends ExperimentPreferencesPanel {

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  private JLabel amplitudeLabel;
  private JTextField amplitudeTextField;

  private JLabel pulseWidthLabel;
  private JTextField pulseWidthTextField;

  private JLabel numTrainEpochsLabel;
  private JTextField numTrainEpochsTextField;

  /**
   * Constructor
   *
   * @param owner
   */
  public ClassifyPreferencesPanel(JFrame owner) {

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
    this.seriesResistorTextField.setText(String.valueOf(experimentPreferences.getInteger(ClassifyPreferences.SERIES_R_INIT_KEY,
        ClassifyPreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.amplitudeLabel = new JLabel("Amplitude [V]:");
    preferencesPanel.add(amplitudeLabel, gc);

    gc.gridx = 1;
    this.amplitudeTextField = new JTextField(12);
    this.amplitudeTextField.setText(String.valueOf(experimentPreferences.getFloat(ClassifyPreferences.AMPLITUDE_INIT_FLOAT_KEY,
        ClassifyPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(amplitudeTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.pulseWidthLabel = new JLabel("Pulse Width [ns]:");
    preferencesPanel.add(pulseWidthLabel, gc);

    gc.gridx = 1;
    this.pulseWidthTextField = new JTextField(12);
    this.pulseWidthTextField.setText(String.valueOf(experimentPreferences.getInteger(ClassifyPreferences.PULSE_WIDTH_INIT_KEY,
        ClassifyPreferences.PULSE_WIDTH_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(pulseWidthTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.numTrainEpochsLabel = new JLabel("Sample Rate [s]:");
    preferencesPanel.add(numTrainEpochsLabel, gc);

    gc.gridx = 1;
    this.numTrainEpochsTextField = new JTextField(12);
    this.numTrainEpochsTextField.setText(String.valueOf(experimentPreferences.getInteger(ClassifyPreferences.NUM_TRAIN_EPOCHS_INIT_KEY,
        ClassifyPreferences.NUM_TRAIN_EPOCHS_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(numTrainEpochsTextField, gc);

  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setInteger(ClassifyPreferences.SERIES_R_INIT_KEY, Integer.parseInt(seriesResistorTextField.getText()));
    experimentPreferences.setFloat(ClassifyPreferences.AMPLITUDE_INIT_FLOAT_KEY, Float.parseFloat(amplitudeTextField.getText()));
    experimentPreferences.setInteger(ClassifyPreferences.PULSE_WIDTH_INIT_KEY, Integer.parseInt(pulseWidthTextField.getText()));
    experimentPreferences.setInteger(ClassifyPreferences.NUM_TRAIN_EPOCHS_INIT_KEY, Integer.parseInt(numTrainEpochsTextField.getText()));
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new ClassifyPreferences();
  }

  @Override
  public String getAppName() {

    return "Classify";
  }
}
