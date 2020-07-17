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
package org.knowm.memristor.discovery.gui.mvc.experiments.shelflife;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.core.FileUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;

public class ShelfLifePreferencesPanel extends ExperimentPreferencesPanel {

  private String saveDirectory = "";
  private JLabel saveDirectoryLabel;
  private JButton saveDirectoryButton;
  private JTextField saveDirectoryTextField;

  private JLabel timeUnitLabel;
  private JComboBox<TimeUnit> timeUnitComboBox;

  private JLabel repeatIntervalLabel;
  private JTextField repeatIntervalTextField;

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  private JLabel readVoltageAmplitudeLabel;
  private JTextField readVoltageAmplitudeTextField;

  private JLabel writeVoltageAmplitudeLabel;
  private JTextField writeVoltageAmplitudeTextField;

  private JLabel eraseVoltageAmplitudeLabel;
  private JTextField eraseVoltageAmplitudeTextField;

  private JLabel readPulseWidthInMicroSecondsLabel;
  private JTextField readPulseWidthInMicroSecondsTextField;

  private JLabel writePulseWidthInMicroSecondsLabel;
  private JTextField writePulseWidthInMicroSecondsTextField;

  private JLabel erasePulseWidthInMicroSecondsLabel;
  private JTextField erasePulseWidthInMicroSecondsTextField;

  private JLabel minEraseResistanceLabel;
  private JTextField minEraseResistanceTextField;

  private JLabel maxWriteResistanceLabel;
  private JTextField maxWriteResistanceTextField;

  /**
   * Constructor
   *
   * @param owner
   */
  public ShelfLifePreferencesPanel(JFrame owner, String experimentName) {

    super(owner, experimentName);
  }

  @Override
  public void doCreateAndShowGUI(JPanel preferencesPanel) {

    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(2, 10, 2, 10);

    gc.gridy = 0;
    gc.gridx = 0;
    this.saveDirectoryLabel = new JLabel("Save Directory:");
    preferencesPanel.add(saveDirectoryLabel, gc);

    saveDirectoryButton = new JButton("Choose");
    saveDirectoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    gc.gridx = 1;
    preferencesPanel.add(saveDirectoryButton, gc);
    saveDirectoryButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            try {
              saveDirectory =
                  FileUtils.showSaveAsDialog(preferencesPanel, saveDirectoryLabel.getText());
              saveDirectoryTextField.setText(saveDirectory);
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        });
    gc.gridy++;
    gc.gridx = 0;
    gc.gridwidth = 2;
    this.saveDirectoryTextField = new JTextField(24);

    this.saveDirectoryTextField.setText(
        String.valueOf(
            experimentPreferences.getString(
                ShelfLifePreferences.SAVE_DIRECTORY_INIT_KEY,
                ShelfLifePreferences.SAVE_DIRECTORY_INIT_DEFAULT_VALUE)));

    preferencesPanel.add(saveDirectoryTextField, gc);

    gc.gridy++;
    gc.gridx = 0;
    gc.gridwidth = 1;
    this.timeUnitLabel = new JLabel("Time Unit:");
    preferencesPanel.add(timeUnitLabel, gc);

