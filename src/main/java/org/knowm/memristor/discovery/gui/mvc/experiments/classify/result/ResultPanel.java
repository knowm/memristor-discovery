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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify.result;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class ResultPanel extends JPanel {

  XYChart trainChart;
  XChartPanel<XYChart> trainChartPanel;

  XYChart synapticWeightsChart;
  XChartPanel<XYChart> synapticWeightChartPanel;

  /** Constructor */
  public ResultPanel() {

    // setLayout(new BorderLayout());
    setLayout(new GridLayout(2, 1));
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    trainChart =
        new XYChartBuilder()
            .width(200)
            .title("Train Accuracy")
            .height(200)
            .xAxisTitle("Time Step")
            .yAxisTitle("Cummulative Accuracy")
            .build();
    trainChart.getStyler().setLegendVisible(false);

    XYSeries series1 = trainChart.addSeries("Train Accuracy", null, Arrays.asList(0.0));
    series1.setMarker(SeriesMarkers.NONE);
    series1.setLineWidth(1f);

    trainChartPanel = new XChartPanel<>(trainChart);
    trainChartPanel.setLayout(new BorderLayout());
    add(trainChartPanel, BorderLayout.CENTER);

    synapticWeightsChart =
        new XYChartBuilder()
            .width(200)
            .title("Synaptic Weights")
            .height(200)
            .xAxisTitle("Epoch")
            .yAxisTitle("Value")
            .build();
    synapticWeightsChart.getStyler().setLegendVisible(true);

    for (int i = 0; i < 8; i++) {
      XYSeries series =
          synapticWeightsChart.addSeries("Synapse " + (i + 1), null, Arrays.asList(0.0));
      series.setMarker(SeriesMarkers.NONE);
      series.setLineWidth(1f);
    }

    synapticWeightChartPanel = new XChartPanel<>(synapticWeightsChart);
    synapticWeightChartPanel.setLayout(new BorderLayout());
    add(synapticWeightChartPanel, BorderLayout.CENTER);
  }

  //  @Override
  //  public void repaint() {
  //
  //    if (trainChartPanel != null) {
  //      trainChartPanel.revalidate();
  //      trainChartPanel.repaint();
  //
  //      testChartPanel.revalidate();
  //      testChartPanel.repaint();
  //    }
  //
  //  }

  public XYChart getSynapticWeightsChart() {
    return synapticWeightsChart;
  }

  public XYChart getTrainChart() {
    return trainChart;
  }

  //  public XChartPanel<XYChart> getChartPanel() {
  //
  //    return testChartPanel;
  //  }

}
