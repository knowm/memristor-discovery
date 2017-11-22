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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify;

import static org.knowm.memristor.discovery.gui.mvc.experiments.classify.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.plot.PlotPanel;

public class ClassifyExperiment extends Experiment {

  private SwingWorker routineWorker;
  private SwingWorker resetWorker;

  private final ControlModel controlModel = new ControlModel();
  private ControlPanel controlPanel;

  private PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  private AHaHController_21 aHaHController;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public ClassifyExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    new ControlController(controlPanel, controlModel, dwfProxy);

    aHaHController = new AHaHController_21(controlModel);
    aHaHController.setdWFProxy(dwfProxy);

  }

  @Override
  public void doCreateAndShowGUI() {

    controlPanel.runTrialButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        //        runTrialsWorker = new RunTrialsWorker();
        //        runTrialsWorker.execute();

      }
    });
    controlPanel.resetAllButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        resetWorker = new ResetWorker();
        resetWorker.execute();

      }
    });
  }

  private class ResetWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }

  }

  private class RunTrialsWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }

  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can
   * respond to those events and make sure the corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    switch (propName) {

      case EVENT_INSTRUCTION_UPDATE:

        // System.out.println(controlModel.getInstruction());
        // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

        break;

      default:
        break;
    }
  }

  @Override
  public ExperimentControlModel getControlModel() {

    return controlModel;
  }

  @Override
  public ExperimentControlPanel getControlPanel() {

    return controlPanel;
  }

  @Override
  public ExperimentPlotPanel getPlotPanel() {

    return plotPanel;
  }

  @Override
  public SwingWorker getCaptureWorker() {

    return null;
  }

}