    gc.gridx = 1;
    this.timeUnitComboBox = new JComboBox<>();
    this.timeUnitComboBox.setModel(new DefaultComboBoxModel<>(TimeUnit.values()));
    TimeUnit timeUnit =
        TimeUnit.valueOf(
            experimentPreferences.getString(
                ShelfLifePreferences.TIME_UNIT_INIT_KEY,
                ShelfLifePreferences.TIME_UNIT_DEFAULT_VALUE));
    this.timeUnitComboBox.setSelectedItem(timeUnit);
    preferencesPanel.add(timeUnitComboBox, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.repeatIntervalLabel = new JLabel("Repeat Interval (Time Unit):");
    preferencesPanel.add(repeatIntervalLabel, gc);

    gc.gridx = 1;
    this.repeatIntervalTextField = new JTextField(12);
    this.repeatIntervalTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                ShelfLifePreferences.REPEAT_INTERVAL_INIT_KEY,
                ShelfLifePreferences.REPEAT_INTERVAL_DEFAULT_VALUE)));
    preferencesPanel.add(repeatIntervalTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.seriesResistorLabel = new JLabel("Series Resistor (Ω):");
    preferencesPanel.add(seriesResistorLabel, gc);

    gc.gridx = 1;
    this.seriesResistorTextField = new JTextField(12);
    this.seriesResistorTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                ShelfLifePreferences.SERIES_R_INIT_KEY,
                ShelfLifePreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.readVoltageAmplitudeLabel = new JLabel("Read Pulse Amplitude(V):");
    preferencesPanel.add(readVoltageAmplitudeLabel, gc);

    gc.gridx = 1;
    this.readVoltageAmplitudeTextField = new JTextField(12);
    this.readVoltageAmplitudeTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                ShelfLifePreferences.READ_VOLTS_INIT_KEY,
                ShelfLifePreferences.READ_VOLTS_DEFAULT_VALUE)));
    preferencesPanel.add(readVoltageAmplitudeTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.readPulseWidthInMicroSecondsLabel = new JLabel("Read Pulse Width(μs):");
    preferencesPanel.add(readPulseWidthInMicroSecondsLabel, gc);

    gc.gridx = 1;
    this.readPulseWidthInMicroSecondsTextField = new JTextField(12);
    this.readPulseWidthInMicroSecondsTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                ShelfLifePreferences.READ_PULSE_WIDTH_INIT_KEY,
                ShelfLifePreferences.READ_PULSE_WIDTH_DEFAULT_VALUE)));
    preferencesPanel.add(readPulseWidthInMicroSecondsTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.writeVoltageAmplitudeLabel = new JLabel("Write Pulse Amplitude(V):");
    preferencesPanel.add(writeVoltageAmplitudeLabel, gc);

    gc.gridx = 1;
    this.writeVoltageAmplitudeTextField = new JTextField(12);
    this.writeVoltageAmplitudeTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                ShelfLifePreferences.WRITE_VOLTS_INIT_KEY,
                ShelfLifePreferences.WRITE_VOLTS_DEFAULT_VALUE)));
    preferencesPanel.add(writeVoltageAmplitudeTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.writePulseWidthInMicroSecondsLabel = new JLabel("Write Pulse Width(μs):");
    preferencesPanel.add(writePulseWidthInMicroSecondsLabel, gc);

    gc.gridx = 1;
    this.writePulseWidthInMicroSecondsTextField = new JTextField(12);
    this.writePulseWidthInMicroSecondsTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                ShelfLifePreferences.WRITE_PULSE_WIDTH_INIT_KEY,
                ShelfLifePreferences.WRITE_PULSE_WIDTH_DEFAULT_VALUE)));
    preferencesPanel.add(writePulseWidthInMicroSecondsTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.eraseVoltageAmplitudeLabel = new JLabel("Erase Pulse Amplitude(V):");
    preferencesPanel.add(eraseVoltageAmplitudeLabel, gc);

    gc.gridx = 1;
    this.eraseVoltageAmplitudeTextField = new JTextField(12);
    this.eraseVoltageAmplitudeTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                ShelfLifePreferences.ERASE_VOLTS_INIT_KEY,
                ShelfLifePreferences.ERASE_VOLTS_DEFAULT_VALUE)));
    preferencesPanel.add(eraseVoltageAmplitudeTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.erasePulseWidthInMicroSecondsLabel = new JLabel("Erase Pulse Width(μs):");
    preferencesPanel.add(erasePulseWidthInMicroSecondsLabel, gc);

    gc.gridx = 1;
    this.erasePulseWidthInMicroSecondsTextField = new JTextField(12);
    this.erasePulseWidthInMicroSecondsTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                ShelfLifePreferences.ERASE_PULSE_WIDTH_INIT_KEY,
                ShelfLifePreferences.ERASE_PULSE_WIDTH_DEFAULT_VALUE)));
    preferencesPanel.add(erasePulseWidthInMicroSecondsTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.maxWriteResistanceLabel = new JLabel("Max Write Resistance (kΩ)");
    preferencesPanel.add(maxWriteResistanceLabel, gc);

    gc.gridx = 1;
    this.maxWriteResistanceTextField = new JTextField(12);
    this.maxWriteResistanceTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                ShelfLifePreferences.MAX_WRITE_RESISTANCE_INIT_KEY,
                ShelfLifePreferences.MAX_WRITE_RESISTANCE_DEFAULT_VALUE)));
    preferencesPanel.add(maxWriteResistanceTextField, gc);

    /////////////////////////////////////////////////////////

    gc.gridy++;
    gc.gridx = 0;
    this.minEraseResistanceLabel = new JLabel("Min Erase Resistance (kΩ)");
    preferencesPanel.add(minEraseResistanceLabel, gc);

    gc.gridx = 1;
    this.minEraseResistanceTextField = new JTextField(12);
    this.minEraseResistanceTextField.setText(
        String.valueOf(
            experimentPreferences.getFloat(
                ShelfLifePreferences.MIN_ERASE_RESISTANCE_INIT_KEY,
                ShelfLifePreferences.MIN_ERASE_RESISTANCE_DEFAULT_VALUE)));
    preferencesPanel.add(minEraseResistanceTextField, gc);

    /////////////////////////////////////////////////////////

  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setString(
        ShelfLifePreferences.SAVE_DIRECTORY_INIT_KEY, saveDirectoryTextField.getText().trim());
    experimentPreferences.setString(
        ShelfLifePreferences.TIME_UNIT_INIT_KEY,
        timeUnitComboBox.getSelectedItem().toString().trim());
    experimentPreferences.setInteger(
        ShelfLifePreferences.REPEAT_INTERVAL_INIT_KEY,
        Integer.parseInt(repeatIntervalTextField.getText()));
    experimentPreferences.setInteger(
        ShelfLifePreferences.SERIES_R_INIT_KEY,
        Integer.parseInt(seriesResistorTextField.getText()));

    experimentPreferences.setFloat(
        ShelfLifePreferences.READ_VOLTS_INIT_KEY,
        Float.parseFloat(readVoltageAmplitudeTextField.getText()));
    experimentPreferences.setFloat(
        ShelfLifePreferences.WRITE_VOLTS_INIT_KEY,
        Float.parseFloat(writeVoltageAmplitudeTextField.getText()));
    experimentPreferences.setFloat(
        ShelfLifePreferences.ERASE_VOLTS_INIT_KEY,
        Float.parseFloat(eraseVoltageAmplitudeTextField.getText()));

    experimentPreferences.setInteger(
        ShelfLifePreferences.READ_PULSE_WIDTH_INIT_KEY,
        Integer.parseInt(readPulseWidthInMicroSecondsTextField.getText()));
    experimentPreferences.setInteger(
        ShelfLifePreferences.WRITE_PULSE_WIDTH_INIT_KEY,
        Integer.parseInt(writePulseWidthInMicroSecondsTextField.getText()));
    experimentPreferences.setInteger(
        ShelfLifePreferences.ERASE_PULSE_WIDTH_INIT_KEY,
        Integer.parseInt(erasePulseWidthInMicroSecondsTextField.getText()));

    experimentPreferences.setFloat(
        ShelfLifePreferences.MAX_WRITE_RESISTANCE_INIT_KEY,
        Float.parseFloat(maxWriteResistanceTextField.getText()));
    experimentPreferences.setFloat(
        ShelfLifePreferences.MIN_ERASE_RESISTANCE_INIT_KEY,
        Float.parseFloat(minEraseResistanceTextField.getText()));
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new ShelfLifePreferences();
  }
}
