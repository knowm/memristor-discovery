/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2020 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify12.result;

import java.util.List;

public class ResultController {

  private final ResultPanel resultPanel;
  private final ResultModel resultModel;

  /**
   * Constructor
   *
   * @param resultPanel
   * @param resultModel
   */
  public ResultController(ResultPanel resultPanel, ResultModel resultModel) {

    this.resultPanel = resultPanel;
    this.resultModel = resultModel;
  }

  public void addTrainAccuracyDataPoint(double accuracy) {
    resultModel.getTrainAccuracy().add(accuracy);
    resultPanel
        .getTrainChart()
        .updateXYSeries("Train Accuracy", null, resultModel.getTrainAccuracy(), null);
    resultPanel.repaint();
  }

  public void addSynapticWeightValuesPoint(List<Double> synapseValues) {

    resultModel.addSynapticWeightValues(synapseValues);

    for (int i = 0; i < 8; i++) {
      resultPanel
          .getSynapticWeightsChart()
          .updateXYSeries("Synapse " + (i + 1), null, resultModel.getSynapseWeightHistory(i), null);
    }
    resultPanel.repaint();
  }

  public void resetChart() {

    resultModel.clearData();
  }
}
