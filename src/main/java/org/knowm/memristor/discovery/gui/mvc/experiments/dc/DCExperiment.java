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
package org.knowm.memristor.discovery.gui.mvc.experiments.dc;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.PostProcessDataUtils;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentResultsPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.View;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.result.ResultPanel;
import org.knowm.waveforms4j.DWF;

public class DCExperiment extends Experiment {

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ResultModel resultModel;
  private final ResultController resultController;
  private ControlPanel controlPanel;
  private ResultPanel resultPanel;

  // SwingWorkers
  private SwingWorker experimentCaptureWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public DCExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel(); //

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);
  }

  @Override
  public void doCreateAndShowGUI() {

    //     trigger waveform update event
    PropertyChangeEvent evt =
        new PropertyChangeEvent(this, Model.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);

    // when the control panel is manipulated, we need to communicate the changes to the results
    // panel
    getControlModel().addListener(this);
  }

  @Override
  public void addWorkersToButtonEvents() {

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
  }

  @Override
  public Model getControlModel() {

    return controlModel;
  }

  @Override
  public View getControlPanel() {

    return controlPanel;
  }

  @Override
  public Model getResultModel() {
    return resultModel;
  }

  @Override
  public ExperimentResultsPanel getResultPanel() {

    return resultPanel;
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying
   * controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {
      case Model.EVENT_WAVEFORM_UPDATE:
        if (!controlModel.isStartToggled()) {

          resultPanel.switch2WaveformChart();
          resultController.updateWaveformChart(
              controlModel.getWaveformTimeData(),
              controlModel.getWaveformAmplitudeData(),
              controlModel.getAmplitude(),
              controlModel.getPeriod());
        }
        break;

      default:
        break;
    }
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new DCPreferences();
  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // ////////////////////////////////
      // Analog In /////////////////
      // ////////////////////////////////

      int samplesPerPulse =
          200; // adjust this down if you want to capture more pulses as the buffer size is limited.
      double sampleFrequency =
          controlModel.getCalculatedFrequency()
              * samplesPerPulse; // adjust this down if you want to capture more pulses as the
      // buffer size is limited.
      dwfProxy
          .getDwf()
          .startAnalogCaptureBothChannelsLevelTrigger(
              sampleFrequency,
              0.02 * (controlModel.getAmplitude() > 0 ? 1 : -1),
              samplesPerPulse * controlModel.getPulseNumber());

      dwfProxy.waitUntilArmed();

      // ////////////////////////////////
      // Pulse Out /////////////////
      // ////////////////////////////////

      double[] customWaveform =
          WaveformUtils.generateCustomWaveform(
              controlModel.getWaveform(),
              controlModel.getAmplitude(),
              controlModel.getCalculatedFrequency());
      dwfProxy
          .getDwf()
          .startCustomPulseTrain(
              DWF.WAVEFORM_CHANNEL_1,
              controlModel.getCalculatedFrequency(),
              0,
              controlModel.getPulseNumber(),
              customWaveform);

      // ////////////////////////////////
      // ////////////////////////////////

      // Read In Data
      boolean success =
          dwfProxy.capturePulseData(
              controlModel.getCalculatedFrequency(), controlModel.getPulseNumber());
      if (!success) {
        // Stop Analog In and Out
        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        controlPanel.getStartStopButton().doClick();
        return false;
      }

      // Get Raw Data from Oscilloscope
      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 =
          dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 =
          dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

      // /////////////////////////
      // Create Chart Data //////
      // /////////////////////////

      double[] V2MinusV1 = PostProcessDataUtils.getV1MinusV2(v1, v2);

      int bufferLength = v1.length;

      // create time data
      double[] timeData = new double[bufferLength];
      double timeStep = 1 / sampleFrequency * DCPreferences.TIME_UNIT.getDivisor();
      for (int i = 0; i < bufferLength; i++) {
        timeData[i] = i * timeStep;
      }

      // create current data
      double[] current = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {
        current[i] =
            v2[i] / controlModel.getSeriesResistance() * DCPreferences.CURRENT_UNIT.getDivisor();
      }

      // create conductance data
      double[] conductance = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {

        double I = v2[i] / controlModel.getSeriesResistance();
        double G = I / (v1[i] - v2[i]) * DCPreferences.CONDUCTANCE_UNIT.getDivisor();
        G = G < 0 ? 0 : G;
        conductance[i] = G;
      }

      publish(new double[][] {timeData, v1, v2, V2MinusV1, current, conductance});

      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      double[][] newestChunk = chunks.get(chunks.size() - 1);

      resultController.updateCaptureChartData(
          newestChunk[0],
          newestChunk[1],
          newestChunk[2],
          newestChunk[3],
          controlModel.getPeriod(),
          controlModel.getAmplitude());
      resultController.updateIVChartData(
          newestChunk[1], newestChunk[4], controlModel.getPeriod(), controlModel.getAmplitude());
      resultController.updateGVChartData(
          newestChunk[1], newestChunk[5], controlModel.getPeriod(), controlModel.getAmplitude());

      if (resultPanel.getCaptureButton().isSelected()) {
        resultController.repaintCaptureChart();
        resultPanel.switch2CaptureChart();
      } else if (resultPanel.getIVButton().isSelected()) {
        resultController.repaintItChart();
        resultPanel.switch2IVChart();
      } else {
        resultController.repaintRtChart();
        resultPanel.switch2GVChart();
      }
      controlPanel.getStartStopButton().doClick();
    }
  }
}
