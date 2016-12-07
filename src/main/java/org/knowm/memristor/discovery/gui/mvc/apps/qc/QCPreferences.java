/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016 Knowm Inc. www.knowm.org
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

package org.knowm.memristor.discovery.gui.mvc.apps.qc;

import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences;

/**
 * Stores various operational preferences
 *
 * @author timmolter
 */
public class QCPreferences extends AppPreferences {

  private static final String PREFIX = "QC_";

  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";
  public static final int SERIES_R_INIT_DEFAULT_VALUE = 49_900;

  public static final String AMPLITUDE_INIT_FLOAT_KEY = PREFIX + "AMPLITUDE_INIT_FLOAT_KEY";
  public static final float AMPLITUDE_INIT_FLOAT_DEFAULT_VALUE = 1.25f;

  public static final String OFFSET_INIT_FLOAT_KEY = PREFIX + "OFFSET_INIT_FLOAT_KEY";
  public static final float OFFSET_INIT_FLOAT_DEFAULT_VALUE = -.25f;

  public static final String FREQUENCY_INIT_KEY = PREFIX + "FREQUENCY_INIT_KEY";
  public static final int FREQUENCY_INIT_DEFAULT_VALUE = 10;

  public static final String REPORT_DIRECTORY_PATH_KEY = "REPORT_DIRECTORY_PATH_KEY";
  public static final String REPORT_DIRECTORY_PATH_DEFAULT_VALUE = "~/Documents/Knowm/MemristorDiscovery/QC";

  // /////////////////////////////////////////////////////////////////////////////////////

  public static final String CURRENT_UNIT_KEY = "CURRENT_UNIT_KEY";
  public static final CurrentUnits CURRENT_UNIT_DEFAULT_VALUE = CurrentUnits.MicroAmps;

  public static final boolean IS_VIN = false;

  public static final int CAPTURE_BUFFER_SIZE = 400;
  public static final int CAPTURE_PERIOD_COUNT = 2;

  public static final int MAX_CAPTURE_COUNT = 50;
  public static final int START_QC_CAPTURE_COUNT = 50;// this many to condition before tests

  public static final float P_BELOW_MAX_MIN_V = .5f;// will only measure resistance if voltage is this percentage below the max or min v.

  // public static final float MIN_Q = 3;
  public static final float MIN_Q = 5;// ratio of HRS/LRS should exceed this
  public static final float R_TARGET = 100000;// LRS should be under this, HRS should be above this. This is assuming a 50k series resistor!

  /**
   * Constructor
   */
  public QCPreferences() {

    super(QCPreferences.class);
  }

}
