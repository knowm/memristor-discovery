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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify.plot;

import java.util.ArrayList;
import java.util.List;

public class PlotControlModel {

  private List<Double> trainAccuracy = new ArrayList<>();
  private List<List<Double>> synapticWeights = new ArrayList<>();

  public PlotControlModel() {

    for (int i = 0; i < 8; i++) {
      synapticWeights.add(new ArrayList<Double>());
    }
  }

  public List<Double> getTrainAccuracy() {
    return trainAccuracy;
  }

  public void addSynapticWeightValues(List<Double> synapticWeightValues) {
    for (int i = 0; i < synapticWeightValues.size(); i++) {
      synapticWeights.get(i).add(synapticWeightValues.get(i));
    }
  }

  public List<Double> getSynapseWeightHistory(int synapse) {
    return synapticWeights.get(synapse);
  }

  public void addTrainAccuracyDataPoint(Double trainAccuracy) {
    this.trainAccuracy.add(trainAccuracy);
  }

  public void clearData() {
    trainAccuracy.clear();

    for (int i = 0; i < 8; i++) {
      synapticWeights.get(i).clear();
    }
  }
}
