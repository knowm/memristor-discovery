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

import java.util.List;

public class PlotController {

  private final ResultsPanel plotPanel;
  private final PlotControlModel plotModel;

  /**
   * Constructor
   *
   * @param plotPanel
   * @param plotModel
   */
  public PlotController(ResultsPanel plotPanel, PlotControlModel plotModel) {

    this.plotPanel = plotPanel;
    this.plotModel = plotModel;
  }

  public void addTrainAccuracyDataPoint(double accuracy) {
    plotModel.getTrainAccuracy().add(accuracy);
    plotPanel
        .getTrainChart()
        .updateXYSeries("Train Accuracy", null, plotModel.getTrainAccuracy(), null);
    plotPanel.repaint();
  }

  public void addSynapticWeightValuesPoint(List<Double> synapseValues) {

    plotModel.addSynapticWeightValues(synapseValues);

    for (int i = 0; i < 8; i++) {
      plotPanel
          .getSynapticWeightsChart()
          .updateXYSeries("Synapse " + (i + 1), null, plotModel.getSynapseWeightHistory(i), null);
    }
    plotPanel.repaint();
  }

  public void resetChart() {

    plotModel.clearData();
  }
}
