/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control;

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

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.utils.Util;

/**
 * Provides controls for running the control
 *
 * @author timmolter
 */
public class ControlPanel extends ExperimentControlPanel {

  private final Box waveformRadioButtonBox;
  private final ButtonGroup waveformRadioButtonGroup;
  private final JRadioButton sineRadioButton;
  private final JRadioButton triangleRadioButton;
  private final JRadioButton squareRadioButton;

  private final JSlider offsetSlider;
  private final JSlider amplitudeSlider;
  private final JSlider frequencySlider;
  private final JSlider frequencySliderLog;

  private final JLabel seriesLabel;
  private final JTextField seriesTextField;

  /**
   * Constructor
   */
  public ControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    // setBackground(Color.yellow);

    sineRadioButton = new JRadioButton("Sine");
    triangleRadioButton = new JRadioButton("Triangle");
    squareRadioButton = new JRadioButton("Square");
    waveformRadioButtonGroup = new ButtonGroup();
    waveformRadioButtonGroup.add(sineRadioButton);
    waveformRadioButtonGroup.add(triangleRadioButton);
    // waveformRadioButtonGroup.add(squareRadioButton);
    add(sineRadioButton);
    add(triangleRadioButton);
    // add(squareRadioButton);
    waveformRadioButtonBox = Box.createVerticalBox();
    waveformRadioButtonBox.setBorder(BorderFactory.createTitledBorder("Waveform"));
    waveformRadioButtonBox.add(sineRadioButton);
    waveformRadioButtonBox.add(triangleRadioButton);
    // waveformRadioButtonBox.add(squareRadioButton);
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(waveformRadioButtonBox, c);

    offsetSlider = new JSlider(JSlider.HORIZONTAL, -200, 100, 0);
    offsetSlider.setBorder(BorderFactory.createTitledBorder("Offset [V]"));
    offsetSlider.setMajorTickSpacing(25);
    offsetSlider.setMinorTickSpacing(5);
    offsetSlider.setPaintTicks(true);
    offsetSlider.setPaintLabels(true);
    offsetSlider.setSnapToTicks(true);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
    labelTable.put(-200, new JLabel("-2"));
    labelTable.put(-150, new JLabel("-1.5"));
    labelTable.put(-100, new JLabel("-1"));
    labelTable.put(-50, new JLabel("-.5"));
    labelTable.put(0, new JLabel("0"));
    labelTable.put(50, new JLabel(".5"));
    labelTable.put(100, new JLabel("1"));
    offsetSlider.setLabelTable(labelTable);
    offsetSlider.setPreferredSize(new Dimension(300, 80));
    c.gridy++;
    add(offsetSlider, c);

    amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    amplitudeSlider.setBorder(BorderFactory.createTitledBorder("Amplitude [V]"));
    amplitudeSlider.setMajorTickSpacing(10);
    amplitudeSlider.setMinorTickSpacing(2);
    amplitudeSlider.setPaintTicks(true);
    amplitudeSlider.setPaintLabels(true);
    amplitudeSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(0, new JLabel("0"));
    labelTable.put(20, new JLabel(".2"));
    labelTable.put(40, new JLabel(".4"));
    labelTable.put(60, new JLabel(".6"));
    labelTable.put(80, new JLabel(".8"));
    labelTable.put(100, new JLabel("1"));
    amplitudeSlider.setLabelTable(labelTable);
    c.gridy++;
    add(amplitudeSlider, c);

    frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
    frequencySlider.setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
    frequencySlider.setMajorTickSpacing(20);
    frequencySlider.setMinorTickSpacing(5);
    frequencySlider.setPaintTicks(true);
    frequencySlider.setPaintLabels(true);
    frequencySlider.setSnapToTicks(true);
    c.gridy++;
    add(frequencySlider, c);

    frequencySliderLog = new JSlider(JSlider.HORIZONTAL, 0, 4, 0);
    frequencySliderLog.setBorder(BorderFactory.createTitledBorder("Frequency (Log) [Hz]"));
    frequencySliderLog.setMajorTickSpacing(1);
    // frequencySlider.setMinorTickSpacing(5);
    frequencySliderLog.setPaintTicks(true);
    frequencySliderLog.setPaintLabels(true);
    frequencySliderLog.setSnapToTicks(true);
    c.gridy++;
    add(frequencySliderLog, c);

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
    JLabel logoLabel = new JLabel(Util.createImageIcon("img/logo_200.png"));
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    sineRadioButton.setEnabled(enabled);
    triangleRadioButton.setEnabled(enabled);
    squareRadioButton.setEnabled(enabled);
    offsetSlider.setEnabled(enabled);
    amplitudeSlider.setEnabled(enabled);
    frequencySlider.setEnabled(enabled);
    frequencySliderLog.setEnabled(enabled);
    seriesTextField.setEnabled(enabled);
    startStopButton.setEnabled(enabled);
  }

  public ButtonGroup getWaveformRadioButtonGroup() {

    return waveformRadioButtonGroup;
  }

  public JRadioButton getSineRadioButton() {

    return sineRadioButton;
  }

  public JRadioButton getTriangleRadioButton() {

    return triangleRadioButton;
  }

  public JRadioButton getSquareRadioButton() {

    return squareRadioButton;
  }

  public JSlider getOffsetSlider() {

    return offsetSlider;
  }

  public JSlider getAmplitudeSlider() {

    return amplitudeSlider;
  }

  public JSlider getFrequencySlider() {

    return frequencySlider;
  }

  public JSlider getFrequencySliderLog() {

    return frequencySliderLog;
  }

  public JTextField getSeriesTextField() {

    return seriesTextField;
  }
}
