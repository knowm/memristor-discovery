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
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control;

import java.text.DecimalFormat;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.PulsePreferences;
import org.knowm.memristor.discovery.utils.Util;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.HalfSine;
import org.knowm.memristor.discovery.utils.driver.QuarterSine;
import org.knowm.memristor.discovery.utils.driver.Sawtooth;
import org.knowm.memristor.discovery.utils.driver.Square;
import org.knowm.memristor.discovery.utils.driver.SquareSmooth;
import org.knowm.memristor.discovery.utils.driver.Triangle;

public class ControlModel extends ExperimentControlModel {

  /**
   * Waveform
   */
  public PulsePreferences.Waveform waveform;

  private boolean isMemristorVoltageDropSelected = false;
  private float amplitude;
  private int pulseWidth; // model store pulse width in nanoseconds
  private int pulseNumber;
  private double appliedAmplitude;
  private double appliedCurrent;
  private double appliedEnergy;
  private double appliedMemristorEnergy;

  private double lastG;
  public DecimalFormat ohmFormatter = new DecimalFormat("#,### Ω");

  private final double[] waveformTimeData = new double[PulsePreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData = new double[PulsePreferences.CAPTURE_BUFFER_SIZE];

  /**
   * Constructor
   */
  public ControlModel() {

    updateWaveformChartData();
  }

  @Override
  public void loadModelFromPrefs() {

    waveform = PulsePreferences.Waveform.valueOf(experimentPreferences.getString(PulsePreferences.WAVEFORM_INIT_STRING_KEY, PulsePreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    seriesResistance = experimentPreferences.getInteger(PulsePreferences.SERIES_R_INIT_KEY, PulsePreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude = experimentPreferences.getFloat(PulsePreferences.AMPLITUDE_INIT_FLOAT_KEY, PulsePreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    appliedAmplitude = amplitude;
    pulseWidth = experimentPreferences.getInteger(PulsePreferences.PULSE_WIDTH_INIT_KEY, PulsePreferences.PULSE_WIDTH_INIT_DEFAULT_VALUE);
    pulseNumber = experimentPreferences.getInteger(PulsePreferences.NUM_PULSES_INIT_KEY, PulsePreferences.NUM_PULSES_INIT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_PREFERENCES_UPDATE, true, false);
  }

  /**
   * Given the state of the model, update the waveform x and y axis data arrays.
   */
  void updateWaveformChartData() {

    Driver driver;
    switch (waveform) {
      case Sawtooth:
        driver = new Sawtooth("Sawtooth", amplitude / 2, 0, amplitude / 2, getCalculatedFrequency());
        break;
      case QuarterSine:
        driver = new QuarterSine("QuarterSine", 0, 0, amplitude, getCalculatedFrequency());
        break;
      case Triangle:
        driver = new Triangle("Triangle", 0, 0, amplitude, getCalculatedFrequency());
        break;
      case Square:
        driver = new Square("Square", amplitude / 2, 0, amplitude / 2, getCalculatedFrequency());
        break;
      case SquareSmooth:
        driver = new SquareSmooth("SquareSmooth", 0, 0, amplitude, getCalculatedFrequency());
        break;
      default:
        driver = new HalfSine("HalfSine", 0, 0, amplitude, getCalculatedFrequency());
        break;
    }

    double stopTime = 1 / getCalculatedFrequency() * pulseNumber;
    double timeStep = 1 / getCalculatedFrequency() / PulsePreferences.CAPTURE_BUFFER_SIZE * pulseNumber;

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= PulsePreferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i * 1_000_000;
      waveformAmplitudeData[counter++] = driver.getSignal(i);
    }
  }

  /////////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getPulseWidth() {

    return pulseWidth;
  }

  public double getCalculatedFrequency() {

    // System.out.println("pulseWidth = " + pulseWidth);
    return (1.0 / (2.0 * (double) pulseWidth) * 1_000_000_000); // 50% duty cycle
  }

  public void setPulseWidth(int pulseWidth) {

    this.pulseWidth = pulseWidth;
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

  public boolean isMemristorVoltageDropSelected() {

    return isMemristorVoltageDropSelected;
  }

  public void setMemristorVoltageDropSelected(boolean memristorVoltageDropSelected) {

    isMemristorVoltageDropSelected = memristorVoltageDropSelected;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public double getAppliedAmplitude() {

    return appliedAmplitude;
  }

  public PulsePreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(PulsePreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    waveform = Enum.valueOf(PulsePreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public double getLastG() {

    return lastG;
  }

  public void setLastG(double lastG) {

    this.lastG = lastG;
  }

  public double getLastR() {

    return 1.0 / lastG * PulsePreferences.CONDUCTANCE_UNIT.getDivisor();
  }

  public double getAppliedCurrent() {

    return appliedCurrent;
  }

  public double getAppliedEnergy() {

    return appliedEnergy;
  }

  public double getAppliedMemristorEnergy() {

    return appliedMemristorEnergy;
  }

  public String getLastRAsString() {

    return ohmFormatter.format(getLastR());
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new PulsePreferences();
  }

  public void updateEnergyData() {

    // calculate applied voltage
    if (lastG > 0.0) {
      if (isMemristorVoltageDropSelected) {
        this.appliedAmplitude = amplitude / (1 - seriesResistance / (seriesResistance + getLastR() + Util.getSwitchesSeriesResistance()));
      }
      else {
        this.appliedAmplitude = amplitude;
      }
      this.appliedCurrent = appliedAmplitude / (getLastR() + seriesResistance + Util.getSwitchesSeriesResistance()) * PulsePreferences.CURRENT_UNIT.getDivisor();
      this.appliedEnergy = appliedAmplitude * appliedAmplitude / (getLastR() + seriesResistance + Util.getSwitchesSeriesResistance()) * pulseNumber * pulseWidth / 2;// divided by two to guestimate the energy savings of a quarter sine wave vs a square wave.

      // V=IR =
      double voltageDropOnMemristor = appliedCurrent / PulsePreferences.CURRENT_UNIT.getDivisor() * getLastR();
      System.out.println("voltageDropOnMemristor = " + voltageDropOnMemristor);
      this.appliedMemristorEnergy = voltageDropOnMemristor * voltageDropOnMemristor / getLastR() * pulseNumber * pulseWidth / 2 * 1000;// divided by two to guestimate the energy savings of a quarter sine wave vs a square wave.
      System.out.println("appliedMemristorEnergy = " + appliedMemristorEnergy);
    }
    else {
      this.appliedAmplitude = amplitude;
    }
  }
}
