package org.knowm.memristor.discovery;

import java.util.prefs.Preferences;

public class MemristorDiscoveryPreferences {

  public static final String BOARD_INIT_STRING_KEY = "BOARD_INIT_STRING_KEY";
  public static final String BOARD_INIT_STRING_DEFAULT_VALUE = "V0";

  public static final String EXPERIMENT_INIT_STRING_KEY = "EXPERIMENT_INIT_STRING_KEY";
  public static final String EXPERIMENT_INIT_STRING_DEFAULT_VALUE = "HYSTERESIS";

  private String boardVersion;
  private String experiment;

  protected Preferences preferences;

  /** Constructor */
  public MemristorDiscoveryPreferences() {

    preferences = Preferences.userNodeForPackage(MemristorDiscoveryPreferences.class);
    boardVersion = this.preferences.get(BOARD_INIT_STRING_KEY, BOARD_INIT_STRING_DEFAULT_VALUE);
    experiment =
        this.preferences.get(EXPERIMENT_INIT_STRING_KEY, EXPERIMENT_INIT_STRING_DEFAULT_VALUE);
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
