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
package org.knowm.memristor.discovery.core.experiment_common;

import java.util.Arrays;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.memristor.discovery.core.gpio.MuxController;
import org.knowm.memristor.discovery.core.gpio.MuxController.Destination;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.waveforms4j.DWF;

public class PulseUtility {

  private Model model;
  private DWFProxy dwfProxy;
  private MuxController muxController;
  private float voltageReadNoiseFloor;
  private static int sleep = 5;

  public PulseUtility(Model controlModel, DWFProxy dwfProxy, MuxController muxController, float voltageReadNoiseFloor) {
    this.model = controlModel;
    this.dwfProxy = dwfProxy;
    this.muxController = muxController;
    this.voltageReadNoiseFloor = voltageReadNoiseFloor;
  }

  /*
   * measures starting resistance values writes devices, measure resistance values erase, measure resistance values returns array of resistance values
   * for each device (in kOhms): start, write, erase
   */

  public float[][] testMeminline(Waveform writeEraseWaveform, float V_WRITE, float V_ERASE, float V_READ, int READ_PULSE_WIDTH_IN_MICRO_SECONDS,
      int WRITE_PULSE_WIDTH_IN_MICRO_SECONDS, int ERASE_PULSE_WIDTH_IN_MICRO_SECONDS) {

    try {

      // initialize in erased state
      measureAllSwitchResistances(writeEraseWaveform, V_ERASE, ERASE_PULSE_WIDTH_IN_MICRO_SECONDS);
      Thread.sleep(25);
      float[][] reads = new float[3][9];
      reads[0] = measureAllSwitchResistances(Waveform.Square, V_READ, READ_PULSE_WIDTH_IN_MICRO_SECONDS);
      Thread.sleep(25);
      measureAllSwitchResistances(writeEraseWaveform, V_WRITE, WRITE_PULSE_WIDTH_IN_MICRO_SECONDS);
      Thread.sleep(25);
      reads[1] = measureAllSwitchResistances(Waveform.Square, V_READ, READ_PULSE_WIDTH_IN_MICRO_SECONDS);
      Thread.sleep(25);
      measureAllSwitchResistances(writeEraseWaveform, V_ERASE, ERASE_PULSE_WIDTH_IN_MICRO_SECONDS);
      Thread.sleep(25);
      reads[2] = measureAllSwitchResistances(Waveform.Square, V_READ, READ_PULSE_WIDTH_IN_MICRO_SECONDS);
      return reads;
    } catch (InterruptedException e) {

      model.swingPropertyChangeSupport.firePropertyChange(Model.EVENT_NEW_CONSOLE_LOG, null, e.getMessage());
      return null;
    }
  }

  public float[] measureAllSwitchResistances(Waveform waveform, float readVoltage, int pulseWidthInMicroSeconds) {

    if (muxController != null) {
      muxController.setW1(Destination.A);
      muxController.setW2(Destination.OUT);
      muxController.setScope1(Destination.A);
      muxController.setScope2(Destination.B);
      dwfProxy.setUpper8IOStates(muxController.getGPIOConfig());
    }

    float[] r_array = new float[9];

    r_array[0] = getSwitchResistancekOhm(waveform, readVoltage, pulseWidthInMicroSeconds, DWF.WAVEFORM_CHANNEL_1); // all switches off

    for (int i = 0; i < 8; i++) {

      dwfProxy.update2DigitalIOStatesAtOnce(i, true);

      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {

      }

      r_array[i + 1] = getSwitchResistancekOhm(waveform, readVoltage, pulseWidthInMicroSeconds, DWF.WAVEFORM_CHANNEL_1);

      dwfProxy.update2DigitalIOStatesAtOnce(i, false);
    }

    return r_array;
  }

  public float getSwitchResistancekOhm(Waveform waveform, float readVoltage, int pulseWidthInMicroSeconds, int dWFWaveformChannel) {

    float[] vMeasure = getScopesAverageVoltage(waveform, readVoltage, pulseWidthInMicroSeconds, dWFWaveformChannel);

    model.swingPropertyChangeSupport.firePropertyChange(Model.EVENT_NEW_CONSOLE_LOG, null,
        "Measuring switch resistance. [V1,V2]=" + Arrays.toString(vMeasure));

    if (vMeasure == null) {

      model.swingPropertyChangeSupport.firePropertyChange(Model.EVENT_NEW_CONSOLE_LOG, null,
          "WARNING: getScopesAverageVoltage() returned null. This is likely a pulse catpure failure.");
      return Float.NaN;
    }

    if (vMeasure[1] <= voltageReadNoiseFloor) {
      model.swingPropertyChangeSupport.firePropertyChange(Model.EVENT_NEW_CONSOLE_LOG, null,
          "WARNING: Voltage drop across series resistor (" + vMeasure[1] + ") is at or below noise threshold.");
      return Float.POSITIVE_INFINITY;
    }

    /*
     * Vy/Rseries=I Vdrop/I=Rswitch (Vin-Vy)/I=Rswitch
     */

    float I = Math.abs(vMeasure[1] / model.seriesResistance);
    float rSwitch = (Math.abs(vMeasure[0] - vMeasure[1]) / I) - (float) ExperimentPreferences.TOTAL_PARASITIC_RESISTANCE;

    return rSwitch / 1000; // to kilohms
  }

  public float[] getScopesAverageVoltage(Waveform waveform, float readVoltage, int pulseWidthInMicroSeconds, int dWFWaveformChannel) {

    int samplesPerPulse = 300;

    int sampleFrequency = (int) (1.0 / (pulseWidthInMicroSeconds * 2 * 1E-6));

    int samples = sampleFrequency * samplesPerPulse;

    dwfProxy.getDwf().startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(dWFWaveformChannel, samples, samplesPerPulse, true);
    dwfProxy.waitUntilArmed();
    double[] pulse = WaveformUtils.generateCustomWaveform(waveform, readVoltage, sampleFrequency);
    dwfProxy.getDwf().startCustomPulseTrain(dWFWaveformChannel, sampleFrequency, 0, 1, pulse);
    boolean success = dwfProxy.capturePulseData(samples, 1);
    if (success) {
      int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
      double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
      double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

      /*
       * Note from Alex: The output is a pulse with the last half of the measurement data at ground. Taking the first 50% insures we get the pulse
       * amplitude.
       */

      float aveScope1 = 0;
      float aveScope2 = 0;

      for (int i = 0; i < v1.length / 2; i++) {
        aveScope1 += v1[i];
        aveScope2 += v2[i];
      }

      aveScope1 /= v1.length / 2;
      aveScope2 /= v2.length / 2;

      return new float[]{(float) aveScope1, (float) aveScope2};

    } else {
      return null;
    }
  }
}
