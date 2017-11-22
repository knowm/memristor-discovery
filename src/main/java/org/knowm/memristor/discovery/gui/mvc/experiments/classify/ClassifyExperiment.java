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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.AHaHController_21.Instruction;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.plot.PlotPanel;

public class ClassifyExperiment extends Experiment {

  private SwingWorker runTrialWorker;
  private SwingWorker resetWorker;

  private final ControlModel controlModel = new ControlModel();
  private ControlPanel controlPanel;

  private PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  private AHaHController_21 aHaHController;

  //exponential running average for measuring train accuracy.
  double trainAccuracy = 0;
  double k = .025f;

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

  private class ResetWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {

        System.out.println("Reset Worker Called. TODO: Fill This In");

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }

  }

  private class TrialWorker extends SwingWorker<Boolean, Double> {
    private List<List<Integer>> sevenSegmentDataWithBias = controlModel.getSevenSegmentDataWithBias();
    private List<Integer> idx = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Override
    protected Boolean doInBackground() throws Exception {

      try {

        for (int epoch = 0; epoch < controlModel.getNumTrainEpochs(); epoch++) {
          Collections.shuffle(idx);
          System.out.println("epoch = " + epoch);

          for (int i = 0; i < idx.size(); i++) {
            int patternID = idx.get(i);
            List<Integer> spikePatternWithBias = sevenSegmentDataWithBias.get(patternID);
            loadSpikePattern(spikePatternWithBias);
            aHaHController.executeInstruction(Instruction.FFLV);

            double Vy = aHaHController.getVy();
            System.out.println("Vy=" + Vy);

            System.out.println("PatternID=" + patternID);
            System.out.println("  >pattern=" + spikePatternWithBias);
            System.out.println("  >dataset=" + controlModel.dataset.getPatternID());

            if (patternID == controlModel.dataset.getPatternID()) {//always reinforce the target label
              aHaHController.executeInstruction(Instruction.FF_RH);

              if (Vy > 0) {//got it!
                trainAccuracy = (1 - k) * trainAccuracy + k * 1f;
              } else {//missed it
                trainAccuracy = (1 - k) * trainAccuracy;
              }

            } else {

              if (Vy > 0) {//false positive
                aHaHController.executeInstruction(Instruction.FF_RL);
                // aHaHController.executeInstruction(Instruction.FF_RH);
                trainAccuracy = (1 - k) * trainAccuracy;
              } else {
                trainAccuracy = (1 - k) * trainAccuracy + k * 1f;
              }

            }

            System.out.println("Accuracy = " + trainAccuracy);

            plotController.addTrainAccuracyDataPoint(trainAccuracy);

          }

          //measure the synaptic weights after each epoch

          List<Double> synapticWeights = new ArrayList<Double>();
          for (int i = 0; i < 8; i++) {
            List<Integer> spike = Arrays.asList(i);
            loadSpikePattern(spike);
            aHaHController.executeInstruction(Instruction.FFLV);
            synapticWeights.add(aHaHController.getVy());
            //Thread.sleep(1000);
          }
          plotController.addSynapticWeightValuesPoint(synapticWeights);

        }

        //train

        //test

        //        System.out.println("Trial Worker Called");
        //        System.out.println("  >numTrainEpochs=" + controlModel.getNumTrainEpochs());
        //        System.out.println("  >Pattern=" + controlModel.getDataset());
      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }

    private void loadSpikePattern(List<Integer> spikes) {

      dwfProxy.turnOffAllSwitches();
      dwfProxy.update2DigitalIOStatesAtOnce(spikes, true);//set spike pattern

    }

  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can
   * respond to those events and make sure the corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

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
