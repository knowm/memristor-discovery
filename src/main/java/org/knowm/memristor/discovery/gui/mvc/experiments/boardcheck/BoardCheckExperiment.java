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

import static org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.consol.ConsolControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.consol.ConsolPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.control.ControlPanel;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.memristor.discovery.utils.gpio.MuxController;
import org.knowm.memristor.discovery.utils.gpio.MuxController.Destination;
import org.knowm.waveforms4j.DWF;

public class BoardCheckExperiment extends Experiment {

  private final ControlModel controlModel = new ControlModel();

  private final static float V_MEASURE = 1.2345f;// the voltage used to test the waveform generators/muxes
  private final static float R_SWITCH = 73F;// Resistance of one of the switches. DG Switchs. Old AD Switches are 50.

  private DecimalFormat percentFormat = new DecimalFormat("0.00%");
  private DecimalFormat ohmFormat = new DecimalFormat("0.00 kΩ");

  private ControlPanel controlPanel;
  private ConsolPanel consolPanel;
  private final ConsolControlModel consolModel = new ConsolControlModel();
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
    // consolController = new ConsolController(consolPanel, consolModel);
    muxController = new MuxController();
  }

  @Override
  public void doCreateAndShowGUI() {

  }

  public float[] measureAllSwitchResistances() {

    muxController.setW1(Destination.A);
    muxController.setW2(Destination.OUT);

    muxController.setScope1(Destination.A);
    muxController.setScope2(Destination.B);
    dwfProxy.setUpper8IOStates(muxController.getGPIOConfig());

    // dwfProxy.setAllIOStates(0b0000_0000);// turn off all switches.

    float[] r_array = new float[9];
    r_array[0] = getSwitchResistancekOhm(DWF.WAVEFORM_CHANNEL_1);// all switch off

    int sleep = dwfProxy.isV1Board() ? 20 : 300;

    for (int i = 0; i < 8; i++) {
      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
      }

      dwfProxy.update2DigitalIOStatesAtOnce(i, true);

      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {

      }

      r_array[i + 1] = getSwitchResistancekOhm(DWF.WAVEFORM_CHANNEL_1);

      dwfProxy.update2DigitalIOStatesAtOnce(i, false);

    }

    return r_array;

  }

  private float getSwitchResistancekOhm(int dWFWaveformChannel) {

    float[] vMeasure = getScopesAverageVoltage(dWFWaveformChannel);
    /*
     * Vy/Rseries=I
     * Vdrop/I=Rswitch
     * (Vin-Vy)/I=Rswitch
     */

    System.out.println("measurevoltage: " + Arrays.toString(vMeasure));
    System.out.println("series resistance: " + controlModel.getSeriesResistance());
    float I = vMeasure[1] / controlModel.getSeriesResistance();
    float rSwitch = ((vMeasure[0] - vMeasure[1]) / I) - 2 * R_SWITCH;

    return rSwitch / 1000;
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

    float[] scopeReading = getScopesAverageVoltage(dWFWaveformChannel);

    return new float[] { Math.abs(scopeReading[0] - V_MEASURE), Math.abs(scopeReading[1] - V_MEASURE) };

  }

  private float[] getScopesAverageVoltage(int dWFWaveformChannel) {

    int samplesPerPulse = 300;
    int sampleFrequency = 100 * samplesPerPulse;
    // consolPanel.println("Starting Pulse Measurment");

    dwfProxy.getDwf().startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(dWFWaveformChannel, sampleFrequency, samplesPerPulse);
    dwfProxy.waitUntilArmed();
    double[] pulse = WaveformUtils.generateCustomWaveform(Waveform.Square, V_MEASURE, 100);
    dwfProxy.getDwf().startCustomPulseTrain(dWFWaveformChannel, 100, 0, 1, pulse);
    boolean success = dwfProxy.capturePulseData(sampleFrequency, 1);
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

  private class CaptureWorker extends SwingWorker<Boolean, Double> {

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

        consolPanel.println("Testing Muxes");

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

        consolPanel.println("                 Scope 1+       Scope 2+      ");

        float[] deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.A);
        consolPanel.println("W1-->A      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.B);
        consolPanel.println("W1-->B      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_1, Destination.Y);
        consolPanel.println("W1-->Y      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.A);
        consolPanel.println("W2-->A      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.B);
        consolPanel.println("W2-->B      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        deviations = measureMuxDeviation(DWF.WAVEFORM_CHANNEL_2, Destination.Y);
        consolPanel.println("W2-->Y      " + percentFormat.format(deviations[0]) + "          " + percentFormat.format(deviations[1]));

        consolPanel.println("");

      }

      consolPanel.println("Testing Switches. ");
      // consolPanel.println("ALL OFF                1              2              3              4              5              6              7              8");

      float[] r = measureAllSwitchResistances();

      for (int i = 0; i < r.length; i++) {

        StringBuilder b = new StringBuilder();

        if (i == 0) {
          b.append("   ALL OFF: ");
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
        consolPanel.println(b.toString());

      }

      return true;
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

    switch (propName) {

    case EVENT_INSTRUCTION_UPDATE:

      // System.out.println(controlModel.getInstruction());
      // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

      break;

    default:
      break;
    }
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

    return new CaptureWorker();
  }
}
