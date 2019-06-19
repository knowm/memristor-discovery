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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;

/**
 * Stores various operational preferences
 *
 * @author timmolter
 */
public class HysteresisPreferences extends ExperimentPreferences {

  // NOT YET IN PREFERENCES
  public static final CurrentUnits CURRENT_UNIT = CurrentUnits.MicroAmps;
  public static final ConductanceUnits CONDUCTANCE_UNIT = ConductanceUnits.MilliSiemens;
  // public static final boolean IS_VIN = true;
  public static final int CAPTURE_BUFFER_SIZE = 200;
  public static final int CAPTURE_PERIOD_COUNT = 1;

  // ALREADY IN PREFERENCES
  private static final String PREFIX = "HYSTERESIS_";
  public static final String WAVEFORM_INIT_STRING_KEY = PREFIX + "WAVEFORM_INIT_STRING_KEY";
  public static final String WAVEFORM_INIT_STRING_DEFAULT_VALUE = "Sine";
  public static final String OFFSET_INIT_FLOAT_KEY = PREFIX + "OFFSET_INIT_FLOAT_KEY";
  public static final float OFFSET_INIT_FLOAT_DEFAULT_VALUE = 0.0f;
  public static final String AMPLITUDE_INIT_FLOAT_KEY = PREFIX + "AMPLITUDE_INIT_FLOAT_KEY";
  public static final float AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = 0.5f;
  public static final String FREQUENCY_INIT_KEY = PREFIX + "FREQUENCY_INIT_KEY";
  public static final int FREQUENCY_INIT_DEFAULT_VALUE = 100;
  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 10_000;
  public static final String K_INIT_FLOAT_KEY = PREFIX + "K_INIT_FLOAT_KEY";
  public static final float K_INIT_FLOAT_DEFAULT_VALUE = 1.0f;

  /** Constructor */
  public HysteresisPreferences() {

    super(HysteresisPreferences.class);
  }
}
