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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse21;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.Synapse12Preferences;

public class Synapse21PreferencesPanel extends ExperimentPreferencesPanel {

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  private JLabel amplitudeLabel;
  private JTextField amplitudeTextField;

  private JLabel pulseWidthLabel;
  private JTextField pulseWidthTextField;

  private JLabel sampleRateLabel;
  private JTextField sampleRateTextField;


  private JLabel scopeOneOffsetLabel;
  private JTextField scopeOneOffsetTextField;

  private JLabel scopeTwoOffsetLabel;
  private JTextField scopeTwoOffsetTextField;

  private JLabel wOneOffsetLabel;
  private JTextField wOneOffsetTextField;
  

  /**
   * Constructor
   *
   * @param owner
   */
  public Synapse21PreferencesPanel(JFrame owner, String experimentName) {

    super(owner, experimentName);
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
    this.seriesResistorTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                Synapse21Preferences.SERIES_R_INIT_KEY,
                Synapse21Preferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.amplitudeLabel = new JLabel("Amplitude [V]:");
    preferencesPanel.add(amplitudeLabel, gc);

    gc.gridx = 1;
    this.amplitudeTextField = new JTextField(12);
    this.amplitudeTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                Synapse21Preferences.AMPLITUDE_INIT_FLOAT_KEY,
                Synapse21Preferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(amplitudeTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.pulseWidthLabel = new JLabel("Pulse Width [ns]:");
    preferencesPanel.add(pulseWidthLabel, gc);

    gc.gridx = 1;
    this.pulseWidthTextField = new JTextField(12);
    this.pulseWidthTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                Synapse21Preferences.PULSE_WIDTH_INIT_KEY,
                Synapse21Preferences.PULSE_WIDTH_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(pulseWidthTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.sampleRateLabel = new JLabel("Sample Rate [s]:");
    preferencesPanel.add(sampleRateLabel, gc);

    gc.gridx = 1;
    this.sampleRateTextField = new JTextField(12);
    this.sampleRateTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                Synapse21Preferences.SAMPLE_RATE_INIT_KEY,
                Synapse21Preferences.SAMPLE_RATE_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(sampleRateTextField, gc);


    gc.gridy++;

    gc.gridx = 0;
    this.scopeOneOffsetLabel = new JLabel("Scope (1+) Offset:");
    preferencesPanel.add(scopeOneOffsetLabel, gc);

    gc.gridx = 1;
    this.scopeOneOffsetTextField = new JTextField(12);
    this.scopeOneOffsetTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                Synapse21Preferences.SCOPE_ONE_OFFSET_KEY,
                Synapse21Preferences.SCOPE_ONE_OFFSET_DEFAULT_VALUE)));
    preferencesPanel.add(scopeOneOffsetTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.scopeTwoOffsetLabel = new JLabel("Scope (2+) Offset:");
    preferencesPanel.add(scopeTwoOffsetLabel, gc);

    gc.gridx = 1;
    this.scopeTwoOffsetTextField = new JTextField(12);
    this.scopeTwoOffsetTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                Synapse21Preferences.SCOPE_TWO_OFFSET_KEY,
                Synapse21Preferences.SCOPE_TWO_OFFSET_DEFAULT_VALUE)));
    preferencesPanel.add(scopeTwoOffsetTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.wOneOffsetLabel = new JLabel("W1 Offset:");
    preferencesPanel.add(wOneOffsetLabel, gc);

    gc.gridx = 1;
    this.wOneOffsetTextField = new JTextField(12);
    this.wOneOffsetTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                Synapse21Preferences.W_ONE_OFFSET_KEY,
                Synapse21Preferences.W_ONE_OFFSET_DEFAULT_VALUE)));
    preferencesPanel.add(wOneOffsetTextField, gc);
    
  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setInteger(
        Synapse21Preferences.SERIES_R_INIT_KEY,
        Integer.parseInt(seriesResistorTextField.getText()));
    experimentPreferences.setFloat(
        Synapse21Preferences.AMPLITUDE_INIT_FLOAT_KEY,
        Float.parseFloat(amplitudeTextField.getText()));
    experimentPreferences.setInteger(
        Synapse21Preferences.PULSE_WIDTH_INIT_KEY, Integer.parseInt(pulseWidthTextField.getText()));
    experimentPreferences.setInteger(
        Synapse21Preferences.SAMPLE_RATE_INIT_KEY, Integer.parseInt(sampleRateTextField.getText()));

    experimentPreferences.setFloat(
        Synapse21Preferences.SCOPE_ONE_OFFSET_KEY,
        Float.parseFloat(scopeOneOffsetTextField.getText()));
    experimentPreferences.setFloat(
        Synapse21Preferences.SCOPE_TWO_OFFSET_KEY,
        Float.parseFloat(scopeTwoOffsetTextField.getText()));
    experimentPreferences.setFloat(
        Synapse21Preferences.W_ONE_OFFSET_KEY, Float.parseFloat(wOneOffsetTextField.getText()));
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new Synapse21Preferences();
  }
}
