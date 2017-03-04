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

import org.knowm.memristor.discovery.DWFProxy;

public abstract class App {

  public final DWFProxy dwfProxy;

  public abstract AppModel getExperimentModel();

  public abstract AppModel getPlotModel();

  /**
   * @param dwfProxy
   */
  public App(DWFProxy dwfProxy) {

    this.dwfProxy = dwfProxy;
  }

  public void refreshModelFromPreferences() {

    getExperimentModel().loadModelFromPrefs();
    getPlotModel().loadModelFromPrefs();
  }

  public boolean capturePulseData() {

    // Read In Data
    int bailCount = 0;
    while (true) {
      byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
      System.out.println("status: " + status);
      if (status == 2) { // done capturing
        return true;
      }
      if (bailCount++ > 10) {
        System.out.println("Bailed!!!");
        return false;
      }
    }

  }
}
