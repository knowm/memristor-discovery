/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.control;

import java.text.DecimalFormat;
import org.knowm.memristor.discovery.core.driver.Driver;
import org.knowm.memristor.discovery.core.driver.waveform.HalfSine;
import org.knowm.memristor.discovery.core.driver.waveform.QuarterSine;
import org.knowm.memristor.discovery.core.driver.waveform.Sawtooth;
import org.knowm.memristor.discovery.core.driver.waveform.Square;
import org.knowm.memristor.discovery.core.driver.waveform.SquareSmooth;
import org.knowm.memristor.discovery.core.driver.waveform.Triangle;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.KTRAM_Controller_12.Instruction12;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.Synapse12Preferences;

public class ControlModel extends Model {

  /** Events */
  public static final String EVENT_INSTRUCTION_UPDATE = "EVENT_INSTRUCTION_UPDATE";

  private final double[] waveformTimeData = new double[Synapse12Preferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData =
      new double[Synapse12Preferences.CAPTURE_BUFFER_SIZE];
  /** Waveform */
  public Synapse12Preferences.Waveform waveform;

  public Instruction12 instruction = Instruction12.FLV;
  public DecimalFormat ohmFormatter = new DecimalFormat("#,### Ω");

  private float amplitude;
  private float amplitudeReverse;

  private int pulseWidth; // model store pulse width in nanoseconds
  private int pulseNumber;
  private double lastY;
  private int sampleRate;

  private boolean isStartToggled = false;

  /** Constructor */
  public ControlModel() {

    // updateWaveformChartData();
  }

  @Override
  public void doLoadModelFromPrefs(ExperimentPreferences experimentPreferences) {

    seriesResistance =
        experimentPreferences.getInteger(
            Synapse12Preferences.SERIES_R_INIT_KEY,
            Synapse12Preferences.SERIES_R_INIT_DEFAULT_VALUE);
    waveform =
        Synapse12Preferences.Waveform.valueOf(
            experimentPreferences.getString(
                Synapse12Preferences.WAVEFORM_INIT_STRING_KEY,
                Synapse12Preferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));

    amplitude =
        experimentPreferences.getFloat(
            Synapse12Preferences.AMPLITUDE_INIT_FLOAT_KEY,
            Synapse12Preferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);

    amplitudeReverse =
        experimentPreferences.getFloat(
            Synapse12Preferences.AMPLITUDE_REVERSE_INIT_FLOAT_KEY,
            Synapse12Preferences.AMPLITUDE_REVERSE_INIT_FLOAT_DEFAULT_VALUE);

    pulseWidth =
        experimentPreferences.getInteger(
            Synapse12Preferences.PULSE_WIDTH_INIT_KEY,
            Synapse12Preferences.PULSE_WIDTH_INIT_DEFAULT_VALUE);
    pulseNumber =
        experimentPreferences.getInteger(
            Synapse12Preferences.NUM_PULSES_INIT_KEY,
            Synapse12Preferences.NUM_PULSES_INIT_DEFAULT_VALUE);
    sampleRate =
        experimentPreferences.getInteger(
            Synapse12Preferences.SAMPLE_RATE_INIT_KEY,
            Synapse12Preferences.SAMPLE_RATE_INIT_DEFAULT_VALUE);
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
    double timeStep =
        1 / getCalculatedFrequency() / Synapse12Preferences.CAPTURE_BUFFER_SIZE * pulseNumber;

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= Synapse12Preferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i * Synapse12Preferences.TIME_UNIT.getDivisor();
      waveformAmplitudeData[counter++] = driver.getSignal(i);
    }
  }

  // ///////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  // ///////////////////////////////////////////////////////////

  public float getForwardAmplitude() {

    return amplitude;
  }

  public float getReverseAmplitude() {

    return amplitudeReverse;
  }

  public void setForwardAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setReverseAmplitude(float amplitude) {

    this.amplitudeReverse = amplitude;
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

    // System.out.println("pulseWidth = " + pulseWidth);
    return (1.0 / (2.0 * pulseWidth) * 1_000_000_000); // 50% duty cycle
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

  public Synapse12Preferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(Synapse12Preferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    this.waveform = Enum.valueOf(Synapse12Preferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public Instruction12 getInstruction() {

    return instruction;
  }

  public void setInstruction(Instruction12 instruction) {

    this.instruction = instruction;
    swingPropertyChangeSupport.firePropertyChange(EVENT_INSTRUCTION_UPDATE, true, false);
  }

  public void setInstruction(String text) {

    this.instruction = Enum.valueOf(Instruction12.class, text);
    swingPropertyChangeSupport.firePropertyChange(EVENT_INSTRUCTION_UPDATE, true, false);
  }

  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public double getLastY() {

    return lastY;
  }

  public void setLastY(double lastY) {

    this.lastY = lastY;
  }

  public boolean isStartToggled() {

    return isStartToggled;
  }

  public void setStartToggled(boolean isStartToggled) {

    this.isStartToggled = isStartToggled;
  }
}
