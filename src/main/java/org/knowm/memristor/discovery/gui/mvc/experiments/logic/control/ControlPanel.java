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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.AHaHController_21.AHaHLogicRoutine;
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

  private final ButtonGroup routineRadioButtonGroup;
  private final Box routineRadioButtonBox;

  // private final ButtonGroup inputAButtonGroup;
  private final Box inputAButtonBox;
  private final Box inputBButtonBox;

  // private final ButtonGroup inputBButtonGroup;
  // private final Box inputBButtonBox;

  private final JLabel numExecutionsLabel;
  private final JTextField numExecutionsTextField;

  public JButton runRoutineButton;

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
    // labelTable.put(-250, new JLabel("-2.5"));
    // labelTable.put(-200, new JLabel("-2"));
    // labelTable.put(-100, new JLabel("-1"));
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

    // input A
    inputAButtonBox = Box.createHorizontalBox();
    inputAButtonBox.setBorder(BorderFactory.createTitledBorder("Input A"));

    for (int i = 0; i < 8; i++) {
      JRadioButton radioButton = new JRadioButton("" + (i + 1));
      add(radioButton);
      inputAButtonBox.add(radioButton);
    }

    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(inputAButtonBox, c);

    // input B

    inputBButtonBox = Box.createHorizontalBox();
    inputBButtonBox.setBorder(BorderFactory.createTitledBorder("Input B"));

    for (int i = 0; i < 8; i++) {
      JRadioButton radioButton = new JRadioButton("" + (i + 1));
      add(radioButton);
      inputBButtonBox.add(radioButton);
    }

    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(inputBButtonBox, c);

    // routine
    routineRadioButtonGroup = new ButtonGroup();
    routineRadioButtonBox = Box.createVerticalBox();
    routineRadioButtonBox.setBorder(BorderFactory.createTitledBorder("Routine"));
    for (AHaHLogicRoutine routine : AHaHLogicRoutine.values()) {
      JRadioButton radioButton = new JRadioButton(routine.name());
      routineRadioButtonGroup.add(radioButton);
      add(radioButton);
      routineRadioButtonBox.add(radioButton);
    }

    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(routineRadioButtonBox, c);

    numExecutionsLabel = new JLabel("Number of Executions");
    numExecutionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(numExecutionsLabel, c);

    numExecutionsTextField = new JTextField();
    numExecutionsTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(numExecutionsTextField, c);

    runRoutineButton = new JButton("Go");
    runRoutineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(runRoutineButton, c);

    c.gridy++;
    JLabel logoLabel = new JLabel(Util.createImageIcon("img/logo_200.png"));
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    waveformComboBox.setEnabled(enabled);
    amplitudeSlider.setEnabled(enabled);
    pulseWidthSlider.setEnabled(enabled);
    runRoutineButton.setEnabled(enabled);
    inputAButtonBox.setEnabled(enabled);
    inputAButtonBox.setEnabled(enabled);

    routineRadioButtonBox.setEnabled(enabled);
    Enumeration<AbstractButton> enumeration = routineRadioButtonGroup.getElements();
    while (enumeration.hasMoreElements()) {
      enumeration.nextElement().setEnabled(enabled);
    }
    // startStopButton.setEnabled(enabled);
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

  public JTextField getNumExecutionsTextField() {

    return numExecutionsTextField;
  }

  public ButtonGroup getInstructionRadioButtonGroup() {

    return routineRadioButtonGroup;
  }
}
