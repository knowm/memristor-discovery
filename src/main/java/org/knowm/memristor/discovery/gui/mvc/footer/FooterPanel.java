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
package org.knowm.memristor.discovery.gui.mvc.footer;

import eu.hansolo.component.SteelCheckBox;
import java.awt.Color;
import javax.swing.JPanel;

public class FooterPanel extends JPanel {

  private final Color warnColor = new Color(255, 33, 33);

  private final SteelCheckBox steelCheckbox;

  /** Constructor */
  public FooterPanel() {

    steelCheckbox = new SteelCheckBox("Board On/Off", 0, 150, 26);
    steelCheckbox.setRised(true);
    steelCheckbox.setColored(true);

    /** Set initial state to unchecked */
    steelCheckbox.setSelected(false);
    setBackground(warnColor);
    steelCheckbox.setBackground(warnColor);
    add(steelCheckbox);
  }

  public void updateBoardOnOffSwitch(boolean ad2Running) {

    steelCheckbox.setSelected(ad2Running);
    if (ad2Running) {
      setBackground(null);
      steelCheckbox.setBackground(null);
    } else {
      setBackground(warnColor);
      steelCheckbox.setBackground(warnColor);
    }
  }

  public SteelCheckBox getSteelCheckbox() {

    return steelCheckbox;
  }
}
