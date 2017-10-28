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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.consol.ConsolPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control.ControlPanel;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.memristor.discovery.utils.gpio.MuxController;
import org.knowm.memristor.discovery.utils.gpio.MuxController.Destination;
import org.knowm.waveforms4j.DWF;

public class BoardCheckExperiment extends Experiment {

  private SwingWorker meminlineTestWorker;
  private SwingWorker muxTestWorker;
  private SwingWorker switchTestWorker;
  private SwingWorker clearConsolWorker;

  private final ControlModel controlModel = new ControlModel();

  private final static float V_MUX_TEST = 1.2345f;// the voltage used to test the waveform generators/muxes
  private final static float V_SWITCH_RESISTANCE = 1;// the voltage used to test the switch resistance

  private final static float V_MEMINLINE_READ = .1f;
  private final static float V_MEMINLINE_WRITE = 1.5f;
  private final static float V_MEMINLINE_RESET = -1.5f;
  private final static float V_MEMINLINE_HARD_RESET = -2.0f;

  private float MEMINLINE_MIN_Q = 2;// minimum ratio between erase/write resistance
  private float MEMINLINE_MIN_R = 10;// if all state are below this (kiloohms), its stuck low
  private float MEMINLINE_MAX_R = 100;// if all state are above this (kilohms), its stuck low
  private float MEMINLINE_MIN_SWITCH_OFF = 1000;// if switch is below this resistance (kOhm) when OFF then its a bad switch

  /*
   * NOTE
   */

  private int meminline_numFailed = 0;

  private final static float R_CALIBRATE = 0;// Line trace resistance, AD2 Calibration.

  private int COL_WIDTH = 12;

  private DecimalFormat percentFormat = new DecimalFormat("0.00 %");
  private DecimalFormat ohmFormat = new DecimalFormat("0.00 kΩ");

