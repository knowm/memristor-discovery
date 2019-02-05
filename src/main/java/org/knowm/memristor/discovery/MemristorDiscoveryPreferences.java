package org.knowm.memristor.discovery;

import java.util.prefs.Preferences;

public class MemristorDiscoveryPreferences {

  public static final String BOARD_INIT_STRING_KEY = "BOARD_INIT_STRING_KEY";
  public static final String BOARD_INIT_STRING_DEFAULT_VALUE = "V0";
  private String boardVersion;

  protected Preferences preferences;

  /** Constructor */
  public MemristorDiscoveryPreferences() {

    preferences = Preferences.userNodeForPackage(MemristorDiscoveryPreferences.class);
    boardVersion = this.preferences.get(BOARD_INIT_STRING_KEY, BOARD_INIT_STRING_DEFAULT_VALUE);
  }

  public void updatePreferences(String boardVersion) {

    this.boardVersion = boardVersion;
    this.preferences.put(BOARD_INIT_STRING_KEY, boardVersion.trim());
  }

  public String getBoardVersion() {
    return boardVersion;
  }
}
