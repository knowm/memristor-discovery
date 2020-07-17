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
package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control;

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
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.kTBitSatSolverPreferences;

public class ControlModel extends Model {

  /** Events */
  public static final String EVENT_INSTRUCTION_UPDATE = "EVENT_INSTRUCTION_UPDATE";

  private final double[] waveformTimeData =
      new double[kTBitSatSolverPreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData =
      new double[kTBitSatSolverPreferences.CAPTURE_BUFFER_SIZE];
  /** Waveform */
  public kTBitSatSolverPreferences.Waveform waveform;

  private int pulseNumber = 1;
  public DecimalFormat ohmFormatter = new DecimalFormat("#,### Ω");

  private float amplitude;
  private float amplitudeReverse;

  private int pulseWidth; // model store pulse width in nanoseconds
  private int numTrainEpochs;

  private String filePath = "";

  /** Constructor */
  public ControlModel() {

    // updateWaveformChartData();
  }

  @Override
  public void doLoadModelFromPrefs(ExperimentPreferences experimentPreferences) {

    filePath =
        experimentPreferences.getString(
            kTBitSatSolverPreferences.FILE_PATH_INIT_KEY,
            kTBitSatSolverPreferences.FILE_PATH_INIT_DEFAULT_VALUE);

    waveform =
        kTBitSatSolverPreferences.Waveform.valueOf(
            experimentPreferences.getString(
                kTBitSatSolverPreferences.WAVEFORM_INIT_STRING_KEY,
                kTBitSatSolverPreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));

    seriesResistance =
        experimentPreferences.getInteger(
            kTBitSatSolverPreferences.SERIES_R_INIT_KEY,
            kTBitSatSolverPreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude =
        experimentPreferences.getFloat(
            kTBitSatSolverPreferences.AMPLITUDE_INIT_FLOAT_KEY,
            kTBitSatSolverPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);

    amplitudeReverse =
        experimentPreferences.getFloat(
            kTBitSatSolverPreferences.AMPLITUDE_REVERSE_INIT_FLOAT_KEY,
            kTBitSatSolverPreferences.AMPLITUDE_REVERSE_INIT_FLOAT_DEFAULT_VALUE);
    pulseWidth =
        experimentPreferences.getInteger(
            kTBitSatSolverPreferences.PULSE_WIDTH_INIT_KEY,
            kTBitSatSolverPreferences.PULSE_WIDTH_INIT_DEFAULT_VALUE);

    numTrainEpochs =
        experimentPreferences.getInteger(
            kTBitSatSolverPreferences.NUM_TRAIN_EPOCHS_INIT_KEY,
            kTBitSatSolverPreferences.NUM_TRAIN_EPOCHS_INIT_DEFAULT_VALUE);
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
        1 / getCalculatedFrequency() / kTBitSatSolverPreferences.CAPTURE_BUFFER_SIZE * pulseNumber;

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= kTBitSatSolverPreferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i * kTBitSatSolverPreferences.TIME_UNIT.getDivisor();
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

  public kTBitSatSolverPreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(kTBitSatSolverPreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    this.waveform = Enum.valueOf(kTBitSatSolverPreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getNumTrainEpochs() {

    return numTrainEpochs;
  }

  public void setNumTrainEpochs(int epochs) {

    this.numTrainEpochs = epochs;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
}
