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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PlotPanel extends ExperimentPlotPanel {

  private final JCheckBox freezeYAxisCheckBoxIV;
  private final JCheckBox resistanceConductanceCheckBox;

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

    gChart = new XYChartBuilder().width(100).title("Synapse State").height(250).xAxisTitle("Seconds from Start").build();
    gChart.getStyler().setLegendVisible(true);
    gChart.getStyler().setLegendPosition(LegendPosition.InsideSW);
    gChart.getStyler().setYAxisGroupPosition(1, Styler.YAxisPosition.Right);

    // gChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);

    List<Double> time = new ArrayList<>();
    time.add((double) 0);
    List<Double> value = new ArrayList<>();
    value.add((double) 0);

    XYSeries series1 = gChart.addSeries("A", time, value);
    series1.setMarker(SeriesMarkers.CIRCLE);
    series1.setLineWidth(1);

    XYSeries series2 = gChart.addSeries("B", time, value);
    series2.setMarker(SeriesMarkers.CIRCLE);
    series2.setLineWidth(1);

    // XYSeries series3 = gChart.addSeries("G(Ma-Mb)", new double[] { 0 }, new double[] { 0 });
    // series3.setMarker(SeriesMarkers.NONE);

    XYSeries series4 = gChart.addSeries("Vy", time, value);
    series4.setYAxisGroup(1);
    series4.setMarker(SeriesMarkers.DIAMOND);
    series4.setLineWidth(1);

    gChartPanel = new XChartPanel<>(gChart);

    gChartPanel.getChart().setYAxisGroupTitle(0, "Conductance (S)");
    gChartPanel.getChart().setYAxisGroupTitle(1, "Vy");

    // gChartPanel.getChart().getStyler().setYAxisMax(1.0);
    // gChartPanel.getChart().getStyler().setYAxisMin(-1.0);
    //

    // ///////////////////////////////////////////////////////////
    // Chart Panel ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    gChartPanel.setLayout(new BorderLayout());
    add(gChartPanel, BorderLayout.CENTER);

    // ///////////////////////////////////////////////////////////
    // Check Box ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    freezeYAxisCheckBoxIV = new JCheckBox("Freeze Y-Axis");
    resistanceConductanceCheckBox = new JCheckBox("Display Resistance");
    gvChartControlPanel = new JPanel();
    gvChartControlPanel.add(freezeYAxisCheckBoxIV);
    gvChartControlPanel.add(resistanceConductanceCheckBox);
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

  /**
   * @return the gvChartControlPanel
   */
  public JPanel getGvChartControlPanel() {

    return gvChartControlPanel;
  }

  /**
   * @return the resistanceConductanceCheckBox
   */
  public JCheckBox getResistanceConductanceCheckBox() {

    return resistanceConductanceCheckBox;
  }
}
