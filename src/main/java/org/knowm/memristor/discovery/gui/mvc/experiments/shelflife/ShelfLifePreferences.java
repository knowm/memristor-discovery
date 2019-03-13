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
package org.knowm.memristor.discovery.gui.mvc.experiments.shelflife;

import java.util.concurrent.TimeUnit;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;

/**
 * Stores various operational preferences
 *
 * @author timmolter
 */
public class ShelfLifePreferences extends ExperimentPreferences {

  // NOT YET IN PREFERENCES
  public static final int CAPTURE_BUFFER_SIZE = 8000;

  // ALREADY IN PREFERENCES
  private static final String PREFIX = "SHELFLIFE_";
  public static final String SAVE_DIRECTORY_INIT_KEY = PREFIX + "SAVE_DIRECTORY_INIT_KEY";
  public static final String SAVE_DIRECTORY_INIT_DEFAULT_VALUE = "/";
  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 5_000;
  public static final String TIME_UNIT_INIT_KEY = PREFIX + "TIME_UNIT_INIT_KEY";
  public static final String TIME_UNIT_DEFAULT_VALUE = TimeUnit.SECONDS.name();
  public static final String REPEAT_INTERVAL_INIT_KEY = PREFIX + "REPEAT_INTERVAL";
  public static final Integer REPEAT_INTERVAL_DEFAULT_VALUE = 5;

  public static final String READ_VOLTS_INIT_KEY = PREFIX + "READ_VOLTS_INIT_KEY";
  public static final float READ_VOLTS_DEFAULT_VALUE = .1f;

  public static final String WRITE_VOLTS_INIT_KEY = PREFIX + "WRITE_VOLTS_INIT_KEY";
  public static final float WRITE_VOLTS_DEFAULT_VALUE = 2f;

  public static final String ERASE_VOLTS_INIT_KEY = PREFIX + "ERASE_VOLTS_INIT_KEY";
  public static final float ERASE_VOLTS_DEFAULT_VALUE = -2f;

  public static final String READ_PULSE_WIDTH_INIT_KEY = PREFIX + "READ_PULSE_WIDTH_INIT_KEY";
  public static final int READ_PULSE_WIDTH_DEFAULT_VALUE = 1000;

  public static final String WRITE_PULSE_WIDTH_INIT_KEY = PREFIX + "WRITE_PULSE_WIDTH_INIT_KEY";
  public static final int WRITE_PULSE_WIDTH_DEFAULT_VALUE = 1000;

  public static final String ERASE_PULSE_WIDTH_INIT_KEY = PREFIX + "ERASE_PULSE_WIDTH_INIT_KEY";
  public static final int ERASE_PULSE_WIDTH_DEFAULT_VALUE = 1000;

  /** Constructor */
  public ShelfLifePreferences() {

    super(ShelfLifePreferences.class);
  }
}
