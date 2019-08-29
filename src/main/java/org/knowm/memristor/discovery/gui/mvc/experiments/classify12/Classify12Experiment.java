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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify12;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.Classify12Preferences.AHaHRoutine;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.KTRAM_Controller_12.Instruction12;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.result.ResultPanel;

public class Classify12Experiment extends Experiment {

  private final double k = .05f;

  // exponential running average for measuring train accuracy.
  private double trainAccuracy = 0;
  private ControlPanel controlPanel;
  private ResultPanel resultPanel;

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ResultModel resultModel;
  private final ResultController resultController;

  // SwingWorkers
  private SwingWorker runTrialWorker;
  private SwingWorker resetWorker;

  private KTRAM_Controller_12 KTRAM_Controller;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public Classify12Experiment(DWFProxy dwfProxy, Container mainFrameContainer, int boardVersion) {

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
  public void doCreateAndShowGUI() {
  }

  @Override
  public void addWorkersToButtonEvents() {

    controlPanel.clearPlotButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("should reset chart now");
        resultController.resetChart();
      }
    });

    controlPanel.runTrialButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        runTrialWorker = new TrialWorker();
        runTrialWorker.execute();
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

  private void learnCombo(SupervisedPattern pattern, double Vy) {
    if (pattern.state) {
      KTRAM_Controller.executeInstruction(pattern, Instruction12.FA);
      KTRAM_Controller.executeInstruction(pattern, Instruction12.RB);
    } else if (Vy > 0) {
      KTRAM_Controller.executeInstruction(pattern, Instruction12.RA);
      KTRAM_Controller.executeInstruction(pattern, Instruction12.FB);
    }
  }

  private void learnAlways(SupervisedPattern pattern, double Vy) {
    if (pattern.state) {
      KTRAM_Controller.executeInstruction(pattern, Instruction12.FA);
      KTRAM_Controller.executeInstruction(pattern, Instruction12.RB);
    } else {
      KTRAM_Controller.executeInstruction(pattern, Instruction12.RA);
      KTRAM_Controller.executeInstruction(pattern, Instruction12.FB);
    }
  }

  private void learnOnMistakes(SupervisedPattern pattern, double Vy) {
    if (Vy < 0 && pattern.state) {
      KTRAM_Controller.executeInstruction(pattern, Instruction12.FA);
      KTRAM_Controller.executeInstruction(pattern, Instruction12.RB);
    } else if (Vy > 0 && !pattern.state) {
      KTRAM_Controller.executeInstruction(pattern, Instruction12.RA);
      KTRAM_Controller.executeInstruction(pattern, Instruction12.FB);
    }
  }

  private void readAllSynapses() {

    int pw = controlModel.getPulseWidth();

    controlModel.setPulseWidth(50_000);

    List<Double> synapticWeights = new ArrayList<Double>();
    for (int i = 0; i < 8; i++) {
      SupervisedPattern pattern = new SupervisedPattern(true, Arrays.asList(i));
      KTRAM_Controller.executeInstruction(pattern, Instruction12.FLV);
      synapticWeights.add(KTRAM_Controller.getVy());

      // System.out.println(i + "=" + aHaHController.getVy());

    }
    resultController.addSynapticWeightValuesPoint(synapticWeights);

    controlModel.setPulseWidth(pw);
  }

  //  private void loadSpikePattern(List<Integer> spikes) {
  //
  //    dwfProxy.turnOffAllSwitches();
  //    dwfProxy.update2DigitalIOStatesAtOnce(spikes, true); // set spike pattern
  //  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can
   * respond to those events and make sure the corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
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

    return new Classify12Preferences();
  }

  private class ResetWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      List<Integer> allSpikes = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);

      try {

        for (int epoch = 0; epoch < controlModel.getNumTrainEpochs(); epoch++) {
          for (int i = 0; i < 4; i++) {
            Collections.shuffle(allSpikes);
            SupervisedPattern pattern = new SupervisedPattern(true, allSpikes.subList(0, 1));
            KTRAM_Controller.executeInstruction(pattern, Instruction12.FLV);
            if (KTRAM_Controller.getVy() >= 0) {
              KTRAM_Controller.executeInstruction(pattern, Instruction12.RA);
              KTRAM_Controller.executeInstruction(pattern, Instruction12.FB);

            } else {
              KTRAM_Controller.executeInstruction(pattern, Instruction12.FA);
              KTRAM_Controller.executeInstruction(pattern, Instruction12.RB);
            }
          }
          readAllSynapses();
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

        List<SupervisedPattern> dataset = controlModel.dataset.getDataset();

        System.out.println("Dataset=" + controlModel.dataset);

        for (int epoch = 0; epoch < controlModel.getNumTrainEpochs(); epoch++) {
          Collections.shuffle(dataset);
          //          System.out.println("epoch = " + epoch);

          for (SupervisedPattern pattern : dataset) {

            KTRAM_Controller.executeInstruction(pattern, Instruction12.FLV);
            double Vy = KTRAM_Controller.getVy();
            if ((Vy < 0 && pattern.state) || (Vy > 0 && !pattern.state)) { // mistake
              trainAccuracy = (1 - k) * trainAccuracy;
            } else { // got it.
              trainAccuracy = (1 - k) * trainAccuracy + k * 1f;
            }

            if (controlModel.getAhahroutine() == AHaHRoutine.LearnOnMistakes) {
              learnOnMistakes(pattern, Vy);
            } else if (controlModel.getAhahroutine() == AHaHRoutine.LearnAlways) {
              learnAlways(pattern, Vy);
            } else {
              learnCombo(pattern, Vy);
            }

            resultController.addTrainAccuracyDataPoint(trainAccuracy);
          }

          readAllSynapses();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }
  }
}
