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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify.control;

import java.text.DecimalFormat;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.ClassifyPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.ClassifyPreferences.AHaHRoutine;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.ClassifyPreferences.Datasets;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.HalfSine;
import org.knowm.memristor.discovery.utils.driver.QuarterSine;
import org.knowm.memristor.discovery.utils.driver.Sawtooth;
import org.knowm.memristor.discovery.utils.driver.Square;
import org.knowm.memristor.discovery.utils.driver.SquareSmooth;
import org.knowm.memristor.discovery.utils.driver.Triangle;

public class ControlModel extends ExperimentControlModel {

  /** Waveform */
  public ClassifyPreferences.Waveform waveform;

  /** Events */
  public static final String EVENT_INSTRUCTION_UPDATE = "EVENT_INSTRUCTION_UPDATE";

  private float amplitude;
  private int pulseWidth; // model store pulse width in nanoseconds
  // private int pulseNumber = 1;
  public DecimalFormat ohmFormatter = new DecimalFormat("#,### Ω");
  private final double[] waveformTimeData = new double[ClassifyPreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData =
      new double[ClassifyPreferences.CAPTURE_BUFFER_SIZE];
  private int numTrainEpochs;
  public ClassifyPreferences.Datasets dataset = Datasets.Ortho4Pattern;
  public AHaHRoutine ahahroutine = AHaHRoutine.LearnAlways;

  /** Constructor */
  public ControlModel() {

    updateWaveformChartData();
  }

  @Override
  public void loadModelFromPrefs() {

    waveform =
        ClassifyPreferences.Waveform.valueOf(
            experimentPreferences.getString(
                ClassifyPreferences.WAVEFORM_INIT_STRING_KEY,
                ClassifyPreferences.WAVEFORM_INIT_STRING_DEFAULT_VALUE));
    seriesResistance =
        experimentPreferences.getInteger(
            ClassifyPreferences.SERIES_R_INIT_KEY, ClassifyPreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude =
        experimentPreferences.getFloat(
            ClassifyPreferences.AMPLITUDE_INIT_FLOAT_KEY,
            ClassifyPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    pulseWidth =
        experimentPreferences.getInteger(
            ClassifyPreferences.PULSE_WIDTH_INIT_KEY,
            ClassifyPreferences.PULSE_WIDTH_INIT_DEFAULT_VALUE);

    numTrainEpochs =
        experimentPreferences.getInteger(
            ClassifyPreferences.NUM_TRAIN_EPOCHS_INIT_KEY,
            ClassifyPreferences.NUM_TRAIN_EPOCHS_INIT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(
        ExperimentControlModel.EVENT_PREFERENCES_UPDATE, true, false);
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

    // double stopTime = 1 / getCalculatedFrequency() * pulseNumber;
    //    double timeStep = 1 / getCalculatedFrequency() / ClassifyPreferences.CAPTURE_BUFFER_SIZE *
    // pulseNumber;

    //    int counter = 0;
    //    for (double i = 0.0; i < stopTime; i = i + timeStep) {
    //      if (counter >= ClassifyPreferences.CAPTURE_BUFFER_SIZE) {
    //        break;
    //      }
    //      waveformTimeData[counter] = i * ClassifyPreferences.TIME_UNIT.getDivisor();
    //      waveformAmplitudeData[counter++] = driver.getSignal(i);
    //    }
  }

  // ///////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  // ///////////////////////////////////////////////////////////

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(
        ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getPulseWidth() {

    return pulseWidth;
  }

  public double getCalculatedFrequency() {

    // System.out.println("pulseWidth = " + pulseWidth);
    return (1.0 / (2.0 * pulseWidth) * 1_000_000_000); // 50% duty cycle
  }

  public void setPulseWidth(int pulseWidth) {

    this.pulseWidth = pulseWidth;
    swingPropertyChangeSupport.firePropertyChange(
        ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public double[] getWaveformTimeData() {

    return waveformTimeData;
  }

  public double[] getWaveformAmplitudeData() {

    return waveformAmplitudeData;
  }

  //  public int getPulseNumber() {
  //
  //    return pulseNumber;
  //  }
  //
  //  public void setPulseNumber(int pulseNumber) {
  //
  //    this.pulseNumber = pulseNumber;
  //    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_WAVEFORM_UPDATE,
  // true, false);
  //  }

  public ClassifyPreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(ClassifyPreferences.Waveform waveform) {

    this.waveform = waveform;
    swingPropertyChangeSupport.firePropertyChange(
        ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public void setWaveform(String text) {

    this.waveform = Enum.valueOf(ClassifyPreferences.Waveform.class, text);
    swingPropertyChangeSupport.firePropertyChange(
        ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getNumTrainEpochs() {

    return numTrainEpochs;
  }

  public void setNumTrainEpochs(int epochs) {

    this.numTrainEpochs = epochs;
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new ClassifyPreferences();
  }

  public ClassifyPreferences.Datasets getDataset() {

    return dataset;
  }

  public void setDataStructure(String text) {

    this.dataset = Enum.valueOf(ClassifyPreferences.Datasets.class, text);
  }

  public AHaHRoutine getAhahroutine() {
    return ahahroutine;
  }
}
