/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2018 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.conductance;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.PostProcessDataUtils;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;

public class ConductanceExperiment extends Experiment {

  private final ControlModel controlModel = new ControlModel();
  private ControlPanel controlPanel;

  private PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  private ResetCaptureWorker resetCaptureWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public ConductanceExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    new ControlController(controlPanel, plotPanel, controlModel, dwfProxy);
  }

  @Override
  public void doCreateAndShowGUI() {

  }

  private class ResetCaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // Send off Single Pulse and capture the response

      // ////////////////////////////////
      // Analog In /////////////////
      // ////////////////////////////////

      int sampleFrequencyMultiplier = 200; // adjust this down if you want to capture more pulses as the buffer size is limited.
      double sampleFrequency = controlModel.getCalculatedFrequency() * sampleFrequencyMultiplier; // adjust this down if you want to capture more pulses as the buffer size is limited.
      dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02 * (controlModel.getResetAmplitude() > 0 ? 1 : -1), 8000);
      Thread.sleep(10); // Attempt to allow Analog In to get fired up for the next set of pulses

      // ////////////////////////////////
      // Pulse Out /////////////////
      // ////////////////////////////////

      // custom waveform
      double[] customWaveform = WaveformUtils.generateCustomWaveform(controlModel.getResetPulseType(), controlModel.getResetAmplitude(), controlModel.getCalculatedFrequency());
      dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, customWaveform);

      // Read In Data
      boolean success = dwfProxy.capturePulseData(controlModel.getCalculatedFrequency(), 1);
      if (!success) {
        // Stop Analog In and Out
        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        controlPanel.getStartStopButton().doClick();
        return false;
      }

      // Get Raw Data from Oscilloscope
      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
      // System.out.println("validSamples: " + validSamples);

      // /////////////////////////
      // Create Chart Data //////
      // /////////////////////////

      double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.02, 10);
      double[] V1Trimmed = trimmedRawData[0];
      double[] V2Trimmed = trimmedRawData[1];
      int bufferLength = V1Trimmed.length;

      // create time data
      double[] timeData = new double[bufferLength];
      double timeStep = 1 / sampleFrequency * ConductancePreferences.TIME_UNIT.getDivisor();
      for (int i = 0; i < bufferLength; i++) {
        timeData[i] = i * timeStep;
      }

      // create current data
      double[] current = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {
        current[i] = V2Trimmed[i] / controlModel.getSeriesResistance() * ConductancePreferences.CURRENT_UNIT.getDivisor();
      }

      // create conductance data
      double[] conductance = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {

        double I = V2Trimmed[i] / controlModel.getSeriesResistance();
        double G = I / (V1Trimmed[i] - V2Trimmed[i]) * ConductancePreferences.CONDUCTANCE_UNIT.getDivisor();
        G = G < 0 ? 0 : G;
        conductance[i] = G;
      }

      publish(new double[][]{timeData, V1Trimmed, V2Trimmed, current, conductance});

      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      double[][] newestChunk = chunks.get(chunks.size() - 1);
      // System.out.println("" + chunks.size());

      plotController.udpateVtChart(newestChunk[0], newestChunk[1], newestChunk[2], controlModel.getResetPulseWidth(), controlModel.getResetAmplitude());
      plotController.udpateIVChart(newestChunk[1], newestChunk[3], controlModel.getResetPulseWidth(), controlModel.getResetAmplitude());
      plotController.updateGVChartReset(newestChunk[1], newestChunk[4], controlModel.getResetPulseWidth(), controlModel.getResetAmplitude());

      if (plotPanel.getCaptureButton().isSelected()) {
        plotController.repaintVtChart();
        plotPanel.switch2CaptureChart();
      } else if (plotPanel.getIVButton().isSelected()) {
        plotController.repaintIVChart();
        plotPanel.switch2IVChart();
      } else {
        plotController.repaintGVChart();
        plotPanel.switch2GVChart();
      }
      controlPanel.getStartStopButton().doClick();
    }
  }

  private class SetCaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      while (!isCancelled()) {

        try {
          Thread.sleep(100); // TODO change this to a small amount after debugged and working app
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        }

        // 1. set pulse

        // ////////////////////////////////
        // Analog In /////////////////
        // ////////////////////////////////

        int sampleFrequencyMultiplier = 200; // adjust this down if you want to capture more pulses as the buffer size is limited.
        double sampleFrequency = controlModel.getCalculatedFrequency() * sampleFrequencyMultiplier; // adjust this down if you want to capture more pulses as the buffer size is limited.
        dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02 * (controlModel.getSetAmplitude() > 0 ? 1 : -1), 8000);
        Thread.sleep(20); // Attempt to allow Analog In to get fired up for the next set of pulses

        // ////////////////////////////////
        // Pulse Out /////////////////
        // ////////////////////////////////

        // custom waveform
        double[] customWaveform = WaveformUtils.generateCustomWaveform(Waveform.Square, controlModel.getSetAmplitude(), controlModel.getCalculatedFrequency());
        dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, customWaveform);

        // Get Raw Data from Oscilloscope
        int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
        double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
        // System.out.println("validSamples: " + validSamples);

        // /////////////////////////
        // Create Chart Data //////
        // /////////////////////////

        double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, controlModel.getSetAmplitude() * .98, 0);
        double[] V1Trimmed = trimmedRawData[0];
        double[] V2Trimmed = trimmedRawData[1];
        int bufferLength = V1Trimmed.length;

        // create time data
        double[] timeData = new double[bufferLength];
        double timeStep = 1 / sampleFrequency * ConductancePreferences.TIME_UNIT.getDivisor();
        for (int i = 0; i < bufferLength; i++) {
          timeData[i] = i * timeStep;
        }

        // create current data
        double[] current = new double[bufferLength];
        for (int i = 0; i < bufferLength; i++) {
          current[i] = V2Trimmed[i] / controlModel.getSeriesResistance() * ConductancePreferences.CURRENT_UNIT.getDivisor();
        }

        // create conductance data - a single number equal to the average of all points in the trimmed data
        double runningTotal = 0.0;
        for (int i = 3; i < bufferLength - 3; i++) {
          double I = V2Trimmed[i] / controlModel.getSeriesResistance();
          double G = I / (V1Trimmed[i] - V2Trimmed[i]);
          G = G < 0 ? 0 : G;
          runningTotal += G;
        }
        // conductance value packed in a one-element array
        double[] conductance = new double[]{runningTotal / (bufferLength - 6) * ConductancePreferences.CONDUCTANCE_UNIT.getDivisor()};

        publish(new double[][]{timeData, V1Trimmed, V2Trimmed, current, conductance});
      }

      controlPanel.getStartStopButton().doClick();
      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      double[][] newestChunk = chunks.get(chunks.size() - 1);

      // System.out.println("" + chunks.size());

      plotController.udpateVtChart(newestChunk[0], newestChunk[1], newestChunk[2], controlModel.getSetPulseWidth(), controlModel.getSetAmplitude());
      plotController.udpateIVChart(newestChunk[1], newestChunk[3], controlModel.getSetPulseWidth(), controlModel.getSetAmplitude());
      plotController.updateGVChart(newestChunk[4], controlModel.getSetPulseWidth(), controlModel.getSetAmplitude());

      if (plotPanel.getCaptureButton().isSelected()) {
        plotController.repaintVtChart();
      } else if (plotPanel.getIVButton().isSelected()) {
        plotController.repaintIVChart();
      } else {
        plotController.repaintGVChart();
      }
    }
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

      case ExperimentControlModel.EVENT_WAVEFORM_UPDATE:

        if (controlModel.isStartToggled()) {

          double[] customWaveform = WaveformUtils.generateCustomWaveform(Waveform.Square, controlModel.getSetAmplitude(), controlModel.getCalculatedFrequency());
          dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, customWaveform);
        } else {

          plotPanel.switch2WaveformChart();
          plotController.udpateWaveformChart(controlModel.getWaveformTimeData(), controlModel.getWaveformAmplitudeData(), controlModel.getResetAmplitude(), controlModel.getResetPulseWidth());
        }
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

    return new SetCaptureWorker();
  }
}
