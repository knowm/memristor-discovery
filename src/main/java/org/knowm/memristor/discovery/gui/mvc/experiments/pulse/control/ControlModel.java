/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2020 Knowm Inc. www.knowm.org
 *
 * <p>This package also includes various components that are not part of Memristor-Discovery itself:
 *
 * <p>* `Multibit`: Copyright 2011 multibit.org, MIT License * `SteelCheckBox`: Copyright 2012
 * Gerrit, BSD license
 *
 * <p>Knowm, Inc. holds copyright and/or sufficient licenses to all components of the
 * Memristor-Discovery package, and therefore can grant, at its sole discretion, the ability for
 * companies, individuals, or organizations to create proprietary or open source (even if not GPL)
 * modules which may be dynamically linked at runtime with the portions of Memristor-Discovery which
 * fall under our copyright/license umbrella, or are distributed under more flexible licenses than
 * GPL.
 *
 * <p>The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * <p>If you have any questions regarding our licensing policy, please contact us at
 * `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control;

import java.text.DecimalFormat;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.core.driver.Driver;
import org.knowm.memristor.discovery.core.driver.pulse.HalfSinePulse;
import org.knowm.memristor.discovery.core.driver.pulse.QuarterSinePulse;
import org.knowm.memristor.discovery.core.driver.pulse.SquareDecayPulse;
import org.knowm.memristor.discovery.core.driver.pulse.SquareLongDecayPulse;
import org.knowm.memristor.discovery.core.driver.pulse.SquarePulse;
import org.knowm.memristor.discovery.core.driver.pulse.SquareSmoothPulse;
import org.knowm.memristor.discovery.core.driver.pulse.TrianglePulse;
import org.knowm.memristor.discovery.core.driver.waveform.Sawtooth;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.PulsePreferences;

public class ControlModel extends Model {

  private final DecimalFormat ohmFormatter = new DecimalFormat("#,### Ω");
  private final double[] waveformTimeData = new double[PulsePreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData = new double[PulsePreferences.CAPTURE_BUFFER_SIZE];

  public PulsePreferences.Waveform waveform;
  private boolean isMemristorVoltageDropSelected = false;
  private float amplitude;
  private int pulseWidth; // model store pulse width in nanoseconds

  private double dutyCycle; // 0 to 1.

  private int pulseNumber;
  private double appliedCurrent;
  private double appliedEnergy;
  private double lastG;
  private int sampleRate;

  private boolean isStartToggled = false;

  /** Constructor */
  public ControlModel() {}

  @Override
  public void doLoadModelFromPrefs(ExperimentPreferences experimentPreferences) {

    waveform =
        PulsePreferences.Waveform.valueOf(
            experimentPreferences.getString(
                PulsePreferences.WAVEFORM_INIT_STRING_KEY,
                PulsePreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    seriesResistance =
        experimentPreferences.getInteger(
            PulsePreferences.SERIES_R_INIT_KEY, PulsePreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude =
        experimentPreferences.getFloat(
            PulsePreferences.AMPLITUDE_INIT_FLOAT_KEY,
            PulsePreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    pulseWidth =
        experimentPreferences.getInteger(
            PulsePreferences.PULSE_WIDTH_INIT_KEY, PulsePreferences.PULSE_WIDTH_INIT_DEFAULT_VALUE);
    pulseNumber =
        experimentPreferences.getInteger(
            PulsePreferences.NUM_PULSES_INIT_KEY, PulsePreferences.NUM_PULSES_INIT_DEFAULT_VALUE);
    sampleRate =
        experimentPreferences.getInteger(
            PulsePreferences.SAMPLE_RATE_INIT_KEY, PulsePreferences.SAMPLE_RATE_INIT_DEFAULT_VALUE);

    dutyCycle =
        experimentPreferences.getFloat(
            PulsePreferences.PULSE_DUTY_CYCLE_KEY, PulsePreferences.PULSE_DUTY_CYCLE_DEFAULT_VALUE);

    updateWaveformChartData();
  }

  /** Given the state of the model, update the waveform x and y axis data arrays. */
  void updateWaveformChartData() {

    Driver driver;
    switch (waveform) {
      case Sawtooth:
        driver =
            new Sawtooth("Sawtooth", amplitude / 2, 0, amplitude / 2, getCalculatedFrequency());
        break;
      case QuarterSine:
        driver = new QuarterSinePulse("QuarterSine", 0, pulseWidth, dutyCycle, amplitude);
        break;
      case Triangle:
        driver = new TrianglePulse("Triangle", 0, pulseWidth, dutyCycle, amplitude);
        break;
      case Square:
        driver = new SquarePulse("Square", 0, pulseWidth, dutyCycle, amplitude);
        break;
      case SquareSmooth:
        driver = new SquareSmoothPulse("SquareSmooth", 0, pulseWidth, dutyCycle, amplitude);
        break;
      case SquareDecay:
        driver = new SquareDecayPulse("SquareDecay", 0, pulseWidth, dutyCycle, amplitude);
        break;
      case SquareLongDecay:
        driver = new SquareLongDecayPulse("SquareLongDecay", 0, pulseWidth, dutyCycle, amplitude);
        break;
      default:
        driver = new HalfSinePulse("HalfSine", 0, pulseWidth, dutyCycle, amplitude);
        break;
    }

    double stopTime = driver.getPeriod() * pulseNumber;
    double timeStep = driver.getPeriod() / PulsePreferences.CAPTURE_BUFFER_SIZE * pulseNumber;

    //    System.out.println("driver.getPeriod()=" + driver.getPeriod());
    //    System.out.println("stopTime=" + stopTime);
    //    System.out.println("timeStep=" + timeStep);

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= PulsePreferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i * PulsePreferences.TIME_UNIT.getDivisor();
      waveformAmplitudeData[counter++] = driver.getSignal(i);
    }
  }

  /////////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public void setSeriesResistance(int seriesResistance) {

    this.seriesResistance = seriesResistance;
  }

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getPulseWidth() {

    return pulseWidth;
  }

  public void setPulseWidth(int pulseWidth) {

    this.pulseWidth = pulseWidth;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public double getCalculatedFrequency() {
    return (1.0 / (pulseWidth / dutyCycle) * 1_000_000_000); // 50% duty cycle
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
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public boolean isMemristorVoltageDropSelected() {

    return isMemristorVoltageDropSelected;
  }

  public void setMemristorVoltageDropSelected(boolean memristorVoltageDropSelected) {

    isMemristorVoltageDropSelected = memristorVoltageDropSelected;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public PulsePreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(PulsePreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    waveform = Enum.valueOf(PulsePreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
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

  public String getLastRAsString() {

    return ohmFormatter.format(getLastR());
  }

  public void updateEnergyData() {

    this.appliedCurrent =
        amplitude
            / (getLastR() + seriesResistance + Util.getSwitchesSeriesResistance())
            * PulsePreferences.CURRENT_UNIT.getDivisor();
    this.appliedEnergy =
        amplitude
            * amplitude
            / (getLastR() + seriesResistance + Util.getSwitchesSeriesResistance())
            * pulseNumber
            * pulseWidth
            / 1E9;
  }

  public boolean isStartToggled() {

    return isStartToggled;
  }

  public void setStartToggled(boolean isStartToggled) {

    this.isStartToggled = isStartToggled;
  }

  public double getDutyCycle() {
    return dutyCycle;
  }

  public void setDutyCycle(double dutyCycle) {
    this.dutyCycle = dutyCycle;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

}
