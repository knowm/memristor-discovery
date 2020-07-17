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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse21;

import static org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.gpio.MuxController;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.KTRAM_Controller_21.Instruction21;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.result.ResultPanel;

public class Synapse21Experiment extends Experiment {

  private static final float INIT_CONDUCTANCE = .0002f;

  private final MuxController muxController;

  DecimalFormat df = new DecimalFormat("0.000E0");
  private KTRAM_Controller_21 kTRAM_Controller;

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ControlPanel controlPanel;
  private final ResultModel resultModel;
  private final ResultPanel resultPanel;
  private final ResultController resultController;

  // SwingWorkers
  private SwingWorker initSynapseWorker;
  private SwingWorker experimentCaptureWorker;
  private SwingWorker clearChartWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public Synapse21Experiment(DWFProxy dwfProxy, Container mainFrameContainer, int boardVersion) {

    super(dwfProxy, mainFrameContainer, boardVersion);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel();

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);

    dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

    kTRAM_Controller = new KTRAM_Controller_21(controlModel);
    kTRAM_Controller.setdWFProxy(dwfProxy);
    muxController = new MuxController();
  }

  @Override
  public void doCreateAndShowGUI() {

    JOptionPane.showMessageDialog(
        null,
        "This experiment has been replaced with a better one! Use Synapse12 experiment with the V2 or higher board instead. If you have purchased a"
            + " V1 board, Knowm Inc will replace it for free (excluding shipping). Email contact@knowm.org for details.");
  }

  @Override
  public void addWorkersToButtonEvents() {

    controlPanel.clearPlotButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            clearChartWorker = new ClearChartWorker();
            clearChartWorker.execute();
          }
        });

    controlPanel
        .getStartStopButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                if (!controlModel.isStartToggled()) {

                  controlModel.setStartToggled(true);
                  controlPanel.getStartStopButton().setText("Stop");

                  // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
                  experimentCaptureWorker = new CaptureWorker();
                  experimentCaptureWorker.execute();
                } else {

                  controlModel.setStartToggled(false);
                  controlPanel.getStartStopButton().setText("Start");

                  // cancel the worker
                  experimentCaptureWorker.cancel(true);
                }
              }
            });

    controlPanel.initSynapseButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            initSynapseWorker = new InitSynapseWorker();
            initSynapseWorker.execute();
          }
        });
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

        // TODO handle instruction updates here.
        // System.out.println(controlModel.getInstruction());
        // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

        break;

      default:
        break;
    }
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new Synapse21Preferences();
  }

  private class ClearChartWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      resultController.resetChart();
      return true;
    }
  }

  private class CaptureWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      kTRAM_Controller.executeInstruction(controlModel.getInstruction());
      while (!isCancelled()) {

        try {
          Thread.sleep(controlModel.getSampleRate() * 1000);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called

        }

        // set to constant pulse width for reads to help mitigate RC issues
        int pW = controlModel.getPulseWidth();
        controlModel.setPulseWidth(100_000);
        kTRAM_Controller.executeInstruction(Instruction21.FFLV);
        controlModel.setPulseWidth(pW); // set it back to whatever it was

        // System.out.println("Vy=" + aHaHController.getVy());
        publish(kTRAM_Controller.getGa(), kTRAM_Controller.getGb(), kTRAM_Controller.getVy());
      }
      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

      resultController.updateYChartData(chunks.get(0), chunks.get(1), chunks.get(2));
      resultController.repaintYChart();
    }
  }

  private class InitSynapseWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {
        int initPulseWidth = controlModel.getPulseWidth();
        float initVoltage = controlModel.getAmplitude();

        // 1. Reset both memristors to low conducting state-->
        controlModel.setPulseWidth(500_000); // 500us.
        controlModel.setAmplitude(1.5f);

        // hard reset.
        for (int i = 0; i < 10; i++) {
          kTRAM_Controller.executeInstruction(Instruction21.RLadn);
          kTRAM_Controller.executeInstruction(Instruction21.RHbdn);
        }

        // drive each memristor to target conductance.
        boolean aGood = false;
        boolean bGood = false;
        for (int i = 0; i < 100; i++) { // drive pulses until conductance reaches threshold
          kTRAM_Controller.executeInstruction(Instruction21.FFLV);
          if (Double.isNaN(kTRAM_Controller.getGa())
              || kTRAM_Controller.getGa() < INIT_CONDUCTANCE) {
            kTRAM_Controller.executeInstruction(Instruction21.RHaup);
          } else {

            // System.out.println("Here A: aHaHController.getGa()=" + aHaHController.getGa());

            aGood = true;
          }

          if (Double.isNaN(kTRAM_Controller.getGb())
              || kTRAM_Controller.getGb() < INIT_CONDUCTANCE) {
            kTRAM_Controller.executeInstruction(Instruction21.RLbup);
          } else {

            // System.out.println("Here B: aHaHController.getGb()=" + aHaHController.getGb());

            bGood = true;
          }

          if (aGood && bGood) {

            getControlModel()
                .swingPropertyChangeSupport
                .firePropertyChange(Model.EVENT_NEW_CONSOLE_LOG, null, "Step 1 Passed.");
            break;
          }

          getControlModel()
              .swingPropertyChangeSupport
              .firePropertyChange(
                  Model.EVENT_NEW_CONSOLE_LOG,
                  null,
                  "  A="
                      + df.format(kTRAM_Controller.getGa())
                      + "mS, B="
                      + df.format(kTRAM_Controller.getGb())
                      + "mS");
        }

        if (!aGood || !bGood) { // failed

          String failed = "";
          if (!aGood) {
            failed += "A";
          }

          if (!bGood) {
            if (failed.length() > 0) {
              failed += ", B";
            } else {
              failed += "B";
            }
          }

          getControlModel()
              .swingPropertyChangeSupport
              .firePropertyChange(
                  Model.EVENT_NEW_CONSOLE_LOG,
                  null,
                  "Step 1 Failed. Memristor ("
                      + failed
                      + ") could not reach target of "
                      + INIT_CONDUCTANCE
                      + "mS");
        }

        // zero them out.
        int stateChangedCount = 0;
        boolean s = true;
        for (int i = 0; i < 8; i++) {
          kTRAM_Controller.executeInstruction(Instruction21.FFLV);

          boolean state = kTRAM_Controller.getVy() > 0;
          if (i > 0) {
            if (state != s) { // state changed
              stateChangedCount++;
            }
          }
          s = state;
          kTRAM_Controller.executeInstruction(Instruction21.FF_RA);
        }

        if (stateChangedCount == 0) {
          getControlModel()
              .swingPropertyChangeSupport
              .firePropertyChange(
                  Model.EVENT_NEW_CONSOLE_LOG,
                  null,
                  "Step 2 Failed. State did not change upon application of Anti-Hebbian cycles");
        } else {

          float a = (float) (stateChangedCount / 7.0);

          getControlModel()
              .swingPropertyChangeSupport
              .firePropertyChange(
                  Model.EVENT_NEW_CONSOLE_LOG, null, "Step 2 Passed. Q=" + df.format(a));
        }

        if (aGood && bGood && stateChangedCount > 0) {
          getControlModel()
              .swingPropertyChangeSupport
              .firePropertyChange(
                  Model.EVENT_NEW_CONSOLE_LOG, null, "Synapse Initialized Sucessfully");
        }

        getControlModel()
            .swingPropertyChangeSupport
            .firePropertyChange(
                Model.EVENT_NEW_CONSOLE_LOG,
                null,
                "  A="
                    + df.format(kTRAM_Controller.getGa())
                    + "mS, B="
                    + df.format(kTRAM_Controller.getGb())
                    + "mS");

        /*
         * drive both memristors up in conductance until one or the other exceeds target. drive the remaining memristor up until it exceeds the other
         * memristors or time runs out.
         */

        controlModel.setPulseWidth(initPulseWidth);
        controlModel.setAmplitude(initVoltage);

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }
  }
}
