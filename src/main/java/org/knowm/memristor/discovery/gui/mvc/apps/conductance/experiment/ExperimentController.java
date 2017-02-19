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
package org.knowm.memristor.discovery.gui.mvc.apps.conductance.experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.plot.PlotPanel;

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

    // init resetWaveform chart
    plotPanel.switch2WaveformChart();
  }

  private void initGUIComponents() {

    experimentPanel.getStopButton().setEnabled(false);
    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    // RESET
    switch (experimentModel.getResetPulseType()) {
      case Sawtooth:
        experimentPanel.getSawToothRadioButton().setSelected(true);
        break;
      case Triangle:
        experimentPanel.getSquareRadioButton().setSelected(true);
        break;
      default:
        experimentPanel.getSawToothRadioButton().setSelected(true);
        break;
    }

    experimentPanel.getResetAmplitudeSlider().setValue((int) (experimentModel.getResetAmplitude() * 100));
    experimentPanel.getResetAmplitudeSlider().setBorder(BorderFactory.createTitledBorder(" Reset Amplitude [V] = " + experimentModel.getResetAmplitude()));
    experimentPanel.getResetPulseWidthSlider().setValue((experimentModel.getResetPulseWidth()));
    experimentPanel.getResetPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Reset Period [µs] = " + experimentModel.getResetPulseWidth() / 1000));

    // SET

    experimentPanel.getSetAmplitudeSlider().setValue((int) (experimentModel.getSetAmplitude() * 100));
    experimentPanel.getSetAmplitudeSlider().setBorder(BorderFactory.createTitledBorder(" Set Amplitude [V] = " + experimentModel.getSetAmplitude()));
    experimentPanel.getSetPulseWidthSlider().setValue((experimentModel.getSetPulseWidth()));
    experimentPanel.getSetPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Set Period [µs] = " + experimentModel.getSetPulseWidth() / 1000));

    experimentPanel.getSeriesTextField().setText("" + experimentModel.getSeriesR());
  }

  /**
   * Here, all the action listeners are attached to the GUI components
   */
  private void setUpViewEvents() {

    experimentPanel.getSawToothRadioButton().addActionListener(waveformRadioButtonActionListener);
    experimentPanel.getSquareRadioButton().addActionListener(waveformRadioButtonActionListener);

    experimentPanel.getResetAmplitudeSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setResetAmplitude(source.getValue() / (float) 100);
          experimentPanel.getResetAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Set Amplitude [V] = " + experimentModel.getResetAmplitude()));
        }
      }
    });

    experimentPanel.getResetPulseWidthSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setResetPulseWidth(source.getValue());
          experimentPanel.getResetPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Set Pulse Width [µs] = " + (double) experimentModel.getResetPulseWidth() / 1000));
        }
      }
    });

    // SET

    experimentPanel.getSetConductanceSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setSetConductance(source.getValue() / (float) 1000);
          experimentPanel.getSetConductanceSlider().setBorder(BorderFactory.createTitledBorder("Set Conductance [mS] = " + experimentModel.getSetConductance()));
        }
      }
    });
    experimentPanel.getSetAmplitudeSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setSetAmplitude(source.getValue() / (float) 100);
          experimentPanel.getSetAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Set Amplitude [V] = " + experimentModel.getSetAmplitude()));
        }
      }
    });

    experimentPanel.getSetPulseWidthSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting())) {
          experimentModel.setSetPulseWidth(source.getValue());
          experimentPanel.getSetPulseWidthSlider().setBorder(BorderFactory.createTitledBorder("Set Pulse Width [µs] = " + (double) experimentModel.getSetPulseWidth() / 1000));
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

  ActionListener waveformRadioButtonActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

      for (Enumeration<AbstractButton> buttons = experimentPanel.getResetPulseTypeRadioButtonGroup().getElements(); buttons.hasMoreElements(); ) {
        AbstractButton button = buttons.nextElement();
        if (button.isSelected()) {
          experimentModel.setWaveform(button.getText());
        }
      }
    }
  };

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
