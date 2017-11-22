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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.ClassifyPreferences.Datasets;
import org.knowm.memristor.discovery.utils.Util;

/**
 * Provides controls for running the control
 *
 * @author alexnugent
 */
public class ControlPanel extends ExperimentControlPanel {

  private JComboBox<Waveform> waveformComboBox;

  private final JSlider amplitudeSlider;
  private final JSlider pulseWidthSlider;

  private JComboBox<Datasets> datasetComboBox;

  private final JLabel numTrainEpochsLabel;
  private final JTextField numTrainEpochsTextField;

  public JButton resetAllButton;
  public JButton runTrialButton;

  /**
   * Constructor
   */
  public ControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    c.gridx = 0;

    this.waveformComboBox = new JComboBox<>();
    waveformComboBox.setFocusable(false);
    c.gridy++;
    c.insets = new Insets(0, 0, 4, 6);
    add(waveformComboBox, c);

    amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 150, 12);
    amplitudeSlider.setBorder(BorderFactory.createTitledBorder("Amplitude [V]"));
    amplitudeSlider.setMajorTickSpacing(50);
    amplitudeSlider.setMinorTickSpacing(10);
    amplitudeSlider.setPaintTicks(true);
    amplitudeSlider.setPaintLabels(true);
    amplitudeSlider.setSnapToTicks(true);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
    labelTable.put(0, new JLabel("0"));
    labelTable.put(50, new JLabel(".5"));
    labelTable.put(100, new JLabel("1"));
    labelTable.put(150, new JLabel("1.5"));
    amplitudeSlider.setLabelTable(labelTable);
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    amplitudeSlider.setPreferredSize(new Dimension(300, 80));
    add(amplitudeSlider, c);

    pulseWidthSlider = new JSlider(JSlider.HORIZONTAL, 1000, 500000, 100000);
    pulseWidthSlider.setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
    pulseWidthSlider.setMinorTickSpacing(5000);
    pulseWidthSlider.setPaintTicks(true);
    pulseWidthSlider.setPaintLabels(true);
    pulseWidthSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(1000, new JLabel("1"));
    labelTable.put(250000, new JLabel("250"));
    labelTable.put(500000, new JLabel("500"));
    pulseWidthSlider.setLabelTable(labelTable);
    c.gridy++;
    add(pulseWidthSlider, c);
    c.gridy++;

    // data
    this.datasetComboBox = new JComboBox<>();
    datasetComboBox.setFocusable(false);
    c.gridy++;
    c.insets = new Insets(0, 0, 4, 6);
    add(datasetComboBox, c);

    numTrainEpochsLabel = new JLabel("NumExecutions (NE)");
    numTrainEpochsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(numTrainEpochsLabel, c);

    numTrainEpochsTextField = new JTextField();
    numTrainEpochsTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(numTrainEpochsTextField, c);

    resetAllButton = new JButton("Reset");
    resetAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(resetAllButton, c);

    runTrialButton = new JButton("Run");
    runTrialButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(runTrialButton, c);

    c.gridy++;
    JLabel logoLabel = new JLabel(Util.createImageIcon("img/logo_200.png"));
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    waveformComboBox.setEnabled(enabled);
    amplitudeSlider.setEnabled(enabled);
    pulseWidthSlider.setEnabled(enabled);
    runTrialButton.setEnabled(enabled);
  }

  public JComboBox<Waveform> getWaveformComboBox() {

    return waveformComboBox;
  }

  public JSlider getAmplitudeSlider() {

    return amplitudeSlider;
  }

  public JSlider getPulseWidthSlider() {

    return pulseWidthSlider;
  }

  public JTextField getNumTrainEpochsTextField() {

    return numTrainEpochsTextField;
  }

  public JComboBox<Datasets> getDatasetComboBox() {

    return datasetComboBox;
  }

}
