/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2020 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.MemristorDiscoveryPreferences;
import org.knowm.memristor.discovery.core.experiment_common.PulseUtility;
import org.knowm.memristor.discovery.core.gpio.MuxController;
import org.knowm.memristor.discovery.core.gpio.MuxController.Destination;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.result.ResultPanel;
import org.knowm.waveforms4j.DWF;

public class BoardCheckExperiment extends Experiment {

  private static final float V_MUX_TEST =
      1.5f; // the voltage used to test the waveform generators/muxes
  private static final float V_READ = .1f;
  private static final float V_WRITE = 1.5f;
  private static final float V_RESET = -2f;
  private static final float MIN_DEVIATION = .03F; // Line trace resistance, AD2 Calibration.
  private final MuxController muxController;

  private static final int PULSE_WIDTH_IN_MICRO_SECONDS = 50_000;

  // private static final float VOLTAGE_READ_NOISE_FLOOR = .001f; // if the measured voltage across
  // series resistor is less than this, you are getting
  // noisy.

  private float MIN_Q = 2; // minimum ratio between erase/write resistance
  private float MEMINLINE_MIN_R = 10; // if all states are below this (kiloohms), its stuck low
  private float MEMINLINE_MAX_R = 100; // if all state are above this (kilohms), its stuck low
  private float MEMINLINE_MIN_SWITCH_OFF =
      1000; // if switch is below this resistance (kOhm) when OFF then its a bad switch
  private int meminline_numFailed = 0;
  private int COL_WIDTH = 10;
  private DecimalFormat qFormat = new DecimalFormat("0.00 X");
  private DecimalFormat percentFormat = new DecimalFormat("0.00 %");
  private DecimalFormat ohmFormat = new DecimalFormat("0.00 kΩ");

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ControlPanel controlPanel;
  private final ResultModel resultModel;
  private final ResultPanel resultPanel;
  private final ResultController resultController;

  // SwingWorkers
  //  private SwingWorker aHAH12X7TestWorker;
  private SwingWorker meminlineTestWorker;
  private SwingWorker muxTestWorker;
  private SwingWorker switchTestWorker;
  private SwingWorker clearConsoleWorker;
  //  private SwingWorker synapse12TestWorker;
  //  private SwingWorker synapse12iTestWorker;

  private PulseUtility pulseUtility;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public BoardCheckExperiment(DWFProxy dwfProxy, Container mainFrameContainer, int boardVersion) {

    super(dwfProxy, mainFrameContainer, boardVersion);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel();

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);

    muxController = new MuxController();

