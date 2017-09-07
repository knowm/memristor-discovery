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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;

public class BoardCheckPreferencesPanel extends ExperimentPreferencesPanel {

  private JLabel seriesResistorLabel;
  private JTextField seriesResistorTextField;

  // private JLabel amplitudeLabel;
  // private JTextField amplitudeTextField;
  //
  // private JLabel pulseWidthLabel;
  // private JTextField pulseWidthTextField;
  //
  // private JLabel sampleRateLabel;
  // private JTextField sampleRateTextField;

  /**
   * Constructor
   *
   * @param owner
   */
  public BoardCheckPreferencesPanel(JFrame owner) {

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
    this.seriesResistorTextField.setText(String.valueOf(experimentPreferences.getInteger(BoardCheckPreferences.SERIES_R_INIT_KEY, BoardCheckPreferences.SERIES_R_INIT_DEFAULT_VALUE)));
    preferencesPanel.add(seriesResistorTextField, gc);

    gc.gridy++;

  }

  @Override
  public void doSavePreferences() {

    experimentPreferences.setInteger(BoardCheckPreferences.SERIES_R_INIT_KEY, Integer.parseInt(seriesResistorTextField.getText()));

  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new BoardCheckPreferences();
  }

  @Override
  public String getAppName() {

    return "Pulse";
  }
}
