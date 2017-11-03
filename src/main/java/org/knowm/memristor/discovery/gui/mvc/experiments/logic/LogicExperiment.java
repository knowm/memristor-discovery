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
  private SwingWorker runTrialsWorker;
  private SwingWorker resetWorker;

  private final ControlModel controlModel = new ControlModel();
  private ControlPanel controlPanel;

  private PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  private AHaHController_21 aHaHController;

  private static final double MAX_G = .0005;// init of synapses will terminate at this conductance

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

    controlPanel.FFRUButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        routineWorker = new TraceWorker(Instruction.FF_RU);
        routineWorker.execute();

      }
    });
    controlPanel.FFRAButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        routineWorker = new TraceWorker(Instruction.FF_RA);
        routineWorker.execute();

      }
    });
    controlPanel.runTrialsButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        runTrialsWorker = new RunTrialsWorker();
        runTrialsWorker.execute();

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

      int initPulseWidth = controlModel.getPulseWidth();

      System.out.println("initPulseWidth=" + initPulseWidth);

      // turn on all synapses
      dwfProxy.update2DigitalIOStatesAtOnce(getCombinedPattern(), true);

      // drive memristors low with long pulse (1mS)
      controlModel.setPulseWidth(1000000);

      // INIT ALL MEMRISTORS LOW.
      for (int i = 0; i < 10; i++) {
        aHaHController.executeInstruction(Instruction.RLadn);
        aHaHController.executeInstruction(Instruction.RHbdn);
      }

      // INIT SYNAPSE PAIRS TO HIGH CONDUCTANCE
      // turn off B. A is now selected.
      dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskB(), false);

      System.out.println("Driving Synapse A to high conductance");
      for (int i = 0; i < 50; i++) {
        aHaHController.executeInstruction(Instruction.FF);
        aHaHController.executeInstruction(Instruction.FFLV);
        System.out.println("Ga=" + aHaHController.getGa() + ", Gb=" + aHaHController.getGb());
        if (aHaHController.getGa() > MAX_G || aHaHController.getGb() > MAX_G) {
          System.out.println("MaxG reached for Synapse A");
          break;
        }
      }
      dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), false);

      // B is now selected
      dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskB(), true);
      for (int i = 0; i < 50; i++) {
        aHaHController.executeInstruction(Instruction.FF);
        aHaHController.executeInstruction(Instruction.FFLV);
        System.out.println("Ga=" + aHaHController.getGa() + ", Gb=" + aHaHController.getGb());
        if (aHaHController.getGa() > MAX_G || aHaHController.getGb() > MAX_G) {
          System.out.println("MaxG Reached for Synapse B");
          break;
        }
      }

      // ZERO EACH SYNAPSE
      controlModel.setPulseWidth(initPulseWidth);

      // synapse B
      zeroSynapse(100);

      dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskB(), false);
      dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), true);
      // synapse A is now selected
      zeroSynapse(100);

      dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), false);

      return true;
    }
  }

  private void zeroSynapse(int maxPulses) {

    int c = 0;
    int cMax = Math.random() > .5 ? 2 : 1;

    aHaHController.executeInstruction(Instruction.FFLV);
    boolean state = aHaHController.getVy() > 0;
    for (int i = 0; i < maxPulses; i++) {
      aHaHController.executeInstruction(Instruction.FF_RA);
      aHaHController.executeInstruction(Instruction.FFLV);
      boolean newState = aHaHController.getVy() > 0;
      if (newState != state) {
        c++;
      }

      if (c >= cMax) {// this insures some randomness in the initializations
        break;
      }

      state = newState;

    }
  }

  private class RunTrialsWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      ResetWorker worker_reset = new ResetWorker();
      TraceWorker worker_RU = new TraceWorker(Instruction.FF_RU);
      for (int i = 0; i < 25; i++) {

        worker_RU.doInBackground();
        worker_reset.doInBackground();

      }

      return true;
    }

  }

  private class TraceWorker extends SwingWorker<Boolean, Double> {

    private Instruction instruction;

    public TraceWorker(Instruction instruction) {

      this.instruction = instruction;
    }

    @Override
    protected Boolean doInBackground() throws Exception {

      // System.out.println("FFRUTraceWorker");
      // System.out.println("NumExecutions: " + controlModel.getNumExecutions());
      // System.out.println("MaskA: " + controlModel.getInputMaskA());
      // System.out.println("MaskB: " + controlModel.getInputMaskB());
      // System.out.println("BiasMask: " + controlModel.getInputBiasMask());

      List<TraceDatum> trace = new ArrayList<TraceDatum>();

      for (int i = 0; i < controlModel.getNumExecutions(); i++) {

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

        List<Integer> pattern = getNextPattern();
        dwfProxy.update2DigitalIOStatesAtOnce(pattern, true);

        // execute instruction over pattern-->
        aHaHController.executeInstruction(Instruction.FFLV);// need this to make FF-RU work
        aHaHController.executeInstruction(instruction);
        // turn off pattern-->
        dwfProxy.update2DigitalIOStatesAtOnce(pattern, false);

      }

      if (instruction == Instruction.FF_RU) {
        plotController.addFFRUTrace(trace);
      }
      else {
        plotController.addFFRATrace(trace);
      }

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

        List<Integer> combined = getCombinedPattern();
        return combined;
      }
    }
    else {
      return null;
    }

  }

  private List<Integer> getCombinedPattern() {

    List<Integer> combined = new ArrayList<Integer>();
    combined.addAll(controlModel.getInputMaskA());
    combined.addAll(controlModel.getInputMaskB());

    return combined;
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

    return null;
  }

}
