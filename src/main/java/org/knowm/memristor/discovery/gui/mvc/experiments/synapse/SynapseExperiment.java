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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse;

import static org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

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
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.PostProcessDataUtils;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;

public class SynapseExperiment extends Experiment {

  private final ControlModel controlModel = new ControlModel();
  private ControlPanel controlPanel;

  private PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public SynapseExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    new ControlController(controlPanel, controlModel, dwfProxy);
    System.out.println(controlModel.getInstruction());
    dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());
  }

  @Override
  public void doCreateAndShowGUI() {

  }

  private class CaptureWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // NOTE: everytime start is clicked this runs. It first applied the desired instruction (no recording of what happens directly from the operation), followed by continuous FFLV pulses, reading the `y` value.

      //////////////////////////////////
      // Analog In /////////////////
      //////////////////////////////////

      // int samplesPerPulse = 100;
      // double sampleFrequency = controlModel.getCalculatedFrequency() * samplesPerPulse;
      // dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02 * (controlModel.getAmplitude() > 0 ? 1 : -1), samplesPerPulse * controlModel.getPulseNumber());
      //
      // waitUntilArmed();

      //////////////////////////////////
      // Pulse Out /////////////////////
      //////////////////////////////////

      // TODO implement AHaH Controller here. Given the instruction, set everything and send the pulse(es).

      // Get the waveform given the selected instruction
      double[] customWaveform = WaveformUtils.generateCustomWaveform(controlModel.getWaveform(), controlModel.getAmplitude(), controlModel.getCalculatedFrequency());

      // TODO According to the documentation if you set the `idxChannel` to -1, it will configure and start a pulse for BOTH channels. I never tested it though (yet).
      // WAVEFORM_CHANNEL_BOTH
      dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, controlModel.getPulseNumber(), customWaveform);
      // TODO verify with oscilloscope that this is working ( it should )

      //////////////////////////////////
      //////////////////////////////////

      // // Read In Data
      // boolean success = capturePulseData(controlModel.getCalculatedFrequency(), controlModel.getPulseNumber());
      // if (!success) {
      //   // Stop Analog In and Out
      //   dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
      //   dwfProxy.getDwf().stopAnalogCaptureBothChannels();
      //   controlPanel.getStartStopButton().doClick();
      //   return false;
      // }
      //
      // // Get Raw Data from Oscilloscope
      // int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      // double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      // double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
      // // System.out.println("validSamples: " + validSamples);
      //
      // // Stop Analog In and Out
      // dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
      // dwfProxy.getDwf().stopAnalogCaptureBothChannels();
      //
      // ///////////////////////////
      // // Create Chart Data //////
      // ///////////////////////////
      //
      // double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.05, 10);
      // double[] V1Trimmed = trimmedRawData[0];
      // double[] V2Trimmed = trimmedRawData[1];
      // double[] V2MinusV1 = PostProcessDataUtils.getV1MinusV2(V1Trimmed, V2Trimmed);
      //
      // int bufferLength = V1Trimmed.length;
      //
      // // create time data
      // double[] timeData = new double[bufferLength];
      // double timeStep = 1.0 / sampleFrequency * SynapsePreferences.TIME_UNIT.getDivisor();
      // for (int i = 0; i < bufferLength; i++) {
      //   timeData[i] = i * timeStep;
      // }
      //
      // // create current data
      // double[] current = new double[bufferLength];
      // for (int i = 0; i < bufferLength; i++) {
      //   current[i] = V2Trimmed[i] / controlModel.getSeriesResistance() * SynapsePreferences.CURRENT_UNIT.getDivisor();
      // }
      //
      // // create conductance data
      // double[] conductance = new double[bufferLength];
      // for (int i = 0; i < bufferLength; i++) {
      //
      //   double I = V2Trimmed[i] / controlModel.getSeriesResistance();
      //   double G = I / (V1Trimmed[i] - V2Trimmed[i]) * SynapsePreferences.CONDUCTANCE_UNIT.getDivisor();
      //   G = G < 0 ? 0 : G;
      //   conductance[i] = G;
      // }
      //
      // publish(new double[][]{timeData, V1Trimmed, V2Trimmed, V2MinusV1, current, conductance, null});
      //
      // while (!initialPulseTrainCaptured) {
      //   // System.out.println("Waiting...");
      //   Thread.sleep(50);
      // }

      //////////////////////////////////
      // FFLV READ PULSES /////////////////
      //////////////////////////////////

      // TODO set W1 MUX to A, W2 MUX to no, 1+ Mux to Y, 1+ Mux to no
      // TODO it be even better to wrap that in a FFLV instruction utility, in fact all instructions should be wrapped in a utility.

      while (!isCancelled()) {

        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        }

        //////////////////////////////////
        // Analog In /////////////////
        //////////////////////////////////

        // trigger on 20% the rising .1 V read pulse
        int samplesPerPulse = 300;
        int sampleFrequency = 100_000 * samplesPerPulse;
        dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02, samplesPerPulse * 1);
        waitUntilArmed();

        //////////////////////////////////
        // Pulse Out /////////////////
        //////////////////////////////////

        // TODO decide on the FFLV voltage ampl. and width. It's hardcoded here. Should be added to GUI as configurable?
        // FFLV pulse: 0.1 V, 5 us pulse width
        customWaveform = WaveformUtils.generateCustomWaveform(Waveform.SquareSmooth, 0.1, 100_000);
        dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, 100_000, 0, 1, customWaveform);

        ////////////////////////////////
        // Read In /////////////////////
        ////////////////////////////////

        boolean success = capturePulseData(100_000, 1);
        if (!success) {
          // Stop Analog In and Out
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
          controlPanel.getStartStopButton().doClick();
          return false;
        }
        else {

          // Get Raw Data from Oscilloscope
          int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
          double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
          // double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

          ///////////////////////////
          // Create Chart Data //////
          ///////////////////////////

          double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v1, 0.08, 0);
          double[] V1Trimmed = trimmedRawData[0];
          int bufferLength = V1Trimmed.length;

          // create conductance data - a single number equal to the average of all points in the trimmed data
          double runningTotal = 0.0;
          for (int i = 3; i < bufferLength - 3; i++) {
            double y = V1Trimmed[i] / 0.1; // 0.1V is the above hardcoded FFLV read pulse ampl. y=is the voltage divider value: V_y/V_applied
            y = y < 0 ? 0 : y;
            runningTotal += y;
          }
          double yAve = runningTotal / (bufferLength - 6); // an array with one value: `y`

          publish(yAve);
        }
        // Stop Analog In and Out
        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
      }
      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

      double newestChunk = chunks.get(chunks.size() - 1);

      // update Y chart
      controlModel.setLastY(newestChunk);
      plotController.updateYChartData(controlModel.getLastY());
      plotController.repaintYChart();
    }
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    switch (propName) {

      case EVENT_INSTRUCTION_UPDATE:

        // System.out.println(controlModel.getInstruction());
        dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

        break;

      default:
        break;
    }
  }

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

    return new CaptureWorker();
  }
}
