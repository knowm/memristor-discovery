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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control;

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
import org.knowm.memristor.discovery.gui.mvc.LeftAndRightArrowKeyListener;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;

public class ControlController extends ExperimentControlController {

  private final ControlPanel controlPanel;
  private final ControlModel controlModel;

  LeftAndRightArrowKeyListener leftAndRightArrowKeyListener = new LeftAndRightArrowKeyListener();

  /**
   * Constructor
   *
   * @param controlPanel
   * @param controlModel
   * @param dwf
   */
  public ControlController(ControlPanel controlPanel, ControlModel controlModel, DWFProxy dwf) {

    super(controlPanel, controlModel);

    this.controlPanel = controlPanel;
    this.controlModel = controlModel;
    dwf.addListener(this);

    initGUIComponents();
    setUpViewEvents();

    // register the controller as the listener of the model
    controlModel.addListener(this);
  }

  private void initGUIComponents() {

    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    switch (controlModel.getWaveform()) {
      case Sine:
        controlPanel.getSineRadioButton().setSelected(true);
        break;
      case Triangle:
        controlPanel.getTriangleRadioButton().setSelected(true);
        break;
      case Square:
        controlPanel.getSquareRadioButton().setSelected(true);
        break;
      default:
        controlPanel.getSineRadioButton().setSelected(true);
        break;
    }

    controlPanel.getOffsetSlider().setValue((int) (controlModel.getOffset() * 100));
    controlPanel.getOffsetSlider().setBorder(BorderFactory.createTitledBorder("Offset [V] = " + controlModel.getOffset()));
    controlPanel.getAmplitudeSlider().setValue((int) (controlModel.getAmplitude() * 100));
    controlPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + controlModel.getAmplitude()));
    if (controlModel.getFrequency() <= 100) {
      controlPanel.getFrequencySlider().setValue(controlModel.getFrequency());
      controlPanel.getFrequencySliderLog().setValue((int) Math.log10(controlModel.getFrequency() + 1));
      controlPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz] = " + controlModel.getFrequency()));
      controlPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency (Log) [Hz]"));
    } else {
      controlPanel.getFrequencySlider().setValue(controlModel.getFrequency());
      controlPanel.getFrequencySliderLog().setValue((int) Math.log10(controlModel.getFrequency() + 1));
      controlPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency (Log) [Hz] = " + controlModel.getFrequency()));
      controlPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
    }
    controlPanel.getSeriesTextField().setText("" + controlModel.getSeriesResistance());
  }

  public void doSetUpViewEvents() {

    controlPanel.getSineRadioButton().addActionListener(waveformRadioButtonActionListener);
    controlPanel.getTriangleRadioButton().addActionListener(waveformRadioButtonActionListener);
    controlPanel.getSquareRadioButton().addActionListener(waveformRadioButtonActionListener);

    controlPanel.getOffsetSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftAndRightArrowKeyListener.isLeftRightArrowKeyPressed()) {
          controlModel.setOffset(source.getValue() / (float) 100);
          controlPanel.getOffsetSlider().setBorder(BorderFactory.createTitledBorder("Offset [V] = " + controlModel.getOffset()));
        }
      }
    });
    controlPanel.getOffsetSlider().addKeyListener(leftAndRightArrowKeyListener);

    controlPanel.getAmplitudeSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftAndRightArrowKeyListener.isLeftRightArrowKeyPressed()) {
          controlModel.setAmplitude(source.getValue() / (float) 100);
          controlPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + controlModel.getAmplitude()));
        }
      }
    });
    controlPanel.getAmplitudeSlider().addKeyListener(leftAndRightArrowKeyListener);

    controlPanel.getFrequencySlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftAndRightArrowKeyListener.isLeftRightArrowKeyPressed()) {
          controlModel.setFrequency(source.getValue());
          controlPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz] = " + controlModel.getFrequency()));
          controlPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
        }
      }
    });
    controlPanel.getFrequencySlider().addKeyListener(leftAndRightArrowKeyListener);

    controlPanel.getFrequencySliderLog().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftAndRightArrowKeyListener.isLeftRightArrowKeyPressed()) {
          controlModel.setFrequency((int) Math.pow(10, source.getValue()));
          controlPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency [Hz] = " + controlModel.getFrequency()));
          controlPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
        }
      }
    });
    controlPanel.getFrequencySliderLog().addKeyListener(leftAndRightArrowKeyListener);

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
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to those events and make sure the corresponding GUI
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
