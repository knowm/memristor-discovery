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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control;

import java.beans.PropertyChangeListener;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.HysteresisPreferences;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.Sine;
import org.knowm.memristor.discovery.utils.driver.Square;
import org.knowm.memristor.discovery.utils.driver.Triangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlModel extends ExperimentControlModel {

  private final Logger logger = LoggerFactory.getLogger(ControlModel.class);

  /**
   * Waveform
   */
  public HysteresisPreferences.Waveform waveform;
  float offset;
  private float amplitude;
  private int frequency;
  private final double[] waveformTimeData = new double[HysteresisPreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData = new double[HysteresisPreferences.CAPTURE_BUFFER_SIZE];

  /**
   * Constructor
   */
  public ControlModel() {

    updateWaveformChartData();
  }

  @Override
  public void loadModelFromPrefs() {

    // load model from prefs
    waveform = HysteresisPreferences.Waveform.valueOf(experimentPreferences.getString(HysteresisPreferences.WAVEFORM_INIT_STRING_KEY, HysteresisPreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    seriesResistance = experimentPreferences.getInteger(HysteresisPreferences.SERIES_R_INIT_KEY, HysteresisPreferences.SERIES_R_INIT_DEFAULT_VALUE);
    offset = experimentPreferences.getFloat(HysteresisPreferences.OFFSET_INIT_FLOAT_KEY, HysteresisPreferences.OFFSET_INIT_FLOAT_DEFAULT_VALUE);
    amplitude = experimentPreferences.getFloat(HysteresisPreferences.AMPLITUDE_INIT_FLOAT_KEY, HysteresisPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    frequency = experimentPreferences.getInteger(HysteresisPreferences.FREQUENCY_INIT_KEY, HysteresisPreferences.FREQUENCY_INIT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_PREFERENCES_UPDATE, true, false);
  }

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   *
   * @param listener
   */
  public void addListener(PropertyChangeListener listener) {

    swingPropertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * Given the state of the model, update the waveform x and y axis data arrays.
   */
  void updateWaveformChartData() {

    Driver driver;
    switch (waveform) {
      case Sine:
        driver = new Sine("Sine", offset, 0, amplitude, frequency);
        break;
      case Triangle:
        driver = new Triangle("Triangle", offset, 0, amplitude, frequency);
        break;
      case Square:
        driver = new Square("Square", offset, 0, amplitude, frequency);
        break;
      default:
        driver = new Sine("Sine", offset, 0, amplitude, frequency);
        break;
    }

    double stopTime = 1 / (double) frequency * HysteresisPreferences.CAPTURE_PERIOD_COUNT;
    double timeStep = 1 / (double) frequency * HysteresisPreferences.CAPTURE_PERIOD_COUNT / HysteresisPreferences.CAPTURE_BUFFER_SIZE;

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= HysteresisPreferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i;
      waveformAmplitudeData[counter++] = driver.getSignal(i);
    }
  }

  /////////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public HysteresisPreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(HysteresisPreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    waveform = Enum.valueOf(HysteresisPreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public float getOffset() {

    return offset;
  }

  public void setOffset(float offset) {

    this.offset = offset;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getFrequency() {

    return frequency;
  }

  public void setFrequency(int frequency) {

    int oldFreq = this.frequency;

    this.frequency = frequency;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_FREQUENCY_UPDATE, oldFreq, frequency);
  }

  public double[] getWaveformTimeData() {

    return waveformTimeData;
  }

  public double[] getWaveformAmplitudeData() {

    return waveformAmplitudeData;
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new HysteresisPreferences();
  }
}
