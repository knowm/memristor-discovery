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
package org.knowm.memristor.discovery.gui.mvc.experiments.conductance;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.waveforms4j.DWF;

/**
 * Stores various operational preferences
 *
 * @author timmolter
 */
public class ConductancePreferences extends ExperimentPreferences {

  private static final String PREFIX = "CONDUCTANCE_";

  public static final String RESET_PULSE_TYPE_INIT_STRING_KEY =
      PREFIX + "RESET_PULSE_TYPE_INIT_STRING_KEY";
  public static final String RESET_PULSE_TYPE_INIT_STRING_DEFAULT_VALUE = "Sawtooth";

  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 1_000;

  public static final String RESET_AMPLITUDE_INIT_FLOAT_KEY =
      PREFIX + "RESET_AMPLITUDE_INIT_FLOAT_KEY";
  public static final float RESET_AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = -1.0f;

  public static final String RESET_PULSE_WIDTH_INIT_KEY = PREFIX + "RESET_PULSE_WIDTH_INIT_KEY";
  public static final int RESET_PERIOD_INIT_DEFAULT_VALUE = 5000;

  // SET

  public static final String SET_CONDUCTANCE_INIT_KEY = PREFIX + "SET_CONDUCTANCE_INIT_KEY";
  public static final float SET_CONDUCTANCE_INIT_DEFAULT_VALUE = 1.0f;

  public static final String SET_AMPLITUDE_INIT_FLOAT_KEY = PREFIX + "SET_AMPLITUDE_INIT_FLOAT_KEY";
  public static final float SET_AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = 1.0f;

  public static final String SET_PULSE_WIDTH_INIT_KEY = PREFIX + "SET_PULSE_WIDTH_INIT_KEY";
  public static final int SET_PERIOD_INIT_DEFAULT_VALUE = 5000;

  ///////////////////////////////////////////////////////////////////////////////////////

  public static final CurrentUnits CURRENT_UNIT = CurrentUnits.MicroAmps;
  public static final ResistanceUnits RESISTANCE_UNIT = ResistanceUnits.KiloOhms;
  public static final ConductanceUnits CONDUCTANCE_UNIT = ConductanceUnits.MilliSiemens;
  public static final TimeUnits TIME_UNIT = TimeUnits.MicroSeconds;

  public static final int CAPTURE_BUFFER_SIZE = DWF.AD2_MAX_BUFFER_SIZE;

  /** Constructor */
  public ConductancePreferences() {

    super(ConductancePreferences.class);
  }
}
