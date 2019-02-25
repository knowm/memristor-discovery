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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse.result;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.SynapsePreferences;

public class ResultModel extends ExperimentControlModel {

  /** Min Max params */
  Double yMaxIV = null;

  Double yMinIV = null;

  Double yMaxGV = null;
  Double yMinGV = null;

  List<Double> time1Data = new ArrayList<>();
  List<Double> time2Data = new ArrayList<>();
  List<Double> timeVyData = new ArrayList<>();

  List<Double> gr1Data = new ArrayList<>();
  List<Double> gr2Data = new ArrayList<>();
  List<Double> vyData = new ArrayList<>();

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   *
   * @param listener
   */
  @Override
  public void addListener(PropertyChangeListener listener) {

    swingPropertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void clearData() {

    time1Data.clear();
    time2Data.clear();
    timeVyData.clear();
    gr1Data.clear();
    gr2Data.clear();
    vyData.clear();
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

    return new SynapsePreferences();
  }

  @Override
  public void loadModelFromPrefs() {}

  /** @return the vyData */
  public List<Double> getVyData() {

    return vyData;
  }

  /** @param vyData the vyData to set */
  public void setVyData(List<Double> vyData) {

    this.vyData = vyData;
  }

  /** @return the gr1Data */
  public List<Double> getGr1Data() {

    return gr1Data;
  }

  /** @param gr1Data the gr1Data to set */
  public void setGr1Data(List<Double> gr1Data) {

    this.gr1Data = gr1Data;
  }

  /** @return the gr2Data */
  public List<Double> getGr2Data() {

    return gr2Data;
  }

  /** @param gr2Data the gr2Data to set */
  public void setGr2Data(List<Double> gr2Data) {

    this.gr2Data = gr2Data;
  }

  /** @return the time1Data */
  public List<Double> getTime1Data() {

    return time1Data;
  }

  /** @param time1Data the time1Data to set */
  public void setTime1Data(List<Double> time1Data) {

    this.time1Data = time1Data;
  }

  /** @return the time2Data */
  public List<Double> getTime2Data() {

    return time2Data;
  }

  /** @param time2Data the time2Data to set */
  public void setTime2Data(List<Double> time2Data) {

    this.time2Data = time2Data;
  }

  /** @return the time3Data */
  public List<Double> getTimeVyData() {

    return timeVyData;
  }

  /** @param time3Data the time3Data to set */
  public void setTimeVyData(List<Double> timeVyData) {

    this.timeVyData = timeVyData;
  }

  // /**
  // * @return the gr3Data
  // */
  // public List<Double> getGr3Data() {
  //
  // return gr3Data;
  // }
  //
  // /**
  // * @param gr3Data the gr3Data to set
  // */
  // public void setGr3Data(List<Double> gr3Data) {
  //
  // this.gr3Data = gr3Data;
  // }
}
