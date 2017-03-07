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
package org.knowm.memristor.discovery.gui.mvc.experiments.qc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * Provides controls for running the control
 *
 * @author timmolter
 */
public class QCControlPanel extends JPanel {

  private final JLabel memristorTypeLabel;
  private final ButtonGroup memristorTypeButtonGroup;
  private final JRadioButton wTypeMemristor;
  private final JRadioButton snTypeMemristor;
  private final JRadioButton crTypeMemristor;
  private final JRadioButton cTypeMemristor;

  private final JLabel serialNumberLabel;
  private final JTextField serialNumberTextField;

  private final JLabel reportPathLabel;
  private final JTextField reportPathTextField;

  private final JSlider amplitudeSlider;
  private final JSlider frequencySlider;
  private final JSlider offsetSlider;

  private final JLabel seriesLabel;
  private final JTextField seriesTextField;

  private final JButton startButton;
  private final JButton stopButton;

  /**
   * Constructor
   */
  public QCControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    // setBackground(Color.yellow);

    memristorTypeLabel = new JLabel("Memristor Type");
    memristorTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(memristorTypeLabel, c);

    wTypeMemristor = new JRadioButton("BS-AF-W");
    wTypeMemristor.setSelected(true);
    snTypeMemristor = new JRadioButton("BS-AF-Sn");
    crTypeMemristor = new JRadioButton("BS-AF-Cr");
    cTypeMemristor = new JRadioButton("BS-AF-C");

    memristorTypeButtonGroup = new ButtonGroup();
    memristorTypeButtonGroup.add(wTypeMemristor);
    memristorTypeButtonGroup.add(snTypeMemristor);
    memristorTypeButtonGroup.add(crTypeMemristor);
    memristorTypeButtonGroup.add(cTypeMemristor);

    c.gridy++;
    // c.gridx++;
    // c.insets = new Insets(1, 1, 1, 1);
    add(wTypeMemristor, c);
    c.gridy++;
    add(snTypeMemristor, c);
    c.gridy++;
    add(crTypeMemristor, c);
    c.gridy++;
    add(cTypeMemristor, c);
    // c.fill = GridBagConstraints.HORIZONTAL;

    serialNumberLabel = new JLabel("Serial Number");
    serialNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(serialNumberLabel, c);

    serialNumberTextField = new JTextField();
    serialNumberTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(serialNumberTextField, c);

    reportPathLabel = new JLabel("Report Directory");
    reportPathLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(reportPathLabel, c);

    reportPathTextField = new JTextField();
    reportPathTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(reportPathTextField, c);

    c.insets = new Insets(0, 0, 0, 0);

    amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 0);
    amplitudeSlider.setBorder(BorderFactory.createTitledBorder("Amplitude [V]"));
    amplitudeSlider.setMajorTickSpacing(100);
    amplitudeSlider.setMinorTickSpacing(25);
    amplitudeSlider.setPaintTicks(true);
    amplitudeSlider.setPaintLabels(true);
    amplitudeSlider.setSnapToTicks(true);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
    labelTable.put(new Integer(0), new JLabel("0"));
    labelTable.put(new Integer(100), new JLabel("1"));
    labelTable.put(new Integer(200), new JLabel("2"));
    labelTable.put(new Integer(300), new JLabel("3"));
    labelTable.put(new Integer(400), new JLabel("4"));
    labelTable.put(new Integer(500), new JLabel("5"));
    amplitudeSlider.setLabelTable(labelTable);
    amplitudeSlider.setPreferredSize(new Dimension(300, 80));
    c.gridy++;
    add(amplitudeSlider, c);

    offsetSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
    offsetSlider.setBorder(BorderFactory.createTitledBorder("Offset [V]"));
    offsetSlider.setMajorTickSpacing(20);
    offsetSlider.setMinorTickSpacing(5);
    offsetSlider.setPaintTicks(true);
    offsetSlider.setPaintLabels(true);
    offsetSlider.setSnapToTicks(true);

    Hashtable<Integer, JLabel> labelTable2 = new Hashtable<>();
    labelTable2.put(new Integer(0), new JLabel("0"));
    labelTable2.put(new Integer(20), new JLabel(".2"));
    labelTable2.put(new Integer(40), new JLabel(".4"));
    labelTable2.put(new Integer(60), new JLabel(".6"));
    labelTable2.put(new Integer(80), new JLabel(".8"));
    labelTable2.put(new Integer(100), new JLabel("1"));
    labelTable2.put(new Integer(-20), new JLabel("-.2"));
    labelTable2.put(new Integer(-40), new JLabel("-.4"));
    labelTable2.put(new Integer(-60), new JLabel("-.6"));
    labelTable2.put(new Integer(-80), new JLabel("-.8"));
    labelTable2.put(new Integer(-100), new JLabel("-1"));
    offsetSlider.setLabelTable(labelTable2);

    c.gridy++;
    add(offsetSlider, c);

    frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
    frequencySlider.setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
    frequencySlider.setMajorTickSpacing(20);
    frequencySlider.setMinorTickSpacing(5);
    frequencySlider.setPaintTicks(true);
    frequencySlider.setPaintLabels(true);
    frequencySlider.setSnapToTicks(true);

    c.gridy++;
    add(frequencySlider, c);

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

    startButton = new JButton("Start");
    startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    // startButton.setSize(128, 28);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(startButton, c);

    stopButton = new JButton("Stop");
    stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;

    add(stopButton, c);

  }

  public void enableAllChildComponents(boolean enabled) {

    amplitudeSlider.setEnabled(enabled);
    frequencySlider.setEnabled(enabled);
    seriesTextField.setEnabled(enabled);
    startButton.setEnabled(enabled);
    stopButton.setEnabled(false);
  }

  public JTextField getReportPathTextField() {

    return reportPathTextField;
  }

  public JTextField getSerialNumberTextField() {

    return serialNumberTextField;
  }

  public JSlider getAmplitudeSlider() {

    return amplitudeSlider;
  }

  /**
   * @return the offsetSlider
   */
  public JSlider getOffsetSlider() {

    return offsetSlider;
  }

  public JSlider getFrequencySlider() {

    return frequencySlider;
  }

  public JTextField getSeriesTextField() {

    return seriesTextField;
  }

  public JButton getStartButton() {

    return startButton;
  }

  public JButton getStopButton() {

    return stopButton;
  }

  /**
   * @return the wTypeMemristor
   */
  public JRadioButton getwTypeMemristor() {

    return wTypeMemristor;
  }

  /**
   * @return the snTypeMemristor
   */
  public JRadioButton getSnTypeMemristor() {

    return snTypeMemristor;
  }

  /**
   * @return the crTypeMemristor
   */
  public JRadioButton getCrTypeMemristor() {

    return crTypeMemristor;
  }

  /**
   * @return the cTypeMemristor
   */
  public JRadioButton getcTypeMemristor() {

    return cTypeMemristor;
  }

}