    pulseUtility =
        new PulseUtility(
            boardVersion,
            controlModel,
            dwfProxy,
            muxController,
            MemristorDiscoveryPreferences.MIN_VOLTAGE_MEASURE_AMPLITUDE);
  }

  @Override
  public void doCreateAndShowGUI() {}

  /*
   * Here action listeners are attached to the widgets in the control panel and mapped to a worker, also defined here in the experiment.
   */
  @Override
  public void addWorkersToButtonEvents() {

    //    controlPanel.synapse12iTestButton.addActionListener(new ActionListener() {
    //
    //      @Override
    //      public void actionPerformed(ActionEvent e) {
    //
    //        synapse12iTestWorker = new Synapse12iTestWorker();
    //        synapse12iTestWorker.execute();
    //      }
    //    });

    //    controlPanel.synapse12TestButton.addActionListener(new ActionListener() {
    //
    //      @Override
    //      public void actionPerformed(ActionEvent e) {
    //
    //        synapse12TestWorker = new Synapse12TestWorker();
    //        synapse12TestWorker.execute();
    //      }
    //    });

    //    controlPanel.aHAH12X7TestButton.addActionListener(new ActionListener() {
    //
    //      @Override
    //      public void actionPerformed(ActionEvent e) {
    //
    //        aHAH12X7TestWorker = new AHaH21X7TestWorker();
    //        aHAH12X7TestWorker.execute();
    //      }
    //    });

    controlPanel.meminlineTestButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            meminlineTestWorker = new MeminlineTestWorker();
            meminlineTestWorker.execute();
          }
        });

    controlPanel.muxTestButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            muxTestWorker = new MuxDiagnosticWorker();
            muxTestWorker.execute();
          }
        });

    controlPanel.switchTestButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            switchTestWorker = new SwitchDiagnosticWorker();
            switchTestWorker.execute();
          }
        });

    controlPanel.clearConsolButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            clearConsoleWorker = new ClearConsoleWorker();
            clearConsoleWorker.execute();
          }
        });
  }

  float[] measureMuxDeviation(int dWFWaveformChannel, Destination destination) {

    muxController.setScope1(destination);
    muxController.setScope2(destination);

    if (dWFWaveformChannel == DWF.WAVEFORM_CHANNEL_1) {
      muxController.setW1(destination);
      muxController.setW2(Destination.OUT);
    } else if (dWFWaveformChannel == DWF.WAVEFORM_CHANNEL_2) {
      muxController.setW1(Destination.OUT);
      muxController.setW2(destination);
    }

    dwfProxy.setUpper8IOStates(muxController.getGPIOConfig());

    float[] scopeReading =
        pulseUtility.getScopesAverageVoltage(
            Waveform.Square, V_MUX_TEST, PULSE_WIDTH_IN_MICRO_SECONDS, dWFWaveformChannel);

    if (scopeReading == null) {
      resultController.addNewLine("Pulse capture failed. This is usually a triggering issue.");
    }

    return new float[] {
      Math.abs(scopeReading[0] - V_MUX_TEST), Math.abs(scopeReading[1] - V_MUX_TEST)
    };
  }

  private boolean isWithenPercentPercent(float value, float reference, float percent) {
    float p = Math.abs(value - reference) / reference;
    return p <= percent;
  }

  private void appendWhiteSpace(String s, StringBuilder b, int COL_WIDTH) {

    // white space
    b.append(s);
    for (int j = 0; j < (COL_WIDTH - s.length()); j++) {
      b.append(" ");
    }
  }

  private String prependWhiteSpace(String s, int COL_WIDTH) {

    String s_out = s;
    for (int j = 0; j < (COL_WIDTH - s.length()); j++) {
      s_out = " " + s_out;
    }
    return s_out;
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying
   * controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    // String propName = evt.getPropertyName();

    // switch (propName) {
    //
    // case EVENT_INSTRUCTION_UPDATE:
    //
    // // System.out.addNewLine(controlModel.getInstruction());
    // // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());
    //
    // break;
    //
    // default:
    // break;
    // }
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
  public ExperimentPreferences initAppPreferences() {

    return new BoardCheckPreferences();
  }

  private class SwitchDiagnosticWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      resultController.addNewLine("");
      resultController.addNewLine("Testing Board Switches");
      if (boardVersion == 2) {
        resultController.addNewLine("NOTE: V2 board must be in Mode 1");
      }
      resultController.addNewLine("");

      // getControlModel().swingPropertyChangeSupport.firePropertyChange(Model.EVENT_NEW_CONSOLE_LOG, null, "Measuring Switch Resistances");

      float[] r;
      if (boardVersion == 2) {
        r =
            pulseUtility.measureAllSwitchResistances(
                Waveform.Square, -V_READ, PULSE_WIDTH_IN_MICRO_SECONDS);
      } else {
        r =
            pulseUtility.measureAllSwitchResistances(
                Waveform.Square, V_READ, PULSE_WIDTH_IN_MICRO_SECONDS);
      }

      for (int i = 0; i < r.length; i++) {

        StringBuilder b = new StringBuilder();

        if (i == 0) {
          b.append(" ALL OFF: ");
        } else {
          b.append("SWITCH " + i + ": ");
        }

        if (r[i] < 0) {
          b.append("INF");
        } else {
          b.append(ohmFormat.format(r[i]));
        }

        resultController.addNewLine(b.toString());
      }

      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

      // plotController.updateYChartData(chunks.get(0), chunks.get(chunks.size() - 1));
      // plotController.repaintYChart();
    }
  }

  private class MuxDiagnosticWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      if (boardVersion == 1) {

        // TEST MUXES

        /*
         * MUX DIO PINOUT Order ==> W2, W1, 2+, 1+ 00 E 10 Y 01 A 11 B
         */
        resultController.addNewLine("");
        resultController.addNewLine("Testing 1-4 Board Muxes");
        resultController.addNewLine("");

        /*
         * Procedure: 1) Route W1 to A. 2) Route 1+ and 2+ to A. 3) Drive 1.234 Volts DC on W1. 4) Measure 1+ and 2+. 5) TEST: Is 1+ and 2+ equal to
         * 1.234 within p percent? If both fail, possibly bad W1 mux. Test with W2. If one or the other fail, its a bad scope mux.
         */

        resultController.addNewLine("Route       Scope 1+       Scope 2+      ");

        boolean pass = true;

        float[] deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.A);
        resultController.addNewLine(
            "W1-->A      "
                + percentFormat.format(deviations[0])
                + "          "
                + percentFormat.format(deviations[1]));

        if (deviations[0] > MIN_DEVIATION | deviations[1] > MIN_DEVIATION) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.B);
        resultController.addNewLine(
            "W1-->B      "
                + percentFormat.format(deviations[0])
                + "          "
                + percentFormat.format(deviations[1]));

        if (deviations[0] > MIN_DEVIATION | deviations[1] > MIN_DEVIATION) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.Y);
        resultController.addNewLine(
            "W1-->Y      "
                + percentFormat.format(deviations[0])
                + "          "
                + percentFormat.format(deviations[1]));

        if (deviations[0] > MIN_DEVIATION | deviations[1] > MIN_DEVIATION) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.A);
        resultController.addNewLine(
            "W2-->A      "
                + percentFormat.format(deviations[0])
                + "          "
                + percentFormat.format(deviations[1]));

        if (deviations[0] > MIN_DEVIATION | deviations[1] > MIN_DEVIATION) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.B);
        resultController.addNewLine(
            "W2-->B      "
                + percentFormat.format(deviations[0])
                + "          "
                + percentFormat.format(deviations[1]));

        if (deviations[0] > MIN_DEVIATION | deviations[1] > MIN_DEVIATION) {
          pass = false;
        }

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.Y);
        resultController.addNewLine(
            "W2-->Y      "
                + percentFormat.format(deviations[0])
                + "          "
                + percentFormat.format(deviations[1]));

        if (deviations[0] > MIN_DEVIATION | deviations[1] > MIN_DEVIATION) {
          pass = false;
        }

        resultController.addNewLine("");

        if (pass) {
          resultController.addNewLine("PASS");
        } else {
          resultController.addNewLine("FAIL");
          resultController.addNewLine(
              "NOTE:  If W1-->B and W2-->B routes are failing, remove series resistor and test again. Error is likely due to waveform generator loading.)");
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

  private class ClearConsoleWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      resultController.clear();
      return true;
    }
  }

  private class MeminlineTestWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // if its a v1 board, we must set the MUXes

      if (boardVersion == 2) {
        resultController.addNewLine("NOTE: V2 board must be in Mode 1");
      }

      if (boardVersion == 1) {
        MuxController muxController = new MuxController();
        dwfProxy.setUpper8IOStates(
            muxController
                .getGPIOConfig()); // default configuration is for series resistor measurment.
      }

      resultController.addNewLine("Mem-Inline Chip Test");
      resultController.addNewLine("");
      resultController.addNewLine("V_WRITE: " + V_WRITE + "V");
      resultController.addNewLine("V_RESET: " + V_RESET + "V");
      resultController.addNewLine("V_READ: " + V_READ + "V");
      resultController.addNewLine("");

      int n = 9;
      if (boardVersion == 2) {
        n = 17;
      }

      StringBuilder b = new StringBuilder();
      b.append("            ");
      for (int i = 0; i < n; i++) {
        String w = "ALL OFF";
        if (i > 0) {
          w = "S" + i;
        }
        appendWhiteSpace(w, b, COL_WIDTH + 1);
      }

      resultController.addNewLine(b.toString());

      float[][] reads;

      // form device
      if (boardVersion == 2) {
        reads =
            pulseUtility.testMeminline(
                Waveform.HalfSine,
                -V_WRITE,
                -V_RESET,
                -V_READ,
                PULSE_WIDTH_IN_MICRO_SECONDS,
                PULSE_WIDTH_IN_MICRO_SECONDS,
                PULSE_WIDTH_IN_MICRO_SECONDS);
      } else {
        reads =
            pulseUtility.testMeminline(
                Waveform.HalfSine,
                V_WRITE,
                V_RESET,
                V_READ,
                PULSE_WIDTH_IN_MICRO_SECONDS,
                PULSE_WIDTH_IN_MICRO_SECONDS,
                PULSE_WIDTH_IN_MICRO_SECONDS);
      }

      resultController.addNewLine(formatResistanceArray("ERASE       ", reads[0]));
      resultController.addNewLine(formatResistanceArray("WRITE       ", reads[1]));
      resultController.addNewLine(formatResistanceArray("ERASE      ", reads[2]));

      resultController.addNewLine("RESULT      " + verifyMemInlineReads(reads));
      resultController.addNewLine("");

      // for debugging-->
      boolean pulseCaptureFail = false;
      controlModel.swingPropertyChangeSupport.firePropertyChange(
          Model.EVENT_NEW_CONSOLE_LOG, null, "MeminlineTestWorker Read Voltage Array: ");
      for (int i = 0; i < reads.length; i++) {
        controlModel.swingPropertyChangeSupport.firePropertyChange(
            Model.EVENT_NEW_CONSOLE_LOG, null, Arrays.toString(reads[i]));
        for (int j = 0; j < reads[i].length; j++) {
          if (reads[i][j] == Float.NaN) {
            pulseCaptureFail = true;
          }
        }
      }

      if (pulseCaptureFail) {
        resultController.addNewLine("PULSE CAPTURE FAILURE");
        return true;
      }

      if (meminline_numFailed == 0) {
        resultController.addNewLine("TIER 1");
      } else if (meminline_numFailed <= 2) {
        resultController.addNewLine("TIER 2");
      } else if (meminline_numFailed <= 4) {
        resultController.addNewLine("BURN AND LEARN");
      } else {
        resultController.addNewLine("REJECT");
      }

      if (meminline_numFailed > 3) {
        if (boardVersion == 2) {
          resultController.addNewLine("NOTE: Is board in mode 2?");
        }
      }

      return true;
    }

    private String verifyMemInlineReads(float[][] reads) {

      meminline_numFailed = 0;
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < reads[0].length; i++) {
        String testResult = "✓";
        if (i == 0) { // this is all switches off.
          if (reads[0][0] < MEMINLINE_MIN_SWITCH_OFF) { // should be in high resistance state.
            testResult = "SWITCHES FAILED!";
            appendWhiteSpace(testResult, b, COL_WIDTH + 1);
            break;
          }
        } else { // memristors

          float q1 = reads[0][i] / reads[1][i];
          float q2 = reads[2][i] / reads[1][i];

          if (reads[0][i] < MEMINLINE_MIN_R
              && reads[1][i] < MEMINLINE_MIN_R
              && reads[2][i] < MEMINLINE_MIN_R) {
            testResult = "X [STK LOW]";
            meminline_numFailed++;
          } else if (reads[0][i] > MEMINLINE_MAX_R
              && reads[1][i] > MEMINLINE_MAX_R
              && reads[2][i] > MEMINLINE_MAX_R) {
            testResult = "X [STK HIGH]";
            meminline_numFailed++;
          } else if (q1 < MIN_Q) {
            testResult = "X [Q2<MIN]";
            meminline_numFailed++;
          } else if (q2 < MIN_Q) {
            testResult = "X [Q2<MIN]";
            meminline_numFailed++;
          }
        }

        appendWhiteSpace(testResult, b, COL_WIDTH + 1);
      }
      return b.toString();
    }

    private String formatResistanceArray(String prefix, float[] r) {

      StringBuilder b = new StringBuilder();
      b.append(prefix);
      for (int i = 0; i < r.length; i++) {

        String s;
        if (r[i] > 0) {
          s = ohmFormat.format(r[i]);
        } else {
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
}
