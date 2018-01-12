/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2018 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by timmolter on 2/21/17.
 *
 * This is a hack around a bug in the JDK, which doesn't properly detect held-down lefft and righht arrow keys. Use this for experiments that continuously run and when slider values change the AD2 needs to stopped and started again. See HysteresisExperiment.java for example.
 */
public class LeftAndRightArrowKeyListener implements KeyListener {

  private boolean leftRightArrowKeyPressed = false;

  @Override
  public void keyTyped(KeyEvent e) {

    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
      leftRightArrowKeyPressed = true;
    }
    // System.out.println("Typed");
  }

  @Override
  public void keyPressed(KeyEvent e) {

    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
      leftRightArrowKeyPressed = true;
    }
    // System.out.println("keyPressed");
  }

  @Override
  public void keyReleased(KeyEvent e) {

    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
      leftRightArrowKeyPressed = false;
    }
    // System.out.println("keyReleased");
    JSlider slider = (JSlider) e.getSource();
    ChangeEvent ce = new ChangeEvent(slider);
    for (ChangeListener cl : slider.getChangeListeners()) {
      cl.stateChanged(ce);
    }
  }

  public boolean isLeftRightArrowKeyPressed() {

    return leftRightArrowKeyPressed;
  }
}
