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
package org.knowm.memristor.discovery.gui.mvc.apps.pulse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;

public class PulseController implements PropertyChangeListener {

  private final PulseControlPanel controlPanel;
  private final PulseMainPanel mainPanel;
  private final PulseModel model;
  private final DWFProxy dwf;

  /**
   * Constructor
   *
   * @param controlPanel
   * @param mainPanel
   * @param model
   * @param dwf
   */
  public PulseController(PulseControlPanel controlPanel, PulseMainPanel mainPanel, PulseModel model, DWFProxy dwf) {

    this.controlPanel = controlPanel;
    this.mainPanel = mainPanel;
    this.model = model;
    this.dwf = dwf;
    dwf.addListener(this);

    initGUIComponents();
    setUpViewEvents();

    // register the controller as the listener of the model
    model.addListener(this);

    // init waveform chart
    mainPanel.switch2WaveformChart();
    mainPanel.udpateWaveformChart(model.getWaveformTimeData(), model.getWaveformAmplitudeData(), model.getAmplitude(), model.getPulseWidth());
  }

  private void initGUIComponents() {

    controlPanel.getStopButton().setEnabled(false);
    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    controlPanel.getSeriesTextField().setText("" + model.getShunt());
    controlPanel.getAmplitudeSlider().setValue((int) (model.getAmplitude() * 100));
    controlPanel.getPulseWidthSlider().setValue(model.getPulseWidth());
  }

  /**
   * Here, all the action listeners are attached to the GUI components
   */
  private void setUpViewEvents() {

    controlPanel.getAmplitudeSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          model.setAmplitude(source.getValue() / (float) 100);
        }
      }
    });

    controlPanel.getPulseWidthSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          model.setPulseWidth(source.getValue());
        }
      }
    });

    controlPanel.getSeriesTextField().addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        JTextField textField = (JTextField) e.getSource();
        String text = textField.getText();

        try {
          int newShuntValue = Integer.parseInt(text);
          model.setShunt(newShuntValue);
        } catch (Exception ex) {
          // parsing error, default back to previous value
          textField.setText(Integer.toString(model.getShunt()));
        }
      }
    });
    mainPanel.getCaptureButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        mainPanel.switch2CaptureChart();
      }
    });
    mainPanel.getItButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        mainPanel.switch2ITChart();
      }
    });
    mainPanel.getRtButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        mainPanel.switch2RTChart();
      }
    });
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

    case DWFProxy.AD2_STARTUP_CHANGE:

      controlPanel.enableAllChildComponents((Boolean) evt.getNewValue());
      break;

    case AppModel.EVENT_PREFERENCES_UPDATE:

      initGUIComponentsFromModel();
      break;

    case AppModel.EVENT_WAVEFORM_UPDATE:

      model.updateWaveformChartData();
      if (!dwf.isAD2Capturing()) {
        mainPanel.switch2WaveformChart();
        mainPanel.udpateWaveformChart(model.getWaveformTimeData(), model.getWaveformAmplitudeData(), model.getAmplitude(), model.getPulseWidth());
      }
      break;

    default:
      break;
    }

  }
}
