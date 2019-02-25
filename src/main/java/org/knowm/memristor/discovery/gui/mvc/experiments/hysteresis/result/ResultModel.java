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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.result;

import java.beans.PropertyChangeListener;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.HysteresisPreferences;

public class ResultModel extends ExperimentControlModel {

  /** Min Max params */
  Double yMaxIV = null;

  Double yMinIV = null;
  Double yMaxGV = null;
  Double yMinGV = null;
  /** Averaging params */
  private double ave = 0;

  private double k;

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   *
   * @param listener
   */
  @Override
  public void addListener(PropertyChangeListener listener) {

    swingPropertyChangeSupport.addPropertyChangeListener(listener);
  }

  public double getAve() {

    return ave;
  }

  public void setAve(double ave) {

    this.ave = ave;
  }

  public double getK() {

    return k;
  }

  public void setK(double k) {

    if (k > 1) {
      this.k = 1;
    } else if (k < 0) {
      this.k = 0;
    } else {
      this.k = k;
    }
  }

  public Double getyMaxIV() {

    return yMaxIV;
  }

  public void setyMaxIV(Double yMaxIV) {

    this.yMaxIV = yMaxIV;
  }

  public Double getyMinIV() {

    return yMinIV;
  }

  public void setyMinIV(Double yMinIV) {

    this.yMinIV = yMinIV;
  }

  public Double getyMaxGV() {

    return yMaxGV;
  }

  public void setyMaxGV(Double yMaxGV) {

    this.yMaxGV = yMaxGV;
  }

  public Double getyMinGV() {

    return yMinGV;
  }

  public void setyMinGV(Double yMinGV) {

    this.yMinGV = yMinGV;
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new HysteresisPreferences();
  }

  @Override
  public void loadModelFromPrefs() {

    k =
        experimentPreferences.getFloat(
            HysteresisPreferences.K_INIT_FLOAT_KEY,
            HysteresisPreferences.K_INIT_FLOAT_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(
        ExperimentControlModel.EVENT_PREFERENCES_UPDATE, true, false);
  }
}
