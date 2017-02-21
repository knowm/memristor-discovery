package org.knowm.memristor.discovery.gui.mvc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by timmolter on 2/21/17.
 *
 * This is a hack around a bug in the JDK, which doesn't properly detect held-down lefft and righht arrow keys. Use this for apps that continuously run and when slider values change the AD2 needs to stopped and started again. See HysteresisApp.java for example.
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
