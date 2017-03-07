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
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.App;
import org.knowm.memristor.discovery.gui.mvc.experiments.AppModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.AppPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.ConductancePreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.experiment.ExperimentController;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.experiment.ExperimentModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.experiment.ExperimentPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.plot.PlotModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.PostProcessDataUtils;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;

public class PulseApp extends App implements PropertyChangeListener {

  private final ExperimentModel experimentModel = new ExperimentModel();
  private ExperimentPanel experimentPanel;

  private PlotPanel plotPanel;
  private final PlotModel plotModel = new PlotModel();
  private final PlotController plotController;

  private CaptureWorker captureWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public PulseApp(DWFProxy dwfProxy, Container mainFrameContainer) {

    super(dwfProxy);

    experimentPanel = new ExperimentPanel();
    JScrollPane jScrollPane = new JScrollPane(experimentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane.setBorder(createEmptyBorder());
    mainFrameContainer.add(jScrollPane, BorderLayout.WEST);

    // ///////////////////////////////////////////////////////////
    // START/STOP BUTTON ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    experimentPanel.getStartStopButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (experimentModel.isStartToggled()) {

          experimentModel.setStartToggled(false);
          experimentPanel.getStartStopButton().setText("Stop");

          // switch to capture view
          if (plotPanel.getCaptureButton().isSelected()) {
            plotPanel.switch2CaptureChart();
          }
          else if (plotPanel.getIVButton().isSelected()) {
            plotPanel.switch2IVChart();
          }
          else {
            plotPanel.switch2GVChart();
          }

          // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
          captureWorker = new CaptureWorker();
          captureWorker.execute();
        }
        else {

          experimentModel.setStartToggled(true);
          experimentPanel.getStartStopButton().setText("Start");

          // stop AD2 waveform 1 and stop AD2 capture on channel 1 and 2
          captureWorker.cancel(true);
        }
      }
    });

    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    mainFrameContainer.add(plotPanel, BorderLayout.CENTER);

    new ExperimentController(experimentPanel, experimentModel, dwfProxy);

    // register this as the listener of the experimentModel
    experimentModel.addListener(this);

    // trigger plot of waveform
    PropertyChangeEvent evt = new PropertyChangeEvent(this, AppModel.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);
  }

  boolean initialPulseTrainCaptured = false;

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      //////////////////////////////////
      // Analog In /////////////////
      //////////////////////////////////

      int sampleFrequencyMultiplier = 300; // adjust this down if you want to capture more pulses as the buffer size is limited.
      double sampleFrequency = experimentModel.getCalculatedFrequency() * sampleFrequencyMultiplier; // adjust this down if you want to capture more pulses as the buffer size is limited.
      dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02 * (experimentModel.getAmplitude() > 0 ? 1 : -1));
      Thread.sleep(20); // Attempt to allow Analog In to get fired up for the next set of pulses

      //////////////////////////////////
      // Pulse Out /////////////////
      //////////////////////////////////

      double[] customWaveform = WaveformUtils.generateCustomWaveform(experimentModel.getWaveform(), experimentModel.getAppliedAmplitude(), experimentModel.getCalculatedFrequency());
      dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, experimentModel.getCalculatedFrequency(), 0, experimentModel.getPulseNumber(), customWaveform);

      //////////////////////////////////
      //////////////////////////////////

      // Read In Data
      boolean success = capturePulseData();
      if (!success) {
        return false;
      }

      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
      // System.out.println("validSamples: " + validSamples);

      // Stop Analog In and Out
      dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
      dwfProxy.getDwf().stopAnalogCaptureBothChannels();

      ///////////////////////////
      // Create Chart Data //////
      ///////////////////////////

      double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.05, 10);
      double[] V1Trimmed = trimmedRawData[0];
      double[] V2Trimmed = trimmedRawData[1];
      // double[] V2Zeroed = PostProcessDataUtils.zeroIdleData(V1Trimmed, V2Trimmed, 0.05);
      double[] V2MinusV1 = PostProcessDataUtils.getV2MinusV1(V1Trimmed, V2Trimmed);

      int bufferLength = V1Trimmed.length;

      // create time data
      double[] timeData = new double[bufferLength];
      double timeStep = 1 / sampleFrequency * DCPreferences.TIME_UNIT.getDivisor();
      for (int i = 0; i < bufferLength; i++) {
        timeData[i] = i * timeStep;
      }

      // create current data
      double[] current = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {
        current[i] = V2Trimmed[i] / experimentModel.getSeriesR() * DCPreferences.CURRENT_UNIT.getDivisor();
      }

      // create conductance data
      double[] conductance = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {

        double I = V2Trimmed[i] / experimentModel.getSeriesR();
        double G = I / (V1Trimmed[i] - V2Trimmed[i]) * DCPreferences.CONDUCTANCE_UNIT.getDivisor();
        G = G < 0 ? 0 : G;
        conductance[i] = G;
      }

      publish(new double[][]{timeData, V1Trimmed, V2Trimmed, V2MinusV1, current, conductance, null});

      // New addition: Loop and capture read data (0.1V, 10µs) until stop is pushed.

      while (!initialPulseTrainCaptured) {
        // System.out.println("Waiting...");
        Thread.sleep(50);
      }

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

        // trigger on half the rising .1 V read pulse
        dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.05);
        Thread.sleep(20); // Attempt to allow Analog In to get fired up for the next set of pulses

        //////////////////////////////////
        // Pulse Out /////////////////
        //////////////////////////////////

        // read pulse: 0.1 V, 10 us pulse width
        customWaveform = WaveformUtils.generateCustomWaveform(Waveform.SquareSmooth, 0.1, experimentModel.getCalculatedFrequency());
        dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, 100_000, 0, 1, customWaveform);

        // Read In Data
        success = capturePulseData();
        if (!success) {
          // System.out.println("returning false");
          // experimentPanel.getStopButton().doClick();
          // return false;
          System.out.println("continuing...");
          continue;
        }

        // Get Raw Data from Oscilloscope
        validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
        v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
        // System.out.println("validSamples: " + validSamples);

        ///////////////////////////
        // Create Chart Data //////
        ///////////////////////////

        trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.02, 0);
        V1Trimmed = trimmedRawData[0];
        V2Trimmed = trimmedRawData[1];
        bufferLength = V1Trimmed.length;

        // create conductance data - a single number equal to the average of all points in the trimmed data
        double runningTotal = 0.0;
        for (int i = 3; i < bufferLength - 3; i++) {
          double I = V2Trimmed[i] / experimentModel.getSeriesR();
          double G = I / (V1Trimmed[i] - V2Trimmed[i]);
          G = G < 0 ? 0 : G;
          runningTotal += G;
        }
        // conductance value packed in a one-element array
        double[] conductanceAve = new double[]{runningTotal / (bufferLength - 6) * ConductancePreferences.CONDUCTANCE_UNIT.getDivisor()};

        publish(new double[][]{null, null, null, null, null, null, conductanceAve});
      }
      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      double[][] newestChunk = chunks.get(chunks.size() - 1);

      if (newestChunk[6] == null) {
        // System.out.println("" + chunks.size());
        initialPulseTrainCaptured = true;

        plotController.udpateVtChart(newestChunk[0], newestChunk[1], newestChunk[2], newestChunk[3], experimentModel.getPulseWidth(), experimentModel
            .getAmplitude());
        plotController.udpateIVChart(newestChunk[0], newestChunk[4], experimentModel.getPulseWidth(), experimentModel
            .getAmplitude());
        plotController.updateGVChart(newestChunk[0], newestChunk[5], experimentModel.getPulseWidth(), experimentModel
            .getAmplitude());

        if (plotPanel.getCaptureButton().isSelected()) {
          plotController.repaintVtChart();
        }
        else if (plotPanel.getIVButton().isSelected()) {
          plotController.repaintItChart();
        }
        else {
          plotController.repaintRtChart();
        }
      }
      else {

        // update G chart
        // System.out.println("updating G Chart");
        experimentModel.setLastG(newestChunk[6][0]);
        plotController.updateGChart(experimentModel.getLastG(), experimentModel.getLastRAsString());
        plotController.repaintGChart();

        experimentModel.updateEnergyData();
        experimentPanel.updateEnergyGUI(experimentModel.getAppliedAmplitude(), experimentModel.getAppliedCurrent(), experimentModel.getAppliedEnergy());
      }
    }
  }

  /**
   * These property change events are triggered in the experimentModel in the case where the underlying experimentModel is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    switch (propName) {

      case AppModel.EVENT_WAVEFORM_UPDATE:

        plotPanel.switch2WaveformChart();
        plotController.udpateWaveformChart(experimentModel.getWaveformTimeData(), experimentModel.getWaveformAmplitudeData(), experimentModel.getAmplitude(), experimentModel.getPulseWidth());

        break;

      default:
        break;
    }
  }

  @Override
  public AppModel getExperimentModel() {

    return experimentModel;
  }

  @Override
  public AppModel getPlotModel() {

    return plotModel;
  }
}
