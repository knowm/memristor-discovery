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
package org.knowm.memristor.discovery.gui.mvc.apps.dc;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.apps.App;
import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.dc.experiment.ExperimentController;
import org.knowm.memristor.discovery.gui.mvc.apps.dc.experiment.ExperimentModel;
import org.knowm.memristor.discovery.gui.mvc.apps.dc.experiment.ExperimentPanel;
import org.knowm.memristor.discovery.gui.mvc.apps.dc.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.apps.dc.plot.PlotModel;
import org.knowm.memristor.discovery.gui.mvc.apps.dc.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.driver.Driver;
import org.knowm.memristor.discovery.utils.driver.Sawtooth;
import org.knowm.memristor.discovery.utils.driver.SawtoothUpDown;
import org.knowm.memristor.discovery.utils.driver.Triangle;
import org.knowm.memristor.discovery.utils.driver.TriangleUpDown;
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.AcquisitionMode;
import org.knowm.waveforms4j.DWF.AnalogTriggerCondition;
import org.knowm.waveforms4j.DWF.AnalogTriggerType;

public class DCApp extends App implements PropertyChangeListener {

  private final DWFProxy dwfProxy;

  private final ExperimentModel experimentModel = new ExperimentModel();
  private ExperimentPanel experimentPanel;

  private PlotPanel plotPanel;
  private final PlotModel plotModel = new PlotModel();
  private final PlotController plotController;

  private CaptureWorker captureWorker;
  private boolean allowPlotting = true;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public DCApp(DWFProxy dwfProxy, Container mainFrameContainer) {

    this.dwfProxy = dwfProxy;

    experimentPanel = new ExperimentPanel();
    JScrollPane jScrollPane = new JScrollPane(experimentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane.setBorder(createEmptyBorder());
    mainFrameContainer.add(jScrollPane, BorderLayout.WEST);

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
    });

    // ///////////////////////////////////////////////////////////
    // STOP BUTTON //////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    experimentPanel.getStopButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        dwfProxy.setAD2Capturing(false);

        // switchPanel.enableAllDigitalIOCheckBoxes(true);
        // experimentPanel.enableAllChildComponents(true);
        experimentPanel.getStartButton().setEnabled(true);
        experimentPanel.getStopButton().setEnabled(false);

