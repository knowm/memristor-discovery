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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;

/**
 * Stores various operational preferences
 *
 * @author timmolter
 */
public class BoardCheckPreferences extends ExperimentPreferences {

  public static final int SERIES_R_INIT_DEFAULT_VALUE = 10_000;
  public static final CurrentUnits CURRENT_UNIT = CurrentUnits.MicroAmps;
  public static final ResistanceUnits RESISTANCE_UNIT = ResistanceUnits.KiloOhms;
  public static final ConductanceUnits CONDUCTANCE_UNIT = ConductanceUnits.MilliSiemens;
  public static final TimeUnits TIME_UNIT = TimeUnits.MicroSeconds;
  public static final int CAPTURE_BUFFER_SIZE = 8000;
  private static final String PREFIX = "BOARDCHECK_";
  public static final String SERIES_R_INIT_KEY = PREFIX + "SERIES_R_INIT_KEY";

  /** Constructor */
  public BoardCheckPreferences() {

    super(BoardCheckPreferences.class);
  }
}
