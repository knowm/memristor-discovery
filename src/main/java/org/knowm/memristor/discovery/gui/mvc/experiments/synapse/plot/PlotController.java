/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PlotController implements PropertyChangeListener {

  private final PlotPanel plotPanel;
  private final PlotControlModel plotModel;

  private Long startTime = null;

  /**
   * Constructor
   *
   * @param plotPanel
   * @param plotModel
   */
  public PlotController(PlotPanel plotPanel, PlotControlModel plotModel) {

    this.plotPanel = plotPanel;
    this.plotModel = plotModel;

    initGUIComponents();
    setUpViewEvents();
  }
  
  public void resetChart() {
    plotModel.clearData();
    startTime=System.currentTimeMillis();
    repaintYChart();
  }

  public void initGUIComponents() {

    plotPanel.getFreezeYAxisCheckBoxIV().setSelected(false);
    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

  }

  private void setUpViewEvents() {

    plotPanel.getFreezeYAxisCheckBoxIV().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (plotPanel.getFreezeYAxisCheckBoxIV().isSelected()) {
          plotModel.setyMaxIV(plotPanel.getYChartMax());
          plotModel.setyMinIV(plotPanel.getYChartMin());
        }
        else {
          plotModel.setyMaxIV(null);
          plotModel.setyMinIV(null);
        }
      }
    });

    plotPanel.getResistanceConductanceCheckBox().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // change data-->
        plotModel.setGr1Data(toOneOver(plotModel.getGr1Data()));
        plotModel.setGr2Data(toOneOver(plotModel.getGr2Data()));

        // update series-->
        plotPanel.getGChart().updateXYSeries("A", null, plotModel.getGr1Data(), null);
        plotPanel.getGChart().updateXYSeries("B", null, plotModel.getGr2Data(), null);

        if (plotPanel.getResistanceConductanceCheckBox().isSelected()) {
          plotPanel.getGChart().setYAxisGroupTitle(0, "Resistance (Ω)");
        }
        else {
          plotPanel.getGChart().setYAxisGroupTitle(0, "Conductance (S)");
        }
        repaintYChart();
      }
    });

  }

  private List<Double> toOneOver(List<Double> array) {

    List<Double> r = new ArrayList<Double>();
    for (int i = 0; i < array.size(); i++) {

      if (array.get(i) > 0) {
        r.add(1 / array.get(i));
      }
      else {
        r.add(1000000.0);// assume 1MOhm
      }

    }
    return r;

  }

  public void updateYChartData(Double g_a, Double g_b, Double vy) {

    if (startTime == null) {
      startTime = System.currentTimeMillis();
    }

    double timeFromStart = (System.currentTimeMillis() - startTime) / 1000.0;

    if (plotPanel.getResistanceConductanceCheckBox().isSelected()) {

      if (!g_a.isNaN()) {
        plotModel.getGr1Data().add(1 / g_a);
      }

      if (!g_b.isNaN()) {
        plotModel.getGr2Data().add(1 / g_b);
      }

    }
    else {
      if (!g_a.isNaN()) {
        plotModel.getGr1Data().add(g_a);
      }

      if (!g_b.isNaN()) {
        plotModel.getGr2Data().add(g_b);
      }

    }
    if (!vy.isNaN()) {
      plotModel.getVyData().add(vy);
      plotModel.getTimeVyData().add(timeFromStart);
    }

    if (!g_a.isNaN()) {
      plotModel.getTime1Data().add(timeFromStart);
    }
    if (!g_b.isNaN()) {
      plotModel.getTime2Data().add(timeFromStart);
    }

//    System.out.println(plotModel.getGr1Data());
//    System.out.println(plotModel.getGr2Data());

    if (plotModel.getGr1Data() != null && plotModel.getGr1Data().size() > 0) {
      plotPanel.getGChart().updateXYSeries("A", plotModel.getTime1Data(), plotModel.getGr1Data(), null);
    }

    if (plotModel.getGr2Data() != null && plotModel.getGr2Data().size() > 0) {
      plotPanel.getGChart().updateXYSeries("B", plotModel.getTime2Data(), plotModel.getGr2Data(), null);
    }

    if (plotModel.getVyData() != null && plotModel.getVyData().size() > 0) {
      plotPanel.getGChart().updateXYSeries("Vy", plotModel.getTimeVyData(), plotModel.getVyData(), null);
    }

  }

  public void repaintYChart() {

    plotPanel.getGChartPanel().revalidate();
    plotPanel.getGChartPanel().repaint();
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

    case ExperimentControlModel.EVENT_PREFERENCES_UPDATE:

      initGUIComponentsFromModel();
      break;

    default:
      break;
    }
  }
}
