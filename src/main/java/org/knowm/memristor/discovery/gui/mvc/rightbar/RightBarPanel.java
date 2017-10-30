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
package org.knowm.memristor.discovery.gui.mvc.rightbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RightBarPanel extends JPanel {

  private static final Logger logger = LoggerFactory.getLogger(RightBarPanel.class);

  private final Box oscilloscopeProbe1RadioButtonBox;
  private final ButtonGroup oscilloscopeProbe1RadioButtonGroup;
  private final JRadioButton oscilloscopeProbe10RadioButton;
  private final JRadioButton oscilloscopeProbe1ARadioButton;
  private final JRadioButton oscilloscopeProbe1BRadioButton;
  private final JRadioButton oscilloscopeProbe1YRadioButton;

  private final Box oscilloscopeProbe2RadioButtonBox;
  private final ButtonGroup oscilloscopeProbe2RadioButtonGroup;
  private final JRadioButton oscilloscopeProbe20RadioButton;
  private final JRadioButton oscilloscopeProbe2ARadioButton;
  private final JRadioButton oscilloscopeProbe2BRadioButton;
  private final JRadioButton oscilloscopeProbe2YRadioButton;

  private final Box awg1RadioButtonBox;
  private final ButtonGroup awg1RadioButtonGroup;
  private final JRadioButton awg10RadioButton;
  private final JRadioButton awg1ARadioButton;
  private final JRadioButton awg1BRadioButton;
  private final JRadioButton awg1YRadioButton;

  private final Box awg2RadioButtonBox;
  private final ButtonGroup awg2RadioButtonGroup;
  private final JRadioButton awg20RadioButton;
  private final JRadioButton awg2ARadioButton;
  private final JRadioButton awg2BRadioButton;
  private final JRadioButton awg2YRadioButton;

  /**
   * Constructor
   */
  public RightBarPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    c.insets = new Insets(0, 6, 4, 16);

    oscilloscopeProbe10RadioButton = new JRadioButton("E");
    oscilloscopeProbe1ARadioButton = new JRadioButton("A");
    oscilloscopeProbe1BRadioButton = new JRadioButton("B");
    oscilloscopeProbe1YRadioButton = new JRadioButton("Y");
    oscilloscopeProbe1RadioButtonGroup = new ButtonGroup();
    oscilloscopeProbe1RadioButtonGroup.add(oscilloscopeProbe10RadioButton);
    oscilloscopeProbe1RadioButtonGroup.add(oscilloscopeProbe1ARadioButton);
    oscilloscopeProbe1RadioButtonGroup.add(oscilloscopeProbe1BRadioButton);
    oscilloscopeProbe1RadioButtonGroup.add(oscilloscopeProbe1YRadioButton);
    add(oscilloscopeProbe10RadioButton);
    add(oscilloscopeProbe1ARadioButton);
    add(oscilloscopeProbe1BRadioButton);
    add(oscilloscopeProbe1YRadioButton);
    oscilloscopeProbe1RadioButtonBox = Box.createVerticalBox();
    oscilloscopeProbe1RadioButtonBox.setBorder(BorderFactory.createTitledBorder("1+"));
    oscilloscopeProbe1RadioButtonBox.add(oscilloscopeProbe10RadioButton);
    oscilloscopeProbe1RadioButtonBox.add(oscilloscopeProbe1ARadioButton);
    oscilloscopeProbe1RadioButtonBox.add(oscilloscopeProbe1BRadioButton);
    oscilloscopeProbe1RadioButtonBox.add(oscilloscopeProbe1YRadioButton);
    c.gridx = 0;
    c.gridy++;
    add(oscilloscopeProbe1RadioButtonBox, c);

    oscilloscopeProbe20RadioButton = new JRadioButton("E");
    oscilloscopeProbe2ARadioButton = new JRadioButton("A");
    oscilloscopeProbe2BRadioButton = new JRadioButton("B");
    oscilloscopeProbe2YRadioButton = new JRadioButton("Y");
    oscilloscopeProbe2RadioButtonGroup = new ButtonGroup();
    oscilloscopeProbe2RadioButtonGroup.add(oscilloscopeProbe20RadioButton);
    oscilloscopeProbe2RadioButtonGroup.add(oscilloscopeProbe2ARadioButton);
    oscilloscopeProbe2RadioButtonGroup.add(oscilloscopeProbe2BRadioButton);
    oscilloscopeProbe2RadioButtonGroup.add(oscilloscopeProbe2YRadioButton);
    add(oscilloscopeProbe20RadioButton);
    add(oscilloscopeProbe2ARadioButton);
    add(oscilloscopeProbe2BRadioButton);
    add(oscilloscopeProbe2YRadioButton);
    oscilloscopeProbe2RadioButtonBox = Box.createVerticalBox();
    oscilloscopeProbe2RadioButtonBox.setBorder(BorderFactory.createTitledBorder("2+"));
    oscilloscopeProbe2RadioButtonBox.add(oscilloscopeProbe20RadioButton);
    oscilloscopeProbe2RadioButtonBox.add(oscilloscopeProbe2ARadioButton);
    oscilloscopeProbe2RadioButtonBox.add(oscilloscopeProbe2BRadioButton);
    oscilloscopeProbe2RadioButtonBox.add(oscilloscopeProbe2YRadioButton);
    c.gridy++;
    add(oscilloscopeProbe2RadioButtonBox, c);

    awg10RadioButton = new JRadioButton("E");
    awg1ARadioButton = new JRadioButton("A");
    awg1BRadioButton = new JRadioButton("B");
    awg1YRadioButton = new JRadioButton("Y");
    awg1RadioButtonGroup = new ButtonGroup();
    awg1RadioButtonGroup.add(awg10RadioButton);
    awg1RadioButtonGroup.add(awg1ARadioButton);
    awg1RadioButtonGroup.add(awg1BRadioButton);
    awg1RadioButtonGroup.add(awg1YRadioButton);
    add(awg10RadioButton);
    add(awg1ARadioButton);
    add(awg1BRadioButton);
    add(awg1YRadioButton);
    awg1RadioButtonBox = Box.createVerticalBox();
    awg1RadioButtonBox.setBorder(BorderFactory.createTitledBorder("W1"));
    awg1RadioButtonBox.add(awg10RadioButton);
    awg1RadioButtonBox.add(awg1ARadioButton);
    awg1RadioButtonBox.add(awg1BRadioButton);
    awg1RadioButtonBox.add(awg1YRadioButton);
    c.gridy++;
    add(awg1RadioButtonBox, c);

    awg20RadioButton = new JRadioButton("E");
    awg2ARadioButton = new JRadioButton("A");
    awg2BRadioButton = new JRadioButton("B");
    awg2YRadioButton = new JRadioButton("Y");
    awg2RadioButtonGroup = new ButtonGroup();
    awg2RadioButtonGroup.add(awg20RadioButton);

    awg2RadioButtonGroup.add(awg2ARadioButton);
    awg2RadioButtonGroup.add(awg2BRadioButton);
    awg2RadioButtonGroup.add(awg2YRadioButton);
    add(awg20RadioButton);
    add(awg2ARadioButton);
    add(awg2BRadioButton);
    add(awg2YRadioButton);
    awg2RadioButtonBox = Box.createVerticalBox();
    awg2RadioButtonBox.setBorder(BorderFactory.createTitledBorder("W2"));
    awg2RadioButtonBox.add(awg20RadioButton);
    awg2RadioButtonBox.add(awg2ARadioButton);
    awg2RadioButtonBox.add(awg2BRadioButton);
    awg2RadioButtonBox.add(awg2YRadioButton);
    c.gridy++;
    add(awg2RadioButtonBox, c);
  }

  public JRadioButton getOscilloscopeProbe10RadioButton() {

    return oscilloscopeProbe10RadioButton;
  }

  public JRadioButton getOscilloscopeProbe1ARadioButton() {

    return oscilloscopeProbe1ARadioButton;
  }

  public JRadioButton getOscilloscopeProbe1BRadioButton() {

    return oscilloscopeProbe1BRadioButton;
  }

  public JRadioButton getOscilloscopeProbe1YRadioButton() {

    return oscilloscopeProbe1YRadioButton;
  }

  public JRadioButton getOscilloscopeProbe20RadioButton() {

    return oscilloscopeProbe20RadioButton;
  }

  public JRadioButton getOscilloscopeProbe2ARadioButton() {

    return oscilloscopeProbe2ARadioButton;
  }

  public JRadioButton getOscilloscopeProbe2BRadioButton() {

    return oscilloscopeProbe2BRadioButton;
  }

  public JRadioButton getOscilloscopeProbe2YRadioButton() {

    return oscilloscopeProbe2YRadioButton;
  }

  public JRadioButton getAwg10RadioButton() {

    return awg10RadioButton;
  }

  public JRadioButton getAwg1ARadioButton() {

    return awg1ARadioButton;
  }

  public JRadioButton getAwg1BRadioButton() {

    return awg1BRadioButton;
  }

  public JRadioButton getAwg1YRadioButton() {

    return awg1YRadioButton;
  }

  public JRadioButton getAwg20RadioButton() {

    return awg20RadioButton;
  }

  public JRadioButton getAwg2ARadioButton() {

    return awg2ARadioButton;
  }

  public JRadioButton getAwg2BRadioButton() {

    return awg2BRadioButton;
  }

  public JRadioButton getAwg2YRadioButton() {

    return awg2YRadioButton;
  }

  public void updateAllRadioButtons(int digitalIOStates) {

    logger.debug(Integer.toBinaryString(digitalIOStates));

    oscilloscopeProbe10RadioButton.setSelected(false);
    oscilloscopeProbe1ARadioButton.setSelected(false);
    oscilloscopeProbe1BRadioButton.setSelected(false);
    oscilloscopeProbe1YRadioButton.setSelected(false);

    oscilloscopeProbe20RadioButton.setSelected(false);
    oscilloscopeProbe2ARadioButton.setSelected(false);
    oscilloscopeProbe2BRadioButton.setSelected(false);
    oscilloscopeProbe2YRadioButton.setSelected(false);

    awg10RadioButton.setSelected(false);
    awg1ARadioButton.setSelected(false);
    awg1BRadioButton.setSelected(false);
    awg1YRadioButton.setSelected(false);

    awg20RadioButton.setSelected(false);
    awg2ARadioButton.setSelected(false);
    awg2BRadioButton.setSelected(false);
    awg2YRadioButton.setSelected(false);

    int lowBit = (digitalIOStates >> 8) & 1;
    int highBit = (digitalIOStates >> 9) & 1;
    if (lowBit == 0 && highBit == 0) {
      oscilloscopeProbe10RadioButton.setSelected(true);
    }
    else if (lowBit == 0 && highBit == 1) {
      oscilloscopeProbe1YRadioButton.setSelected(true);
    }
    else if (lowBit == 1 && highBit == 0) {
      oscilloscopeProbe1ARadioButton.setSelected(true);
    }
    else {
      oscilloscopeProbe1BRadioButton.setSelected(true);
    }

    lowBit = (digitalIOStates >> 10) & 1;
    highBit = (digitalIOStates >> 11) & 1;
    if (lowBit == 0 && highBit == 0) {
      oscilloscopeProbe20RadioButton.setSelected(true);
    }
    else if (lowBit == 0 && highBit == 1) {
      oscilloscopeProbe2YRadioButton.setSelected(true);
    }
    else if (lowBit == 1 && highBit == 0) {
      oscilloscopeProbe2ARadioButton.setSelected(true);
    }
    else {
      oscilloscopeProbe2BRadioButton.setSelected(true);
    }

    lowBit = (digitalIOStates >> 12) & 1;
    highBit = (digitalIOStates >> 13) & 1;
    if (lowBit == 0 && highBit == 0) {
      awg10RadioButton.setSelected(true);
    }
    else if (lowBit == 0 && highBit == 1) {
      awg1YRadioButton.setSelected(true);
    }
    else if (lowBit == 1 && highBit == 0) {
      awg1ARadioButton.setSelected(true);
    }
    else {
      awg1BRadioButton.setSelected(true);
    }

    lowBit = (digitalIOStates >> 14) & 1;
    highBit = (digitalIOStates >> 15) & 1;
    if (lowBit == 0 && highBit == 0) {
      awg20RadioButton.setSelected(true);
    }
    else if (lowBit == 0 && highBit == 1) {
      awg2YRadioButton.setSelected(true);
    }
    else if (lowBit == 1 && highBit == 0) {
      awg2ARadioButton.setSelected(true);
    }
    else {
      awg2BRadioButton.setSelected(true);
    }
  }

  public void enableAllRadioButtons(boolean ad2Running) {

    ad2Running = false;

    oscilloscopeProbe10RadioButton.setEnabled(ad2Running);
    oscilloscopeProbe1ARadioButton.setEnabled(ad2Running);
    oscilloscopeProbe1BRadioButton.setEnabled(ad2Running);
    oscilloscopeProbe1YRadioButton.setEnabled(ad2Running);

    oscilloscopeProbe20RadioButton.setEnabled(ad2Running);
    oscilloscopeProbe2ARadioButton.setEnabled(ad2Running);
    oscilloscopeProbe2BRadioButton.setEnabled(ad2Running);
    oscilloscopeProbe2YRadioButton.setEnabled(ad2Running);

    awg10RadioButton.setEnabled(ad2Running);
    awg1ARadioButton.setEnabled(ad2Running);
    awg1BRadioButton.setEnabled(ad2Running);
    awg1YRadioButton.setEnabled(ad2Running);

    awg20RadioButton.setEnabled(ad2Running);
    awg2ARadioButton.setEnabled(ad2Running);
    awg2BRadioButton.setEnabled(ad2Running);
    awg2YRadioButton.setEnabled(ad2Running);
  }
}