  private ControlPanel controlPanel;
  private ConsolPanel consolPanel;
  private final MuxController muxController;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public BoardCheckExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    consolPanel = new ConsolPanel();
    muxController = new MuxController();
  }

  /*
   * Here action listeners are attached to the widgets in the control panel and mapped to
   * a worker, also defined here in the experiment.
   */
  @Override
  public void doCreateAndShowGUI() {

    controlPanel.meminlineTestButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // System.out.println("Meminline button was clicked. e=" + e.toString());

        meminlineTestWorker = new MeminlineTestWorker();
        meminlineTestWorker.execute();

      }
    });

    controlPanel.muxTestButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // System.out.println("Meminline button was clicked. e=" + e.toString());

        muxTestWorker = new MuxDiagnosticWorker();
        muxTestWorker.execute();

      }
    });

    controlPanel.switchTestButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // System.out.println("Meminline button was clicked. e=" + e.toString());

        switchTestWorker = new SwitchDiagnosticWorker();
        switchTestWorker.execute();

      }
    });

    controlPanel.clearConsolButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // System.out.println("Meminline button was clicked. e=" + e.toString());

        clearConsolWorker = new ClearConsolWorker();
        clearConsolWorker.execute();

      }
    });

  }

  public float[] measureAllSwitchResistances(float readVoltage, int sleep, boolean configureMux) {

    if (configureMux) {
      muxController.setW1(Destination.A);
      muxController.setW2(Destination.OUT);
      muxController.setScope1(Destination.A);
      muxController.setScope2(Destination.B);
      dwfProxy.setUpper8IOStates(muxController.getGPIOConfig());
    }

    // dwfProxy.setAllIOStates(0b0000_0000);// turn off all switches.

    float[] r_array = new float[9];
    r_array[0] = getSwitchResistancekOhm(readVoltage, DWF.WAVEFORM_CHANNEL_1);// all switches off

    for (int i = 0; i < 8; i++) {

      // try {
      // Thread.sleep(sleep);
      // } catch (InterruptedException e) {
      // }

      dwfProxy.update2DigitalIOStatesAtOnce(i, true);

      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {

      }

      r_array[i + 1] = getSwitchResistancekOhm(readVoltage, DWF.WAVEFORM_CHANNEL_1);

      dwfProxy.update2DigitalIOStatesAtOnce(i, false);

    }

    return r_array;

  }

  private float getSwitchResistancekOhm(float readVoltage, int dWFWaveformChannel) {

    float[] vMeasure = getScopesAverageVoltage(readVoltage, dWFWaveformChannel);
    /*
     * Vy/Rseries=I
     * Vdrop/I=Rswitch
     * (Vin-Vy)/I=Rswitch
     */

    // System.out.println("measurevoltage: " + Arrays.toString(vMeasure));
    // System.out.println("series resistance: " + controlModel.getSeriesResistance());
    // BoardCheckPreferences.SERIES_R_INIT_KEY, Integer.parseInt(seriesResistorTextField.getText())
    float seriesResistance = controlModel.seriesResistance;
    // System.out.println("seriesResistance: " + seriesResistance);

    float I = Math.abs(vMeasure[1] / seriesResistance);
    float rSwitch = (Math.abs(vMeasure[0] - vMeasure[1]) / I) - 2 * ExperimentPreferences.R_SWITCH - R_CALIBRATE;

    return rSwitch / 1000;// to kilohms
  }

  public float[] measureMuxDeviation(int dWFWaveformChannel, Destination destination) {

    muxController.setScope1(destination);
    muxController.setScope2(destination);

    if (dWFWaveformChannel == DWF.WAVEFORM_CHANNEL_1) {
      muxController.setW1(destination);
      muxController.setW2(Destination.OUT);
    }
    else if (dWFWaveformChannel == DWF.WAVEFORM_CHANNEL_2) {
      muxController.setW1(Destination.OUT);
      muxController.setW2(destination);
    }

    dwfProxy.setUpper8IOStates(muxController.getGPIOConfig());

    float[] scopeReading = getScopesAverageVoltage(V_MUX_TEST, dWFWaveformChannel);

    return new float[] { Math.abs(scopeReading[0] - V_MUX_TEST), Math.abs(scopeReading[1] - V_MUX_TEST) };

  }

  private float[] getScopesAverageVoltage(float readVoltage, int dWFWaveformChannel) {

    int samplesPerPulse = 300;
    int sampleFrequency = 50;
    int samples = sampleFrequency * samplesPerPulse;

    // consolPanel.println("Starting Pulse Measurment");

    dwfProxy.getDwf().startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(dWFWaveformChannel, samples, samplesPerPulse);
    dwfProxy.waitUntilArmed();
    double[] pulse = WaveformUtils.generateCustomWaveform(Waveform.Square, readVoltage, sampleFrequency);
    dwfProxy.getDwf().startCustomPulseTrain(dWFWaveformChannel, sampleFrequency, 0, 1, pulse);
    boolean success = dwfProxy.capturePulseData(samples, 1);
    if (success) {
      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

      // System.out.println("v1: " + Arrays.toString(v1));
      // System.out.println("v2: " + Arrays.toString(v2));

      /*
       * The output is a pulse with the last half of the measurement data at ground.
       * Taking the first 25% insures we get the pulse amplitude.
       */

      // average over the first 25%.

      float aveScope1 = 0;
      float aveScope2 = 0;
      for (int i = 0; i < v1.length / 4; i++) {
        aveScope1 += v1[i];
        aveScope2 += v2[i];
      }

      aveScope1 /= v1.length / 4;
      aveScope2 /= v2.length / 4;

      return new float[] { aveScope1, aveScope2 };

    }
    else {
      consolPanel.println("Pulse capture failed. This is usually a triggering issue.");
      return null;
    }

  }

  private class SwitchDiagnosticWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      consolPanel.println("");
      consolPanel.println("Testing Board Switches(assuming 5kΩ resistors in socket)");
      consolPanel.println("");
      int sleep = 250;// so the tester can see the LEDs blink in the V1 board.

      float[] r = measureAllSwitchResistances(V_SWITCH_RESISTANCE, sleep, true);

      boolean pass = true;
      for (int i = 0; i < r.length; i++) {

        StringBuilder b = new StringBuilder();

        if (i == 0) {
          b.append(" ALL OFF: ");
        }
        else {
          b.append("SWITCH " + i + ": ");
        }

        if (r[i] < 0) {
          b.append("INF");
        }
        else {
          b.append(ohmFormat.format(r[i]));
        }

        if (i > 0 && !isWithenPercentPercent(r[i], 5.0f, .05f)) {
          b.append("  FAILED!");
          pass = false;
        }
        else if (i == 0) {

          if (r[i] < 1000) {
            b.append("  FAILED!");
            pass = false;
          }

        }

        if (i > 0) {
          b.append(" (" + percentFormat.format((r[i] - 5.0f) / 5.0f) + ")");
        }

        consolPanel.println(b.toString());

      }

      consolPanel.println("");
      if (pass) {
        consolPanel.println("PASS");
      }
      else {
        consolPanel.println("FAIL!");
      }

      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

      // plotController.updateYChartData(chunks.get(0), chunks.get(chunks.size() - 1));
      // plotController.repaintYChart();
    }
  }

  private boolean isWithenPercentPercent(float value, float reference, float percent) {

    float p = Math.abs(value - reference) / reference;

    System.out.println("p=" + p);
    System.out.println("value=" + value);
    System.out.println("reference=" + reference);

    return p <= percent;
  }

  private class MuxDiagnosticWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      if (dwfProxy.isV1Board()) {

        // TEST MUXES

        /*
         * MUX DIO PINOUT
         * Order ==> W2, W1, 2+, 1+
         * 00 E
         * 10 Y
         * 01 A
         * 11 B
         */
        consolPanel.println("");
        consolPanel.println("Testing 1-4 Board Muxes");
        consolPanel.println("");

        /*
         * Procedure:
         * 1) Route W1 to A.
         * 2) Route 1+ and 2+ to A.
         * 3) Drive 1.234 Volts DC on W1.
         * 4) Measure 1+ and 2+.
         * 5) TEST: Is 1+ and 2+ equal to 1.234 within p percent?
         * If both fail, possibly bad W1 mux. Test with W2.
         * If one or the other fail, its a bad scope mux.
         */

        consolPanel.println("            Scope 1+       Scope 2+      ");

        boolean pass = true;
        float[] deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.A);
        consolPanel.println("W1-->A      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        if (deviations[0] > .02 | deviations[1] > .02) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.B);
        consolPanel.println("W1-->B      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        if (deviations[0] > .02 | deviations[1] > .02) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.Y);
        consolPanel.println("W1-->Y      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        if (deviations[0] > .02 | deviations[1] > .02) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.A);
        consolPanel.println("W2-->A      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        if (deviations[0] > .02 | deviations[1] > .02) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.B);
        consolPanel.println("W2-->B      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        if (deviations[0] > .02 | deviations[1] > .02) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.Y);
        consolPanel.println("W2-->Y      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        if (deviations[0] > .02 | deviations[1] > .02) {
          pass = false;
        }

        consolPanel.println("");

        if (pass) {
          consolPanel.println("PASS");
        }
        else {
          consolPanel.println("FAIL!");
        }

      }

      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

      // plotController.updateYChartData(chunks.get(0), chunks.get(chunks.size() - 1));
      // plotController.repaintYChart();
    }
  }

  private class ClearConsolWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      consolPanel.clear();
      return true;
    }
  }

  private class MeminlineTestWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // if its a v1 board, we must set the MUXes

      if (dwfProxy.isV1Board()) {
        MuxController muxController = new MuxController();
        dwfProxy.setUpper8IOStates(muxController.getGPIOConfig());// default configuration is for series resistor measurment.
      }

      consolPanel.println("");
      consolPanel.println("Mem-Inline Chip Test");
      consolPanel.println("");

      StringBuilder b = new StringBuilder();
      b.append("            ");
      for (int i = 0; i < 9; i++) {
        String w = "ALL OFF";
        if (i > 0) {
          w = "S" + i;
        }
        appendWhiteSpace(w, b, COL_WIDTH + 1);
      }

      consolPanel.println(b.toString());

      float[][] reads = new float[3][9];
      measureAllSwitchResistances(V_MEMINLINE_WRITE, 0, true);// first call to measureAllSwitchResistances will set the muxes.
      Thread.sleep(50);

      measureAllSwitchResistances(V_MEMINLINE_RESET, 0, false);
      Thread.sleep(50);

      reads[0] = measureAllSwitchResistances(V_MEMINLINE_READ, 0, false);
      Thread.sleep(50);
      consolPanel.println(formatResistanceArray("ERASE       ", reads[0]));

      measureAllSwitchResistances(V_MEMINLINE_WRITE, 0, false);
      Thread.sleep(50);

      reads[1] = measureAllSwitchResistances(V_MEMINLINE_READ, 0, false);
      Thread.sleep(50);
      consolPanel.println(formatResistanceArray("WRITE       ", reads[1]));
      measureAllSwitchResistances(V_MEMINLINE_RESET, 0, false);
      Thread.sleep(50);

      reads[2] = measureAllSwitchResistances(V_MEMINLINE_READ, 0, false);
      Thread.sleep(50);
      consolPanel.println(formatResistanceArray("ERASE2      ", reads[2]));

      consolPanel.println("RESULT      " + verifyMemInlineReads(reads));
      consolPanel.println("");

      if (meminline_numFailed == 0) {
        consolPanel.println("TIER 1");
      }
      else if (meminline_numFailed == 1) {
        consolPanel.println("TIER 2");
      }
      else if (meminline_numFailed <= 4) {
        consolPanel.println("BURN AND LEARN");
      }
      else {
        consolPanel.println("REJECT");
      }

      return true;
    }

    private String verifyMemInlineReads(float[][] reads) {

      // System.out.println("WTF MATE?");
      meminline_numFailed = 0;
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < reads[0].length; i++) {
        // System.out.println("WTF MATE 2?");
        String testResult = "✓";
        if (i == 0) {// this is all switches off. R1, R2 and R3 should all be over 10mOhm
          if (reads[0][0] < MEMINLINE_MIN_SWITCH_OFF) {// should be in high resistance state.
            testResult = "SWITCHES FAILED!";
            appendWhiteSpace(testResult, b, COL_WIDTH + 1);
            break;
          }
        }
        else {// memristor
          // System.out.println("WTF MATE 3?");

          float q1 = reads[0][i] / reads[1][i];
          float q2 = reads[2][i] / reads[1][i];

          // System.out.println("WTF MATE 4?");
          System.out.println("q1=" + q1);
          System.out.println("q2=" + q2);

          if (reads[0][i] < MEMINLINE_MIN_R && reads[1][i] < MEMINLINE_MIN_R && reads[2][i] < MEMINLINE_MIN_R) {
            testResult = "X [STK LOW]";
            meminline_numFailed++;
          }
          else if (reads[0][i] > MEMINLINE_MAX_R && reads[1][i] > MEMINLINE_MAX_R && reads[2][i] > MEMINLINE_MAX_R) {
            testResult = "X [STK HIGH]";
            meminline_numFailed++;
          }
          else if (q1 < MEMINLINE_MIN_Q) {
            testResult = "X [Q2<MIN]";
            meminline_numFailed++;
          }
          else if (q2 < MEMINLINE_MIN_Q) {
            testResult = "X [Q2<MIN]";
            meminline_numFailed++;
          }

          // else if (reads[0][i] < min_hrs) {
          // testResult = "X";
          // meminline_numFailed++;
          // }
          // else if (reads[1][i] > max_lrs) {
          // testResult = "X";
          // meminline_numFailed++;
          // }

        }

        System.out.println("i=:" + i + ": " + testResult);
        System.out.println("reads[0][i]=" + reads[0][i]);
        System.out.println("reads[1][i]=" + reads[1][i]);
        System.out.println("reads[2][i]=" + reads[2][i]);

        appendWhiteSpace(testResult, b, COL_WIDTH + 1);

        // b.append("|");
      }
      return b.toString();

    }

    private void appendWhiteSpace(String s, StringBuilder b, int COL_WIDTH) {

      // white space
      b.append(s);
      for (int j = 0; j < (COL_WIDTH - s.length()); j++) {
        b.append(" ");
      }
    }

    private String formatResistanceArray(String prefix, float[] r) {

      StringBuilder b = new StringBuilder();
      b.append(prefix);
      for (int i = 0; i < r.length; i++) {

        String s;
        if (r[i] > 0) {
          s = ohmFormat.format(r[i]);
        }
        else {
          s = "∞ Ω";
        }

        appendWhiteSpace(s, b, COL_WIDTH);

        b.append("|");
      }
      return b.toString();

    }

    @Override
    protected void process(List<Double> chunks) {

      // plotController.updateYChartData(chunks.get(0), chunks.get(chunks.size() - 1));
      // plotController.repaintYChart();
    }
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    // switch (propName) {
    //
    // case EVENT_INSTRUCTION_UPDATE:
    //
    // // System.out.println(controlModel.getInstruction());
    // // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());
    //
    // break;
    //
    // default:
    // break;
    // }
  }

  @Override
  public ExperimentControlModel getControlModel() {

    return controlModel;
  }

  @Override
  public ExperimentControlPanel getControlPanel() {

    return controlPanel;
  }

  @Override
  public ExperimentPlotPanel getPlotPanel() {

    return consolPanel;
  }

  @Override
  public SwingWorker getCaptureWorker() {

    return new ClearConsolWorker();
  }
}
