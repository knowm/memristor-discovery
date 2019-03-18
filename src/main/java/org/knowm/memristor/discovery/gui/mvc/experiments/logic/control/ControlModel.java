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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic.control;

import java.text.DecimalFormat;
import java.util.List;
import org.knowm.memristor.discovery.core.driver.Driver;
import org.knowm.memristor.discovery.core.driver.waveform.HalfSine;
import org.knowm.memristor.discovery.core.driver.waveform.QuarterSine;
import org.knowm.memristor.discovery.core.driver.waveform.Sawtooth;
import org.knowm.memristor.discovery.core.driver.waveform.Square;
import org.knowm.memristor.discovery.core.driver.waveform.SquareSmooth;
import org.knowm.memristor.discovery.core.driver.waveform.Triangle;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.AHaHController_21.AHaHLogicRoutine;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicPreferences.DataStructure;

public class ControlModel extends Model {

  /** Events */
  public static final String EVENT_INSTRUCTION_UPDATE = "EVENT_INSTRUCTION_UPDATE";

  private final double[] waveformTimeData = new double[LogicPreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData = new double[LogicPreferences.CAPTURE_BUFFER_SIZE];
  /** Waveform */
  public LogicPreferences.Waveform waveform;

  public AHaHLogicRoutine routine = AHaHLogicRoutine.FFRU_Trace;
  public DecimalFormat ohmFormatter = new DecimalFormat("#,### Ω");
  public LogicPreferences.DataStructure dataStructure = DataStructure.TwoPattern;
  private float amplitude;
  private int pulseWidth; // model store pulse width in nanoseconds
  private int pulseNumber;
  private double lastY;
  private int numExecutions;
  private List<Integer> inputMaskA;
  private List<Integer> inputMaskB;
  private List<Integer> inputBiasMask;

  /** Constructor */
  public ControlModel() {

    // updateWaveformChartData();
  }

  @Override
  public void doLoadModelFromPrefs(ExperimentPreferences experimentPreferences) {

    waveform = LogicPreferences.Waveform
        .valueOf(experimentPreferences.getString(LogicPreferences.WAVEFORM_INIT_STRING_KEY, LogicPreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));

    System.out.println("waveform: " + waveform);

    seriesResistance = experimentPreferences.getInteger(LogicPreferences.SERIES_R_INIT_KEY, LogicPreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude = experimentPreferences.getFloat(LogicPreferences.AMPLITUDE_INIT_FLOAT_KEY, LogicPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    pulseWidth = experimentPreferences.getInteger(LogicPreferences.PULSE_WIDTH_INIT_KEY, LogicPreferences.PULSE_WIDTH_INIT_DEFAULT_VALUE);
    pulseNumber = experimentPreferences.getInteger(LogicPreferences.NUM_PULSES_INIT_KEY, LogicPreferences.NUM_PULSES_INIT_DEFAULT_VALUE);
    numExecutions = experimentPreferences.getInteger(LogicPreferences.NUM_EXECUTIONS_INIT_KEY, LogicPreferences.NUM_EXECUTIONS_INIT_DEFAULT_VALUE);
  }

  /** Given the state of the model, update the waveform x and y axis data arrays. */
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
    double timeStep = 1 / getCalculatedFrequency() / LogicPreferences.CAPTURE_BUFFER_SIZE * pulseNumber;

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= LogicPreferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i * LogicPreferences.TIME_UNIT.getDivisor();
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

  public LogicPreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(LogicPreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    this.waveform = Enum.valueOf(LogicPreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public AHaHLogicRoutine getRoutine() {

    return routine;
  }

  public void setAHaHLogicRoutine(AHaHLogicRoutine routine) {

    this.routine = routine;
    swingPropertyChangeSupport.firePropertyChange(EVENT_INSTRUCTION_UPDATE, true, false);
  }

  public void setInstruction(String text) {

    this.routine = Enum.valueOf(AHaHLogicRoutine.class, text);
    swingPropertyChangeSupport.firePropertyChange(EVENT_INSTRUCTION_UPDATE, true, false);
  }

  public int getNumExecutions() {

    return numExecutions;
  }

  public void setNumExecutions(int sampleRate) {

    this.numExecutions = sampleRate;
  }

  public double getLastY() {

    return lastY;
  }

  public void setLastY(double lastY) {

    this.lastY = lastY;
  }

  public List<Integer> getInputMaskA() {

    return inputMaskA;
  }

  public void setInputMaskA(List<Integer> inputMaskA) {

    this.inputMaskA = inputMaskA;
  }

  public List<Integer> getInputMaskB() {

    return inputMaskB;
  }

  public void setInputMaskB(List<Integer> inputMaskB) {

    this.inputMaskB = inputMaskB;
  }

  public LogicPreferences.DataStructure getDataStructure() {

    return dataStructure;
  }

  public void setDataStructure(String text) {

    this.dataStructure = Enum.valueOf(LogicPreferences.DataStructure.class, text);
  }

  public List<Integer> getInputBiasMask() {

    return inputBiasMask;
  }

  public void setInputBiasMask(List<Integer> inputBiasMask) {

    this.inputBiasMask = inputBiasMask;
  }
}
