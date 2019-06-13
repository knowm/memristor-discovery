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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.control;

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
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.AHaHController_21.Instruction21;

/**
 * Provides controls for running the control
 *
 * @author timmolter
 */
public class ControlPanel extends ControlView {

  private final JSlider amplitudeSlider;
  private final JSlider pulseWidthSlider;
  private final ButtonGroup instructionRadioButtonGroup;
  // private final JSlider pulseWidthSliderNs;

  // private final JSlider pulseNumberSlider;
  private final Box instructionRadioButtonBox;
  private final JLabel sampleRateLabel;;
  private final JTextField sampleRateTextField;
  public JButton clearPlotButton;
  public JButton initSynapseButton;
  private JComboBox<Waveform> waveformComboBox;

  public final JButton startStopButton;

  /** Constructor */
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

    amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 1);
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
    labelTable.put(200, new JLabel("2.0"));
    labelTable.put(250, new JLabel("2.5"));
    labelTable.put(300, new JLabel("3.0"));

    amplitudeSlider.setLabelTable(labelTable);
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    amplitudeSlider.setPreferredSize(new Dimension(300, 80));
    add(amplitudeSlider, c);

    pulseWidthSlider = new JSlider(JSlider.HORIZONTAL, 1000, 500_000, 1_000);
    pulseWidthSlider.setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
    pulseWidthSlider.setMinorTickSpacing(10000);
    pulseWidthSlider.setPaintTicks(true);
    pulseWidthSlider.setPaintLabels(true);
    pulseWidthSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(1000, new JLabel("1"));
    labelTable.put(100_000, new JLabel("100"));
    labelTable.put(200_000, new JLabel("200"));
    labelTable.put(300_000, new JLabel("300"));
    labelTable.put(400_000, new JLabel("400"));
    labelTable.put(500_000, new JLabel("500"));
    pulseWidthSlider.setLabelTable(labelTable);
    c.gridy++;
    add(pulseWidthSlider, c);

    c.gridy++;
    instructionRadioButtonGroup = new ButtonGroup();
    instructionRadioButtonBox = Box.createVerticalBox();
    instructionRadioButtonBox.setBorder(BorderFactory.createTitledBorder("Instruction"));
    for (Instruction21 instr : Instruction21.values()) {

      JRadioButton radioButton = new JRadioButton(instr.name());
      instructionRadioButtonGroup.add(radioButton);
      add(radioButton);
      instructionRadioButtonBox.add(radioButton);
    }
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(instructionRadioButtonBox, c);

    sampleRateLabel = new JLabel("Sample Rate [s]");
    sampleRateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(sampleRateLabel, c);

    sampleRateTextField = new JTextField();
    sampleRateTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(sampleRateTextField, c);

    initSynapseButton = new JButton("Initialize Synapse");
    initSynapseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(initSynapseButton, c);

    clearPlotButton = new JButton("Clear Plot");
    clearPlotButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(clearPlotButton, c);

    startStopButton = new JButton("Start");
    startStopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(startStopButton, c);

    c.gridy++;
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    waveformComboBox.setEnabled(enabled);
    amplitudeSlider.setEnabled(enabled);
    pulseWidthSlider.setEnabled(enabled);
    // pulseWidthSliderNs.setEnabled(enabled);
    // pulseNumberSlider.setEnabled(enabled);
    instructionRadioButtonBox.setEnabled(enabled);
    Enumeration<AbstractButton> enumeration = instructionRadioButtonGroup.getElements();
    while (enumeration.hasMoreElements()) {
      enumeration.nextElement().setEnabled(enabled);
    }
    startStopButton.setEnabled(enabled);
    initSynapseButton.setEnabled(enabled);
    clearPlotButton.setEnabled(enabled);
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

  // public JSlider getPulseWidthSliderNs() {
  //
  // return pulseWidthSliderNs;
  // }

  public JTextField getSampleRateTextField() {

    return sampleRateTextField;
  }

  public ButtonGroup getInstructionRadioButtonGroup() {

    return instructionRadioButtonGroup;
  }

  public JButton getStartStopButton() {
    return startStopButton;
  }
}
