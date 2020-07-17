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
package org.knowm.memristor.discovery.gui.mvc.experiments.dc.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCPreferences;

/** Provides controls for running the control */
public class ControlPanel extends ControlView {

  private final Box waveformRadioButtonBox;
  private final ButtonGroup waveformRadioButtonGroup;
  private final JRadioButton sawToothRadioButton;
  private final JRadioButton sawtoothUpDownRadioButton;
  private final JRadioButton triangleRadioButton;
  private final JRadioButton triangleUpDownRadioButton;

  private final JSlider amplitudeSlider;
  private final JSlider periodSlider;
  private final JSlider periodSliderNs;

  private final JLabel seriesLabel;
  private final JTextField seriesTextField;

  private final JSlider pulseNumberSlider;

  public final JButton startStopButton;

  /** Constructor */
  public ControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    // setBackground(Color.yellow);

    sawToothRadioButton = new JRadioButton("Sawtooth");
    sawtoothUpDownRadioButton = new JRadioButton("SawtoothUpDown");
    triangleRadioButton = new JRadioButton("Triangle");
    triangleUpDownRadioButton = new JRadioButton("TriangleUpDown");
    waveformRadioButtonGroup = new ButtonGroup();
    waveformRadioButtonGroup.add(sawToothRadioButton);
    waveformRadioButtonGroup.add(sawtoothUpDownRadioButton);
    waveformRadioButtonGroup.add(triangleRadioButton);
    waveformRadioButtonGroup.add(triangleUpDownRadioButton);
    add(sawToothRadioButton);
    add(sawtoothUpDownRadioButton);
    add(triangleRadioButton);
    add(triangleUpDownRadioButton);
    waveformRadioButtonBox = Box.createVerticalBox();
    waveformRadioButtonBox.setBorder(BorderFactory.createTitledBorder("Waveform"));
    waveformRadioButtonBox.add(sawToothRadioButton);
    waveformRadioButtonBox.add(sawtoothUpDownRadioButton);
    waveformRadioButtonBox.add(triangleRadioButton);
    waveformRadioButtonBox.add(triangleUpDownRadioButton);
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(waveformRadioButtonBox, c);

    amplitudeSlider = new JSlider(JSlider.HORIZONTAL, -300, 300, 0);
    amplitudeSlider.setBorder(BorderFactory.createTitledBorder("Amplitude [V]"));
    amplitudeSlider.setMajorTickSpacing(50);
    amplitudeSlider.setMinorTickSpacing(10);
    amplitudeSlider.setPaintTicks(true);
    amplitudeSlider.setPaintLabels(true);
    amplitudeSlider.setSnapToTicks(true);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
    labelTable.put(-300, new JLabel("-3"));
    labelTable.put(-200, new JLabel("-2"));
    labelTable.put(-100, new JLabel("-1"));
    labelTable.put(0, new JLabel("0"));
    labelTable.put(100, new JLabel("1"));
    labelTable.put(200, new JLabel("2"));
    labelTable.put(300, new JLabel("3"));
    amplitudeSlider.setLabelTable(labelTable);
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    amplitudeSlider.setPreferredSize(new Dimension(300, 80));
    add(amplitudeSlider, c);

    periodSlider = new JSlider(JSlider.HORIZONTAL, 100, 1000, 100);
    periodSlider.setBorder(
        BorderFactory.createTitledBorder("Period [" + DCPreferences.TIME_UNIT.getLabel() + "]"));
    periodSlider.setMinorTickSpacing(100);
    periodSlider.setPaintTicks(true);
    periodSlider.setPaintLabels(true);
    periodSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(100, new JLabel("100"));
    labelTable.put(500, new JLabel("500"));
    labelTable.put(1000, new JLabel("1000"));
    periodSlider.setLabelTable(labelTable);
    c.gridy++;
    add(periodSlider, c);

    periodSliderNs = new JSlider(JSlider.HORIZONTAL, 10, 100, 100);
    periodSliderNs.setBorder(
        BorderFactory.createTitledBorder("Period [" + DCPreferences.TIME_UNIT.getLabel() + "]"));
    periodSliderNs.setMinorTickSpacing(10);
    periodSliderNs.setPaintTicks(true);
    periodSliderNs.setPaintLabels(true);
    periodSliderNs.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(10, new JLabel("10"));
    labelTable.put(50, new JLabel("50"));
    labelTable.put(100, new JLabel("100"));
    periodSliderNs.setLabelTable(labelTable);
    c.gridy++;
    add(periodSliderNs, c);

    pulseNumberSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
    pulseNumberSlider.setBorder(BorderFactory.createTitledBorder("Pulse Number"));
    pulseNumberSlider.setMinorTickSpacing(1);
    pulseNumberSlider.setPaintTicks(true);
    pulseNumberSlider.setPaintLabels(true);
    pulseNumberSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(1, new JLabel("1"));
    labelTable.put(5, new JLabel("5"));
    labelTable.put(10, new JLabel("10"));
    pulseNumberSlider.setLabelTable(labelTable);
    c.gridy++;
    add(pulseNumberSlider, c);

    seriesLabel = new JLabel("Series R [Ohm]");
    seriesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(seriesLabel, c);

    seriesTextField = new JTextField();
    seriesTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(seriesTextField, c);

    startStopButton = new JButton("Start");
    startStopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(startStopButton, c);

    c.gridy++;
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    sawToothRadioButton.setEnabled(enabled);
    sawtoothUpDownRadioButton.setEnabled(enabled);
    triangleRadioButton.setEnabled(enabled);
    triangleUpDownRadioButton.setEnabled(enabled);
    amplitudeSlider.setEnabled(enabled);
    periodSlider.setEnabled(enabled);
    periodSliderNs.setEnabled(enabled);
    pulseNumberSlider.setEnabled(enabled);
    seriesTextField.setEnabled(enabled);
    startStopButton.setEnabled(enabled);
  }

  public ButtonGroup getWaveformRadioButtonGroup() {

    return waveformRadioButtonGroup;
  }

  public JRadioButton getSawtoothUpDownRadioButton() {

    return sawtoothUpDownRadioButton;
  }

  public JRadioButton getTriangleRadioButton() {

    return triangleRadioButton;
  }

  public JRadioButton getTriangleUpDownRadioButton() {

    return triangleUpDownRadioButton;
  }

  public JRadioButton getSawToothRadioButton() {

    return sawToothRadioButton;
  }

  public JSlider getAmplitudeSlider() {

    return amplitudeSlider;
  }

  public JSlider getPeriodSlider() {

    return periodSlider;
  }

  public JSlider getPeriodSliderNs() {

    return periodSliderNs;
  }

  public JSlider getPulseNumberSlider() {

    return pulseNumberSlider;
  }

  public JTextField getSeriesTextField() {

    return seriesTextField;
  }

  public JButton getStartStopButton() {
    return startStopButton;
  }
}
