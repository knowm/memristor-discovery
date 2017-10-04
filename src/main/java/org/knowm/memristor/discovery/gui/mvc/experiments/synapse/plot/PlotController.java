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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;

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

    initGUIComponents();
    setUpViewEvents();
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
  }

  public void updateYChartData(double g_a, double g_b) {

    plotModel.getGM1Data().add(g_a);
    plotModel.getGM2Data().add(g_b);
    plotModel.getGM3Data().add(g_a - g_b);
    // plotPanel.getGChart().getStyler().setYAxisMax(plotModel.getyMaxGV());
    // plotPanel.getGChart().getStyler().setYAxisMin(plotModel.getyMinGV());
    plotPanel.getGChart().updateXYSeries("G(Ma)", null, plotModel.getGM1Data(), null);
    plotPanel.getGChart().updateXYSeries("G(Mb)", null, plotModel.getGM2Data(), null);
    plotPanel.getGChart().updateXYSeries("G(Ma-Mb)", null, plotModel.getGM3Data(), null);
    // plotPanel.getGChart().updateXYSeries("ylast", new double[]{1, plotModel.getGData().size()}, new double[]{conductance, conductance}, null);
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
