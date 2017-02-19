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

import java.beans.PropertyChangeListener;

import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.ConductancePreferences;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.Sawtooth;
import org.knowm.memristor.discovery.utils.driver.SawtoothUpDown;
import org.knowm.memristor.discovery.utils.driver.Triangle;
import org.knowm.memristor.discovery.utils.driver.TriangleUpDown;

public class ExperimentModel extends AppModel {

  /**
   * Waveform
   */
  public ConductancePreferences.Waveform waveform;

  private float amplitude;
  private int period; // model store period in nanoseconds
  private int pulseNumber = 1;

  /**
   * Series Resistor
   */
  private int seriesResistance;

  private final double[] waveformTimeData = new double[ConductancePreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData = new double[ConductancePreferences.CAPTURE_BUFFER_SIZE];

  /**
   * Constructor
   */
  public ExperimentModel() {

    updateWaveformChartData();
  }

  @Override
  public void loadModelFromPrefs() {

    // load model from prefs
    waveform = ConductancePreferences.Waveform.valueOf(appPreferences.getString(ConductancePreferences.WAVEFORM_INIT_STRING_KEY, ConductancePreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    // waveform = Waveform.Sawtooth;
    seriesResistance = appPreferences.getInteger(ConductancePreferences.SERIES_R_INIT_KEY, ConductancePreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude = appPreferences.getFloat(ConductancePreferences.AMPLITUDE_INIT_FLOAT_KEY, ConductancePreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    period = appPreferences.getInteger(ConductancePreferences.PERIOD_INIT_KEY, ConductancePreferences.PERIOD_INIT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_PREFERENCES_UPDATE, true, false);
  }

  /**
   * Given the state of the model, update the waveform x and y axis data arrays.
   */
  void updateWaveformChartData() {

    // TODO Do this better, time axis is not correct

    // double[] waveform = WaveformUtils.generatePositiveAndNegativeDCRamps(amplitude);
    //
    // // double stopTime = 1 / getCalculatedFrequency() * ConductancePreferences.CAPTURE_PERIOD_COUNT * pulseNumber;
    // // double timeStep = 1 / getCalculatedFrequency() * ConductancePreferences.CAPTURE_PERIOD_COUNT / ConductancePreferences.CAPTURE_BUFFER_SIZE * pulseNumber;
    //
    // int counter = 0;
    // for (int i = 0; i < ConductancePreferences.CAPTURE_BUFFER_SIZE; i++) {
    //   waveformAmplitudeData[counter++] = waveform[i * waveform.length / ConductancePreferences.CAPTURE_BUFFER_SIZE];
    // }

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

    double timeStep = 1 / getCalculatedFrequency() * pulseNumber / ConductancePreferences.CAPTURE_BUFFER_SIZE;

    int counter = 0;
    do {
      double time = counter * timeStep;
      waveformTimeData[counter] = time * 1_000_000;
      waveformAmplitudeData[counter] = driver.getSignal(time);
    } while (++counter < ConductancePreferences.CAPTURE_BUFFER_SIZE);

    // System.out.println("Arrays.toString(waveformTimeData) = " + Arrays.toString(waveformTimeData));
    // System.out.println("Arrays.toString(waveformAmplitudeData) = " + Arrays.toString(waveformAmplitudeData));
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

  public ConductancePreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(ConductancePreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    waveform = Enum.valueOf(ConductancePreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getPeriod() {

    return period;
  }

  public double getCalculatedFrequency() {

    System.out.println("period = " + period);
    // return (1.0 / (2.0 * (double) period) * 1_000_000_000); // 50% duty cycle
    return (1.0 / ((double) period) * 1_000_000_000); // 50% duty cycle
  }

  public void setPeriod(int period) {

    this.period = period;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
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
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getSeriesR() {

    return seriesResistance;
  }

  public void setSeriesR(int seriesResistance) {

    this.seriesResistance = seriesResistance;
  }

  @Override
  public AppPreferences initAppPreferences() {

    return new ConductancePreferences();
  }
}
