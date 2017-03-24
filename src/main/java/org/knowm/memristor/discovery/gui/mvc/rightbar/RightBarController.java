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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JRadioButton;

import org.knowm.memristor.discovery.DWFProxy;

public class RightBarController implements PropertyChangeListener {

  private final RightBarPanel rightBarPanel;
  private final DWFProxy dwfProxy;

  /**
   * Constructor
   *
   * @param rightBarPanel
   * @param dwfProxy
   */
  public RightBarController(RightBarPanel rightBarPanel, DWFProxy dwfProxy) {

    this.rightBarPanel = rightBarPanel;
    this.dwfProxy = dwfProxy;
    dwfProxy.addListener(this);
    setUpViewEvents();

    // rightBarPanel.updateAllRadioButtons(dwfProxy.getDigitalIOStates());
  }

  private void setUpViewEvents() {

    rightBarPanel.getOscilloscopeProbe10RadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(8, 9, false, false);
        }
      }
    });
    rightBarPanel.getOscilloscopeProbe1ARadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(8, 9, true, false);
        }
      }
    });
    rightBarPanel.getOscilloscopeProbe1BRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(8, 9, true, true);
        }
      }
    });
    rightBarPanel.getOscilloscopeProbe1YRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(8, 9, false, true);
        }
      }
    });

    ////////////////

    rightBarPanel.getOscilloscopeProbe20RadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(10, 11, false, false);
        }
      }
    });
    rightBarPanel.getOscilloscopeProbe2ARadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(10, 11, true, false);
        }
      }
    });
    rightBarPanel.getOscilloscopeProbe2BRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(10, 11, true, true);
        }
      }
    });
    rightBarPanel.getOscilloscopeProbe2YRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(10, 11, false, true);
        }
      }
    });

    ////////////////

    rightBarPanel.getAwg10RadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(12, 13, false, false);
        }
      }
    });
    rightBarPanel.getAwg1ARadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(12, 13, true, false);
        }
      }
    });
    rightBarPanel.getAwg1BRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(12, 13, true, true);
        }
      }
    });
    rightBarPanel.getAwg1YRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(12, 13, false, true);
        }
      }
    });

    ////////////////

    rightBarPanel.getAwg20RadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(14, 15, false, false);
        }
      }
    });
    rightBarPanel.getAwg2ARadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(14, 15, true, false);
        }
      }
    });
    rightBarPanel.getAwg2BRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(14, 15, true, true);
        }
      }
    });
    rightBarPanel.getAwg2YRadioButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (((JRadioButton) e.getSource()).isSelected()) {

          dwfProxy.update2DigitalIOStatesAtOnce(14, 15, false, true);
        }
      }
    });
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

      case DWFProxy.AD2_STARTUP_CHANGE:

        rightBarPanel.enableAllRadioButtons(dwfProxy.isAD2Running());
        break;

      case DWFProxy.DIGITAL_IO_READ:

        rightBarPanel.updateAllRadioButtons(dwfProxy.getDigitalIOStates());
        break;

      default:
        break;
    }
  }
}
