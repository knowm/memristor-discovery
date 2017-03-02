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
package org.knowm.memristor.discovery.gui.mvc.experiments.dc;

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

public class DCPreferencesPanel extends AppPreferencesPanel {

  private JLabel waveformLabel;
  private JComboBox<Waveform> waveformComboBox;

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  private JLabel amplitudeLabel;
  private JTextField amplitudeTextField;

  private JLabel periodLabel;
  private JTextField periodTextField;

  /**
   * Constructor
   *
   * @param owner
   */
  public DCPreferencesPanel(JFrame owner) {

    super(owner);
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
    this.waveformComboBox.setModel(new DefaultComboBoxModel<>(new Waveform[]{Waveform.Sawtooth, Waveform.SawtoothUpDown, Waveform.Triangle, Waveform.TriangleUpDown}));
    DCPreferences.Waveform waveform = DCPreferences.Waveform.valueOf(appPreferences.getString(DCPreferences.WAVEFORM_INIT_STRING_KEY,
        DCPreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    // DCPreferences.Waveform waveform =  Waveform.Sawtooth;
    this.waveformComboBox.setSelectedItem(waveform);
    preferencesPanel.add(waveformComboBox, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.seriesResistorLabel = new JLabel("Series Resistor:");
    preferencesPanel.add(seriesResistorLabel, gc);

    gc.gridx = 1;
    this.seriesResistorTextField = new JTextField(12);
    this.seriesResistorTextField.setText(String.valueOf(appPreferences.getInteger(DCPreferences.SERIES_R_INIT_KEY, DCPreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.amplitudeLabel = new JLabel("Amplitude [V]:");
    preferencesPanel.add(amplitudeLabel, gc);

    gc.gridx = 1;
    this.amplitudeTextField = new JTextField(12);
    this.amplitudeTextField.setText(String.valueOf(appPreferences.getFloat(DCPreferences.AMPLITUDE_INIT_FLOAT_KEY, DCPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE)));
    preferencesPanel.add(amplitudeTextField, gc);

    gc.gridy++;

    gc.gridx = 0;
    this.periodLabel = new JLabel("Period [ns]:");
    preferencesPanel.add(periodLabel, gc);

    gc.gridx = 1;
    this.periodTextField = new JTextField(12);
    this.periodTextField.setText(String.valueOf(appPreferences.getInteger(DCPreferences.PERIOD_INIT_KEY, DCPreferences.PERIOD_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(periodTextField, gc);
  }

  @Override
  public void doSavePreferences() {

    appPreferences.setString(DCPreferences.WAVEFORM_INIT_STRING_KEY, waveformComboBox.getSelectedItem().toString().trim());
    appPreferences.setInteger(DCPreferences.SERIES_R_INIT_KEY, Integer.parseInt(seriesResistorTextField.getText()));
    appPreferences.setFloat(DCPreferences.AMPLITUDE_INIT_FLOAT_KEY, Float.parseFloat(amplitudeTextField.getText()));
    appPreferences.setInteger(DCPreferences.PERIOD_INIT_KEY, Integer.parseInt(periodTextField.getText()));
  }

  @Override
  public AppPreferences initAppPreferences() {

    return new DCPreferences();
  }

  @Override
  public String getAppName() {

    return "DC";
  }
}
