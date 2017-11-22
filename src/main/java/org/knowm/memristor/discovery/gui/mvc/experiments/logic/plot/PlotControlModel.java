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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.TraceDatum;

public class PlotControlModel extends ExperimentControlModel {

  List<List<TraceDatum>> traces = new ArrayList<List<TraceDatum>>();

  public void addTrace(List<TraceDatum> trace) {

    traces.add(trace);
  }

  public void clearAllTraces() {
    traces.clear();
  }

  public int getNumTraces() {

    return traces.size();
  }

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   *
   * @param listener
   */
  @Override
  public void addListener(PropertyChangeListener listener) {

    swingPropertyChangeSupport.addPropertyChangeListener(listener);
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new LogicPreferences();
  }

  @Override
  public void loadModelFromPrefs() {

  }

}