        // stop AD2 waveform 1 and stop AD2 capture on channel 1 and 2
        allowPlotting = false;
        captureWorker.cancel(true);
      }
    });

    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    mainFrameContainer.add(plotPanel, BorderLayout.CENTER);

    new ExperimentController(experimentPanel, plotPanel, experimentModel, dwfProxy);

    // register this as the listener of the experimentModel
    experimentModel.addListener(this);

    // trigger plot of waveform
    PropertyChangeEvent evt = new PropertyChangeEvent(this, AppModel.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);
  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {


      int sampleFrequencyMultiplier = 200; // adjust this down if you want to capture more pulses as the buffer size is limited.

      //////////////////////////////////
      // Analog In /////////////////
      //////////////////////////////////
      dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
      dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, 2.5);
      dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
      dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, 2.5);
      dwfProxy.getDwf().FDwfAnalogInFrequencySet(experimentModel.getCalculatedFrequency() * sampleFrequencyMultiplier);
      dwfProxy.getDwf().FDwfAnalogInBufferSizeSet(DCPreferences.CAPTURE_BUFFER_SIZE);
      dwfProxy.getDwf().FDwfAnalogInAcquisitionModeSet(AcquisitionMode.Single.getId());
      // Trigger single capture on rising edge of analog signal pulse
      dwfProxy.getDwf().FDwfAnalogInTriggerAutoTimeoutSet(0); // disable auto trigger
      dwfProxy.getDwf().FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcDetectorAnalogIn.getId()); // one of the analog in channels
      dwfProxy.getDwf().FDwfAnalogInTriggerTypeSet(AnalogTriggerType.trigtypeEdge.getId());
      dwfProxy.getDwf().FDwfAnalogInTriggerChannelSet(0); // first channel
      // Trigger Level
      if (experimentModel.getAmplitude() > 0) {
        dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondRisingPositive.getId());
        dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(0.05);
      }
      else {
        dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondFallingNegative.getId());
        dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(-0.05);
      }

      // arm the capture
      dwfProxy.getDwf().FDwfAnalogInConfigure(true, true);

      //////////////////////////////////
      //////////////////////////////////

      Thread.sleep(10); // Attempt to allow Analog In to get fired up for the next set of pulses

      //////////////////////////////////
      // Pulse Out /////////////////
      //////////////////////////////////

      // generate the pulse
      // DWF.Waveform dwfWaveform = WaveformUtils.getDWFWaveform(experimentModel.getWaveform());
      // if(dwfWaveform != DWF.Waveform.Custom) {
      //   dwfProxy.getDwf().startSinglePulse(DWF.WAVEFORM_CHANNEL_1, dwfWaveform, experimentModel.getCalculatedFrequency(), experimentModel.getAmplitude(), 0, 50);
      // }else{

      // custom waveform
      Driver driver;
      switch (experimentModel.getWaveform()) {
        case Sawtooth:
          driver = new Sawtooth("Sawtooth", 0, 0, experimentModel.getAmplitude(), experimentModel.getCalculatedFrequency());
          break;
        case SawtoothUpDown:
          driver = new SawtoothUpDown("SawtoothUpDown", 0, 0, experimentModel.getAmplitude(), experimentModel.getCalculatedFrequency());
          break;
        case Triangle:
          driver = new Triangle("Triangle", 0, 0, experimentModel.getAmplitude(), experimentModel.getCalculatedFrequency());
          break;
        case TriangleUpDown:
          driver = new TriangleUpDown("TriangleUpDown", 0, 0, experimentModel.getAmplitude(), experimentModel.getCalculatedFrequency());
          break;
        default:
          driver = new SawtoothUpDown("SawtoothUpDown", 0, 0, experimentModel.getAmplitude(), experimentModel.getCalculatedFrequency());
          break;
      }

      int counter = 0;
      double[] waveform = new double[4096];
      double timeInc = 1 / experimentModel.getCalculatedFrequency() / 4096;

      do {
        double time = counter * timeInc;
        waveform[counter] = driver.getSignal(time) / 5.0; // / 5.0 to scale between 1 and -1
      } while (++counter < 4096);

      System.out.println("waveform = " + Arrays.toString(waveform));

      // double[] waveform = WaveformUtils.generatePositiveAndNegativeDCRamps(experimentModel.getAmplitude());
      dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, experimentModel.getCalculatedFrequency(), 0, experimentModel.getPulseNumber(), waveform);

      System.out.println("experimentModel.getCalculatedFrequency() = " + experimentModel.getCalculatedFrequency());

      //////////////////////////////////
      //////////////////////////////////

      // Read In Data
      while (true) {
        byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
        System.out.println("status: " + status);
        if (status == 2) { // done capturing
          break;
        }
      }
      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
      // System.out.println("validSamples: " + validSamples);

      dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
      dwfProxy.setAD2Capturing(false);
      dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false); // stop function generator

      ///////////////////////////
      // Create Chart Data //////
      ///////////////////////////

      // The data is a bit weird, as what's captured is a long window of "idle" voltage before the pulses. We clean that now...
      int startIndex = 0;
      for (int i = 0; i < v1.length; i++) {
        if (Math.abs(v1[i]) > .075) {
          startIndex = i;
          break;
        }
      }
      int endIndex = v1.length - 1;
      for (int i = v1.length - 1; i > 0; i--) {
        if (Math.abs(v1[i]) > .075) {
          endIndex = i;
          break;
        }
      }
      int bufferLength = endIndex - startIndex;

      // create time data
      double[] timeData = new double[bufferLength];
      double timeStep = 1 / (experimentModel.getCalculatedFrequency() * sampleFrequencyMultiplier) * 1_000_000;
      for (int i = 0; i < bufferLength; i++) {
        timeData[i] = i * timeStep;
      }

      // create current data
      double[] current = new double[bufferLength];
      double[] V1Cleaned = new double[bufferLength];
      double[] V2Cleaned = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {
        current[i] = v2[i + startIndex] / experimentModel.getSeriesR() * DCPreferences.CURRENT_UNIT.getDivisor();
        V1Cleaned[i] = v1[i + startIndex];
        V2Cleaned[i] = v2[i + startIndex];
      }

      // create conductance data
      double[] conductance = new double[bufferLength];
      for (int i = 0; i < bufferLength; i++) {

        double I = v2[i + startIndex] / experimentModel.getSeriesR();
        double G = I / (v1[i + startIndex] - v2[i + startIndex]) * DCPreferences.CONDUCTANCE_UNIT.getDivisor();
        G = G < 0 ? 0 : G;
        // conductance[i] = G;

        double ave = (1 - plotModel.getK()) * (plotModel.getAve()) + plotModel.getK() * (G);
        plotModel.setAve(ave);
        conductance[i] = ave;
      }

      publish(new double[][]{timeData, V1Cleaned, V2Cleaned, current, conductance});

      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      if (allowPlotting) {

        double[][] newestChunk = chunks.get(chunks.size() - 1);

        // System.out.println("" + chunks.size());

        plotController.udpateVtChart(newestChunk[0], newestChunk[1], newestChunk[2], experimentModel.getPeriod(), experimentModel
            .getAmplitude());
        plotController.udpateIVChart(newestChunk[1], newestChunk[3], experimentModel.getPeriod(), experimentModel
            .getAmplitude());
        plotController.updateGVChart(newestChunk[1], newestChunk[4], experimentModel.getPeriod(), experimentModel
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
      experimentPanel.getStopButton().doClick();
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

          // stop AD2 waveform 1 and stop AD2 capture on channel 1 and 2
          captureWorker.cancel(true);

          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
          captureWorker = new CaptureWorker();
          captureWorker.execute();

          if (plotPanel.getCaptureButton().isSelected()) {
            plotPanel.switch2CaptureChart();
          }
          else if (plotPanel.getIVButton().isSelected()) {
            plotPanel.switch2IVChart();
          }
          else {
            plotPanel.switch2GVChart();
          }
        }
        else {
          plotPanel.switch2WaveformChart();
          plotController.udpateWaveformChart(experimentModel.getWaveformTimeData(), experimentModel.getWaveformAmplitudeData(), experimentModel.getAmplitude(), experimentModel.getPeriod());
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
