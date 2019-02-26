/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
 *
 * <p>This package also includes various components that are not part of Memristor-Discovery itself:
 *
 * <p>* `Multibit`: Copyright 2011 multibit.org, MIT License * `SteelCheckBox`: Copyright 2012
 * Gerrit, BSD license
 *
 * <p>Knowm, Inc. holds copyright and/or sufficient licenses to all components of the
 * Memristor-Discovery package, and therefore can grant, at its sole discretion, the ability for
 * companies, individuals, or organizations to create proprietary or open source (even if not GPL)
 * modules which may be dynamically linked at runtime with the portions of Memristor-Discovery which
 * fall under our copyright/license umbrella, or are distributed under more flexible licenses than
 * GPL.
 *
 * <p>The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * <p>If you have any questions regarding our licensing policy, please contact us at
 * `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentResultsPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.View;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.result.ResultPanel;
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.AcquisitionMode;

public class HysteresisExperiment extends Experiment {

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ControlPanel controlPanel;
  private final ResultPanel resultPanel;
  private final ResultModel resultModel;
  private final ResultController resultController;

  // SwingWorkers
  private SwingWorker experimentCaptureWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public HysteresisExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    new ControlController(controlPanel, controlModel, dwfProxy);

    resultModel = new ResultModel();
    resultPanel = new ResultPanel();
    resultController = new ResultController(resultPanel, resultModel);
  }

  @Override
  public void addWorkersToButtonEvents() {

    controlPanel
        .getStartStopButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                if (!controlModel.isStartToggled()) {

                  controlModel.setStartToggled(true);
                  controlPanel.getStartStopButton().setText("Stop");

                  // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
                  experimentCaptureWorker = new CaptureWorker();
                  experimentCaptureWorker.execute();
                } else {

                  controlModel.setStartToggled(false);
                  controlPanel.getStartStopButton().setText("Start");

                  // cancel the worker
                  experimentCaptureWorker.cancel(true);
                }
              }
            });
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model
   * is updated. Here, the controller can respond to those events and make sure the corresponding
   * GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    //    System.out.println("evt.getPropertyName() = " + evt.getPropertyName());

    switch (evt.getPropertyName()) {
      case Model.EVENT_WAVEFORM_UPDATE:
        if (controlModel.isStartToggled()) {
          // AnalogOut
          DWF.Waveform dwfWaveform = WaveformUtils.getDWFWaveform(controlModel.getWaveform());
          dwfProxy
              .getDwf()
              .startWave(
                  DWF.WAVEFORM_CHANNEL_1,
                  dwfWaveform,
                  controlModel.getFrequency(),
                  controlModel.getAmplitude(),
                  controlModel.getOffset(),
                  50);
        } else {
          resultPanel.switch2WaveformChart();
          resultController.udpateWaveformChart(
              controlModel.getWaveformTimeData(),
              controlModel.getWaveformAmplitudeData(),
              controlModel.getAmplitude(),
              controlModel.getFrequency(),
              controlModel.getOffset());
        }
        break;
      case Model.EVENT_FREQUENCY_UPDATE:

        // a special case when the frequency is changed. Not only does the analog out need to change
        // (above), the capture frequency rate must also be changed.

        if (controlModel.isStartToggled()) {

          // Analog In
          double sampleFrequency =
              (double) controlModel.getFrequency()
                  * HysteresisPreferences.CAPTURE_BUFFER_SIZE
                  / HysteresisPreferences.CAPTURE_PERIOD_COUNT;
          dwfProxy
              .getDwf()
              .startAnalogCaptureBothChannelsImmediately(
                  sampleFrequency,
                  HysteresisPreferences.CAPTURE_BUFFER_SIZE,
                  AcquisitionMode.ScanShift);
        }
        break;
      default:
        break;
    }
  }

  @Override
  public Model getControlModel() {

    return controlModel;
  }

  @Override
  public View getControlPanel() {

    return controlPanel;
  }

  @Override
  public Model getResultModel() {
    return resultModel;
  }

  @Override
  public ExperimentResultsPanel getResultPanel() {

    return resultPanel;
  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // AnalogOut
      DWF.Waveform dwfWaveform = WaveformUtils.getDWFWaveform(controlModel.getWaveform());
      dwfProxy
          .getDwf()
          .startWave(
              DWF.WAVEFORM_CHANNEL_1,
              dwfWaveform,
              controlModel.getFrequency(),
              controlModel.getAmplitude(),
              controlModel.getOffset(),
              50);

      // Analog In
      double sampleFrequency =
          (double) controlModel.getFrequency()
              * HysteresisPreferences.CAPTURE_BUFFER_SIZE
              / HysteresisPreferences.CAPTURE_PERIOD_COUNT;
      dwfProxy
          .getDwf()
          .startAnalogCaptureBothChannelsImmediately(
              sampleFrequency,
              HysteresisPreferences.CAPTURE_BUFFER_SIZE,
              AcquisitionMode.ScanShift);

      while (!isCancelled()) {

        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        }

        // Read In Data
        byte status = dwfProxy.getDwf().FDwfAnalogInStatus(true);
        // System.out.println("status = " + status);

        int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        // System.out.println("validSamples: " + validSamples);

        if (validSamples > 0) {

          double[] rawdata1 =
              dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
          double[] rawdata2 =
              dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

          if (resultPanel.getCaptureButton().isSelected()) { // Capture

            // Calculate time data
            double[] timeData = new double[rawdata1.length];
            double timeStep =
                1
                    / (double) controlModel.getFrequency()
                    * HysteresisPreferences.CAPTURE_PERIOD_COUNT
                    / HysteresisPreferences.CAPTURE_BUFFER_SIZE;
            for (int i = 0; i < timeData.length; i++) {
              timeData[i] = i * timeStep;
            }
            publish(new double[][] {rawdata1, rawdata2, timeData});
          } else if (resultPanel.getIVButton().isSelected()) { // IV

            // create current data
            double[] current = new double[rawdata2.length];
            double[] voltage = new double[rawdata1.length];
            for (int i = 0; i < current.length; i++) {
              current[i] =
                  rawdata2[i]
                      / controlModel.getSeriesResistance()
                      * HysteresisPreferences.CURRENT_UNIT.getDivisor();
            }
            if (!HysteresisPreferences.IS_VIN) {
              for (int i = 0; i < current.length; i++) {
                voltage[i] = rawdata1[i] - rawdata2[i];
              }
            }
            publish(new double[][] {rawdata1, voltage, current});

          } else { // GV

            double[] conductance = new double[rawdata2.length];
            double[] voltage = new double[rawdata1.length];

            for (int i = 0; i < conductance.length; i++) {

              double I = rawdata2[i] / controlModel.getSeriesResistance();

              double G =
                  I
                      / (rawdata1[i] - rawdata2[i])
                      * HysteresisPreferences.CONDUCTANCE_UNIT.getDivisor();

              G = G < 0 ? 0 : G;

              double ave =
                  (1 - resultModel.getK()) * (resultModel.getAve()) + resultModel.getK() * (G);
              resultModel.setAve(ave);

              conductance[i] = ave;
              voltage[i] = rawdata1[i] - rawdata2[i];
            }

            publish(new double[][] {rawdata1, voltage, conductance});
          }
        }
        // testing the result
        //        if (Math.random() < 0.01) {
        //
        // getControlModel().swingPropertyChangeSupport.firePropertyChange(Model.EVENT_NEW_CONSOLE_LOG, "Hi", "Blah");
        //        }
      }

      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      long start = System.nanoTime();

      if (controlModel.isStartToggled()) {

        double[][] newestChunk = chunks.get(chunks.size() - 1);

        if (resultPanel.getCaptureButton().isSelected()) {
          resultController.udpateVtChartData(
              newestChunk[0],
              newestChunk[1],
              newestChunk[2],
              controlModel.getFrequency(),
              controlModel.getAmplitude(),
              controlModel.getOffset());
          resultPanel.switch2CaptureChart();
        } else if (resultPanel.getIVButton().isSelected()) {
          resultController.udpateIVChartData(
              newestChunk[0],
              newestChunk[1],
              newestChunk[2],
              controlModel.getFrequency(),
              controlModel.getAmplitude(),
              controlModel.getOffset());
          resultPanel.switch2IVChart();
        } else {
          resultController.updateGVChartData(
              newestChunk[0],
              newestChunk[1],
              newestChunk[2],
              controlModel.getFrequency(),
              controlModel.getAmplitude(),
              controlModel.getOffset());
          resultPanel.switch2GVChart();
        }
      }

      // Throttle GUI updates at some FPS rate.
      long duration = (System.nanoTime() - start) / 1_000_000;
      try {
        Thread.sleep(Util.SLEEP_TIME - duration);
      } catch (InterruptedException e) {
      }
    }
  }
}
