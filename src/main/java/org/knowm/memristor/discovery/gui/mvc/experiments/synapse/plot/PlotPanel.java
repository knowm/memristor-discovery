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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse.plot;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PlotPanel extends ExperimentPlotPanel {

  private final JCheckBox freezeYAxisCheckBoxIV;
  XYChart gChart;
  XChartPanel<XYChart> gChartPanel;
  private final JPanel gvChartControlPanel;

  /**
   * Constructor
   */
  public PlotPanel() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    // ///////////////////////////////////////////////////////////
    // G Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    gChart = new XYChartBuilder().width(100).title("Synapse Conductance").height(250).xAxisTitle("Sample").yAxisTitle("Conductance (mS)").build();
    gChart.getStyler().setLegendVisible(true);
    gChart.getStyler().setLegendPosition(LegendPosition.InsideSE);
    // gChart.getStyler().setYAxisMin(0.0);

    XYSeries series1 = gChart.addSeries("G(Ma)", new double[] { 0 }, new double[] { 0 });
    series1.setMarker(SeriesMarkers.NONE);
    XYSeries series2 = gChart.addSeries("G(Mb)", new double[] { 0 }, new double[] { 0 });
    series2.setMarker(SeriesMarkers.NONE);
    XYSeries series3 = gChart.addSeries("G(Ma-Mb)", new double[] { 0 }, new double[] { 0 });
    series3.setMarker(SeriesMarkers.NONE);

    gChartPanel = new XChartPanel<>(gChart);

    // ///////////////////////////////////////////////////////////
    // Chart Panel ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    gChartPanel.setLayout(new BorderLayout());
    add(gChartPanel, BorderLayout.CENTER);

    // ///////////////////////////////////////////////////////////
    // Check Box ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    freezeYAxisCheckBoxIV = new JCheckBox("Freeze Y-Axis");
    gvChartControlPanel = new JPanel();
    gvChartControlPanel.add(freezeYAxisCheckBoxIV);
    add(gvChartControlPanel, BorderLayout.NORTH);
  }

  public void switch2GVChart() {

    gChartPanel.revalidate();
    gChartPanel.repaint();
  }

  public JCheckBox getFreezeYAxisCheckBoxIV() {

    return freezeYAxisCheckBoxIV;
  }

  public XYChart getGChart() {

    return gChart;
  }

  public XChartPanel<XYChart> getGChartPanel() {

    return gChartPanel;
  }

  public double getYChartMax() {

    return gChart.getSeriesMap().get("y").getYMax();
  }

  public double getYChartMin() {

    return gChart.getSeriesMap().get("y").getYMin();
  }
}
