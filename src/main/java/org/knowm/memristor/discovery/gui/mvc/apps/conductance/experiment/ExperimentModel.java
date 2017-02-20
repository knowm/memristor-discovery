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
import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.ConductancePreferences;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.Sawtooth;
import org.knowm.memristor.discovery.utils.driver.Triangle;

public class ExperimentModel extends AppModel {

  // RESET
  private Waveform resetPulseType;
  private float resetAmplitude;
  private int resetPulseWidth; // model store resetPulseWidth in nanoseconds

  // SET
  private float setConductance;
  private float setAmplitude;
  private int setPulseWidth; // model store resetPulseWidth in nanoseconds

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

    // RESET
    resetPulseType = ConductancePreferences.Waveform.valueOf(appPreferences.getString(ConductancePreferences.RESET_PULSE_TYPE_INIT_STRING_KEY, ConductancePreferences.RESET_PULSE_TYPE_INIT_STRING_DEFAULT_VALUE));
    resetAmplitude = appPreferences.getFloat(ConductancePreferences.RESET_AMPLITUDE_INIT_FLOAT_KEY, ConductancePreferences.RESET_AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    resetPulseWidth = appPreferences.getInteger(ConductancePreferences.RESET_PULSE_WIDTH_INIT_KEY, ConductancePreferences.RESET_PERIOD_INIT_DEFAULT_VALUE);

    // SET
    setConductance = appPreferences.getFloat(ConductancePreferences.SET_CONDUCTANCE_INIT_KEY, ConductancePreferences.SET_CONDUCTANCE_INIT_DEFAULT_VALUE);
    setAmplitude = appPreferences.getFloat(ConductancePreferences.SET_AMPLITUDE_INIT_FLOAT_KEY, ConductancePreferences.SET_AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    setPulseWidth = appPreferences.getInteger(ConductancePreferences.SET_PULSE_WIDTH_INIT_KEY, ConductancePreferences.SET_PERIOD_INIT_DEFAULT_VALUE);

    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_PREFERENCES_UPDATE, true, false);
    seriesResistance = appPreferences.getInteger(ConductancePreferences.SERIES_R_INIT_KEY, ConductancePreferences.SERIES_R_INIT_DEFAULT_VALUE);
  }

  /**
   * Given the state of the model, update the resetPulseType x and y axis data arrays.
   */
  void updateWaveformChartData() {

    Driver driver;
    switch (resetPulseType) {
      case Sawtooth:
        driver = new Sawtooth("Sawtooth", 0, 0, resetAmplitude, getCalculatedFrequency());
        break;
      case Triangle:
        driver = new Triangle("Triangle", 0, 0, resetAmplitude, getCalculatedFrequency());
        break;
      default:
        driver = new Sawtooth("Sawtooth", 0, 0, resetAmplitude, getCalculatedFrequency());
        break;
    }

    double timeStep = 1 / getCalculatedFrequency() / ConductancePreferences.CAPTURE_BUFFER_SIZE;

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

  public ConductancePreferences.Waveform getResetPulseType() {

    return resetPulseType;
  }

  public void setResetPulseType(ConductancePreferences.Waveform resetPulseType) {

    this.resetPulseType = resetPulseType;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    resetPulseType = Enum.valueOf(ConductancePreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public float getResetAmplitude() {

    return resetAmplitude;
  }

  public void setResetAmplitude(float resetAmplitude) {

    this.resetAmplitude = resetAmplitude;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getResetPulseWidth() {

    return resetPulseWidth;
  }

  public void setResetPulseWidth(int resetPulseWidth) {

    this.resetPulseWidth = resetPulseWidth;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  // SET

  public float getSetConductance() {

    return setConductance;
  }

  public void setSetConductance(float setConductance) {

    this.setConductance = setConductance;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public float getSetAmplitude() {

    return setAmplitude;
  }

  public void setSetAmplitude(float setAmplitude) {

    this.setAmplitude = setAmplitude;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getSetPulseWidth() {

    return setPulseWidth;
  }

  public void setSetPulseWidth(int setPulseWidth) {

    this.setPulseWidth = setPulseWidth;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public double[] getWaveformTimeData() {

    return waveformTimeData;
  }

  public double[] getWaveformAmplitudeData() {

    return waveformAmplitudeData;
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

  public double getCalculatedFrequency() {

    return (1.0 / (2.0 * (double) resetPulseWidth) * 1_000_000_000); // 50% duty cycle
    // return (1.0 / ((double) resetPulseWidth) * 1_000_000_000); // 50% duty cycle
  }
}
