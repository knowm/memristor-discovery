/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2018 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.BoardCheckPreferences;

public class ControlModel extends ExperimentControlModel {

  /**
   * Constructor
   */
  public ControlModel() {

  }

  @Override
  public void loadModelFromPrefs() {

    seriesResistance = experimentPreferences.getInteger(BoardCheckPreferences.SERIES_R_INIT_KEY, BoardCheckPreferences.SERIES_R_INIT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(ExperimentControlModel.EVENT_PREFERENCES_UPDATE, true, false);
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new BoardCheckPreferences();
  }
}
