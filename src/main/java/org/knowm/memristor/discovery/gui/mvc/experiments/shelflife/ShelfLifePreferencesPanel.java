/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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
    gc.insets = new Insets(10, 10, 10, 10);

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
              saveDirectory = FileUtils.showSaveAsDialog(preferencesPanel);
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
    this.repeatIntervalLabel = new JLabel("Repeat Interval:");
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
    this.seriesResistorLabel = new JLabel("Series Resistor:");
    preferencesPanel.add(seriesResistorLabel, gc);

    gc.gridx = 1;
    this.seriesResistorTextField = new JTextField(12);
    this.seriesResistorTextField.setText(
        String.valueOf(
            experimentPreferences.getInteger(
                ShelfLifePreferences.SERIES_R_INIT_KEY,
                ShelfLifePreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);
  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setString(
        ShelfLifePreferences.SAVE_DIRECTORY_INIT_KEY, saveDirectory.trim());
    experimentPreferences.setString(
        ShelfLifePreferences.TIME_UNIT_INIT_KEY,
        timeUnitComboBox.getSelectedItem().toString().trim());
    experimentPreferences.setInteger(
        ShelfLifePreferences.REPEAT_INTERVAL_INIT_KEY,
        Integer.parseInt(repeatIntervalTextField.getText()));
    experimentPreferences.setInteger(
        ShelfLifePreferences.SERIES_R_INIT_KEY,
        Integer.parseInt(seriesResistorTextField.getText()));
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new ShelfLifePreferences();
  }
}
