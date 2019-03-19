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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic;

import static org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.AHaHController_21.Instruction;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicPreferences.DataStructure;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.result.ResultPanel;

public class LogicExperiment extends Experiment {

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ResultModel resultModel;
  private final ResultController resultController;
  private AHaHController_21 aHaHController;
  private ControlPanel controlPanel;
  private ResultPanel resultPanel;

  // SwingWorkers
  private SwingWorker routineWorker;
  private SwingWorker runTrialsWorker;
  private SwingWorker resetWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public LogicExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel();

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);

    aHaHController = new AHaHController_21(controlModel);
    aHaHController.setdWFProxy(dwfProxy);
  }

  @Override
  public void doCreateAndShowGUI() {}

  @Override
  public void addWorkersToButtonEvents() {

    controlPanel.clearPlotButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            resultModel.clearAllTraces();
            resultController.resetChart();
          }
        });

    controlPanel.FFRUButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            routineWorker = new TraceWorker(Instruction.FF_RU);
            routineWorker.execute();
          }
        });
    controlPanel.FFRAButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            routineWorker = new TraceWorker(Instruction.FF_RA);
            routineWorker.execute();
          }
        });
    controlPanel.runTrialsButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            runTrialsWorker = new RunTrialsWorker();
            runTrialsWorker.execute();
          }
        });
    controlPanel.resetAllButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            resetWorker = new ResetWorker();
            resetWorker.execute();
          }
        });
  }

  // determines the logic state (A, B or C)
  private String getLogicState() {

    List<Integer> patternOne = new ArrayList<Integer>();
    patternOne.addAll(controlModel.getInputMaskA());

    List<Integer> patternTwo = new ArrayList<Integer>();
    patternTwo.addAll(controlModel.getInputMaskB());

    List<Integer> patternThree = new ArrayList<Integer>();
    patternThree.addAll(getCombinedPattern());

    if (controlModel.getInputBiasMask() != null) {

      patternOne.addAll(controlModel.getInputBiasMask());
      patternTwo.addAll(controlModel.getInputBiasMask());
      patternThree.addAll(controlModel.getInputBiasMask());
    }

    String out = "";

    dwfProxy.update2DigitalIOStatesAtOnce(patternOne, true);
    aHaHController.executeInstruction(Instruction.FFLV); // need this to make FF-RU/FF-RA work
    out += aHaHController.getVy() > 0 ? "1" : "0";
    dwfProxy.update2DigitalIOStatesAtOnce(patternOne, false);

    dwfProxy.update2DigitalIOStatesAtOnce(patternTwo, true);
    aHaHController.executeInstruction(Instruction.FFLV); // need this to make FF-RU/FF-RA work
    out += aHaHController.getVy() > 0 ? "1" : "0";
    dwfProxy.update2DigitalIOStatesAtOnce(patternTwo, false);

    dwfProxy.update2DigitalIOStatesAtOnce(patternThree, true);
    aHaHController.executeInstruction(Instruction.FFLV); // need this to make FF-RU/FF-RA work
    out += aHaHController.getVy() > 0 ? "1" : "0";
    dwfProxy.update2DigitalIOStatesAtOnce(patternThree, false);

    return out;
  }

  private List<Integer> getNextPatternWithBias() {

    List<Integer> patternOut = new ArrayList<Integer>();

    if (controlModel.getDataStructure() == DataStructure.TwoPattern) {
      if (Math.random() < .5) {
        patternOut.addAll(controlModel.getInputMaskA());
      } else {
        patternOut.addAll(controlModel.getInputMaskB());
      }
    } else if (controlModel.getDataStructure() == DataStructure.ThreePattern) {

      double r = Math.random();

      if (r < .3333) {
        patternOut.addAll(controlModel.getInputMaskA());
      } else if (r < .6666) {
        patternOut.addAll(controlModel.getInputMaskB());
      } else {

        patternOut = getCombinedPattern();
      }
    }

    if (controlModel.getInputBiasMask() != null) {
      // System.out.println("getNextPatternWithBias()");
      // System.out.println("PatternOut=" + patternOut);
      // System.out.println("  MaskA=" + controlModel.getInputMaskA());
      // System.out.println("  MaskB=" + controlModel.getInputMaskB());
      // System.out.println("  BiasMask()=" + controlModel.getInputBiasMask());

      patternOut.addAll(controlModel.getInputBiasMask());
      // System.out.println("PatternOut=" + patternOut);

    }

    return patternOut;
  }

  private List<Integer> getCombinedPattern() {

    List<Integer> combined = new ArrayList<Integer>();
    combined.addAll(controlModel.getInputMaskA());
    combined.addAll(controlModel.getInputMaskB());

    return combined;
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying
   * controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI components get updated.
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
  public Model getControlModel() {

    return controlModel;
  }

  @Override
  public ControlView getControlPanel() {

    return controlPanel;
  }

  @Override
  public Model getResultModel() {
    return resultModel;
  }

  @Override
  public JPanel getResultPanel() {

    return resultPanel;
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new LogicPreferences();
  }

  private class ResetWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {
        int initPulseWidth = controlModel.getPulseWidth();
        float initVoltage = controlModel.getAmplitude();
        DataStructure dataStructure = controlModel.getDataStructure();

        controlModel.setDataStructure(LogicPreferences.DataStructure.ThreePattern.toString());
        controlModel.setPulseWidth(500_000);
        controlModel.setAmplitude(1.5f);

        for (int i = 0; i < 20; i++) {
          List<Integer> pattern = getNextPatternWithBias();
          dwfProxy.update2DigitalIOStatesAtOnce(pattern, true);
          aHaHController.executeInstruction(Instruction.FFLV); // need this to make FF-RU/FF-RA work
          aHaHController.executeInstruction(Instruction.FF_RA);
          dwfProxy.update2DigitalIOStatesAtOnce(pattern, false);
        }

        controlModel.setPulseWidth(initPulseWidth);
        controlModel.setAmplitude(initVoltage);
        controlModel.setDataStructure(dataStructure.toString());

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }

    //    private void zeroSynapse() {
    //
    //      System.out.println("zeroing synapse");
    //
    //      int initPulseWidth = controlModel.getPulseWidth();
    //      float initVoltage = controlModel.getAmplitude();
    //
    //      controlModel.setPulseWidth(1_000_000);
    //      for (float voltage = 1.5f; voltage > .5f; voltage -= .1) {
    //        System.out.println("voltage=" + voltage);
    //        //double vy = 0;
    //        for (int i = 0; i < 5; i++) {
    //
    //          controlModel.setAmplitude(voltage);
    //          // execute instruction over pattern-->
    //          aHaHController.executeInstruction(Instruction.FFLV);// need this to make FF-RU/FF-RA
    // work
    //          double newVy = aHaHController.getVy();
    //          System.out.println("vy=" + newVy);
    //          //          if (i > 0 && newVy * vy < 0) {//the state has changed
    //          //            System.out.println("State Has Changed!");
    //          //
    //          //            break;
    //          //          }
    //          // vy = newVy;
    //          aHaHController.executeInstruction(Instruction.FF_RA);
    //        }
    //      }
    //      controlModel.setAmplitude(initVoltage);
    //      controlModel.setPulseWidth(initPulseWidth);
    //
    //    }

    //    private void zeroSynapse(int maxPulses, int pulseWidthInNs) {
    //
    //      controlModel.setPulseWidth(pulseWidthInNs);
    //
    //      // TODO: work to get ride of this. Should be possible through gradual reduction of pulse
    // width or voltage in a deterministic routine.
    //      int c = 0;
    //      int cMax = Math.random() > .5 ? 2 : 1;
    //
    //      aHaHController.executeInstruction(Instruction.FFLV);
    //      boolean state = aHaHController.getVy() > 0;
    //      for (int i = 0; i < maxPulses; i++) {
    //        aHaHController.executeInstruction(Instruction.FF_RA);
    //        aHaHController.executeInstruction(Instruction.FFLV);
    //        boolean newState = aHaHController.getVy() > 0;
    //        if (newState != state) {
    //          c++;
    //        }
    //
    //        if (c >= cMax) {// this insures some randomness in the initializations
    //          System.out.println("Synapse zeroed on pulse #" + i);
    //          break;
    //
    //        }
    //
    //        state = newState;
    //
    //        if (i == maxPulses - 1) {
    //          System.out.println("Synapse could not be zeroed" + i);
    //        }
    //      }
    //
    //    }
  }

  private class RunTrialsWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {
        ResetWorker worker_reset = new ResetWorker();
        TraceWorker worker_RU = new TraceWorker(Instruction.FF_RU);
        for (int i = 0; i < 25; i++) {

          worker_RU.doInBackground();
          worker_reset.doInBackground();
        }

      } catch (Exception e) {
        e.printStackTrace();
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

        List<Integer> pattern = getNextPatternWithBias();
        dwfProxy.update2DigitalIOStatesAtOnce(pattern, true);

        System.out.println("pattern with bias = " + pattern);

        // execute instruction over pattern-->
        aHaHController.executeInstruction(Instruction.FFLV); // need this to make FF-RU/FF-RA work

        if (controlModel.getInputBiasMask() != null) {
          dwfProxy.update2DigitalIOStatesAtOnce(
              controlModel.getInputBiasMask(), false); // turn off bias for FF-RU/FF-RA update
        }
        aHaHController.executeInstruction(instruction);

        // now give bias FF-RA if there are any .
        if (controlModel.getInputBiasMask() != null) {
          dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), false); // turn off A
          dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskB(), false); // turn off B
          dwfProxy.update2DigitalIOStatesAtOnce(
              controlModel.getInputBiasMask(), true); // turn bias back on
          // bias synapse are now on

          aHaHController.executeInstruction(Instruction.FF_RA);

          dwfProxy.update2DigitalIOStatesAtOnce(
              controlModel.getInputBiasMask(), false); // turn off B
          // all off now
        } else { // no bias. just turn everything off
          // turn off pattern-->
          dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskA(), false); // turn off A
          dwfProxy.update2DigitalIOStatesAtOnce(controlModel.getInputMaskB(), false); // turn off B
        }
      }

      if (instruction == Instruction.FF_RU) {
        resultController.addFFRUTrace(trace, getLogicState());
      } else {
        resultController.addFFRATrace(trace);
      }

      return true;
    }
  }
}
