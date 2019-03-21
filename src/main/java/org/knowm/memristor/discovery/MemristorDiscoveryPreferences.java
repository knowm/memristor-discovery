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
package org.knowm.memristor.discovery;

import java.util.prefs.Preferences;

public class MemristorDiscoveryPreferences {

  public static final String BOARD_INIT_STRING_KEY = "BOARD_INIT_STRING_KEY";
  public static final String BOARD_INIT_STRING_DEFAULT_VALUE = "V0";

  public static final String EXPERIMENT_INIT_STRING_KEY = "EXPERIMENT_INIT_STRING_KEY";
  public static final String EXPERIMENT_INIT_STRING_DEFAULT_VALUE = "HYSTERESIS";
  protected Preferences preferences;
  private String boardVersion;
  private String experiment;

  /** Constructor */
  public MemristorDiscoveryPreferences() {

    preferences = Preferences.userNodeForPackage(MemristorDiscoveryPreferences.class);
    boardVersion = this.preferences.get(BOARD_INIT_STRING_KEY, BOARD_INIT_STRING_DEFAULT_VALUE);
    experiment = this.preferences.get(EXPERIMENT_INIT_STRING_KEY, EXPERIMENT_INIT_STRING_DEFAULT_VALUE);
  }

  public void updateBoardVersion(String boardVersion) {

    this.boardVersion = boardVersion;
    this.preferences.put(BOARD_INIT_STRING_KEY, boardVersion.trim());
  }

  public void updateExperiment(String experiment) {

    this.experiment = experiment;
    this.preferences.put(EXPERIMENT_INIT_STRING_KEY, experiment.trim());
  }

  public String getBoardVersion() {
    return boardVersion;
  }

  public String getExperiment() {
    return experiment;
  }
}
