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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.TraceDatum;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PlotController implements PropertyChangeListener {

  private final PlotPanel plotPanel;
  private final PlotControlModel plotModel;

  /**
   * Constructor
   *
   * @param plotPanel
   * @param plotModel
   */
  public PlotController(PlotPanel plotPanel, PlotControlModel plotModel) {

    this.plotPanel = plotPanel;
    this.plotModel = plotModel;

  }

  public void addFFRATrace(List<TraceDatum> trace) {

    System.out.println("PlotController.addTrace()");

    int traceNum = plotModel.getNumTraces();

    // // green dot for first data point
    // XYSeries series1 = plotPanel.chart.addSeries(traceNum + "_1", new double[] { trace.get(0).vy_a }, new double[] { trace.get(0).vy_b });
    // series1.setMarker(SeriesMarkers.CIRCLE);
    // series1.setMarkerColor(Color.green);

    // blue trace

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

    // // red dot for last data point.
    // XYSeries series3 = plotPanel.chart.addSeries(traceNum + "_3", new double[] { trace.get(trace.size() - 1).vy_a }, new double[] { trace.get(trace.size() - 1).vy_b });
    // series3.setMarker(SeriesMarkers.CIRCLE);
    // series3.setMarkerColor(Color.red);

    plotModel.addTrace(trace);
    repaintChart();

  }

  public void addFFRUTrace(List<TraceDatum> trace) {

    System.out.println("PlotController.addTrace()");

    int traceNum = plotModel.getNumTraces();

    // green dot for first data point
    XYSeries series1 = plotPanel.chart.addSeries(traceNum + "_1", new double[] { trace.get(0).vy_a }, new double[] { trace.get(0).vy_b });
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

    // red dot for last data point.
    XYSeries series3 = plotPanel.chart.addSeries(traceNum + "_3", new double[] { trace.get(trace.size() - 1).vy_a }, new double[] { trace.get(trace.size() - 1).vy_b });
    series3.setMarker(SeriesMarkers.CIRCLE);
    series3.setMarkerColor(Color.red);

    plotModel.addTrace(trace);
    repaintChart();

  }

  public void repaintChart() {

    plotPanel.getChartPanel().revalidate();
    plotPanel.getChartPanel().repaint();
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

    case ExperimentControlModel.EVENT_PREFERENCES_UPDATE:

      break;

    default:
      break;
    }
  }
}
