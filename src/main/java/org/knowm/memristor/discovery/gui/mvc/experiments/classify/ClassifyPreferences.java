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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;

/**
 * Stores various operational preferences
 *
 * @author timmolter
 */
public class ClassifyPreferences extends ExperimentPreferences {

  private static final String PREFIX = "SYNAPSE_";

  public static final String WAVEFORM_INIT_STRING_KEY = PREFIX + "WAVEFORM_INIT_STRING_KEY";
  public static final String WAVEFORM_INIT_STRING_DEFAULT_VALUE = "HalfSine";

  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 5_000;

  public static final String AMPLITUDE_INIT_FLOAT_KEY = PREFIX + "AMPLITUDE_INIT_FLOAT_KEY";
  public static final float AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = 1f;

  public static final String PULSE_WIDTH_INIT_KEY = PREFIX + "PERIOD_INIT_KEY";
  public static final int PULSE_WIDTH_INIT_DEFAULT_VALUE = 50_000;

  public static final String NUM_TRAIN_EPOCHS_INIT_KEY = PREFIX + "NUM_TRAIN_EPOCHS_INIT_KEY";
  public static final int NUM_TRAIN_EPOCHS_INIT_DEFAULT_VALUE = 10;

  // /////////////////////////////////////////////////////////////////////////////////////

  public static final CurrentUnits CURRENT_UNIT = CurrentUnits.MicroAmps;
  public static final ResistanceUnits RESISTANCE_UNIT = ResistanceUnits.KiloOhms;
  public static final ConductanceUnits CONDUCTANCE_UNIT = ConductanceUnits.MilliSiemens;
  public static final TimeUnits TIME_UNIT = TimeUnits.MicroSeconds;

  // public static final int CAPTURE_BUFFER_SIZE = DWF.AD2_MAX_BUFFER_SIZE;
  public static final int CAPTURE_BUFFER_SIZE = 8000;

  public enum Datasets {

    SevenSegment0(0, "Zero"), SevenSegment1(1, "One"), SevenSegment2(2, "Two"), SevenSegment3(3, "Three"), SevenSegment4(4, "Four"), SevenSegment5(5,
        "Five"), SevenSegment6(6, "Six"), SevenSegment7(7, "Seven"), SevenSegment8(8, "Eight"), SevenSegment9(9, "Nine");

    private final int patternID;
    private final String name;

    private Datasets(int patternID, String name) {
      this.patternID = patternID;
      this.name = name;
    }

    public int getPatternID() {
      return patternID;
    }

    public String getName() {
      return name;
    }

  }

  // public static final int CAPTURE_BUFFER_SIZE = 8192 / 11; // AD2 buffer size / most pulses allowed.

  /**
   * Constructor
   */
  public ClassifyPreferences() {

    super(ClassifyPreferences.class);
  }
}
