/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016 Knowm Inc. www.knowm.org
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

package org.knowm.memristor.discovery.gui.mvc.apps.pulse;

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
import org.knowm.waveforms4j.DWF.Waveform;

public class PulseApp extends App implements PropertyChangeListener {

  private final DWFProxy dwfProxy;
  private final PulseModel model = new PulseModel();
  private PulseControlPanel controlPanel;
  private PulseMainPanel mainPanel;

  private CaptureWorker captureWorker;
  private boolean allowPlotting = true;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public PulseApp(DWFProxy dwfProxy, Container mainFrameContainer) {

    this.dwfProxy = dwfProxy;
    createAndShowGUI(mainFrameContainer);
  }

  private void createAndShowGUI(Container mainFrameContainer) {

    controlPanel = new PulseControlPanel();
    mainFrameContainer.add(controlPanel, BorderLayout.WEST);

    /////////////////////////////////////////////////////////////
    // START BUTTON ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

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

    /////////////////////////////////////////////////////////////
    // STOP BUTTON //////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

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

    mainPanel = new PulseMainPanel();
    mainFrameContainer.add(mainPanel, BorderLayout.CENTER);

    new PulseController(controlPanel, mainPanel, model, dwfProxy);

    // register this as the listener of the model
    model.addListener(this);
  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // Analog Out
      double dutycycle = 5;
      double divosior = 10;
      if (model.getAmplitude() > 0) {
        dwfProxy.getDwf().startWave(DWF.WAVEFORM_CHANNEL_1, Waveform.Square, model.getCalculatedFrequency() / divosior, model.getAmplitude() / 2, model.getAmplitude() / 2 + 0.003, dutycycle);
      }
      else {
        dwfProxy.getDwf().startWave(DWF.WAVEFORM_CHANNEL_1, Waveform.Square, model.getCalculatedFrequency() / divosior, model.getAmplitude() / 2, model.getAmplitude() / 2 + 0.003, dutycycle);
      }
      // Analog In
      dwfProxy.getDwf().startAnalogCaptureBothChannels(model.getCalculatedFrequency() * PulsePreferences.CAPTURE_BUFFER_SIZE / PulsePreferences.CAPTURE_PERIOD_COUNT,
          PulsePreferences.CAPTURE_BUFFER_SIZE, AcquisitionMode.ScanShift);

      dwfProxy.setAD2Capturing(true);

      while (!isCancelled()) {

        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
          dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false);
          dwfProxy.setAD2Capturing(false);
          // System.out.println("capture worker shut down.");
        }

        byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
        // System.out.println("status: " + status);

        int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        // System.out.println("validSamples: " + validSamples);

        if (validSamples > 0) {

          // captureAmplitudeData = dwf.FDwfAnalogInStatusData(OSCILLOSCOPE_CHANNEL_1, validSamples);
          double[] rawdata1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
          double[] rawdata2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

          double[] timeData = new double[rawdata1.length];
          double timeStep = 1 / (double) model.getCalculatedFrequency() * PulsePreferences.CAPTURE_PERIOD_COUNT / PulsePreferences.CAPTURE_BUFFER_SIZE;
          for (int i = 0; i < timeData.length; i++) {
            timeData[i] = i * timeStep * 1000000;
          }
          if (mainPanel.getCaptureButton().isSelected()) {

            // Calculate time data
            publish(new double[][] { rawdata1, rawdata2, timeData });
          }
          else if (mainPanel.getItButton().isSelected()) {

            // create current data
            double[] current = new double[rawdata2.length];
            for (int i = 0; i < current.length; i++) {
              current[i] = rawdata2[i] / model.getShunt() * PulsePreferences.CURRENT_UNIT.getDivisor();
            }
            publish(new double[][] { rawdata1, current, timeData });
          }
          else {

            // create resistance data
            double[] resistance = new double[rawdata2.length];
            for (int i = 0; i < resistance.length; i++) {
              if (rawdata1[i] < 0.05 && rawdata1[i] > -0.05) {
                resistance[i] = Double.NaN;
              }
              else {
                resistance[i] = (rawdata1[i] - rawdata2[i]) / (rawdata2[i] / model.getShunt()) / PulsePreferences.RESISTANCE_UNIT.getDivisor();
              }
            }
            publish(new double[][] { rawdata1, resistance, timeData });
          }

        }
      }
      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      long start = System.currentTimeMillis();

      if (allowPlotting) {

        // System.out.println("" + chunks.size());

        // Messages received from the doInBackground() (when invoking the publish() method). See: http://www.javacreed.com/swing-worker-example/

        if (mainPanel.getCaptureButton().isSelected()) {
          mainPanel.udpateVtChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], model.getPulseWidth(), model.getAmplitude());
        }
        else if (mainPanel.getItButton().isSelected()) {
          mainPanel.udpateITChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], model.getPulseWidth(), model.getAmplitude());
        }
        else {
          mainPanel.udpateRTChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], model.getPulseWidth(), model.getAmplitude());
        }
      }
      // controlPanel.getStopButton().doClick();

      long duration = System.currentTimeMillis() - start;
      // System.out.println("duration" + duration);

      // System.out.println("capturedData: " + Arrays.toString(captureAmplitudeData));
      // swingPropertyChangeSupport.firePropertyChange(Events.CAPTURE_UPDATE, true, false);
      try {
        // Thread.sleep(40 - duration); // 40 ms ==> 25fps
        Thread.sleep(50 - duration); // 50 ms ==> 20fps
      } catch (InterruptedException e) {
      }
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
