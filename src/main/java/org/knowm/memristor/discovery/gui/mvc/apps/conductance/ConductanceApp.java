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
package org.knowm.memristor.discovery.gui.mvc.apps.conductance;

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
import org.knowm.memristor.discovery.gui.mvc.apps.App;
import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.experiment.ExperimentController;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.experiment.ExperimentModel;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.experiment.ExperimentPanel;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.plot.PlotModel;
import org.knowm.memristor.discovery.gui.mvc.apps.conductance.plot.PlotPanel;
import org.knowm.memristor.discovery.gui.mvc.apps.dc.DCPreferences;
import org.knowm.memristor.discovery.utils.PostProcessDataUtils;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;

public class ConductanceApp extends App implements PropertyChangeListener {

  private final DWFProxy dwfProxy;

  private final ExperimentModel experimentModel = new ExperimentModel();
  private ExperimentPanel experimentPanel;

  private PlotPanel plotPanel;
  private final PlotModel plotModel = new PlotModel();
  private final PlotController plotController;

  private ResetCaptureWorker resetCaptureWorker;
  private SetCaptureWorker setCaptureWorker;
  private boolean allowPlotting = true;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public ConductanceApp(DWFProxy dwfProxy, Container mainFrameContainer) {

    this.dwfProxy = dwfProxy;

    experimentPanel = new ExperimentPanel();
    JScrollPane jScrollPane = new JScrollPane(experimentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane.setBorder(createEmptyBorder());
    mainFrameContainer.add(jScrollPane, BorderLayout.WEST);

    // ///////////////////////////////////////////////////////////
    // RESET BUTTON ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    experimentPanel.getResetButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        allowPlotting = true;
        dwfProxy.setAD2Capturing(true);

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

        resetCaptureWorker = new ResetCaptureWorker();
        resetCaptureWorker.execute();
      }
    });

    // ///////////////////////////////////////////////////////////
    // START BUTTON ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    experimentPanel.getStartButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        allowPlotting = true;
        dwfProxy.setAD2Capturing(true);

        experimentPanel.getStartButton().setEnabled(false);
        experimentPanel.getStopButton().setEnabled(true);

        // switch to G-V view
        plotPanel.switch2GVChart();

        // start AD2 resetWaveform 1 and start AD2 capture on channel 1 and 2
        setCaptureWorker = new SetCaptureWorker();
        setCaptureWorker.execute();
      }
    });

    // ///////////////////////////////////////////////////////////
    // STOP BUTTON ///////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    experimentPanel.getStopButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        dwfProxy.setAD2Capturing(false);

        // switchPanel.enableAllDigitalIOCheckBoxes(true);
        // experimentPanel.enableAllChildComponents(true);
        experimentPanel.getStartButton().setEnabled(true);
        experimentPanel.getStopButton().setEnabled(false);

        // stop AD2 resetWaveform 1 and stop AD2 capture on channel 1 and 2
        allowPlotting = false;
        setCaptureWorker.cancel(true);
      }
    });

    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    mainFrameContainer.add(plotPanel, BorderLayout.CENTER);

    new ExperimentController(experimentPanel, plotPanel, experimentModel, dwfProxy);

    // register this as the listener of the experimentModel
    experimentModel.addListener(this);

    // trigger plot of resetWaveform
    PropertyChangeEvent evt = new PropertyChangeEvent(this, AppModel.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);
  }

  private class ResetCaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // Send off Single Pulse and capture the response

      //////////////////////////////////
      // Analog In /////////////////
      //////////////////////////////////

      int sampleFrequencyMultiplier = 200; // adjust this down if you want to capture more pulses as the buffer size is limited.
      double sampleFrequency = experimentModel.getCalculatedFrequency() * sampleFrequencyMultiplier; // adjust this down if you want to capture more pulses as the buffer size is limited.
      dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02 * (experimentModel.getResetAmplitude() > 0 ? 1 : -1));
      Thread.sleep(10); // Attempt to allow Analog In to get fired up for the next set of pulses

      //////////////////////////////////
      // Pulse Out /////////////////
      //////////////////////////////////

      // custom waveform
      double[] customWaveform = WaveformUtils.generateCustomWaveform(experimentModel.getResetPulseType(), experimentModel.getResetAmplitude(), experimentModel.getCalculatedFrequency());
      dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, experimentModel.getCalculatedFrequency(), 0, 1, customWaveform);

      // Read In Data
      while (true) {
        byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
        // System.out.println("status: " + status);
        if (status == 2) { // done capturing
          // Stop Analog In and Out
          dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
          dwfProxy.setAD2Capturing(false);
          dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false); // stop function generator
          break;
        }
      }

      // Get Raw Data from Oscilloscope
      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
      // System.out.println("validSamples: " + validSamples);

      ///////////////////////////
      // Create Chart Data //////
      ///////////////////////////

      double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2);
      double[] V1Trimmed = trimmedRawData[0];
      double[] V2Trimmed = trimmedRawData[1];
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

        double ave = (1 - plotModel.getK()) * (plotModel.getAve()) + plotModel.getK() * (G);
        plotModel.setAve(ave);
        conductance[i] = ave;
      }

      publish(new double[][]{timeData, V1Trimmed, V2Trimmed, current, conductance});

      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      if (allowPlotting) {

        double[][] newestChunk = chunks.get(chunks.size() - 1);

        // System.out.println("" + chunks.size());

        plotController.udpateVtChart(newestChunk[0], newestChunk[1], newestChunk[2], experimentModel.getResetPulseWidth(), experimentModel
            .getResetAmplitude());
        plotController.udpateIVChart(newestChunk[1], newestChunk[3], experimentModel.getResetPulseWidth(), experimentModel.getResetAmplitude());
        // plotController.updateGVChart(newestChunk[4], experimentModel.getResetPulseWidth(), experimentModel.getResetAmplitude());
        plotController.updateGVChartReset(newestChunk[1], newestChunk[4], experimentModel.getResetPulseWidth(), experimentModel
            .getResetAmplitude());

        if (plotPanel.getCaptureButton().isSelected()) {
          plotController.repaintVtChart();
        }
        else if (plotPanel.getIVButton().isSelected()) {
          plotController.repaintIVChart();
        }
        else {
          plotController.repaintGVChart();
        }
      }
      experimentPanel.getStopButton().doClick();
    }
  }

  private class SetCaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      boolean isConductanceReached = false;

      int counter = 0;
      while (!isCancelled() || !isConductanceReached) {

        try {
          Thread.sleep(100); // TODO change this to a small amount after debugged and working app
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called

          dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
          dwfProxy.setAD2Capturing(false);
          dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false); // stop function generator
        }

        // 1. set pulse

        //////////////////////////////////
        // Analog In /////////////////
        //////////////////////////////////

        int sampleFrequencyMultiplier = 200; // adjust this down if you want to capture more pulses as the buffer size is limited.
        double sampleFrequency = experimentModel.getCalculatedFrequency() * sampleFrequencyMultiplier; // adjust this down if you want to capture more pulses as the buffer size is limited.
        dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.02 * (experimentModel.getSetAmplitude() > 0 ? 1 : -1));
        Thread.sleep(10); // Attempt to allow Analog In to get fired up for the next set of pulses

        //////////////////////////////////
        // Pulse Out /////////////////
        //////////////////////////////////

        // custom waveform
        double[] customWaveform = WaveformUtils.generateCustomWaveform(Waveform.Square, experimentModel.getSetAmplitude(), experimentModel.getCalculatedFrequency());
        dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, experimentModel.getCalculatedFrequency(), 0, 1, customWaveform);

        // Read In Data
        while (true) {
          byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
          // System.out.println("status: " + status);
          if (status == 2) { // done capturing
            // Stop Analog In and Out
            dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
            dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false); // stop function generator
            break;
          }
        }

        // 2. Generate plot data

        // Get Raw Data from Oscilloscope
        int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
        double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
        // System.out.println("validSamples: " + validSamples);

        ///////////////////////////
        // Create Chart Data //////
        ///////////////////////////

        double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2);
        double[] V1Trimmed = trimmedRawData[0];
        double[] V2Trimmed = trimmedRawData[1];
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

        // create conductance data - a single number equal to the averag eof all points in the trimmed data
        double runningTotal = 0.0;
        for (int i = 3; i < bufferLength - 3; i++) {

          double I = V2Trimmed[i] / experimentModel.getSeriesR();
          double G = I / (V1Trimmed[i] - V2Trimmed[i]) * DCPreferences.CONDUCTANCE_UNIT.getDivisor();
          G = G < 0 ? 0 : G;
          runningTotal += G;
        }
        double[] conductance = new double[]{runningTotal / (bufferLength - 6)};

        publish(new double[][]{timeData, V1Trimmed, V2Trimmed, current, conductance});

        if (counter++ > 10) {
          isConductanceReached = true;
        }
        // 3. Break if conductance reaches desired level.

      }
      // dwfProxy.setAD2Capturing(false);
      experimentPanel.getStopButton().doClick();
      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      if (allowPlotting) {

        double[][] newestChunk = chunks.get(chunks.size() - 1);

        // System.out.println("" + chunks.size());

        plotController.udpateVtChart(newestChunk[0], newestChunk[1], newestChunk[2], experimentModel.getSetPulseWidth(), experimentModel
            .getSetAmplitude());
        plotController.udpateIVChart(newestChunk[1], newestChunk[3], experimentModel.getSetPulseWidth(), experimentModel
            .getSetAmplitude());
        plotController.updateGVChart(newestChunk[4], experimentModel.getSetPulseWidth(), experimentModel.getSetAmplitude());

        // if (plotPanel.getCaptureButton().isSelected()) {
        //   plotController.repaintVtChart();
        // }
        // else if (plotPanel.getIVButton().isSelected()) {
        //   plotController.repaintIVChart();
        // }
        // else {
        plotController.repaintGVChart();
        // }
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

    // System.out.println("propName: " + propName);

    switch (propName) {

      case AppModel.EVENT_WAVEFORM_UPDATE:

        if (dwfProxy.isAD2Capturing()) {

          double[] customWaveform = WaveformUtils.generateCustomWaveform(Waveform.Square, experimentModel.getSetAmplitude(), experimentModel.getCalculatedFrequency());
          dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, experimentModel.getCalculatedFrequency(), 0, 1, customWaveform);
        }
        else {

          plotPanel.switch2WaveformChart();
          plotController.udpateWaveformChart(experimentModel.getWaveformTimeData(), experimentModel.getWaveformAmplitudeData(), experimentModel.getResetAmplitude(), experimentModel.getResetPulseWidth());
        }
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
