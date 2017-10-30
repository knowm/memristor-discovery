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
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.AHaHController_21.Instruction;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicPreferences.DataStructure;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.plot.PlotPanel;

public class LogicExperiment extends Experiment {

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
  public LogicExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

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

    controlPanel.runRoutineButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        routineWorker = new FFRUTraceWorker();
        routineWorker.execute();

      }
    });
    controlPanel.resetButton.addActionListener(new ActionListener() {

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

      // aHaHController.executeInstruction(controlModel.getInstruction());
      System.out.println("RESET WORKER");

      dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), false);

      // publish(aHaHController.getGa(), aHaHController.getGb(), aHaHController.getVy());
      return true;
    }

  }

  private class FFRUTraceWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      System.out.println("FFRUTraceWorker");
      System.out.println("NumExecutions: " + controlModel.getNumExecutions());
      System.out.println("MaskA: " + controlModel.getInputMaskA());
      System.out.println("MaskB: " + controlModel.getInputMaskB());
      System.out.println("BiasMask: " + controlModel.getInputBiasMask());

      List<TraceDatum> trace = new ArrayList<TraceDatum>();

      for (int i = 0; i < controlModel.getNumExecutions(); i++) {
        List<Integer> pattern = getNextPattern();
        dwfProxy.update2DigitalIOStatesAtOnce(pattern, true);

        // execute instruction over pattern-->
        aHaHController.executeInstruction(Instruction.FFLV);// need this to make FF-RU work
        aHaHController.executeInstruction(Instruction.FF_RU);
        // turn off pattern-->
        dwfProxy.update2DigitalIOStatesAtOnce(pattern, false);

        // measure each synapse seperatly with FFLV

        // synapse A
        dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), true);
        aHaHController.executeInstruction(Instruction.FFLV);
        double vy_a = aHaHController.getVy();
        double ga_a = aHaHController.getGa();
        double gb_a = aHaHController.getGb();
        dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), false);

        // synapse b
        dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskB(), true);
        aHaHController.executeInstruction(Instruction.FFLV);
        double vy_b = aHaHController.getVy();
        double ga_b = aHaHController.getGa();
        double gb_b = aHaHController.getGb();
        dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskB(), false);

        TraceDatum traceDatum = new TraceDatum(vy_a, ga_a, gb_a, vy_b, ga_b, gb_b);
        trace.add(traceDatum);

        // publish(aHaHController.getGa(), aHaHController.getGb(), aHaHController.getVy());

        Thread.sleep(500);

      }

      plotController.addTrace(trace);

      return true;
    }

  }

  private List<Integer> getNextPattern() {

    if (controlModel.getDataStructure() == DataStructure.TwoPattern) {
      if (Math.random() < .5) {
        return controlModel.getInputMaskA();
      }
      else {
        return controlModel.getInputMaskB();
      }
    }
    else if (controlModel.getDataStructure() == DataStructure.ThreePattern) {

      double r = Math.random();

      if (r < .3333) {
        return controlModel.getInputMaskA();
      }
      else if (r < .6666) {
        return controlModel.getInputMaskB();
      }
      else {

        List<Integer> combined = new ArrayList<Integer>();
        combined.addAll(controlModel.getInputMaskA());
        combined.addAll(controlModel.getInputMaskB());

        return combined;
      }
    }
    else {
      return null;
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

    return new FFRUTraceWorker();
  }
}
