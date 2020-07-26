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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify12;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;

/** Stores various operational preferences */
public class Classify12Preferences extends ExperimentPreferences {

  /* NOT YET IN PREFERENCES*/
  public static final CurrentUnits CURRENT_UNIT = CurrentUnits.MicroAmps;
  public static final ConductanceUnits CONDUCTANCE_UNIT = ConductanceUnits.MilliSiemens;
  public static final TimeUnits TIME_UNIT = TimeUnits.MicroSeconds;
  public static final int CAPTURE_BUFFER_SIZE = 8000;

  /* ALREADY IN PREFERENCES*/
  public static final String WAVEFORM_INIT_STRING_DEFAULT_VALUE = "HalfSine";
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 20_000;
  public static final float AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = 1f;

  public static final float AMPLITUDE_REVERSE_INIT_FLOAT_DEFAULT_VALUE = 1f;
  public static final int PULSE_WIDTH_INIT_DEFAULT_VALUE = 50_000;
  public static final int NUM_TRAIN_EPOCHS_INIT_DEFAULT_VALUE = 50;

  public static final float SCOPE_ONE_OFFSET_DEFAULT_VALUE = 0f;
  public static final float SCOPE_TWO_OFFSET_DEFAULT_VALUE = 0f;
  public static final float W_ONE_OFFSET_DEFAULT_VALUE = 0f;

  // /////////////////////////////////////////////////////////////////////////////////////

  private static final String PREFIX = "SYNAPSE12_";
  public static final String WAVEFORM_INIT_STRING_KEY = PREFIX + "WAVEFORM_INIT_STRING_KEY";
  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";
  public static final String AMPLITUDE_INIT_FLOAT_KEY = PREFIX + "AMPLITUDE_INIT_FLOAT_KEY";

  public static final String AMPLITUDE_REVERSE_INIT_FLOAT_KEY =
      PREFIX + "AMPLITUDE_REVERSE_INIT_FLOAT_KEY";
  public static final String PULSE_WIDTH_INIT_KEY = PREFIX + "PULSE_WIDTH_INIT_KEY";
  public static final String NUM_TRAIN_EPOCHS_INIT_KEY = PREFIX + "NUM_TRAIN_EPOCHS_INIT_KEY";

  public static final String SCOPE_ONE_OFFSET_KEY = PREFIX + "SCOPE_ONE_OFFSET_KEY";
  public static final String SCOPE_TWO_OFFSET_KEY = PREFIX + "SCOPE_TWO_OFFSET_KEY";
  public static final String W_ONE_OFFSET_KEY = PREFIX + "W_ONE_OFFSET_KEY";

  /** Constructor */
  public Classify12Preferences() {

    super(Classify12Preferences.class);
  }

  public enum AHaHRoutine {
    LearnOnMistakes,
    LearnAlways,
    LearnCombo;
  }

  public enum Datasets {
    Ortho2Pattern(orthogonal2Pattern()),
    AntiOrtho2Pattern(antiOrthogonal2Pattern()),
    Ortho4Pattern(orthogonal4Pattern()),
    AntiOrtho4Pattern(antiOrthogonal4Pattern()),
    Ortho8Pattern(orthogonal8Pattern()),
    AntiOrtho8Pattern(antiOrthogonal8Pattern()),
    TwoEightPattern5Frustrated(TwoEightPattern4Frustrated()),
    TwoPattern36Frustrated(twoPattern25Frustrated());

    private final List<SupervisedPattern> dataset;

    private Datasets(List<SupervisedPattern> dataset) {
      this.dataset = dataset;
    }

    private static List<SupervisedPattern> orthogonal2Pattern() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(true, Arrays.asList(0, 1, 2, 3)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(4, 5, 6, 7)));

      return dataset;
    }

    private static List<SupervisedPattern> antiOrthogonal2Pattern() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(false, Arrays.asList(0, 1, 2, 3)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(4, 5, 6, 7)));

      return dataset;
    }

    private static List<SupervisedPattern> orthogonal4Pattern() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(true, Arrays.asList(0, 1)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(2, 3)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(4, 5)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(6, 7)));

      return dataset;
    }

    private static List<SupervisedPattern> antiOrthogonal4Pattern() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(false, Arrays.asList(0, 1)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(2, 3)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(4, 5)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(6, 7)));

      return dataset;
    }

    private static List<SupervisedPattern> orthogonal8Pattern() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(true, Arrays.asList(0)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(1)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(2)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(3)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(4)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(5)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(6)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(7)));

      return dataset;
    }

    private static List<SupervisedPattern> antiOrthogonal8Pattern() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(false, Arrays.asList(0)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(1)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(2)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(3)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(4)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(5)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(6)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(7)));

      return dataset;
    }

    private static List<SupervisedPattern> TwoEightPattern4Frustrated() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(true, Arrays.asList(0, 1)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(1, 2)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(2, 3)));
      dataset.add(new SupervisedPattern(true, Arrays.asList(3, 4)));

      dataset.add(new SupervisedPattern(false, Arrays.asList(4, 5)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(5, 6)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(6, 7)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(7, 8)));

      return dataset;
    }

    private static List<SupervisedPattern> twoPattern25Frustrated() {

      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
      dataset.add(new SupervisedPattern(true, Arrays.asList(0, 1, 2, 3, 5)));
      dataset.add(new SupervisedPattern(false, Arrays.asList(4, 5, 6, 7, 2)));

      return dataset;
    }

    //    private static List<SupervisedPattern> twoPattern2345Frustrated() {
    //
    //      List<SupervisedPattern> dataset = new ArrayList<SupervisedPattern>();
    //      dataset.add(new SupervisedPattern(true, Arrays.asList(0, 1, 2, 3, 4, 5)));
    //      dataset.add(new SupervisedPattern(false, Arrays.asList(4, 5, 6, 7, 2, 3)));
    //
    //      return dataset;
    //    }

    public List<SupervisedPattern> getDataset() {
      return dataset;
    }
  }
}
