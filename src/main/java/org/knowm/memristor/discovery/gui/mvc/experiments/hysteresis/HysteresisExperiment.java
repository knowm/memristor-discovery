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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.AcquisitionMode;

public class HysteresisExperiment extends Experiment implements PropertyChangeListener {

  private final ControlModel controlModel = new ControlModel();
  private final ControlPanel controlPanel;

  private final PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public HysteresisExperiment(DWFProxy dwfProxy, Container mainFrameContainer) {

    super(dwfProxy, mainFrameContainer);

    controlPanel = new ControlPanel();
    JScrollPane jScrollPane = new JScrollPane(controlPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane.setBorder(createEmptyBorder());
    mainFrameContainer.add(jScrollPane, BorderLayout.WEST);

    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    mainFrameContainer.add(plotPanel, BorderLayout.CENTER);

    new ControlController(controlPanel, controlModel, dwfProxy);

    // register this as the listener of the model
    controlModel.addListener(this);

    // trigger plot of waveform
    PropertyChangeEvent evt = new PropertyChangeEvent(this, ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);
  }

  @Override
  public void doCreateAndShowGUI() {

  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // AnalogOut
      DWF.Waveform dwfWaveform = WaveformUtils.getDWFWaveform(controlModel.getWaveform());
      dwfProxy.getDwf().startWave(DWF.WAVEFORM_CHANNEL_1, dwfWaveform, controlModel.getFrequency(), controlModel.getAmplitude(), controlModel.getOffset(), 50);

      // Analog In
      double sampleFrequency = (double) controlModel.getFrequency() * HysteresisPreferences.CAPTURE_BUFFER_SIZE / HysteresisPreferences.CAPTURE_PERIOD_COUNT;
      dwfProxy.getDwf().startAnalogCaptureBothChannelsImmediately(sampleFrequency, HysteresisPreferences.CAPTURE_BUFFER_SIZE, AcquisitionMode.ScanShift);

      dwfProxy.setAD2Capturing(true);
      while (!isCancelled()) {

        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
          dwfProxy.setAD2Capturing(false);
        }

        // Read In Data
        byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
        // System.out.println("status = " + status);

        int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        // System.out.println("validSamples: " + validSamples);

        if (validSamples > 0) {

          double[] rawdata1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
          double[] rawdata2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

          if (plotPanel.getCaptureButton().isSelected()) { // Capture

            // Calculate time data
            double[] timeData = new double[rawdata1.length];
            double timeStep = 1 / (double) controlModel.getFrequency() * HysteresisPreferences.CAPTURE_PERIOD_COUNT / HysteresisPreferences.CAPTURE_BUFFER_SIZE;
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
              current[i] = rawdata2[i] / controlModel.getSeriesResistance() * HysteresisPreferences.CURRENT_UNIT.getDivisor();
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

              double I = rawdata2[i] / controlModel.getSeriesResistance();

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

      if (controlModel.isStartToggled()) {

        double[][] newestChunk = chunks.get(chunks.size() - 1);

        if (plotPanel.getCaptureButton().isSelected()) {
          plotController.udpateVtChartData(newestChunk[0], newestChunk[1], newestChunk[2], controlModel.getFrequency(), controlModel
              .getAmplitude(), controlModel.getOffset());
          plotPanel.switch2CaptureChart();
        }
        else if (plotPanel.getIVButton().isSelected()) {
          plotController.udpateIVChartData(newestChunk[0], newestChunk[1], newestChunk[2], controlModel.getFrequency(), controlModel
              .getAmplitude(), controlModel.getOffset());
          plotPanel.switch2IVChart();
        }
        else {
          plotController.updateGVChartData(newestChunk[0], newestChunk[1], newestChunk[2], controlModel.getFrequency(), controlModel
              .getAmplitude(), controlModel.getOffset());
          plotPanel.switch2GVChart();
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

      case ExperimentControlModel.EVENT_WAVEFORM_UPDATE:

        if (dwfProxy.isAD2Capturing()) {

          // AnalogOut
          DWF.Waveform dwfWaveform = WaveformUtils.getDWFWaveform(controlModel.getWaveform());
          dwfProxy.getDwf().startWave(DWF.WAVEFORM_CHANNEL_1, dwfWaveform, controlModel.getFrequency(), controlModel.getAmplitude(), controlModel.getOffset(), 50);

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
          plotController.udpateWaveformChart(controlModel.getWaveformTimeData(), controlModel.getWaveformAmplitudeData(), controlModel.getAmplitude(), controlModel.getFrequency(),
              controlModel.getOffset());
        }
        break;
      case ExperimentControlModel.EVENT_FREQUENCY_UPDATE:

        // a special case when the frequency is changed. Not only does the analog out need to change (above), the capture frequency rate must also be changed.

        if (dwfProxy.isAD2Capturing()) {

          // Analog In
          double sampleFrequency = (double) controlModel.getFrequency() * HysteresisPreferences.CAPTURE_BUFFER_SIZE / HysteresisPreferences.CAPTURE_PERIOD_COUNT;
          dwfProxy.getDwf().startAnalogCaptureBothChannelsImmediately(sampleFrequency, HysteresisPreferences.CAPTURE_BUFFER_SIZE, AcquisitionMode.ScanShift);
        }
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
