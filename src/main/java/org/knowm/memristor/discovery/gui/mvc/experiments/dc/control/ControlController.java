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
package org.knowm.memristor.discovery.gui.mvc.experiments.dc.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.plot.PlotPanel;

public class ControlController extends ExperimentControlController {

  private final ControlPanel controlPanel;
  private final ControlModel controlModel;

  private final PlotPanel plotPanel;

  /**
   * Constructor
   *
   * @param controlPanel
   * @param plotPanel
   * @param controlModel
   * @param dwf
   */
  public ControlController(ControlPanel controlPanel, PlotPanel plotPanel, ControlModel controlModel, DWFProxy dwf) {

    super(controlPanel, controlModel);

    this.controlPanel = controlPanel;
    this.plotPanel = plotPanel;
    this.controlModel = controlModel;
    dwf.addListener(this);

    initGUIComponents();
    setUpViewEvents();

    // register the controller as the listener of the controlModel
    controlModel.addListener(this);

    // init waveform chart
    plotPanel.switch2WaveformChart();
  }

  private void initGUIComponents() {

    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    switch (controlModel.getWaveform()) {
      case Sawtooth:
        controlPanel.getSawToothRadioButton().setSelected(true);
        break;
      case SawtoothUpDown:
        controlPanel.getSawtoothUpDownRadioButton().setSelected(true);
        break;
      case Triangle:
        controlPanel.getTriangleRadioButton().setSelected(true);
        break;
      case TriangleUpDown:
        controlPanel.getTriangleUpDownRadioButton().setSelected(true);
        break;
      default:
        controlPanel.getSawtoothUpDownRadioButton().setSelected(true);
        break;
    }

    controlPanel.getSeriesTextField().setText("" + controlModel.getSeriesResistance());
    controlPanel.getAmplitudeSlider().setValue((int) (controlModel.getAmplitude() * 100));
    controlPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + controlModel.getAmplitude()));
    if (controlModel.getPeriod() >= 100) {
      controlPanel.getPeriodSlider().setValue((int) (controlModel.getPeriod()));
      controlPanel.getPeriodSliderNs().setValue(100);
      controlPanel.getPeriodSlider().setBorder(BorderFactory.createTitledBorder("Period [" + DCPreferences.TIME_UNIT.getLabel() + "] = " + controlModel.getPeriod()));
      controlPanel.getPeriodSliderNs().setBorder(BorderFactory.createTitledBorder("Period [" + DCPreferences.TIME_UNIT.getLabel() + "]"));
    } else {
      controlPanel.getPeriodSlider().setValue(100);
      controlPanel.getPeriodSliderNs().setValue(controlModel.getPeriod());
      controlPanel.getPeriodSlider().setBorder(BorderFactory.createTitledBorder("Period [" + DCPreferences.TIME_UNIT.getLabel() + "]"));
      controlPanel.getPeriodSliderNs().setBorder(BorderFactory.createTitledBorder("Period [" + DCPreferences.TIME_UNIT.getLabel() + "] = " + controlModel.getPeriod()));
    }
    controlPanel.getPulseNumberSlider().setBorder(BorderFactory.createTitledBorder("Pulse Number = " + controlModel.getPulseNumber()));
    controlPanel.getPulseNumberSlider().setValue((int) controlModel.getPulseNumber());
  }

  public void doSetUpViewEvents() {

    controlPanel.getSawToothRadioButton().addActionListener(waveformRadioButtonActionListener);
    controlPanel.getSawtoothUpDownRadioButton().addActionListener(waveformRadioButtonActionListener);
    controlPanel.getTriangleRadioButton().addActionListener(waveformRadioButtonActionListener);
    controlPanel.getTriangleUpDownRadioButton().addActionListener(waveformRadioButtonActionListener);

    controlPanel.getAmplitudeSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          controlModel.setAmplitude(source.getValue() / (float) 100);
          controlPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + controlModel.getAmplitude()));
        }
      }
    });

    controlPanel.getPeriodSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          controlModel.setPeriod(source.getValue());
          controlPanel.getPeriodSlider().setBorder(BorderFactory.createTitledBorder("Pulse Width [" + DCPreferences.TIME_UNIT.getLabel() + "] = " + (double) controlModel.getPeriod()));
          controlPanel.getPeriodSliderNs().setBorder(BorderFactory.createTitledBorder("Pulse Width [" + DCPreferences.TIME_UNIT.getLabel() + "]"));
        }
      }
    });

    controlPanel.getPeriodSliderNs().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          controlModel.setPeriod(source.getValue());
          controlPanel.getPeriodSlider().setBorder(BorderFactory.createTitledBorder("Pulse Width [" + DCPreferences.TIME_UNIT.getLabel() + "]"));
          controlPanel.getPeriodSliderNs().setBorder(BorderFactory.createTitledBorder("Pulse Width [" + DCPreferences.TIME_UNIT.getLabel() + "] = " + (double) controlModel.getPeriod()));
        }
      }
    });

    controlPanel.getPulseNumberSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          controlModel.setPulseNumber(source.getValue());
          controlPanel.getPulseNumberSlider().setBorder(BorderFactory.createTitledBorder("Pulse Number = " + controlModel.getPulseNumber()));
        }
      }
    });

    controlPanel.getSeriesTextField().addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        JTextField textField = (JTextField) e.getSource();
        String text = textField.getText();

        try {
          int newSeriesValue = Integer.parseInt(text);
          controlModel.setSeriesResistance(newSeriesValue);
        } catch (Exception ex) {
          // parsing error, default back to previous value
          textField.setText(Integer.toString(controlModel.getSeriesResistance()));
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

  ActionListener waveformRadioButtonActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

      for (Enumeration<AbstractButton> buttons = controlPanel.getWaveformRadioButtonGroup().getElements(); buttons.hasMoreElements(); ) {
        AbstractButton button = buttons.nextElement();
        if (button.isSelected()) {
          controlModel.setWaveform(button.getText());
        }
      }
    }
  };

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

      case DWFProxy.AD2_STARTUP_CHANGE:

        controlPanel.enableAllChildComponents((Boolean) evt.getNewValue());
        break;

      case ExperimentControlModel.EVENT_PREFERENCES_UPDATE:

        initGUIComponentsFromModel();
        break;

      case ExperimentControlModel.EVENT_WAVEFORM_UPDATE:

        controlModel.updateWaveformChartData();
        break;

      default:
        break;
    }
  }
}
