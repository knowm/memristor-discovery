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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.TraceDatum;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

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

  public void resetChart() {

    Set<String> seriesNames = plotPanel.chart.getSeriesMap().keySet();

    List<String> toDelete = new ArrayList<String>();

    for (String name : seriesNames) {
      toDelete.add(name);
    }

    for (int i = 0; i < toDelete.size(); i++) {
      plotPanel.chart.removeSeries(toDelete.get(i));
    }

    repaintChart();
  }

  public void addFFRATrace(List<TraceDatum> trace) {

    int traceNum = plotModel.getNumTraces();

    double[] vy_a = new double[trace.size()];
    double[] vy_b = new double[trace.size()];
    for (int i = 0; i < vy_b.length; i++) {
      vy_a[i] = trace.get(i).vy_a;
      vy_b[i] = trace.get(i).vy_b;
    }

    XYSeries series2 = plotPanel.chart.addSeries(traceNum + "_2", vy_a, vy_b);
    series2.setMarker(SeriesMarkers.NONE);
    series2.setLineColor(Color.gray);
    series2.setLineWidth(.2f);

    plotModel.addTrace(trace);
    repaintChart();
  }

  public void addFFRUTrace(List<TraceDatum> trace, String logicState) {

    int traceNum = plotModel.getNumTraces();

    // green dot for first data point
    XYSeries series1 =
        plotPanel.chart.addSeries(
            traceNum + "_1", new double[] {trace.get(0).vy_a}, new double[] {trace.get(0).vy_b});
    series1.setMarker(SeriesMarkers.CIRCLE);
    series1.setMarkerColor(Color.green);

    // blue trace

    double[] vy_a = new double[trace.size()];
    double[] vy_b = new double[trace.size()];
    for (int i = 0; i < vy_b.length; i++) {
      vy_a[i] = trace.get(i).vy_a;
      vy_b[i] = trace.get(i).vy_b;
    }

    XYSeries series2 = plotPanel.chart.addSeries(traceNum + "_2", vy_a, vy_b);
    series2.setMarker(SeriesMarkers.NONE);
    series2.setLineColor(Color.blue);
    series2.setLineWidth(.3f);

    // red dot for last data point.
    XYSeries series3 =
        plotPanel.chart.addSeries(
            traceNum + "_3",
            new double[] {trace.get(trace.size() - 1).vy_a},
            new double[] {trace.get(trace.size() - 1).vy_b});
    series3.setMarker(SeriesMarkers.CIRCLE);

    if (logicState.equalsIgnoreCase("000") | logicState.equalsIgnoreCase("111")) { // A
      series3.setMarkerColor(Color.red);
    } else if (logicState.equalsIgnoreCase("010") | logicState.equalsIgnoreCase("101")) { // B
      series3.setMarkerColor(Color.orange);
    } else if (logicState.equalsIgnoreCase("011") | logicState.equalsIgnoreCase("100")) { // C
      series3.setMarkerColor(Color.magenta);
    }

    plotModel.addTrace(trace);
    repaintChart();
  }

  public void repaintChart() {

    plotPanel.getChartPanel().revalidate();
    plotPanel.getChartPanel().repaint();
  }
}
