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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class PlotPanel extends ExperimentPlotPanel {

  XYChart chart;
  XChartPanel<XYChart> chartPanel;

  /** Constructor */
  public PlotPanel() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    chart =
        new XYChartBuilder()
            .width(200)
            .title("Synaptic Logic State Traces [A/~A:Red, B/~B:Orange, C/~C:Magenta")
            .height(200)
            .xAxisTitle("Sa")
            .yAxisTitle("Sb")
            .build();
    chart.getStyler().setLegendVisible(false);
    chartPanel = new XChartPanel<>(chart);

    chartPanel.setLayout(new BorderLayout());
    add(chartPanel, BorderLayout.CENTER);
  }

  public XChartPanel<XYChart> getChartPanel() {

    return chartPanel;
  }
}
