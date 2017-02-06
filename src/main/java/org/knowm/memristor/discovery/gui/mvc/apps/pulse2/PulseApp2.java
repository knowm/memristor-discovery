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
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.AcquisitionMode;
import org.knowm.waveforms4j.DWF.AnalogTriggerCondition;
import org.knowm.waveforms4j.DWF.AnalogTriggerType;
import org.knowm.waveforms4j.DWF.Waveform;

public class PulseApp2 extends App implements PropertyChangeListener {

  private final DWFProxy dwfProxy;
  private final PulseModel2 model = new PulseModel2();
  private PulseControlPanel2 controlPanel;
  private PulseMainPanel2 mainPanel;

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
    createAndShowGUI(mainFrameContainer);
  }

  private void createAndShowGUI(Container mainFrameContainer) {

    controlPanel = new PulseControlPanel2();
    mainFrameContainer.add(controlPanel, BorderLayout.WEST);

    // ///////////////////////////////////////////////////////////
    // START BUTTON ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    controlPanel.getStartButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        allowPlotting = true;
        dwfProxy.setAD2Capturing(true);

        // switchPanel.enableAllDigitalIOCheckBoxes(false);
        // controlPanel.enableAllChildComponents(false);
        controlPanel.getStartButton().setEnabled(false);
        controlPanel.getStopButton().setEnabled(true);

        // switch to capture view
        if (mainPanel.getCaptureButton().isSelected()) {
          mainPanel.switch2CaptureChart();
        }
        else if (mainPanel.getItButton().isSelected()) {
          mainPanel.switch2ITChart();
        }
        else {
          mainPanel.switch2RTChart();
        }

        // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
        captureWorker = new CaptureWorker();
        captureWorker.execute();
      }
    });

    // ///////////////////////////////////////////////////////////
    // STOP BUTTON //////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    controlPanel.getStopButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        dwfProxy.setAD2Capturing(false);

        // switchPanel.enableAllDigitalIOCheckBoxes(true);
        // controlPanel.enableAllChildComponents(true);
        controlPanel.getStartButton().setEnabled(true);
        controlPanel.getStopButton().setEnabled(false);

        // stop AD2 waveform 1 and stop AD2 capture on channel 1 and 2
        allowPlotting = false;
        captureWorker.cancel(true);
      }
    });

    mainPanel = new PulseMainPanel2();
    mainFrameContainer.add(mainPanel, BorderLayout.CENTER);

    new PulseController2(controlPanel, mainPanel, model, dwfProxy);

    // register this as the listener of the model
    model.addListener(this);
  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // System.out.println("model.getCalculatedFrequency(): " + model.getCalculatedFrequency());

      // Analog In
      dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
      dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, 2.5);
      dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
      dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, 2.5);
      dwfProxy.getDwf().FDwfAnalogInFrequencySet(model.getCalculatedFrequency() * 1000);
      dwfProxy.getDwf().FDwfAnalogInBufferSizeSet(PulsePreferences2.CAPTURE_BUFFER_SIZE);
      dwfProxy.getDwf().FDwfAnalogInAcquisitionModeSet(AcquisitionMode.Single.getId());
      // dwf.startAnalogCaptureBothChannels(model.getCalculatedFrequency() * 100, PulsePreferences2.CAPTURE_BUFFER_SIZE, AcquisitionMode.Record);
      // dwf.setAD2Capturing(true);
      dwfProxy.getDwf().FDwfAnalogInTriggerAutoTimeoutSet(0); // disable auto trigger
      dwfProxy.getDwf().FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcDetectorAnalogIn.getId()); // one of the analog in channels
      dwfProxy.getDwf().FDwfAnalogInTriggerTypeSet(AnalogTriggerType.trigtypeEdge.getId());
      dwfProxy.getDwf().FDwfAnalogInTriggerChannelSet(0); // first channel

      if (model.getAmplitude() > 0) {

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

      dwfProxy.getDwf().startSinglePulse(DWF.WAVEFORM_CHANNEL_1, Waveform.Sine, model.getCalculatedFrequency(), model.getAmplitude(), 0, 50);

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

      // create time data
      double[] timeData = new double[vin.length];
      double timeStep = 1 / (double) model.getCalculatedFrequency() / 1000;
      for (int i = 0; i < timeData.length; i++) {
        timeData[i] = i * timeStep * 1000000;
      }

      // Calculate time data

      // create current data
      double[] current = new double[vout.length];
      for (int i = 0; i < current.length; i++) {
        current[i] = vout[i] / model.getShunt() * PulsePreferences2.CURRENT_UNIT.getDivisor();
      }

      // create resistance data
      double[] resistance = new double[vout.length];
      // List<Double> resistanceAsList = new ArrayList<Double>();
      int validCount = 0; // use this to ignore the first several data points
      for (int i = 0; i < vin.length; i++) {
        if (vin[i] < 0.05 && vin[i] > -0.05) {
          resistance[i] = Double.NaN;
        }
        else {
          if (validCount++ < 20) { // ignore first 20 points
            resistance[i] = Double.NaN;
          }
          else {
            resistance[i] = (vin[i] - vout[i]) / (vout[i] / model.getShunt()) / PulsePreferences2.RESISTANCE_UNIT.getDivisor();
          }
        }
      }

      publish(new double[][] { timeData, vin, vout, current, resistance });

      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      if (allowPlotting) {

        // System.out.println("" + chunks.size());

        mainPanel.udpateVtChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], model.getPulseWidth(), model.getAmplitude());
        mainPanel.udpateITChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[3], model.getPulseWidth(), model.getAmplitude());
        mainPanel.udpateRTChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[4], model.getPulseWidth(), model.getAmplitude());

        if (mainPanel.getCaptureButton().isSelected()) {
          mainPanel.repaintVtChart();
        }
        else if (mainPanel.getItButton().isSelected()) {
          mainPanel.repaintItChart();
        }
        else {
          mainPanel.repaintRtChart();
        }
      }
      controlPanel.getStopButton().doClick();
    }
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to those events and make sure the corresponding GUI
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

        if (mainPanel.getCaptureButton().isSelected()) {
          mainPanel.switch2CaptureChart();
        }
        else if (mainPanel.getItButton().isSelected()) {
          mainPanel.switch2ITChart();
        }
        else {
          mainPanel.switch2RTChart();
        }
      }
      break;

    default:
      break;
    }

  }

  @Override
  public AppModel getExperimentModel() {

    return model;
  }

  @Override
  public AppModel getPlotModel() {

    // TODO Auto-generated method stub
    return null;
  }
}
