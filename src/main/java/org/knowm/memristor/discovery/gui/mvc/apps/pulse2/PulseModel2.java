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
package org.knowm.memristor.discovery.gui.mvc.apps.pulse2;

import java.beans.PropertyChangeListener;

import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulseModel2 extends AppModel {

  private final Logger logger = LoggerFactory.getLogger(PulseModel2.class);

  /** Waveform */
  private float amplitude;
  private int pulseWidth;

  /** Shunt */
  private int seriesResistance;

  /**
   * Constructor
   */
  public PulseModel2() {

  }

  @Override
  public void loadModelFromPrefs() {

    // load model from prefs
    seriesResistance = appPreferences.getInteger(PulsePreferences2.SERIES_R_INIT_KEY, PulsePreferences2.SERIES_R_INIT_DEFAULT_VALUE);
    // seriesResistance = 1000;
    amplitude = appPreferences.getFloat(PulsePreferences2.AMPLITUDE_INIT_FLOAT_KEY, PulsePreferences2.AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE);
    pulseWidth = appPreferences.getInteger(PulsePreferences2.PULSE_WIDTH_INIT_KEY, PulsePreferences2.PULSE_WIDTH_INIT_DEFAULT_VALUE);
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

  /////////////////////////////////////////////////////////////
  // GETTERS AND SETTERS //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public float getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(float amplitude) {

    this.amplitude = amplitude;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getPulseWidth() {

    return pulseWidth;
  }

  public int getCalculatedFrequency() {

    return (int) (1.0 / (2.0 * (double) pulseWidth) * 1000000000);
  }

  public void setPulseWidth(int pulseWidth) {

    this.pulseWidth = pulseWidth;
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_WAVEFORM_UPDATE, true, false);
  }

  public int getShunt() {

    return seriesResistance;
  }

  public void setShunt(int shunt) {

    this.seriesResistance = shunt;
  }

  @Override
  public AppPreferences initAppPreferences() {

    return new PulsePreferences2();
  }

}
