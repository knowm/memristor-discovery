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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.utils.Util;

/**
 * Provides controls for running the control
 *
 * @author timmolter
 */
public class ControlPanel extends ExperimentControlPanel {

  public JButton clearConsolButton;
  public JButton meminlineTestButton;
  public JButton muxTestButton;
  public JButton switchTestButton;

  /**
   * Constructor
   */
  public ControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    c.gridx = 0;

    clearConsolButton = new JButton("Clear");
    clearConsolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(clearConsolButton, c);
    c.gridy++;

    meminlineTestButton = new JButton("Mem-Inline Chip Test");
    meminlineTestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.insets = new Insets(0, 0, 0, 0);
    add(meminlineTestButton, c);
    c.gridy++;

    muxTestButton = new JButton("1-4 Mux Board Test (V1.x)");
    muxTestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.insets = new Insets(0, 0, 0, 0);
    add(muxTestButton, c);
    c.gridy++;

    switchTestButton = new JButton("Switch Board Test (R=5kΩ)");
    switchTestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.insets = new Insets(0, 0, 0, 0);
    add(switchTestButton, c);
    c.gridy++;

    JLabel logoLabel = new JLabel(Util.createImageIcon("img/logo_200.png"));
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    clearConsolButton.setEnabled(enabled);
    meminlineTestButton.setEnabled(enabled);
    muxTestButton.setEnabled(enabled);
    switchTestButton.setEnabled(enabled);

  }

}
