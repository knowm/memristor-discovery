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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.gui.mvc.experiments.View;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicPreferences.DataStructure;

/**
 * Provides controls for running the control
 *
 * @author alexnugent
 */
public class ControlPanel extends View {

  private final JSlider amplitudeSlider;
  private final JSlider pulseWidthSlider;
  private final Box inputAButtonBox;

  // private final ButtonGroup routineRadioButtonGroup;
  // private final Box routineRadioButtonBox;
  private final Box inputBButtonBox;
  private final Box biasButtonBox;
  private final JLabel numExecutionsLabel;
  private final JTextField numExecutionsTextField;
  public JButton FFRUButton;
  public JButton FFRAButton;
  public JButton resetAllButton;
  public JButton runTrialsButton;
  public JButton clearPlotButton;
  private JComboBox<Waveform> waveformComboBox;
  private JComboBox<DataStructure> dataStructureComboBox;
  private List<JRadioButton> inputAMaskRadioButtons;
  private List<JRadioButton> inputBMaskRadioButtons;
  private List<JRadioButton> biasMaskRadioButtons;

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

    // data
    this.dataStructureComboBox = new JComboBox<>();
    dataStructureComboBox.setFocusable(false);
    c.gridy++;
    c.insets = new Insets(0, 0, 4, 6);
    add(dataStructureComboBox, c);

    // Input A
    inputAButtonBox = Box.createHorizontalBox();
    inputAButtonBox.setBorder(BorderFactory.createTitledBorder("Input A Mask"));
    inputAMaskRadioButtons = new ArrayList<JRadioButton>();
    for (int i = 0; i < 8; i++) {
      JRadioButton radioButton = new JRadioButton("" + (i + 1));
      add(radioButton);
      inputAButtonBox.add(radioButton);
      inputAMaskRadioButtons.add(radioButton);
    }
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(inputAButtonBox, c);

    // Input B
    inputBButtonBox = Box.createHorizontalBox();
    inputBButtonBox.setBorder(BorderFactory.createTitledBorder("Input B Mask"));
    inputBMaskRadioButtons = new ArrayList<JRadioButton>();
    for (int i = 0; i < 8; i++) {
      JRadioButton radioButton = new JRadioButton("" + (i + 1));
      add(radioButton);
      inputBButtonBox.add(radioButton);
      inputBMaskRadioButtons.add(radioButton);
    }
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(inputBButtonBox, c);

    // Bias B
    biasButtonBox = Box.createHorizontalBox();
    biasButtonBox.setBorder(BorderFactory.createTitledBorder("Input Bias Mask"));
    biasMaskRadioButtons = new ArrayList<JRadioButton>();
    for (int i = 0; i < 8; i++) {
      JRadioButton radioButton = new JRadioButton("" + (i + 1));
      add(radioButton);
      biasButtonBox.add(radioButton);
      biasMaskRadioButtons.add(radioButton);
    }
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(biasButtonBox, c);

    numExecutionsLabel = new JLabel("NumExecutions (NE)");
    numExecutionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(numExecutionsLabel, c);

    numExecutionsTextField = new JTextField();
    numExecutionsTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(numExecutionsTextField, c);

    FFRUButton = new JButton("FFRU X NE");
    FFRUButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(FFRUButton, c);

    FFRAButton = new JButton("FFRA X NE");
    FFRAButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(FFRAButton, c);

    resetAllButton = new JButton("Reset");
    resetAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(resetAllButton, c);

    runTrialsButton = new JButton("Reset->[FFRU X NE] X25");
    runTrialsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(runTrialsButton, c);

    clearPlotButton = new JButton("Clear Plot");
    clearPlotButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(clearPlotButton, c);

    c.gridy++;
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    waveformComboBox.setEnabled(enabled);
    amplitudeSlider.setEnabled(enabled);
    pulseWidthSlider.setEnabled(enabled);
    FFRUButton.setEnabled(enabled);
    FFRAButton.setEnabled(enabled);
    runTrialsButton.setEnabled(enabled);
    clearPlotButton.setEnabled(enabled);
    inputAButtonBox.setEnabled(enabled);
    inputBButtonBox.setEnabled(enabled);
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

  // public ButtonGroup getInstructionRadioButtonGroup() {
  //
  // return routineRadioButtonGroup;
  // }

  public List<JRadioButton> getInputAMaskRadioButtons() {

    return inputAMaskRadioButtons;
  }

  public List<JRadioButton> getInputBMaskRadioButtons() {

    return inputBMaskRadioButtons;
  }

  public JComboBox<DataStructure> getDataStructureComboBox() {

    return dataStructureComboBox;
  }

  public List<JRadioButton> getBiasMaskRadioButtons() {

    return biasMaskRadioButtons;
  }

  public JButton getFFRAButton() {

    return FFRAButton;
  }
}
