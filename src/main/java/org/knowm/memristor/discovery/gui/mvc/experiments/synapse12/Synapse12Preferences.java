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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse12;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;

/** Stores various operational preferences */
public class Synapse12Preferences extends ExperimentPreferences {

  /* NOT YET IN PREFERENCES*/

  public static final TimeUnits TIME_UNIT = TimeUnits.MicroSeconds;
  public static final int CAPTURE_BUFFER_SIZE = 8000;

  /* ALREADY IN PREFERENCES*/

  public static final String WAVEFORM_INIT_STRING_DEFAULT_VALUE = "SquareSmooth";
  public static final int NUM_PULSES_INIT_DEFAULT_VALUE = 2;
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 20_000;

  public static final float AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = 1f;
  public static final float AMPLITUDE_REVERSE_INIT_FLOAT_DEFAULT_VALUE = .5f;
  public static final int PULSE_WIDTH_INIT_DEFAULT_VALUE = 50_000;

  public static final int SAMPLE_RATE_INIT_DEFAULT_VALUE = 1;

  public static final float SCOPE_ONE_OFFSET_DEFAULT_VALUE = 0f;
  public static final float SCOPE_TWO_OFFSET_DEFAULT_VALUE = 0f;
  public static final float W_ONE_OFFSET_DEFAULT_VALUE = 0f;

  // /////////////////////////////////////////////////////////////////////////////////////

  private static final String PREFIX = "SYNAPSE12_";

  public static final String WAVEFORM_INIT_STRING_KEY = PREFIX + "WAVEFORM_INIT_STRING_KEY";
  public static final String NUM_PULSES_INIT_KEY = PREFIX + "NUM_PULSES_INIT_KEY";
  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";

  public static final String AMPLITUDE_INIT_FLOAT_KEY = PREFIX + "AMPLITUDE_INIT_FLOAT_KEY";
  public static final String AMPLITUDE_REVERSE_INIT_FLOAT_KEY =
      PREFIX + "AMPLITUDE_REVERSE_INIT_FLOAT_KEY";
  public static final String PULSE_WIDTH_INIT_KEY = PREFIX + "PULSE_WIDTH_INIT_KEY";

  public static final String SAMPLE_RATE_INIT_KEY = PREFIX + "SAMPLE_RATE_INIT_KEY";

  public static final String SCOPE_ONE_OFFSET_KEY = PREFIX + "SCOPE_ONE_OFFSET_KEY";
  public static final String SCOPE_TWO_OFFSET_KEY = PREFIX + "SCOPE_TWO_OFFSET_KEY";
  public static final String W_ONE_OFFSET_KEY = PREFIX + "W_ONE_OFFSET_KEY";

  /** Constructor */
  public Synapse12Preferences() {

    super(Synapse12Preferences.class);
  }
}
