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
package org.knowm.memristor.discovery.gui.mvc.apps.qc;

import java.beans.PropertyChangeListener;
import java.io.File;

import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.Sine;
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.Waveform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QCModel extends AppModel {

  private final Logger logger = LoggerFactory.getLogger(QCModel.class);

  /** Save Path */
  private String savePath;
  private String serialNumber = "";

  private ChipType chipType = ChipType.BSAF_W;

  public enum ChipType {
    BSAF_W, BSAF_Sn, BSAF_Cr, BSAF_C
  };

  /** Waveform */
  public final DWF.Waveform waveform = DWF.Waveform.Sine;
  private float amplitude;
  private int frequency;
  private float offset;
  private final double[] waveformTimeData = new double[QCPreferences.CAPTURE_BUFFER_SIZE];
  private final double[] waveformAmplitudeData = new double[QCPreferences.CAPTURE_BUFFER_SIZE];

  /** Series R */
  private int seriesR;

  /**
   * Constructor
   */
  public QCModel() {

    updateWaveformChartData();
  }

  @Override
  public void loadModelFromPrefs() {

    // load model from prefs
    seriesR = appPreferences.getInteger(QCPreferences.SERIES_R_INIT_KEY, QCPreferences.SERIES_R_INIT_DEFAULT_VALUE);
    amplitude = appPreferences.getFloat(QCPreferences.AMPLITUDE_INIT_FLOAT_KEY, QCPreferences.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    offset = appPreferences.getFloat(QCPreferences.OFFSET_INIT_FLOAT_KEY, QCPreferences.OFFSET_INIT_FLOAT_DEFAULT_VALUE);
    frequency = appPreferences.getInteger(QCPreferences.FREQUENCY_INIT_KEY, QCPreferences.FREQUENCY_INIT_DEFAULT_VALUE);
    savePath = appPreferences.getString(QCPreferences.REPORT_DIRECTORY_PATH_KEY, QCPreferences.REPORT_DIRECTORY_PATH_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_PREFERENCES_UPDATE, true, false);
  }

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   *
   * @param listener
   */
  public void addListener(PropertyChangeListener listener) {

    swingPropertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * Given the state of the model, update the waveform x and y axis data arrays.
   */
  void updateWaveformChartData() {

    Driver driver = new Sine("Sine", 0, 0, amplitude, frequency);

    double stopTime = 1 / (double) frequency * QCPreferences.CAPTURE_PERIOD_COUNT;
    double timeStep = 1 / (double) frequency * QCPreferences.CAPTURE_PERIOD_COUNT / QCPreferences.CAPTURE_BUFFER_SIZE;

    int counter = 0;
    for (double i = 0.0; i < stopTime; i = i + timeStep) {
      if (counter >= QCPreferences.CAPTURE_BUFFER_SIZE) {
        break;
      }
      waveformTimeData[counter] = i;
      waveformAmplitudeData[counter++] = driver.getSignal(i);
    }
  }

  // ///////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  // ///////////////////////////////////////////////////////////

  public Waveform getWaveform() {

    return waveform;
  }

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  /**
   * @return the offset
   */
  public float getOffset() {

    return offset;
  }

  /**
   * @param offset the offset to set
   */
  public void setOffset(float offset) {

    this.offset = offset;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getFrequency() {

    return frequency;
  }

  public void setFrequency(int frequency) {

    this.frequency = frequency;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public double[] getWaveformTimeData() {

    return waveformTimeData;
  }

  public double[] getWaveformAmplitudeData() {

    return waveformAmplitudeData;
  }

  public int getSeriesR() {

    return seriesR;
  }

  public void setSeriesR(int seriesR) {

    this.seriesR = seriesR;
  }

  public String getSavePath() {

    return savePath;
  }

  public void setSavePath(String savePath) {

    this.savePath = savePath;
  }

  public String getSerialNumber() {

    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {

    this.serialNumber = serialNumber;
  }

  public String getBasePath() {

    String basePath = savePath;

    if (basePath.startsWith("~" + File.separator)) {
      basePath = System.getProperty("user.home") + basePath.substring(1);
    }
    if (basePath.substring(basePath.length() - 2).equalsIgnoreCase(File.separator)) {
      basePath = basePath + File.separator;
    }
    return basePath + File.separator + getChipType() + File.separator + getSerialNumber() + File.separator;
  }

  @Override
  public AppPreferences initAppPreferences() {

    return new QCPreferences();

  }

  public void setChipType(ChipType chipType) {

    this.chipType = chipType;
  }

  public ChipType getChipType() {

    return chipType;
  }

}
