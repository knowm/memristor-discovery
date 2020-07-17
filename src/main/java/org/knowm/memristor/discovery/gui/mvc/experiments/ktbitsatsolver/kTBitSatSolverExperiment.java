/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2020 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.KTRAM_Controller_12.Instruction;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.result.ResultPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.sat.Constraint;

public class kTBitSatSolverExperiment extends Experiment {

  private ControlPanel controlPanel;
  private ResultPanel resultPanel;

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ResultModel resultModel;
  private final ResultController resultController;

  // SwingWorkers
  private SwingWorker runTrialWorker;
  private SwingWorker resetWorker;
  private SwingWorker initWorker;

  private KTRAM_Controller_12 KTRAM_Controller;

  private boolean terminate = false;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public kTBitSatSolverExperiment(
      DWFProxy dwfProxy, Container mainFrameContainer, int boardVersion) {

    super(dwfProxy, mainFrameContainer, boardVersion);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel();

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);

    KTRAM_Controller = new KTRAM_Controller_12(controlModel);

    KTRAM_Controller.setdWFProxy(dwfProxy);
  }

  @Override
  public void doCreateAndShowGUI() {}

  @Override
  public void addWorkersToButtonEvents() {

    controlPanel.initButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            initWorker = new InitializeWorker();
            initWorker.execute();
          }
        });

    controlPanel.clearPlotButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("should reset chart now");
            resultController.resetChart();
          }
        });

    controlPanel.runTrialButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            if (controlPanel.runTrialButton.getText().equalsIgnoreCase("Solve")) {

              resultPanel.getSynapticWeightsChart().setTitle("Solving...");
              terminate = false;
              runTrialWorker = new TrialWorker();
              runTrialWorker.execute();
              controlPanel.runTrialButton.setText("Stop");

            } else {
              terminate = true;
              controlPanel.runTrialButton.setText("Solve");
            }
          }
        });
  }

  private float[] initSynapses() {

    // int pw = controlModel.getPulseWidth();
    // controlModel.setPulseWidth(pulseWidthInNs);
    float[] synapticWeights = new float[8];
    for (int i = 0; i < 8; i++) {
      SpikePattern pattern = new SpikePattern(true, Arrays.asList(i));
      KTRAM_Controller.executeInstruction(pattern, Instruction.FLV);
      KTRAM_Controller.executeInstruction(pattern, Instruction.FAB);
      KTRAM_Controller.executeInstruction(pattern, Instruction.ANTI_HEBBIAN);
      synapticWeights[i] = (float) KTRAM_Controller.getVy();
    }

    resultController.addSynapticWeightValuesPoint(synapticWeights);
    // controlModel.setPulseWidth(pw);

    return synapticWeights;
  }

  private float[] readAllSynapses(int pulseWidthInNs) {

    int pw = controlModel.getPulseWidth();

    controlModel.setPulseWidth(pulseWidthInNs);

    float[] synapticWeights = new float[8];
    for (int i = 0; i < 8; i++) {
      SpikePattern pattern = new SpikePattern(true, Arrays.asList(i));
      KTRAM_Controller.executeInstruction(pattern, Instruction.FLV);
      synapticWeights[i] = (float) KTRAM_Controller.getVy();
    }

    resultController.addSynapticWeightValuesPoint(synapticWeights);
    controlModel.setPulseWidth(pw);

    return synapticWeights;
  }

  //  private void loadSpikePattern(List<Integer> spikes) {
  //
  //    dwfProxy.turnOffAllSwitches();
  //    dwfProxy.update2DigitalIOStatesAtOnce(spikes, true); // set spike pattern
  //  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying
   * controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {}

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

    return new kTBitSatSolverPreferences();
  }

  private class InitializeWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {

        for (int i = 0; i < 100; i++) {
          initSynapses();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }
  }

  private class TrialWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {

        // load constraints
        List<Constraint> constraints = new ArrayList<>();
        String textLine;
        int lineNumber = 0;
        BufferedReader reader = new BufferedReader(new FileReader(controlModel.getFilePath()));
        while ((textLine = reader.readLine()) != null) {
          if (lineNumber >= 2) {
            Constraint c = new Constraint(textLine);
            constraints.add(c);
          }
          lineNumber++;
        }

        // map variables to the constraints they are part of
        Map<Integer, List<Constraint>> map = new HashMap<>();
        for (int i = 0; i < constraints.size(); i++) {
          constraints.get(i).loadMap(map);
        }

        while (!terminate) {

          // read the state of the kT-Synapses
          float[] kTBits = readAllSynapses(50_000);

          // System.out.println(Arrays.toString(kTBits));

          // check constraints
          int numConstraintsSatisfied = 0;
          for (int i = 0; i < constraints.size(); i++) {

            constraints.get(i).setSatisfied(kTBits);
            if (constraints.get(i).isSatisfied()) {
              numConstraintsSatisfied++;
            }
          }

          resultModel.setNumConstraintsSatisfied(numConstraintsSatisfied);

          // System.out.println("constraints sat: " + numConstraintsSatisfied);

          // update upper plot with number of constraints satisfied
          resultController.updateConstraintsSatisfiedChart();

          if (numConstraintsSatisfied == constraints.size()) {

            terminate = true;
            controlPanel.runTrialButton.setText("Solve");
            resultPanel
                .getSynapticWeightsChart()
                .setTitle("Solved: " + kTBitsToBinaryString(kTBits));

            break;
          }

          // System.out.println("");
          // System.out.println("Feedback");
          // reward or punish each kTBit
          Set<Integer> variables = map.keySet();
          for (Integer x : variables) {

            List<Constraint> cList = map.get(x);

            int numSat = 0;
            int numNotSat = 0;

            boolean reward = true;
            for (int i = 0; i < cList.size(); i++) {
              if (!cList.get(i).isSatisfied()) {
                reward = false;
                numNotSat++;
              } else {
                numSat++;
              }
            }

            // System.out.println(x + ":  " + numSat + " / " + numNotSat);

            int idx = x - 1; // not zero indexed
            SpikePattern spikePattern = new SpikePattern(Arrays.asList(idx));
            if (reward) { // HEBBIAN feedback
              if (kTBits[idx] >= 0) { // kTBit is positive. make more positive
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.FA);
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.RB);
              } else { // kTBit is negative. Make more negative.
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.RA);
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.FB);
              }
            } else { // ANTIHEBBIAN feedback
              if (kTBits[idx] >= 0) { // kTBit is positive. Make less positive.
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.RA);
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.FB);
              } else { // kTBit is negative. Make less negative.
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.FA);
                KTRAM_Controller.executeInstruction(spikePattern, Instruction.RB);
              }
            }
          }

          // Thread.sleep(2000);

        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }
  }

  private String kTBitsToBinaryString(float[] ktBits) {

    StringBuffer b = new StringBuffer();
    for (int i = 0; i < ktBits.length; i++) {
      b.append(ktBits[i] >= 0 ? "1" : "0");
    }
    return b.toString();
  }
}
