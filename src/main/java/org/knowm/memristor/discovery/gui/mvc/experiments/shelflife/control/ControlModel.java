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
package org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control;

import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.ShelfLifePreferences;

public class ControlModel extends Model {

  private String saveDirectory = "";
  public TimeUnit timeUnit;
  private int repeatInterval;

  private boolean isStartToggled = false;

  private float readVoltageAmplitude;
  private float writeVoltageAmplitude;
  private float eraseVoltageAmplitude;
  private int readPulseWidthInMicroSeconds;
  private int writePulseWidthInMicroSeconds;
  private int erasePulseWidthInMicroSeconds;

  /** Constructor */
  public ControlModel() {
  }

  @Override
  public void doLoadModelFromPrefs(ExperimentPreferences experimentPreferences) {

    // load model from prefs

    saveDirectory = experimentPreferences.getString(ShelfLifePreferences.SAVE_DIRECTORY_INIT_KEY,
        ShelfLifePreferences.SAVE_DIRECTORY_INIT_DEFAULT_VALUE);

    timeUnit = TimeUnit
        .valueOf(experimentPreferences.getString(ShelfLifePreferences.TIME_UNIT_INIT_KEY, ShelfLifePreferences.TIME_UNIT_DEFAULT_VALUE));
    repeatInterval = experimentPreferences.getInteger(ShelfLifePreferences.REPEAT_INTERVAL_INIT_KEY,
        ShelfLifePreferences.REPEAT_INTERVAL_DEFAULT_VALUE);

    seriesResistance = experimentPreferences.getInteger(ShelfLifePreferences.SERIES_R_INIT_KEY, ShelfLifePreferences.SERIES_R_INIT_DEFAULT_VALUE);

    readVoltageAmplitude = experimentPreferences.getFloat(ShelfLifePreferences.READ_VOLTS_INIT_KEY, ShelfLifePreferences.READ_VOLTS_DEFAULT_VALUE);
    writeVoltageAmplitude = experimentPreferences.getFloat(ShelfLifePreferences.WRITE_VOLTS_INIT_KEY, ShelfLifePreferences.WRITE_VOLTS_DEFAULT_VALUE);
    eraseVoltageAmplitude = experimentPreferences.getFloat(ShelfLifePreferences.ERASE_VOLTS_INIT_KEY, ShelfLifePreferences.ERASE_VOLTS_DEFAULT_VALUE);

    readPulseWidthInMicroSeconds = experimentPreferences.getInteger(ShelfLifePreferences.READ_PULSE_WIDTH_INIT_KEY,
        ShelfLifePreferences.READ_PULSE_WIDTH_DEFAULT_VALUE);
    writePulseWidthInMicroSeconds = experimentPreferences.getInteger(ShelfLifePreferences.WRITE_PULSE_WIDTH_INIT_KEY,
        ShelfLifePreferences.WRITE_PULSE_WIDTH_DEFAULT_VALUE);
    erasePulseWidthInMicroSeconds = experimentPreferences.getInteger(ShelfLifePreferences.ERASE_PULSE_WIDTH_INIT_KEY,
        ShelfLifePreferences.ERASE_PULSE_WIDTH_DEFAULT_VALUE);

  }

  /////////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public String getSaveDirectory() {
    return saveDirectory;
  }

  public void setSaveDirectory(String saveDirectory) {
    this.saveDirectory = saveDirectory;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  public void setTimeUnit(String text) {

    timeUnit = Enum.valueOf(TimeUnit.class, text);
  }

  public int getRepeatInterval() {
    return repeatInterval;
  }

  public void setRepeatInterval(int repeatInterval) {
    this.repeatInterval = repeatInterval;
  }

  public boolean isStartToggled() {

    return isStartToggled;
  }

  public void setStartToggled(boolean isStartToggled) {

    this.isStartToggled = isStartToggled;
  }

  public float getReadVoltageAmplitude() {
    return readVoltageAmplitude;
  }

  public void setReadVoltageAmplitude(float readVoltageAmplitude) {
    this.readVoltageAmplitude = readVoltageAmplitude;
  }

  public float getWriteVoltageAmplitude() {
    return writeVoltageAmplitude;
  }

  public void setWriteVoltageAmplitude(float writeVoltageAmplitude) {
    this.writeVoltageAmplitude = writeVoltageAmplitude;
  }

  public float getEraseVoltageAmplitude() {
    return eraseVoltageAmplitude;
  }

  public void setEraseVoltageAmplitude(float eraseVoltageAmplitude) {
    this.eraseVoltageAmplitude = eraseVoltageAmplitude;
  }

  public int getReadPulseWidthInMicroSeconds() {
    return readPulseWidthInMicroSeconds;
  }

  public void setReadPulseWidthInMicroSeconds(int readPulseWidthInMicroSeconds) {
    this.readPulseWidthInMicroSeconds = readPulseWidthInMicroSeconds;
  }

  public int getWritePulseWidthInMicroSeconds() {
    return writePulseWidthInMicroSeconds;
  }

  public void setWritePulseWidthInMicroSeconds(int writePulseWidthInMicroSeconds) {
    this.writePulseWidthInMicroSeconds = writePulseWidthInMicroSeconds;
  }

  public int getErasePulseWidthInMicroSeconds() {
    return erasePulseWidthInMicroSeconds;
  }

  public void setErasePulseWidthInMicroSeconds(int erasePulseWidthInMicroSeconds) {
    this.erasePulseWidthInMicroSeconds = erasePulseWidthInMicroSeconds;
  }
}
