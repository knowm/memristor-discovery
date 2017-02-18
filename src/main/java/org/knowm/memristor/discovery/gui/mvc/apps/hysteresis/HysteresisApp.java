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
package org.knowm.memristor.discovery.gui.mvc.apps.hysteresis;

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
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.experiment.ExperimentController;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.experiment.ExperimentModel;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.experiment.ExperimentPanel;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.plot.PlotModel;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.AcquisitionMode;

public class HysteresisApp extends App implements PropertyChangeListener {

  private final DWFProxy dwfProxy;

  private final ExperimentModel experimentModel = new ExperimentModel();
  private final ExperimentPanel experimentPanel;

  private final PlotPanel plotPanel;
  private final PlotModel plotModel = new PlotModel();
  private final PlotController plotController;

  private HysteresisCaptureWorker captureWorker;
  private boolean allowPlotting = true;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public HysteresisApp(DWFProxy dwfProxy, Container mainFrameContainer) {

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

        // switchPanel.enableAllDigitalIOCheckBoxes(false);
        // controlPanel.enableAllChildComponents(false);
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
        captureWorker = new HysteresisCaptureWorker();
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
        // controlPanel.enableAllChildComponents(true);
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

    ExperimentController hysteresisController = new ExperimentController(experimentPanel, plotPanel, experimentModel, dwfProxy);

    // register this as the listener of the model
    experimentModel.addListener(this);

    // trigger plot of waveform
    PropertyChangeEvent evt = new PropertyChangeEvent(this, AppModel.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);
  }

  private class HysteresisCaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // AnalogOut
      DWF.Waveform dwfWaveform = WaveformUtils.getDWFWaveform(experimentModel.getWaveform());
      dwfProxy.getDwf().startWave(DWF.WAVEFORM_CHANNEL_1, dwfWaveform, experimentModel.getFrequency(), experimentModel.getAmplitude(), experimentModel.getOffset(), 50);

      // Analog In
      double sampleFrequency = (double) experimentModel.getFrequency() * HysteresisPreferences.CAPTURE_BUFFER_SIZE / HysteresisPreferences.CAPTURE_PERIOD_COUNT;
      dwfProxy.getDwf().startAnalogCaptureBothChannels(sampleFrequency, HysteresisPreferences.CAPTURE_BUFFER_SIZE, AcquisitionMode.ScanShift);

      dwfProxy.setAD2Capturing(true);

      while (!isCancelled()) {

        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called

          // AnalogOut
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);

          // Analog In
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();

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

          if (plotPanel.getCaptureButton().isSelected()) { // Capture

            // Calculate time data
            double[] timeData = new double[rawdata1.length];
            double timeStep = 1 / (double) experimentModel.getFrequency() * HysteresisPreferences.CAPTURE_PERIOD_COUNT / HysteresisPreferences.CAPTURE_BUFFER_SIZE;
            for (int i = 0; i < timeData.length; i++) {
              timeData[i] = i * timeStep;
            }
            publish(new double[][]{rawdata1, rawdata2, timeData});
          }
          else if (plotPanel.getIVButton().isSelected()) { // IV

            // create current data
            double[] current = new double[rawdata2.length];
            double[] voltage = new double[rawdata1.length];
            for (int i = 0; i < current.length; i++) {
              current[i] = rawdata2[i] / experimentModel.getSeriesR() * HysteresisPreferences.CURRENT_UNIT.getDivisor();
            }
            if (!HysteresisPreferences.IS_VIN) {
              for (int i = 0; i < current.length; i++) {
                voltage[i] = rawdata1[i] - rawdata2[i];
              }
            }
            publish(new double[][]{rawdata1, voltage, current});
          }
          else {// GV

            double[] conductance = new double[rawdata2.length];
            double[] voltage = new double[rawdata1.length];

            for (int i = 0; i < conductance.length; i++) {

              double I = rawdata2[i] / experimentModel.getSeriesR();

              double G = I / (rawdata1[i] - rawdata2[i]) * HysteresisPreferences.CONDUCTANCE_UNIT.getDivisor();

              G = G < 0 ? 0 : G;

              double ave = (1 - plotModel.getK()) * (plotModel.getAve()) + plotModel.getK() * (G);
              plotModel.setAve(ave);

              conductance[i] = ave;
              voltage[i] = rawdata1[i] - rawdata2[i];
            }

            publish(new double[][]{rawdata1, voltage, conductance});
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

        if (plotPanel.getCaptureButton().isSelected()) {
          plotController.udpateVtChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], experimentModel.getFrequency(), experimentModel
              .getAmplitude(), experimentModel.getOffset());
        }
        else if (plotPanel.getIVButton().isSelected()) {
          plotController.udpateIVChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], experimentModel.getFrequency(), experimentModel
              .getAmplitude(), experimentModel.getOffset());
        }
        else {
          plotController.updateGVChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], experimentModel.getFrequency(), experimentModel
              .getAmplitude(), experimentModel.getOffset());
        }
      }

      // Throttle GUI updates at some FPS rate.
      long duration = System.currentTimeMillis() - start;
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

          // AnalogOut
          DWF.Waveform dwfWaveform = WaveformUtils.getDWFWaveform(experimentModel.getWaveform());
          // TODO if this is a good machanism, use it everywhere else. Perhaps create an exception subclass and catch it higher up.
          if (!dwfProxy.getDwf().startWave(DWF.WAVEFORM_CHANNEL_1, dwfWaveform, experimentModel.getFrequency(), experimentModel.getAmplitude(), experimentModel.getOffset(), 50)) {
            throw new RuntimeException(dwfProxy.getDwf().FDwfGetLastErrorMsg());
          }

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
          plotController.udpateWaveformChart(experimentModel.getWaveformTimeData(), experimentModel.getWaveformAmplitudeData(), experimentModel.getAmplitude(), experimentModel.getFrequency(),
              experimentModel.getOffset());
        }
        break;
      case AppModel.EVENT_FREQUENCY_UPDATE:

        // a special case when the frequency is changed. Not only does the analog out need to change (above), the capture frequency rate must also be changed.

        if (dwfProxy.isAD2Capturing()) {

          // Analog In
          double sampleFrequency = (double) experimentModel.getFrequency() * HysteresisPreferences.CAPTURE_BUFFER_SIZE / HysteresisPreferences.CAPTURE_PERIOD_COUNT;
          dwfProxy.getDwf().startAnalogCaptureBothChannels(sampleFrequency, HysteresisPreferences.CAPTURE_BUFFER_SIZE, AcquisitionMode.ScanShift);
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
