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
package org.knowm.memristor.discovery.gui.mvc.experiments;

import javax.swing.event.SwingPropertyChangeSupport;

public abstract class ExperimentControlModel {

  /**
   * runtime variables
   */
  public int seriesResistance;
  public boolean isStartToggled = true; // TODO set this to false

  /**
   * Events
   */
  public static final String EVENT_WAVEFORM_UPDATE = "EVENT_WAVEFORM_UPDATE";
  public static final String EVENT_FREQUENCY_UPDATE = "EVENT_FREQUENCY_UPDATE";
  public static final String EVENT_PREFERENCES_UPDATE = "EVENT_PREFERENCES_UPDATE";

  public abstract ExperimentPreferences initAppPreferences();

  public abstract void loadModelFromPrefs();

  protected ExperimentPreferences experimentPreferences;
  protected SwingPropertyChangeSupport swingPropertyChangeSupport;

  /**
   * Constructor
   */
  public ExperimentControlModel() {

    swingPropertyChangeSupport = new SwingPropertyChangeSupport(this);
    this.experimentPreferences = initAppPreferences();
    loadModelFromPrefs();
  }

  public int getSeriesResistance() {

    return seriesResistance;
  }

  public void setSeriesResistance(int seriesResistance) {

    this.seriesResistance = seriesResistance;
  }

  public boolean isStartToggled() {

    return isStartToggled;
  }

  public void setStartToggled(boolean isStartToggled) {

    this.isStartToggled = isStartToggled;
  }
}
