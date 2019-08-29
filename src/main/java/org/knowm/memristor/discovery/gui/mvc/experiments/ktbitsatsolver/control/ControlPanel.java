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
package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control;

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

import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;

/**
 * Provides controls for running the control
 *
 * @author alexnugent
 */
public class ControlPanel extends ControlView {

  private final JSlider amplitudeSlider;
  private final JSlider amplitudeReverseSlider;
  private final JSlider pulseWidthSlider;

  public JButton initButton;
  public JButton runTrialButton;
  public JButton clearPlotButton;
  private JComboBox<Waveform> waveformComboBox;

  private final JLabel filePickerLabel;
  private final JButton selectFileButton;
  private final JTextField selectedFileTextField;

  /** Constructor */
  public ControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    c.gridx = 0;
    c.insets = new Insets(0, 0, 30, 6);
    add(new JLabel("REMINDER: SWITCH BOARD TO MODE 2"), c);
    c.gridy++;

    filePickerLabel = new JLabel("Select 3SAT Problem Instance");
    filePickerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(filePickerLabel, c);

    selectFileButton = new JButton("Choose");
    selectFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(selectFileButton, c);

    selectedFileTextField = new JTextField();
    selectedFileTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(selectedFileTextField, c);

    this.waveformComboBox = new JComboBox<>();
    waveformComboBox.setFocusable(false);
    c.gridy++;
    c.insets = new Insets(0, 0, 4, 6);
    add(waveformComboBox, c);

    // Forward Amplitude--->
    amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 1);
    amplitudeSlider.setBorder(BorderFactory.createTitledBorder("Forward Amplitude [V]"));
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
    labelTable.put(200, new JLabel("2.0"));
    labelTable.put(250, new JLabel("2.5"));
    labelTable.put(300, new JLabel("3.0"));

    amplitudeSlider.setLabelTable(labelTable);
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    amplitudeSlider.setPreferredSize(new Dimension(300, 80));
    add(amplitudeSlider, c);

    // Reverse Amplitude--->
    amplitudeReverseSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 1);
    amplitudeReverseSlider.setBorder(BorderFactory.createTitledBorder("Reverse Amplitude [V]"));
    amplitudeReverseSlider.setMajorTickSpacing(50);
    amplitudeReverseSlider.setMinorTickSpacing(10);
    amplitudeReverseSlider.setPaintTicks(true);
    amplitudeReverseSlider.setPaintLabels(true);
    amplitudeReverseSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(0, new JLabel("0"));
    labelTable.put(50, new JLabel(".5"));
    labelTable.put(100, new JLabel("1"));
    labelTable.put(150, new JLabel("1.5"));
    labelTable.put(200, new JLabel("2.0"));
    labelTable.put(250, new JLabel("2.5"));
    labelTable.put(300, new JLabel("3.0"));

    amplitudeReverseSlider.setLabelTable(labelTable);
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    amplitudeReverseSlider.setPreferredSize(new Dimension(300, 80));
    add(amplitudeReverseSlider, c);

    pulseWidthSlider = new JSlider(JSlider.HORIZONTAL, 1000, 200000, 20000);
    pulseWidthSlider.setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
    pulseWidthSlider.setMinorTickSpacing(1000);
    pulseWidthSlider.setPaintTicks(true);
    pulseWidthSlider.setPaintLabels(true);
    pulseWidthSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(1000, new JLabel("1"));
    labelTable.put(50000, new JLabel("50"));
    labelTable.put(100000, new JLabel("100"));
    labelTable.put(150000, new JLabel("150"));
    labelTable.put(200000, new JLabel("200"));
    pulseWidthSlider.setLabelTable(labelTable);
    c.gridy++;
    add(pulseWidthSlider, c);

    clearPlotButton = new JButton("Clear Plot");
    clearPlotButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(clearPlotButton, c);

    initButton = new JButton("Initialize");
    initButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(initButton, c);

    runTrialButton = new JButton("Solve");
    runTrialButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(runTrialButton, c);

    c.gridy++;
    add(logoLabel, c);
  }

  @Override
  public void enableAllChildComponents(boolean enabled) {

    waveformComboBox.setEnabled(enabled);
    amplitudeSlider.setEnabled(enabled);
    amplitudeReverseSlider.setEnabled(enabled);
    pulseWidthSlider.setEnabled(enabled);
    runTrialButton.setEnabled(enabled);
  }

  public JComboBox<Waveform> getWaveformComboBox() {

    return waveformComboBox;
  }

  public JSlider getAmplitudeSlider() {

    return amplitudeSlider;
  }

  public JSlider getAmplitudeReverseSlider() {

    return amplitudeReverseSlider;
  }

  public JSlider getPulseWidthSlider() {

    return pulseWidthSlider;
  }

  public JButton getSelectFileButton() {
    return selectFileButton;
  }

  public JTextField getSelectedFileTextField() {
    return selectedFileTextField;
  }

}
