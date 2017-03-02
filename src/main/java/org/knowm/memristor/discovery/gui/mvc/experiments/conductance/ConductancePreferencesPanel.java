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
package org.knowm.memristor.discovery.gui.mvc.experiments.conductance;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knowm.memristor.discovery.gui.mvc.experiments.AppPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.AppPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.AppPreferencesPanel;

public class ConductancePreferencesPanel extends AppPreferencesPanel {

  // RESET
  private JLabel resetPulseTypeLabel;
  private JComboBox<Waveform> resetPulseTypeComboBox;

  private JLabel resetAmplitudeLabel;
  private JTextField resetAmplitudeTextField;

  private JLabel resetPulseWidthLabel;
  private JTextField resetPulseWidthTextField;

  // SET

  private JLabel setConductanceLabel;
  private JTextField setConductanceTextField;

  private JLabel setAmplitudeLabel;
  private JTextField setAmplitudeTextField;

  private JLabel setPulseWidthLabel;
  private JTextField setPulseWidthTextField;

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  /**
   * Constructor
   *
   * @param owner
   */
  public ConductancePreferencesPanel(JFrame owner) {

    super(owner);
  }

  @Override
  public void doCreateAndShowGUI(JPanel preferencesPanel) {

    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(10, 10, 10, 10);

    gc.gridy = 0;
    gc.gridx = 0;

    // RESET

    this.resetPulseTypeLabel = new JLabel("Reset Pulse Type:");
    preferencesPanel.add(resetPulseTypeLabel, gc);

    gc.gridx = 1;
    this.resetPulseTypeComboBox = new JComboBox<>();
    this.resetPulseTypeComboBox.setModel(new DefaultComboBoxModel<>(new Waveform[]{Waveform.Sawtooth, Waveform.Square}));
    ConductancePreferences.Waveform waveform = ConductancePreferences.Waveform.valueOf(appPreferences.getString(ConductancePreferences.RESET_PULSE_TYPE_INIT_STRING_KEY,
        ConductancePreferences.RESET_PULSE_TYPE_INIT_STRING_DEFAULT_VALUE));
    this.resetPulseTypeComboBox.setSelectedItem(waveform);
    preferencesPanel.add(resetPulseTypeComboBox, gc);

    gc.gridy++;
    gc.gridx = 0;
    this.resetAmplitudeLabel = new JLabel("Reset Amplitude [V]:");
    preferencesPanel.add(resetAmplitudeLabel, gc);

    gc.gridx = 1;
    this.resetAmplitudeTextField = new JTextField(12);
    this.resetAmplitudeTextField.setText(String.valueOf(appPreferences.getFloat(ConductancePreferences.RESET_AMPLITUDE_INIT_FLOAT_KEY, ConductancePreferences.RESET_AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(resetAmplitudeTextField, gc);

    gc.gridy++;
    gc.gridx = 0;
    this.resetPulseWidthLabel = new JLabel("Reset Pulse Width [ns]:");
    preferencesPanel.add(resetPulseWidthLabel, gc);

    gc.gridx = 1;
    this.resetPulseWidthTextField = new JTextField(12);
    this.resetPulseWidthTextField.setText(String.valueOf(appPreferences.getInteger(ConductancePreferences.RESET_PULSE_WIDTH_INIT_KEY, ConductancePreferences.RESET_PERIOD_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(resetPulseWidthTextField, gc);

    // SET

    gc.gridy++;
    gc.gridx = 0;
    this.setConductanceLabel = new JLabel("Set Conductance [mS]:");
    preferencesPanel.add(setConductanceLabel, gc);

    gc.gridx = 1;
    this.setConductanceTextField = new JTextField(12);
    this.setConductanceTextField.setText(String.valueOf(appPreferences.getFloat(ConductancePreferences.SET_CONDUCTANCE_INIT_KEY, ConductancePreferences.SET_CONDUCTANCE_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(setConductanceTextField, gc);

    gc.gridy++;
    gc.gridx = 0;
    this.setAmplitudeLabel = new JLabel("Set Amplitude [V]:");
    preferencesPanel.add(setAmplitudeLabel, gc);

    gc.gridx = 1;
    this.setAmplitudeTextField = new JTextField(12);
    this.setAmplitudeTextField.setText(String.valueOf(appPreferences.getFloat(ConductancePreferences.SET_AMPLITUDE_INIT_FLOAT_KEY, ConductancePreferences.SET_AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(setAmplitudeTextField, gc);

    gc.gridy++;
    gc.gridx = 0;
    this.setPulseWidthLabel = new JLabel("Set Pulse Width [ns]:");
    preferencesPanel.add(setPulseWidthLabel, gc);

    gc.gridx = 1;
    this.setPulseWidthTextField = new JTextField(12);
    this.setPulseWidthTextField.setText(String.valueOf(appPreferences.getInteger(ConductancePreferences.SET_PULSE_WIDTH_INIT_KEY, ConductancePreferences.SET_PERIOD_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(setPulseWidthTextField, gc);

    gc.gridy++;
    gc.gridx = 0;
    this.seriesResistorLabel = new JLabel("Series Resistor:");
    preferencesPanel.add(seriesResistorLabel, gc);

    gc.gridx = 1;
    this.seriesResistorTextField = new JTextField(12);
    this.seriesResistorTextField.setText(String.valueOf(appPreferences.getInteger(ConductancePreferences.SERIES_R_INIT_KEY, ConductancePreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);
  }

  @Override
  public void doSavePreferences() {

    appPreferences.setString(ConductancePreferences.RESET_PULSE_TYPE_INIT_STRING_KEY, resetPulseTypeComboBox.getSelectedItem().toString().trim());
    appPreferences.setFloat(ConductancePreferences.RESET_AMPLITUDE_INIT_FLOAT_KEY, Float.parseFloat(resetAmplitudeTextField.getText()));
    appPreferences.setInteger(ConductancePreferences.RESET_PULSE_WIDTH_INIT_KEY, Integer.parseInt(resetPulseWidthTextField.getText()));

    // SET

    appPreferences.setFloat(ConductancePreferences.SET_CONDUCTANCE_INIT_KEY, Float.parseFloat(setConductanceTextField.getText()));
    appPreferences.setInteger(ConductancePreferences.SERIES_R_INIT_KEY, Integer.parseInt(seriesResistorTextField.getText()));
  }

  @Override
  public AppPreferences initAppPreferences() {

    return new ConductancePreferences();
  }

  @Override
  public String getAppName() {

    return "Conductance";
  }
}
