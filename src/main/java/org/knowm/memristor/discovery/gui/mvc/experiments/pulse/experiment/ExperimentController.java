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
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse.experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.AppModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.plot.PlotPanel;

public class ExperimentController implements PropertyChangeListener {

  private final ExperimentPanel experimentPanel;
  private final ExperimentModel experimentModel;

  private final PlotPanel plotPanel;

  /**
   * Constructor
   *
   * @param experimentPanel
   * @param plotPanel
   * @param experimentModel
   * @param dwf
   */
  public ExperimentController(ExperimentPanel experimentPanel, PlotPanel plotPanel, ExperimentModel experimentModel, DWFProxy dwf) {

    this.experimentPanel = experimentPanel;
    this.plotPanel = plotPanel;
    this.experimentModel = experimentModel;
    dwf.addListener(this);

    initGUIComponents();
    setUpViewEvents();

    // register the controller as the listener of the experimentModel
    experimentModel.addListener(this);

    // init waveform chart
    plotPanel.switch2WaveformChart();
  }

  private void initGUIComponents() {

    experimentPanel.getStopButton().setEnabled(false);
    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    experimentPanel.getSeriesTextField().setText("" + experimentModel.getSeriesR());
    experimentPanel.getAmplitudeSlider().setValue((int) (experimentModel.getAmplitude() * 100));
    experimentPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + experimentModel.getAmplitude()));
    if (experimentModel.getPulseWidth() >= 5000) {
      experimentPanel.getPulseWidthSlider().setValue((int) (experimentModel.getPulseWidth()));
      experimentPanel.getPulseWidthSliderNs().setValue(0);
      experimentPanel.getPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs] = " + experimentModel.getPulseWidth() / 1000));
      experimentPanel.getPulseWidthSliderNs().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
    }
    else {
      experimentPanel.getPulseWidthSlider().setValue(0);
      experimentPanel.getPulseWidthSliderNs().setValue(experimentModel.getPulseWidth());
      experimentPanel.getPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
      experimentPanel.getPulseWidthSliderNs().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs] = " + experimentModel.getPulseWidth() / 1000));
    }
    experimentPanel.getPulseNumberSlider().setBorder(BorderFactory.createTitledBorder("Pulse Number = " + experimentModel.getPulseNumber()));
  }

  /**
   * Here, all the action listeners are attached to the GUI components
   */
  private void setUpViewEvents() {

    experimentPanel.getAmplitudeSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setAmplitude(source.getValue() / (float) 100);
          experimentPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + experimentModel.getAmplitude()));
        }
      }
    });

    experimentPanel.getPulseWidthSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setPulseWidth(source.getValue());
          experimentPanel.getPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs] = " + (double) experimentModel.getPulseWidth() / 1000));
          experimentPanel.getPulseWidthSliderNs().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
        }
      }
    });

    experimentPanel.getPulseWidthSliderNs().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setPulseWidth(source.getValue());
          experimentPanel.getPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
          experimentPanel.getPulseWidthSliderNs().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs] = " + (double) experimentModel.getPulseWidth() / 1000));
        }
      }
    });

    experimentPanel.getPulseNumberSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setPulseNumber(source.getValue());
          experimentPanel.getPulseNumberSlider().setBorder(BorderFactory.createTitledBorder("Pulse Number = " + experimentModel.getPulseNumber()));
        }
      }
    });

    experimentPanel.getSeriesTextField().addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        JTextField textField = (JTextField) e.getSource();
        String text = textField.getText();

        try {
          int newShuntValue = Integer.parseInt(text);
          experimentModel.setSeriesR(newShuntValue);
        } catch (Exception ex) {
          // parsing error, default back to previous value
          textField.setText(Integer.toString(experimentModel.getSeriesR()));
        }
      }
    });
    plotPanel.getCaptureButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        plotPanel.switch2CaptureChart();
      }
    });

  }

  /**
   * These property change events are triggered in the experimentModel in the case where the underlying experimentModel is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

    case DWFProxy.AD2_STARTUP_CHANGE:

      experimentPanel.enableAllChildComponents((Boolean) evt.getNewValue());
      break;

    case AppModel.EVENT_PREFERENCES_UPDATE:

      initGUIComponentsFromModel();
      break;

    case AppModel.EVENT_WAVEFORM_UPDATE:

      experimentModel.updateWaveformChartData();
      break;

    default:
      break;
    }

  }
}
