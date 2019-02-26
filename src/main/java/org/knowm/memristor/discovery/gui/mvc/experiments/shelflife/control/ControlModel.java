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
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.ShelfLifePreferences;

public class ControlModel extends Model {

  public TimeUnit timeUnit;
  private int repeatInterval;
  /** Constructor */
  public ControlModel() {}

  @Override
  public void loadModelFromPrefs() {

    // load model from prefs
    timeUnit =
        TimeUnit.valueOf(
            experimentPreferences.getString(
                ShelfLifePreferences.TIME_UNIT_INIT_KEY,
                ShelfLifePreferences.TIME_UNIT_DEFAULT_VALUE));
    repeatInterval =
        experimentPreferences.getInteger(
            ShelfLifePreferences.REPEAT_INTERVAL_INIT_KEY,
            ShelfLifePreferences.REPEAT_INTERVAL_DEFAULT_VALUE);

    seriesResistance =
        experimentPreferences.getInteger(
            ShelfLifePreferences.SERIES_R_INIT_KEY,
            ShelfLifePreferences.SERIES_R_INIT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(
        Model.EVENT_PREFERENCES_UPDATE, true, false);
  }

  /////////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
    swingPropertyChangeSupport.firePropertyChange(
        Model.EVENT_WAVEFORM_UPDATE, true, false); }

  public void setTimeUnit(String text) {

    timeUnit = Enum.valueOf(TimeUnit.class, text);
    swingPropertyChangeSupport.firePropertyChange(
        Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getRepeatInterval() {
    return repeatInterval;

  }

  public void setRepeatInterval(int repeatInterval) {
    this.repeatInterval = repeatInterval;
    swingPropertyChangeSupport.firePropertyChange(
        Model.EVENT_WAVEFORM_UPDATE, true, false);
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new ShelfLifePreferences();
  }
}
