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
package org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.plot.PlotPanel;

public class ExperimentController implements PropertyChangeListener, KeyListener {

  private final ExperimentPanel experimentPanel;
  private final ExperimentModel experimentModel;

  boolean leftRightArrowKeyPressed = false;

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
    this.experimentModel = experimentModel;
    dwf.addListener(this);

    initGUIComponents();
    setUpViewEvents();

    // register the controller as the listener of the model
    experimentModel.addListener(this);
  }

  private void initGUIComponents() {

    experimentPanel.getStopButton().setEnabled(false);
    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    switch (experimentModel.getWaveform()) {
    case Sine:
      experimentPanel.getSineRadioButton().setSelected(true);
      break;
    case Triangle:
      experimentPanel.getTriangleRadioButton().setSelected(true);
      break;
    default:
      experimentPanel.getSineRadioButton().setSelected(true);
      break;
    }

    experimentPanel.getOffsetSlider().setValue((int) (experimentModel.getOffset() * 100));
    experimentPanel.getOffsetSlider().setBorder(BorderFactory.createTitledBorder("Offset [V] = " + experimentModel.getOffset()));
    experimentPanel.getAmplitudeSlider().setValue((int) (experimentModel.getAmplitude() * 100));
    experimentPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + experimentModel.getAmplitude()));
    if (experimentModel.getFrequency() <= 100) {
      experimentPanel.getFrequencySlider().setValue(experimentModel.getFrequency());
      experimentPanel.getFrequencySliderLog().setValue(0);
      experimentPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz] = " + experimentModel.getFrequency()));
      experimentPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
    }
    else {
      experimentPanel.getFrequencySlider().setValue(0);
      experimentPanel.getFrequencySliderLog().setValue((int) Math.log10(experimentModel.getFrequency() + 1));
      experimentPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency [Hz] = " + experimentModel.getFrequency()));
      experimentPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
    }
    experimentPanel.getSeriesTextField().setText("" + experimentModel.getSeriesR());
  }

  /**
   * Here, all the action listeners are attached to the GUI components
   */
  private void setUpViewEvents() {

    experimentPanel.getSineRadioButton().addActionListener(waveformRadioButtonActionListener);
    experimentPanel.getTriangleRadioButton().addActionListener(waveformRadioButtonActionListener);

    experimentPanel.getOffsetSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftRightArrowKeyPressed) {
          experimentModel.setOffset(source.getValue() / (float) 100);
          experimentPanel.getOffsetSlider().setBorder(BorderFactory.createTitledBorder("Offset [V] = " + experimentModel.getOffset()));
        }
      }
    });
    experimentPanel.getOffsetSlider().addKeyListener(this);

    experimentPanel.getAmplitudeSlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftRightArrowKeyPressed) {
          experimentModel.setAmplitude(source.getValue() / (float) 100);
          experimentPanel.getAmplitudeSlider().setBorder(BorderFactory.createTitledBorder("Amplitude [V] = " + experimentModel.getAmplitude()));
        }
      }
    });
    experimentPanel.getAmplitudeSlider().addKeyListener(this);

    experimentPanel.getFrequencySlider().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftRightArrowKeyPressed) {
          experimentModel.setFrequency(source.getValue());
          experimentPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz] = " + experimentModel.getFrequency()));
          experimentPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
        }
      }

    });
    experimentPanel.getFrequencySlider().addKeyListener(this);

    experimentPanel.getFrequencySliderLog().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        if (!(source.getValueIsAdjusting()) && !leftRightArrowKeyPressed) {
          experimentModel.setFrequency((int) Math.pow(10, source.getValue()));
          experimentPanel.getFrequencySliderLog().setBorder(BorderFactory.createTitledBorder("Frequency [Hz] = " + experimentModel.getFrequency()));
          experimentPanel.getFrequencySlider().setBorder(BorderFactory.createTitledBorder("Frequency [Hz]"));
        }
      }

    });
    experimentPanel.getFrequencySliderLog().addKeyListener(this);

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

  }

  ActionListener waveformRadioButtonActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

      for (Enumeration<AbstractButton> buttons = experimentPanel.getWaveformRadioButtonGroup().getElements(); buttons.hasMoreElements();) {
        AbstractButton button = buttons.nextElement();
        if (button.isSelected()) {
          experimentModel.setWaveform(button.getText());
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

}
