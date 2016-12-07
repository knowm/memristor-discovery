package org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.plot;

import java.beans.PropertyChangeListener;

import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.HysteresisPreferences;

public class PlotModel extends AppModel {

  /** Averaging params */
  private double ave = 0;
  private double k;

  /** Min Max params */

  Double yMaxIV = null;
  Double yMinIV = null;

  Double yMaxGV = null;
  Double yMinGV = null;

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   * 
   * @param listener
   */
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

    this.k = k;
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
  public AppPreferences initAppPreferences() {

    return new HysteresisPreferences();
  }

  @Override
  public void loadModelFromPrefs() {

    k = appPreferences.getDouble(HysteresisPreferences.K_INIT_DOUBLE_KEY, HysteresisPreferences.K_INIT_DOUBLE_DEFAULT_VALUE);
    swingPropertyChangeSupport.firePropertyChange(AppModel.EVENT_PREFERENCES_UPDATE, true, false);
  }

}
