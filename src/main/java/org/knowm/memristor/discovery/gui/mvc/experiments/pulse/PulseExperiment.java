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
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.AveMaxMinVar;
import org.knowm.memristor.discovery.core.ExpRunAve;
import org.knowm.memristor.discovery.core.PostProcessDataUtils;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.memristor.discovery.core.rc_engine.RC_ResistanceComputer;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.ConductancePreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.result.ResultPanel;
import org.knowm.waveforms4j.DWF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulseExperiment extends Experiment {

  private static final Logger LOGGER = LoggerFactory.getLogger(PulseExperiment.class);

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ResultModel resultModel;
  private final ResultController resultController;
  private boolean initialPulseTrainCaptured = false;
  private ControlPanel controlPanel;
  private ResultPanel resultPanel;

  // SwingWorkers
  private SwingWorker experimentCaptureWorker;

  private float readPulseCalibration =
      0.0f; // calibration may not work. This adjusts the readpulse magnitude so that
  // it equals .1V.

  // Bug fix for "bad" AD2 boards and parasitic RC effects
  private RC_ResistanceComputer rcComputer;
  AD2BugCalibrationValues ad2BugCalibrationValues = new AD2BugCalibrationValues();
  private ExpRunAve readPulseAve;

  // TODO move these to control model with GUI and preferences
  // Read-pulse properties
  private final double readPulseWidth = 10; // us
  private double readPulseAmplitude = .1; // V
  //  private double parasiticReadCapacitance = 140E-12;

  // plot data

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public PulseExperiment(DWFProxy dwfProxy, Container mainFrameContainer, int boardVersion) {

    super(dwfProxy, mainFrameContainer, boardVersion);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel();

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);
    if (boardVersion == 2) {
      readPulseAmplitude = -readPulseAmplitude;
    }
  }

  @Override
  public void doCreateAndShowGUI() {

    // trigger waveform update event
    PropertyChangeEvent evt =
        new PropertyChangeEvent(this, Model.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);

    // when the control panel is manipulated, we need to communicate the changes to
    // the results
    // panel
    getControlModel().addListener(this);
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

  @Override
  public Model getControlModel() {

    return controlModel;
  }

  @Override
  public ControlView getControlPanel() {

    return controlPanel;
  }

  @Override
  public Model getResultModel() {
    return resultModel;
  }

  @Override
  public JPanel getResultPanel() {

    return resultPanel;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    switch (propName) {
      case Model.EVENT_WAVEFORM_UPDATE:
        resultPanel.switch2WaveformChart();
        resultController.updateWaveformChart(
            controlModel.getWaveformTimeData(),
            controlModel.getWaveformAmplitudeData(),
            controlModel.getAmplitude(),
            controlModel.getPulseWidth());

        break;

      default:
        break;
    }
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new PulsePreferences();
  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // ////////////////////////////////
      // Analog In /////////////////
      // ////////////////////////////////

      int samplesPerPulse = 200;

      double sampleFrequency = controlModel.getCalculatedFrequency() * samplesPerPulse;
      boolean isScale2V = Math.abs(controlModel.getAmplitude()) <= 2.5;

      int bufferSize = samplesPerPulse * controlModel.getPulseNumber() + samplesPerPulse;

      dwfProxy
          .getDwf()
          .startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(
              DWF.WAVEFORM_CHANNEL_1, sampleFrequency, bufferSize, isScale2V);

      dwfProxy.waitUntilArmed();

      // ////////////////////////////////
      // Pulse Out /////////////////
      // ////////////////////////////////

      double[] customWaveform;
      if (boardVersion == 2) {
        customWaveform =
            WaveformUtils.generateCustomPulse(
                controlModel.getWaveform(),
                -controlModel.getAmplitude(),
                controlModel.getPulseWidth(),
                controlModel.getDutyCycle());
      } else {
        customWaveform =
            WaveformUtils.generateCustomPulse(
                controlModel.getWaveform(),
                controlModel.getAmplitude(),
                controlModel.getPulseWidth(),
                controlModel.getDutyCycle());
      }

      dwfProxy
          .getDwf()
          .startCustomPulseTrain(
              DWF.WAVEFORM_CHANNEL_1,
              controlModel.getCalculatedFrequency(),
              0,
              controlModel.getPulseNumber(),
              customWaveform);

      // ////////////////////////////////

      // Read In Data
      boolean success =
          dwfProxy.capturePulseData(
              controlModel.getCalculatedFrequency(), controlModel.getPulseNumber());
      if (!success) {
        // Stop Analog In and Out
        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        controlPanel.getStartStopButton().doClick();
        return false;
      }

      // Get Raw Data from Oscilloscope
      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 =
          dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 =
          dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

      // Stop Analog In and Out
      dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
      dwfProxy.getDwf().stopAnalogCaptureBothChannels();

      // /////////////////////////
      // Create Chart Data //////
      // /////////////////////////

      double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.05, 10);
      double[] V1Trimmed = trimmedRawData[0];
      double[] V2Trimmed = trimmedRawData[1];
      double[] VMemristor = PostProcessDataUtils.getV1MinusV2(V1Trimmed, V2Trimmed);
      double[] timeData;
      int bufferLength;
      double timeStep;

      if (boardVersion == 2) {
        bufferLength = V1Trimmed.length;

        VMemristor = PostProcessDataUtils.invert(V1Trimmed);

        // create time data
        timeData = new double[bufferLength];
        timeStep = 1.0 / sampleFrequency * PulsePreferences.TIME_UNIT.getDivisor();
        for (int i = 0; i < bufferLength; i++) {
          timeData[i] = i * timeStep;
        }

        // create current data
        double[] current = new double[bufferLength];
        for (int i = 0; i < bufferLength; i++) {
          current[i] =
              (V1Trimmed[i] - V2Trimmed[i])
                  / controlModel.getSeriesResistance()
                  * PulsePreferences.CURRENT_UNIT.getDivisor();
        }

        // create conductance data
        double[] conductance = new double[bufferLength];
        for (int i = 0; i < bufferLength; i++) {

          double I = (V1Trimmed[i] - V2Trimmed[i]) / controlModel.getSeriesResistance();
          double G = I / VMemristor[i] * PulsePreferences.CONDUCTANCE_UNIT.getDivisor();
          G = G < 0 ? 0 : G;
          conductance[i] = G;
        }
        publish(
            new double[][] {
              timeData, V1Trimmed, V2Trimmed, VMemristor, current, conductance, null
            });
      } else {
        bufferLength = V1Trimmed.length;

        // create time data
        timeData = new double[bufferLength];
        timeStep = 1.0 / sampleFrequency * PulsePreferences.TIME_UNIT.getDivisor();
        for (int i = 0; i < bufferLength; i++) {
          timeData[i] = i * timeStep;
        }

        // create current data
        double[] current = new double[bufferLength];
        for (int i = 0; i < bufferLength; i++) {
          current[i] =
              V2Trimmed[i]
                  / controlModel.getSeriesResistance()
                  * PulsePreferences.CURRENT_UNIT.getDivisor();
        }

        //        // create conductance data
        //        double[] conductance = new double[bufferLength];
        //        for (int i = 0; i < bufferLength; i++) {
        //
        //          double I = V2Trimmed[i] / controlModel.getSeriesResistance();
        //          double G =
        //              I / (V1Trimmed[i] - V2Trimmed[i]) *
        // PulsePreferences.CONDUCTANCE_UNIT.getDivisor();
        //          G = G < 0 ? 0 : G;
        //          conductance[i] = G;
        //        }
        publish(new double[][] {timeData, V1Trimmed, V2Trimmed, VMemristor, current, null, null});
      }

      while (!initialPulseTrainCaptured) {
        // System.out.println("Waiting...");
        Thread.sleep(50);
      }

      // ////////////////////////////////
      // READ PULSES /////////////////
      // ////////////////////////////////

      while (!isCancelled()) {

        try {
          Thread.sleep(controlModel.getSampleRate() * 1000);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        }

        // ////////////////////////////////
        // Analog In /////////////////
        // ////////////////////////////////

        samplesPerPulse = 2048;
        double f = 1 / (readPulseWidth / 1_000_000 * 2);
        sampleFrequency = f * samplesPerPulse;

        //        double f = 1 / (pulseWidthInNS * 1E-9);
        bufferSize = 4096; // samplesPerPulse * 2;
        //         sampleFrequency = 1 / ((pulseWidthInNS * 1E-9) / 2048);

        dwfProxy
            .getDwf()
            .startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(
                DWF.WAVEFORM_CHANNEL_1, sampleFrequency, bufferSize, true);
        dwfProxy.waitUntilArmed();

        //////////////////////////////////
        // Pulse Out /////////////////
        //////////////////////////////////

        // read pulse approximately: 0.1 V, 10 us pulse width

        customWaveform =
            WaveformUtils.generateCustomWaveform(
                Waveform.Square, readPulseAmplitude + readPulseCalibration, f);

        dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, f, 0, 1, customWaveform);

        // Read In Data
        success = dwfProxy.capturePulseData(f, 1);

        if (!success) {

          // Stop Analog In and Out
          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
          controlPanel.getStartStopButton().doClick();
          return false;
        } else {

          // Get Raw Data from Oscilloscope
          validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
          v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
          v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

          // /////////////////////////
          // Create Chart Data //////
          // /////////////////////////

          //          trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0, 10);
          //          V1Trimmed = trimmedRawData[0];
          //          V2Trimmed = trimmedRawData[1];

          if (boardVersion == 2) {
            VMemristor = PostProcessDataUtils.invert(v1);
          } else {
            VMemristor = PostProcessDataUtils.getV1MinusV2(v1, v2);
          }

          bufferLength = v1.length;

          // create time data
          timeData = new double[bufferLength];
          timeStep = 1.0 / sampleFrequency * PulsePreferences.TIME_UNIT.getDivisor();
          for (int i = 0; i < bufferLength; i++) {
            timeData[i] = i * timeStep;
          }

          updateReadPulseVoltageAndOffsets(v2, v1);
          initResistanceComputer();
          double resistance = rcComputer.getRFromV(ad2BugCalibrationValues.getReadPulseVoltage());

          double[] conductanceAve =
              new double[] {
                (1 / resistance) * ConductancePreferences.CONDUCTANCE_UNIT.getDivisor()
              };

          if (boardVersion == 2) {
            publish(new double[][] {timeData, v1, v2, VMemristor, null, null, conductanceAve});
          } else {
            publish(new double[][] {timeData, v1, v2, VMemristor, null, null, conductanceAve});
          }
        }
        // Stop Analog In and Out
        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
      }
      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      double[][] newestChunk = chunks.get(chunks.size() - 1);

      if (newestChunk[6] == null) {
        initialPulseTrainCaptured = true;

        resultController.updateCaptureChartData(
            newestChunk[0],
            newestChunk[1],
            newestChunk[2],
            newestChunk[3],
            controlModel.getPulseWidth(),
            controlModel.getAmplitude());
        resultController.updateIVChartData(
            newestChunk[0],
            newestChunk[4],
            controlModel.getPulseWidth(),
            controlModel.getAmplitude());

        if (resultPanel.getCaptureButton().isSelected()) {
          resultPanel.switch2CaptureChart();
          resultController.repaintVtChart();
        } else if (resultPanel.getIVButton().isSelected()) {
          resultPanel.switch2IVChart();
          resultController.repaintItChart();
        } else {
          resultPanel.switchReadPulseCaptureChart();
          resultController.repaintReadPulseCaptureChart();
        }

      } else {

        // update read pulse capture chart....

        resultController.updateReadPulseCaptureChartData(
            newestChunk[0],
            newestChunk[1],
            newestChunk[2],
            newestChunk[3],
            controlModel.getPulseWidth(),
            controlModel.getAmplitude());
        resultController.repaintReadPulseCaptureChart();

        // update G chart
        controlModel.setLastG(newestChunk[6][0]);
        resultController.updateGChartData(
            controlModel.getLastG(), controlModel.getLastR(), controlModel.getLastRAsString());
        resultController.repaintGChart();

        controlModel.updateEnergyData();
        controlPanel.updateEnergyGUI(
            controlModel.getAmplitude(),
            controlModel.getAppliedCurrent(),
            controlModel.getAppliedEnergy());
      }
    }
  }

  private void initResistanceComputer() {

    double pulseAmp =
        ad2BugCalibrationValues.getReadPulseOffset()
            - ad2BugCalibrationValues.getReadPulseZeroOffset();
    double pulseWidth = readPulseWidth / 1_000_000; // in microseconds

    // if the measured applied pulse amplitude differs by more than 5%,
    // re-initialized the RC Computer. Use exp run average.
    if (rcComputer != null) {
      double dpA = (pulseAmp - rcComputer.getReadPulseAmplitude()) / pulseAmp;
      // System.out.println("dpA=" + dpA);
      if (Math.abs(dpA) < .05) {
        return;
      } else {
        LOGGER.warn(
            "Measured pulse amplitude has drifted by "
                + dpA
                + ". RC Computer re-initialized. If this is frequent there is a noise problem.");
      }
    }

    long t = System.currentTimeMillis();
    this.rcComputer =
        new RC_ResistanceComputer(
            boardVersion,
            readPulseAve.getValue(),
            pulseWidth,
            controlModel.getSeriesResistance(),
            ad2BugCalibrationValues.getReadPulseZeroOffset(),
            ad2BugCalibrationValues.getReadPulseInitialVoltage());

    LOGGER.info(
        "RC_Resistance Computer was initialized in " + (System.currentTimeMillis() - t) + " ms");
  }

  private void updateReadPulseVoltageAndOffsets(double[] v2, double[] v1) {

    try {
      double[] xz = Arrays.copyOfRange(v2, 3097, 4097);
      AveMaxMinVar statz = new AveMaxMinVar(xz);
      ad2BugCalibrationValues.setReadPulseZeroOffset(statz.getAve());

      double[] x = Arrays.copyOfRange(v2, 960, 1020);
      AveMaxMinVar stat = new AveMaxMinVar(x);
      ad2BugCalibrationValues.setReadPulseOffset(stat.getAve());

      double[] xv = Arrays.copyOfRange(v1, 1010, 1020);
      AveMaxMinVar statv = new AveMaxMinVar(xv);
      ad2BugCalibrationValues.setReadPulseVoltage(statv.getAve());

      double[] xvi =
          Arrays.copyOfRange(
              v1, 3097,
              4097); // take from the end because the start can get clipped for big pulses.
      AveMaxMinVar statvi = new AveMaxMinVar(xvi);
      ad2BugCalibrationValues.setReadPulseInitialVoltage(statvi.getAve());

      double pulseAmp =
          ad2BugCalibrationValues.getReadPulseOffset()
              - ad2BugCalibrationValues.getReadPulseZeroOffset();
      if (readPulseAve == null) {
        readPulseAve = new ExpRunAve((float) pulseAmp, .1f);
      } else {
        readPulseAve.update((float) pulseAmp);
      }

//      System.out.println("ad2BugCalibrationValues = " + ad2BugCalibrationValues);

    } catch (Exception e) {
      LOGGER.error("", e);
    }
  }
}
