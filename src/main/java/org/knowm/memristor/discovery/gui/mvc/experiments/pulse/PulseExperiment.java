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
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse;

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
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentResultsPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.View;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.ConductancePreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.result.ResultPanel;
import org.knowm.waveforms4j.DWF;

public class PulseExperiment extends Experiment {

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ResultModel resultModel;
  private final ResultController resultController;
  private boolean initialPulseTrainCaptured = false;
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
  public PulseExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlModel = new ControlModel();
    // TODO remove passing in controlModel here
    controlPanel = new ControlPanel(controlModel);
    new ControlController(controlPanel, controlModel, dwfProxy);

    resultModel = new ResultModel();
    resultPanel = new ResultPanel();
    resultController = new ResultController(resultPanel, resultModel);
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

  /**
   * These property change events are triggered in the controlModel in the case where the underlying
   * controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    switch (propName) {
      case Model.EVENT_WAVEFORM_UPDATE:
        resultPanel.switch2WaveformChart();
        resultController.updateWaveformChart(
            controlModel.getWaveformTimeData(),
            controlModel.getWaveformAmplitudeData(),
            controlModel.getAmplitude(),
            controlModel.getPulseWidth());

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

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // ////////////////////////////////
      // Analog In /////////////////
      // ////////////////////////////////

      int samplesPerPulse = 100;
      double sampleFrequency = controlModel.getCalculatedFrequency() * samplesPerPulse;
      // dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02 *
      // (controlModel.getAmplitude() > 0 ? 1 : -1), samplesPerPulse *
      // controlModel.getPulseNumber());

      boolean isScale2V = controlModel.getAppliedAmplitude() <= 2.5;

      dwfProxy
          .getDwf()
          .startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(
              DWF.WAVEFORM_CHANNEL_1,
              sampleFrequency,
              samplesPerPulse * controlModel.getPulseNumber(),
              isScale2V);

      dwfProxy.waitUntilArmed();

      // ////////////////////////////////
      // Pulse Out /////////////////
      // ////////////////////////////////

      double[] customWaveform =
          WaveformUtils.generateCustomWaveform(
              controlModel.getWaveform(),
              controlModel.getAppliedAmplitude(),
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
      // System.out.println("validSamples: " + validSamples);

      // Stop Analog In and Out
      dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
      dwfProxy.getDwf().stopAnalogCaptureBothChannels();

      // /////////////////////////
      // Create Chart Data //////
      // /////////////////////////

      double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.05, 10);
      double[] V1Trimmed = trimmedRawData[0];
      double[] V2Trimmed = trimmedRawData[1];
      double[] V2MinusV1 = PostProcessDataUtils.getV1MinusV2(V1Trimmed, V2Trimmed);

      int bufferLength = V1Trimmed.length;

      // create time data
      double[] timeData = new double[bufferLength];
      double timeStep = 1.0 / sampleFrequency * PulsePreferences.TIME_UNIT.getDivisor();
      for (int i = 0; i < bufferLength; i++) {
        timeData[i] = i * timeStep;
      }

      // create current data
      double[] current = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {
        current[i] =
            V2Trimmed[i]
                / controlModel.getSeriesResistance()
                * PulsePreferences.CURRENT_UNIT.getDivisor();
      }

      // create conductance data
      double[] conductance = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {

        double I = V2Trimmed[i] / controlModel.getSeriesResistance();
        double G =
            I / (V1Trimmed[i] - V2Trimmed[i]) * PulsePreferences.CONDUCTANCE_UNIT.getDivisor();
        G = G < 0 ? 0 : G;
        conductance[i] = G;
      }

      publish(
          new double[][] {timeData, V1Trimmed, V2Trimmed, V2MinusV1, current, conductance, null});

      while (!initialPulseTrainCaptured) {
        // System.out.println("Waiting...");
        Thread.sleep(50);
      }

      // ////////////////////////////////
      // READ PULSES /////////////////
      // ////////////////////////////////

      while (!isCancelled()) {

        try {
          Thread.sleep(controlModel.getSampleRate() * 1000);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        }

        // ////////////////////////////////
        // Analog In /////////////////
        // ////////////////////////////////

        // trigger on 20% the rising .1 V read pulse
        samplesPerPulse = 300;
        sampleFrequency = 100_000 * samplesPerPulse;
        // dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02,
        // samplesPerPulse * 1);

        dwfProxy
            .getDwf()
            .startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(
                DWF.WAVEFORM_CHANNEL_1, sampleFrequency, samplesPerPulse, true);

        dwfProxy.waitUntilArmed();

        // ////////////////////////////////
        // Pulse Out /////////////////
        // ////////////////////////////////

        // read pulse: 0.1 V, 5 us pulse width
        customWaveform = WaveformUtils.generateCustomWaveform(Waveform.SquareSmooth, 0.1, 100_000);
        dwfProxy
            .getDwf()
            .startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, 100_000, 0, 1, customWaveform);

        // Read In Data
        success = dwfProxy.capturePulseData(100_000, 1);
        if (!success) {
          // Stop Analog In and Out
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
          controlPanel.getStartStopButton().doClick();
          return false;
        } else {

          // Get Raw Data from Oscilloscope
          validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
          v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
          v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

          // /////////////////////////
          // Create Chart Data //////
          // /////////////////////////

          trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.08, 0);
          V1Trimmed = trimmedRawData[0];
          V2Trimmed = trimmedRawData[1];
          bufferLength = V1Trimmed.length;

          // create conductance data - a single number equal to the average of all points in the
          // trimmed data
          double runningTotal = 0.0;
          for (int i = 3; i < bufferLength - 3; i++) {
            double I = V2Trimmed[i] / controlModel.getSeriesResistance();
            double G = I / (V1Trimmed[i] - V2Trimmed[i]);
            G = G < 0 ? 0 : G;
            runningTotal += G;
          }
          double[] conductanceAve =
              new double[] {
                runningTotal
                    / (bufferLength - 6)
                    * ConductancePreferences.CONDUCTANCE_UNIT.getDivisor()
              };

          publish(new double[][] {null, null, null, null, null, null, conductanceAve});
        }
        // Stop Analog In and Out
        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
      }
      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      double[][] newestChunk = chunks.get(chunks.size() - 1);

      if (newestChunk[6] == null) {
        initialPulseTrainCaptured = true;

        resultController.updateCaptureChartData(
            newestChunk[0],
            newestChunk[1],
            newestChunk[2],
            newestChunk[3],
            controlModel.getPulseWidth(),
            controlModel.getAmplitude());
        resultController.updateIVChartData(
            newestChunk[0],
            newestChunk[4],
            controlModel.getPulseWidth(),
            controlModel.getAmplitude());
        resultController.updateGVChartData(
            newestChunk[0],
            newestChunk[5],
            controlModel.getPulseWidth(),
            controlModel.getAmplitude());

        if (resultPanel.getCaptureButton().isSelected()) {
          resultPanel.switch2CaptureChart();
          resultController.repaintVtChart();
        } else if (resultPanel.getIVButton().isSelected()) {
          resultPanel.switch2IVChart();
          resultController.repaintItChart();
        } else {
          resultPanel.switch2GVChart();
          resultController.repaintGVChart();
        }
      } else {

        // update G chart
        controlModel.setLastG(newestChunk[6][0]);
        resultController.updateGChartData(controlModel.getLastG(), controlModel.getLastRAsString());
        resultController.repaintGChart();

        controlModel.updateEnergyData();
        controlPanel.updateEnergyGUI(
            controlModel.getAppliedAmplitude(),
            controlModel.getAppliedCurrent(),
            controlModel.getAppliedEnergy(),
            controlModel.getAppliedMemristorEnergy());
      }
    }
  }
}
