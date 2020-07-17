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
package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;

/**
 * Stores various operational preferences
 *
 * @author alexnugent
 */
public class kTBitSatSolverPreferences extends ExperimentPreferences {

  private static final String PREFIX = "KT_BIT_SAT_SOLVER";

  public static final String WAVEFORM_INIT_STRING_DEFAULT_VALUE = "HalfSine";
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 20_000;
  public static final float AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = 1f;
  public static final float AMPLITUDE_REVERSE_INIT_FLOAT_DEFAULT_VALUE = 1f;
  public static final int PULSE_WIDTH_INIT_DEFAULT_VALUE = 50_000;
  public static final int NUM_TRAIN_EPOCHS_INIT_DEFAULT_VALUE = 50;
  public static final CurrentUnits CURRENT_UNIT = CurrentUnits.MicroAmps;
  public static final ResistanceUnits RESISTANCE_UNIT = ResistanceUnits.KiloOhms;
  public static final ConductanceUnits CONDUCTANCE_UNIT = ConductanceUnits.MilliSiemens;
  public static final TimeUnits TIME_UNIT = TimeUnits.MicroSeconds;
  public static final int CAPTURE_BUFFER_SIZE = 8000;

  public static final String FILE_PATH_INIT_KEY = PREFIX + "FILE_DIRECTORY_INIT_KEY";
  public static final String FILE_PATH_INIT_DEFAULT_VALUE = "/Desktop/";

  // /////////////////////////////////////////////////////////////////////////////////////
  public static final String WAVEFORM_INIT_STRING_KEY = PREFIX + "WAVEFORM_INIT_STRING_KEY";
  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";
  public static final String AMPLITUDE_INIT_FLOAT_KEY = PREFIX + "AMPLITUDE_INIT_FLOAT_KEY";
  public static final String AMPLITUDE_REVERSE_INIT_FLOAT_KEY =
      PREFIX + "AMPLITUDE_REVERSE_INIT_FLOAT_KEY";
  public static final String PULSE_WIDTH_INIT_KEY = PREFIX + "PERIOD_INIT_KEY";
  public static final String NUM_TRAIN_EPOCHS_INIT_KEY = PREFIX + "NUM_TRAIN_EPOCHS_INIT_KEY";

  /** Constructor */
  public kTBitSatSolverPreferences() {

    super(kTBitSatSolverPreferences.class);
  }
}
