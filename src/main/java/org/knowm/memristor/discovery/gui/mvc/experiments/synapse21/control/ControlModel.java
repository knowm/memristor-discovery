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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.control;

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
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.Synapse12Preferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.KTRAM_Controller_21.Instruction21;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.Synapse21Preferences;

public class ControlModel extends Model {

  /** Events */
  public static final String EVENT_INSTRUCTION_UPDATE = "EVENT_INSTRUCTION_UPDATE";

  private final double[] waveformTimeData = new double[Synapse21Preferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData =
      new double[Synapse21Preferences.CAPTURE_BUFFER_SIZE];
  /** Waveform */
  public Synapse21Preferences.Waveform waveform;

  public Instruction21 instruction = Instruction21.FFLV;
  public DecimalFormat ohmFormatter = new DecimalFormat("#,### Ω");
  private float amplitude;
  private int pulseWidth; // model store pulse width in nanoseconds
  private int pulseNumber;
  private double lastY;
  private int sampleRate;

  private boolean isStartToggled = false;

  private double scopeOneOffset;
  private double scopeTwoOffset;
  private double wOneOffset;

  /** Constructor */
  public ControlModel() {

    // updateWaveformChartData();
  }

  @Override
  public void doLoadModelFromPrefs(ExperimentPreferences experimentPreferences) {

    seriesResistance =
        experimentPreferences.getInteger(
            Synapse21Preferences.SERIES_R_INIT_KEY,
            Synapse21Preferences.SERIES_R_INIT_DEFAULT_VALUE);
    waveform =
        Synapse21Preferences.Waveform.valueOf(
            experimentPreferences.getString(
                Synapse21Preferences.WAVEFORM_INIT_STRING_KEY,
                Synapse21Preferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));

    amplitude =
        experimentPreferences.getFloat(
            Synapse21Preferences.AMPLITUDE_INIT_FLOAT_KEY,
            Synapse21Preferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    pulseWidth =
        experimentPreferences.getInteger(
            Synapse21Preferences.PULSE_WIDTH_INIT_KEY,
            Synapse21Preferences.PULSE_WIDTH_INIT_DEFAULT_VALUE);
    pulseNumber =
        experimentPreferences.getInteger(
            Synapse21Preferences.NUM_PULSES_INIT_KEY,
            Synapse21Preferences.NUM_PULSES_INIT_DEFAULT_VALUE);
    sampleRate =
        experimentPreferences.getInteger(
            Synapse21Preferences.SAMPLE_RATE_INIT_KEY,
            Synapse21Preferences.SAMPLE_RATE_INIT_DEFAULT_VALUE);

    scopeOneOffset =
        experimentPreferences.getFloat(
            Synapse12Preferences.SCOPE_ONE_OFFSET_KEY,
            Synapse12Preferences.SCOPE_ONE_OFFSET_DEFAULT_VALUE);
    scopeTwoOffset =
        experimentPreferences.getFloat(
            Synapse12Preferences.SCOPE_TWO_OFFSET_KEY,
            Synapse12Preferences.SCOPE_TWO_OFFSET_DEFAULT_VALUE);
    wOneOffset =
        experimentPreferences.getFloat(
            Synapse12Preferences.W_ONE_OFFSET_KEY, Synapse12Preferences.W_ONE_OFFSET_DEFAULT_VALUE);
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
        1 / getCalculatedFrequency() / Synapse21Preferences.CAPTURE_BUFFER_SIZE * pulseNumber;

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= Synapse21Preferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i * Synapse21Preferences.TIME_UNIT.getDivisor();
      waveformAmplitudeData[counter++] = driver.getSignal(i);
    }
  }

  // ///////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  // ///////////////////////////////////////////////////////////

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

  public Synapse21Preferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(Synapse21Preferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    this.waveform = Enum.valueOf(Synapse21Preferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public Instruction21 getInstruction() {

    return instruction;
  }

  public void setInstruction(Instruction21 instruction) {

    this.instruction = instruction;
    swingPropertyChangeSupport.firePropertyChange(EVENT_INSTRUCTION_UPDATE, true, false);
  }

  public void setInstruction(String text) {

    this.instruction = Enum.valueOf(Instruction21.class, text);
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

  public double getScopeOneOffset() {
    return scopeOneOffset;
  }

  public double getScopeTwoOffset() {
    return scopeTwoOffset;
  }

  public double getwOneOffset() {
    return wOneOffset;
  }
}
