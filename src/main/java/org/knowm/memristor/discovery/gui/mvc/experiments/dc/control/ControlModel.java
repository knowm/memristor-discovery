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
package org.knowm.memristor.discovery.gui.mvc.experiments.dc.control;

import java.beans.PropertyChangeListener;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCPreferences;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.Sawtooth;
import org.knowm.memristor.discovery.utils.driver.SawtoothUpDown;
import org.knowm.memristor.discovery.utils.driver.Triangle;
import org.knowm.memristor.discovery.utils.driver.TriangleUpDown;

public class ControlModel extends ExperimentControlModel {

  public DCPreferences.Waveform waveform;
  private float amplitude;
  private int period; // model store period
  private int pulseNumber;

  private final double[] waveformTimeData = new double[DCPreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData = new double[DCPreferences.CAPTURE_BUFFER_SIZE];

  /**
   * Constructor
   */
  public ControlModel() {

    updateWaveformChartData();
  }

  @Override
  public void loadModelFromPrefs() {

    waveform = DCPreferences.Waveform.valueOf(experimentPreferences.getString(DCPreferences.WAVEFORM_INIT_STRING_KEY, DCPreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    seriesResistance = experimentPreferences.getInteger(DCPreferences.SERIES_R_INIT_KEY, DCPreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude = experimentPreferences.getFloat(DCPreferences.AMPLITUDE_INIT_FLOAT_KEY, DCPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    period = experimentPreferences.getInteger(DCPreferences.PERIOD_INIT_KEY, DCPreferences.PERIOD_INIT_DEFAULT_VALUE);
    pulseNumber = experimentPreferences.getInteger(DCPreferences.NUM_PULSES_INIT_KEY, DCPreferences.NUM_PULSES_INIT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_PREFERENCES_UPDATE, true, false);
  }

  void updateWaveformChartData() {

    Driver driver;
    switch (waveform) {
      case Sawtooth:
        driver = new Sawtooth("Sawtooth", 0, 0, amplitude, getCalculatedFrequency());
        break;
      case SawtoothUpDown:
        driver = new SawtoothUpDown("SawtoothUpDown", 0, 0, amplitude, getCalculatedFrequency());
        break;
      case Triangle:
        driver = new Triangle("Triangle", 0, 0, amplitude, getCalculatedFrequency());
        break;
      case TriangleUpDown:
        driver = new TriangleUpDown("Triangle", 0, 0, amplitude, getCalculatedFrequency());
        break;
      default:
        driver = new SawtoothUpDown("SawtoothUpDown", 0, 0, amplitude, getCalculatedFrequency());
        break;
    }

    double timeStep = 1 / getCalculatedFrequency() * pulseNumber / DCPreferences.CAPTURE_BUFFER_SIZE;

    int counter = 0;
    do {
      double time = counter * timeStep;
      waveformTimeData[counter] = time * DCPreferences.TIME_UNIT.getDivisor();
      waveformAmplitudeData[counter] = driver.getSignal(time);
    } while (++counter < DCPreferences.CAPTURE_BUFFER_SIZE);
  }

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   *
   * @param listener
   */
  public void addListener(PropertyChangeListener listener) {

    swingPropertyChangeSupport.addPropertyChangeListener(listener);
  }

  /////////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public DCPreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(DCPreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    waveform = Enum.valueOf(DCPreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getPeriod() {

    return period;
  }

  public double getCalculatedFrequency() {

    // System.out.println("period = " + period);
    return 1.0 / ((double) period) * DCPreferences.TIME_UNIT.getDivisor(); // 50% duty cycle
  }

  public void setPeriod(int period) {

    this.period = period;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public double[] getWaveformTimeData() {

    return waveformTimeData;
  }

  public double[] getWaveformAmplitudeData() {

    return waveformAmplitudeData;
  }

  public int getPulseNumber() {

    return pulseNumber;
  }

  public void setPulseNumber(int pulseNumber) {

    this.pulseNumber = pulseNumber;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new DCPreferences();
  }
}
