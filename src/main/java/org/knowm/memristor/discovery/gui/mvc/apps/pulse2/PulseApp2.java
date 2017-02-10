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
package org.knowm.memristor.discovery.gui.mvc.apps.pulse2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.apps.App;
import org.knowm.memristor.discovery.gui.mvc.apps.AppModel;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.experiment.ExperimentController;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.experiment.ExperimentModel;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.experiment.ExperimentPanel;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.plot.PlotModel;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.plot.PlotPanel;
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.AcquisitionMode;
import org.knowm.waveforms4j.DWF.AnalogTriggerCondition;
import org.knowm.waveforms4j.DWF.AnalogTriggerType;
import org.knowm.waveforms4j.DWF.Waveform;

public class PulseApp2 extends App implements PropertyChangeListener {

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
  public PulseApp2(DWFProxy dwfProxy, Container mainFrameContainer) {

    this.dwfProxy = dwfProxy;

    experimentPanel = new ExperimentPanel();
    mainFrameContainer.add(experimentPanel, BorderLayout.WEST);

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

      // System.out.println("experimentModel.getCalculatedFrequency(): " + experimentModel.getCalculatedFrequency());

      // Analog In
      dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
      dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, 2.5);
      dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
      dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, 2.5);
      dwfProxy.getDwf().FDwfAnalogInFrequencySet(experimentModel.getCalculatedFrequency() * 1_000);
      dwfProxy.getDwf().FDwfAnalogInBufferSizeSet(PulsePreferences2.CAPTURE_BUFFER_SIZE);
      dwfProxy.getDwf().FDwfAnalogInAcquisitionModeSet(AcquisitionMode.Single.getId());
      // dwf.startAnalogCaptureBothChannels(experimentModel.getCalculatedFrequency() * 100, PulsePreferences2.CAPTURE_BUFFER_SIZE, AcquisitionMode.Record);
      // dwf.setAD2Capturing(true);
      dwfProxy.getDwf().FDwfAnalogInTriggerAutoTimeoutSet(0); // disable auto trigger
      dwfProxy.getDwf().FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcDetectorAnalogIn.getId()); // one of the analog in channels
      dwfProxy.getDwf().FDwfAnalogInTriggerTypeSet(AnalogTriggerType.trigtypeEdge.getId());
      dwfProxy.getDwf().FDwfAnalogInTriggerChannelSet(0); // first channel

      if (experimentModel.getAmplitude() > 0) {

        dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondRisingPositive.getId());
        dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(0.05);
      }
      else {
        dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondFallingNegative.getId());
        dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(-0.05);
      }

      // arm the capture
      dwfProxy.getDwf().FDwfAnalogInConfigure(false, true);

      // generate the pulse
      // System.out.println("experimentModel.getCalculatedFrequency() = " + experimentModel.getCalculatedFrequency());
      dwfProxy.getDwf().startSinglePulse(DWF.WAVEFORM_CHANNEL_1, Waveform.Sine, experimentModel.getCalculatedFrequency(), experimentModel.getAmplitude(), 0, 50);

      while (true) {
        byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
        // System.out.println("status: " + status);
        if (status == 2) { // done capturing
          break;
        }
      }

      int validSamples = 0;
      double[] vin = null;
      double[] vout = null;
      byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
      // System.out.println("status: " + status);
      validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      vin = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      vout = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
      // System.out.println("validSamples: " + validSamples);

      dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
      dwfProxy.setAD2Capturing(false);
      dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false); // stop function generator

      ///////////////////////////
      // Create Chart Data //////
      ///////////////////////////

      // create time data
      double[] timeData = new double[vin.length];
      double timeStep = 1 / (double) experimentModel.getCalculatedFrequency();
      // System.out.println("timeStep = " + timeStep);
      for (int i = 0; i < timeData.length; i++) {
        timeData[i] = i * timeStep * 1_000;
      }

      // create current data
      double[] current = new double[vout.length];
      for (int i = 0; i < current.length; i++) {
        current[i] = Math.abs(vout[i] / experimentModel.getSeriesR() * PulsePreferences2.CURRENT_UNIT.getDivisor());
      }

      // create conductance data
      double[] conductance = new double[vout.length];
      for (int i = 0; i < conductance.length; i++) {

        double I = vout[i] / experimentModel.getSeriesR();

        double G = I / (vin[i] - vout[i]) * PulsePreferences2.CONDUCTANCE_UNIT.getDivisor();

        G = G < 0 ? 0 : G;

        double ave = (1 - plotModel.getK()) * (plotModel.getAve()) + plotModel.getK() * (G);
        plotModel.setAve(ave);

        conductance[i] = ave;
      }

      publish(new double[][]{timeData, vin, vout, current, conductance});

      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      if (allowPlotting) {

        // System.out.println("" + chunks.size());

        // Messages received from the doInBackground() (when invoking the publish() method). See: http://www.javacreed.com/swing-worker-example/

        plotController.udpateVtChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], experimentModel.getPulseWidth(), experimentModel
            .getAmplitude());
        plotController.udpateIVChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[3], experimentModel.getPulseWidth(), experimentModel
            .getAmplitude());
        plotController.updateGVChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[4], experimentModel.getPulseWidth(), experimentModel
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
          plotController.udpateWaveformChart(experimentModel.getWaveformTimeData(), experimentModel.getWaveformAmplitudeData(), experimentModel.getAmplitude(), experimentModel.getPulseWidth());
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

    // TODO Auto-generated method stub
    return null;
  }
}
