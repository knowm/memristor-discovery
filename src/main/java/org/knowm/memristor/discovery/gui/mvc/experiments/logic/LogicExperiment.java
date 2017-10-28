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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic;

import static org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.gpio.MuxController;

public class LogicExperiment extends Experiment {

  private SwingWorker routineWorker;

  private final ControlModel controlModel = new ControlModel();
  private ControlPanel controlPanel;

  private PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  private AHaHController_21 aHaHController;
  private final MuxController muxController;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public LogicExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    new ControlController(controlPanel, controlModel, dwfProxy);
    System.out.println(controlModel.getRoutine());

    aHaHController = new AHaHController_21(controlModel);
    aHaHController.setdWFProxy(dwfProxy);
    muxController = new MuxController();

  }

  @Override
  public void doCreateAndShowGUI() {

    controlPanel.runRoutineButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        routineWorker = new RoutineWorker();
        routineWorker.execute();

      }
    });
  }

  private class RoutineWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // aHaHController.executeInstruction(controlModel.getInstruction());

      System.out.println("Routine: " + controlModel.getRoutine());
      System.out.println("NumExecutions: " + controlModel.getNumExecutions());

      // publish(aHaHController.getGa(), aHaHController.getGb(), aHaHController.getVy());
      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

      plotController.updateYChartData(chunks.get(0), chunks.get(1), chunks.get(2));
      plotController.repaintYChart();
    }
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI
   * components get updated.
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

    return new RoutineWorker();
  }
}
