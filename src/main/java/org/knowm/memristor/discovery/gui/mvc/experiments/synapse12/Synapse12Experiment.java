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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse12;

import static org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

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
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.KTRAM_Controller_12.Instruction12;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.result.ResultPanel;

public class Synapse12Experiment extends Experiment {

  DecimalFormat df = new DecimalFormat("0.000E0");
  private KTRAM_Controller_12 kTRAM_Controller;

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ControlPanel controlPanel;
  private final ResultModel resultModel;
  private final ResultPanel resultPanel;
  private final ResultController resultController;

  // SwingWorkers
  //  private SwingWorker initSynapseWorker;
  private SwingWorker experimentCaptureWorker;
  private SwingWorker clearChartWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public Synapse12Experiment(DWFProxy dwfProxy, Container mainFrameContainer, int boardVersion) {

    super(dwfProxy, mainFrameContainer, boardVersion);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel();

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);
    kTRAM_Controller = new KTRAM_Controller_12(controlModel);
    kTRAM_Controller.setdWFProxy(dwfProxy);
  }

  @Override
  public void doCreateAndShowGUI() {}

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

                  if (kTRAM_Controller.readSwitchStates()) {
                    controlModel.setStartToggled(true);
                    controlPanel.getStartStopButton().setText("Stop");
                    // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
                    experimentCaptureWorker = new CaptureWorker();
                    experimentCaptureWorker.execute();
                  } else {
                    JOptionPane.showMessageDialog(
                        null,
                        "Please select two memristors for 'A' and 'B' synapse channels. 'A' must be in the range 1-8 and 'B' in the range 9-16.");
                  }

                } else {

                  controlModel.setStartToggled(false);
                  controlPanel.getStartStopButton().setText("Start");

                  // cancel the worker
                  experimentCaptureWorker.cancel(true);
                }
              }
            });

    //    controlPanel.initSynapseButton.addActionListener(new ActionListener() {
    //
    //      @Override
    //      public void actionPerformed(ActionEvent e) {
    //
    //        initSynapseWorker = new InitSynapseWorker();
    //        initSynapseWorker.execute();
    //      }
    //    });
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

    return new Synapse12Preferences();
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
        controlModel.setPulseWidth(20_000);
        kTRAM_Controller.executeInstruction(Instruction12.FLV);
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
        float initForwardVoltage = controlModel.getForwardAmplitude();
        float initReverseVoltage = controlModel.getReverseAmplitude();

        //  System.out.println("Here1");
        // 1. Reset both memristors to low conducting state-->
        controlModel.setPulseWidth(25_000_000);
        controlModel.setReverseAmplitude(1.0f);
        kTRAM_Controller.executeInstruction(Instruction12.RAB);

        //  System.out.println("Here2");
        // 2. Apply set forward voltage to program
        controlModel.setPulseWidth(25_000_000);
        controlModel.setForwardAmplitude(.5f);
        kTRAM_Controller.executeInstruction(Instruction12.FAB);
        //  System.out.println("Here3");
        // set things back.
        controlModel.setPulseWidth(initPulseWidth);
        controlModel.setForwardAmplitude(initForwardVoltage);
        controlModel.setReverseAmplitude(initReverseVoltage);

      } catch (Exception e) {
        e.printStackTrace();
      }

      return true;
    }
  }
}
