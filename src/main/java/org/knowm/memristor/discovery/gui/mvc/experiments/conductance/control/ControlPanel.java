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
package org.knowm.memristor.discovery.gui.mvc.experiments.conductance.control;

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

  // RESET
  private final Box resetPulseTypeRadioButtonBox;
  private final ButtonGroup resetPulseTypeRadioButtonGroup;
  private final JRadioButton sawToothRadioButton;
  private final JRadioButton squareRadioButton;
  private final JSlider resetAmplitudeSlider;
  private final JSlider resetPulseWidthSlider;
  private final JButton resetButton;

  // SET

  private final JLabel seriesLabel;
  private final JTextField seriesTextField;
  private final JSlider setAmplitudeSlider;
  private final JSlider setPulseWidthSlider;

  private final JSlider setConductanceSlider;

  /** Constructor */
  public ControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    // RESET

    sawToothRadioButton = new JRadioButton("Sawtooth");
    squareRadioButton = new JRadioButton("Triangle");
    resetPulseTypeRadioButtonGroup = new ButtonGroup();
    resetPulseTypeRadioButtonGroup.add(sawToothRadioButton);
    resetPulseTypeRadioButtonGroup.add(squareRadioButton);
    add(sawToothRadioButton);
    add(squareRadioButton);
    resetPulseTypeRadioButtonBox = Box.createVerticalBox();
    resetPulseTypeRadioButtonBox.setBorder(BorderFactory.createTitledBorder("Reset Pulse Type"));
    resetPulseTypeRadioButtonBox.add(sawToothRadioButton);
    resetPulseTypeRadioButtonBox.add(squareRadioButton);
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    add(resetPulseTypeRadioButtonBox, c);

    resetAmplitudeSlider = new JSlider(JSlider.HORIZONTAL, -300, 0, 0);
    resetAmplitudeSlider.setBorder(BorderFactory.createTitledBorder("Reset Amplitude [V]"));
    resetAmplitudeSlider.setMajorTickSpacing(50);
    resetAmplitudeSlider.setMinorTickSpacing(10);
    resetAmplitudeSlider.setPaintTicks(true);
    resetAmplitudeSlider.setPaintLabels(true);
    resetAmplitudeSlider.setSnapToTicks(true);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
    labelTable.put(-300, new JLabel("-3"));
    labelTable.put(-200, new JLabel("-2"));
    labelTable.put(-100, new JLabel("-1"));
    labelTable.put(0, new JLabel("0"));
    resetAmplitudeSlider.setLabelTable(labelTable);
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    resetAmplitudeSlider.setPreferredSize(new Dimension(300, 80));
    add(resetAmplitudeSlider, c);

    resetPulseWidthSlider = new JSlider(JSlider.HORIZONTAL, 5000, 100000, 5000);
    resetPulseWidthSlider.setBorder(BorderFactory.createTitledBorder("Reset Pulse Width [µs]"));
    resetPulseWidthSlider.setMinorTickSpacing(5000);
    resetPulseWidthSlider.setPaintTicks(true);
    resetPulseWidthSlider.setPaintLabels(true);
    resetPulseWidthSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(5000, new JLabel("5"));
    labelTable.put(50000, new JLabel("50"));
    labelTable.put(100000, new JLabel("100"));
    resetPulseWidthSlider.setLabelTable(labelTable);
    c.gridy++;
    add(resetPulseWidthSlider, c);

    resetButton = new JButton("Reset");
    resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(resetButton, c);

    // SET

    setConductanceSlider = new JSlider(JSlider.HORIZONTAL, 100, 1500, 1000);
    setConductanceSlider.setBorder(BorderFactory.createTitledBorder("Set Conductance [mS]"));
    setConductanceSlider.setMinorTickSpacing(100);
    setConductanceSlider.setPaintTicks(true);
    setConductanceSlider.setPaintLabels(true);
    setConductanceSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(100, new JLabel(".1"));
    labelTable.put(500, new JLabel(".5"));
    labelTable.put(1000, new JLabel("1.0"));
    labelTable.put(1500, new JLabel("1.5"));
    setConductanceSlider.setLabelTable(labelTable);
    c.gridy++;
    c.insets = new Insets(20, 0, 0, 0);
    add(setConductanceSlider, c);

    setAmplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 0);
    setAmplitudeSlider.setBorder(BorderFactory.createTitledBorder("Reset Amplitude [V]"));
    setAmplitudeSlider.setMajorTickSpacing(50);
    setAmplitudeSlider.setMinorTickSpacing(10);
    setAmplitudeSlider.setPaintTicks(true);
    setAmplitudeSlider.setPaintLabels(true);
    setAmplitudeSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(0, new JLabel("0"));
    labelTable.put(100, new JLabel("1"));
    labelTable.put(200, new JLabel("2"));
    labelTable.put(300, new JLabel("3"));
    setAmplitudeSlider.setLabelTable(labelTable);
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    setAmplitudeSlider.setPreferredSize(new Dimension(300, 80));
    add(setAmplitudeSlider, c);

    setPulseWidthSlider = new JSlider(JSlider.HORIZONTAL, 5000, 100000, 5000);
    setPulseWidthSlider.setBorder(BorderFactory.createTitledBorder("Reset Pulse Width [µs]"));
    setPulseWidthSlider.setMinorTickSpacing(5000);
    setPulseWidthSlider.setPaintTicks(true);
    setPulseWidthSlider.setPaintLabels(true);
    setPulseWidthSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(5000, new JLabel("5"));
    labelTable.put(50000, new JLabel("50"));
    labelTable.put(100000, new JLabel("100"));
    setPulseWidthSlider.setLabelTable(labelTable);
    c.gridy++;
    add(setPulseWidthSlider, c);

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
    // startButton.setSize(128, 28);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(startStopButton, c);

    c.gridy++;
    JLabel logoLabel = new JLabel(Util.createImageIcon("img/logo_200.png"));
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    sawToothRadioButton.setEnabled(enabled);
    squareRadioButton.setEnabled(enabled);
    resetAmplitudeSlider.setEnabled(enabled);
    resetPulseWidthSlider.setEnabled(enabled);
    seriesTextField.setEnabled(enabled);
    startStopButton.setEnabled(enabled);
  }

  public ButtonGroup getResetPulseTypeRadioButtonGroup() {

    return resetPulseTypeRadioButtonGroup;
  }

  public JRadioButton getSquareRadioButton() {

    return squareRadioButton;
  }

  public JRadioButton getSawToothRadioButton() {

    return sawToothRadioButton;
  }

  public JSlider getResetAmplitudeSlider() {

    return resetAmplitudeSlider;
  }

  public JSlider getResetPulseWidthSlider() {

    return resetPulseWidthSlider;
  }

  public JButton getResetButton() {

    return resetButton;
  }

  public JSlider getSetConductanceSlider() {

    return setConductanceSlider;
  }

  public JSlider getSetAmplitudeSlider() {

    return setAmplitudeSlider;
  }

  public JSlider getSetPulseWidthSlider() {

    return setPulseWidthSlider;
  }

  public JTextField getSeriesTextField() {

    return seriesTextField;
  }
}
