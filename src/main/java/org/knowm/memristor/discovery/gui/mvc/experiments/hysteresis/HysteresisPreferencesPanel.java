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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;

public class HysteresisPreferencesPanel extends ExperimentPreferencesPanel {

  private JLabel waveformLabel;
  private JComboBox<HysteresisPreferences.Waveform> waveformComboBox;

  private JLabel offsetLabel;
  private JTextField offsetTextField;

  private JLabel amplitudeLabel;
  private JTextField amplitudeTextField;

  private JLabel frequencyLabel;
  private JTextField frequencyTextField;

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  private JLabel kLabel;
  private JTextField kTextField;

  public HysteresisPreferencesPanel(JFrame owner, String experimentName) {

    super(owner, experimentName);
  }

  @Override
  public void doCreateAndShowGUI(JPanel preferencesPanel) {

    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(10, 10, 10, 10);

    gc.gridy = 0;
    gc.gridx = 0;
    this.waveformLabel = new JLabel("Waveform:");
    preferencesPanel.add(waveformLabel, gc);

    gc.gridx = 1;
    this.waveformComboBox = new JComboBox<>();
    this.waveformComboBox.setModel(
        new DefaultComboBoxModel<>(new Waveform[] {Waveform.Sine, Waveform.Triangle}));
    HysteresisPreferences.Waveform waveform =
        HysteresisPreferences.Waveform.valueOf(
            experimentPreferences.getString(
                HysteresisPreferences.WAVEFORM_INIT_STRING_KEY,
                HysteresisPreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    this.waveformComboBox.setSelectedItem(waveform);
    preferencesPanel.add(waveformComboBox, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.offsetLabel = new JLabel("Offset [V]:");
    preferencesPanel.add(offsetLabel, gc);

    gc.gridx = 1;
    this.offsetTextField = new JTextField(12);
    this.offsetTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                HysteresisPreferences.OFFSET_INIT_FLOAT_KEY,
                HysteresisPreferences.OFFSET_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(offsetTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.amplitudeLabel = new JLabel("Amplitude [V]:");
    preferencesPanel.add(amplitudeLabel, gc);

    gc.gridx = 1;
    this.amplitudeTextField = new JTextField(12);
    this.amplitudeTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                HysteresisPreferences.AMPLITUDE_INIT_FLOAT_KEY,
                HysteresisPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(amplitudeTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.frequencyLabel = new JLabel("Frequency [Hz]:");
    preferencesPanel.add(frequencyLabel, gc);

    gc.gridx = 1;
    this.frequencyTextField = new JTextField(12);
    this.frequencyTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                HysteresisPreferences.FREQUENCY_INIT_KEY,
                HysteresisPreferences.FREQUENCY_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(frequencyTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.seriesResistorLabel = new JLabel("Series Resistor [Ω]:");
    preferencesPanel.add(seriesResistorLabel, gc);

    gc.gridx = 1;
    this.seriesResistorTextField = new JTextField(12);
    this.seriesResistorTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                HysteresisPreferences.SERIES_R_INIT_KEY,
                HysteresisPreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.kLabel = new JLabel("K:");
    preferencesPanel.add(kLabel, gc);

    gc.gridx = 1;
    this.kTextField = new JTextField(12);
    this.kTextField.setText(
        String.valueOf(
            experimentPreferences.getDouble(
                HysteresisPreferences.K_INIT_FLOAT_KEY,
                HysteresisPreferences.K_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(kTextField, gc);
  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setString(
        HysteresisPreferences.WAVEFORM_INIT_STRING_KEY,
        waveformComboBox.getSelectedItem().toString().trim());
    experimentPreferences.setFloat(
        HysteresisPreferences.OFFSET_INIT_FLOAT_KEY, Float.parseFloat(offsetTextField.getText()));
    experimentPreferences.setFloat(
        HysteresisPreferences.AMPLITUDE_INIT_FLOAT_KEY,
        Float.parseFloat(amplitudeTextField.getText()));
    experimentPreferences.setInteger(
        HysteresisPreferences.FREQUENCY_INIT_KEY, Integer.parseInt(frequencyTextField.getText()));
    experimentPreferences.setInteger(
        HysteresisPreferences.SERIES_R_INIT_KEY,
        Integer.parseInt(seriesResistorTextField.getText()));
    experimentPreferences.setDouble(
        HysteresisPreferences.K_INIT_FLOAT_KEY, Double.parseDouble(kTextField.getText()));
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new HysteresisPreferences();
  }
}
