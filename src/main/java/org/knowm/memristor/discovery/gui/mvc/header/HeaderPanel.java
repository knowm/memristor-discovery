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
package org.knowm.memristor.discovery.gui.mvc.header;

import eu.hansolo.component.SteelCheckBox;
import java.awt.Color;
import javax.swing.JPanel;

public class HeaderPanel extends JPanel {

  private final static int NUM_SWITCHES = 8;
  private final SteelCheckBox[] checkBoxes;

  private final Color warnColor = new Color(255, 33, 33);

  /**
   * Constructor
   */
  public HeaderPanel() {

    checkBoxes = new SteelCheckBox[NUM_SWITCHES];
    for (int i = 0; i < checkBoxes.length; i++) {
      checkBoxes[i] = new SteelCheckBox("" + (i + 1), i);
      checkBoxes[i].setColored(true);
      checkBoxes[i].setRised(true);
      checkBoxes[i].setSelected(false);
      checkBoxes[i].setBackground(warnColor);
      add(checkBoxes[i]);
    }
    /** Set initial state to disabled */
    enableAllDigitalIOCheckBoxes(false);
    setBackground(warnColor);
  }

  public SteelCheckBox[] getCheckBoxes() {

    return checkBoxes;
  }

  public void updateDigitalIOSwitches(int digitalIOStates) {

    for (int i = 0; i < checkBoxes.length; i++) {

      int bit = (digitalIOStates >> i) & 1;
      // System.out.println("bit: " + bit);
      checkBoxes[i].setSelected(bit > 0);
      checkBoxes[i].setBackground(null);
    }
    //  check lowest 8 bits for all zeros
    if ((byte) (digitalIOStates & 0xFF) == 0) {
      setBackground(warnColor);
    } else {
      setBackground(null);
    }
  }

  public void enableAllDigitalIOCheckBoxes(boolean enabled) {

    for (int i = 0; i < checkBoxes.length; i++) {
      checkBoxes[i].setEnabled(enabled);
    }
  }
}
