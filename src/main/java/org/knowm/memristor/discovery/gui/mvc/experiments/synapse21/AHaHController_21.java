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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse21;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse21.control.ControlModel;
import org.knowm.waveforms4j.DWF;

public class AHaHController_21 {

  private final double MIN_V_RESOLUTION = .0025;
  private DWFProxy dWFProxy;
  private ControlModel controlModel;
  private double vy; // last read value
  private double ga;
  private double gb;

  public AHaHController_21(ControlModel controlModel) {

    this.controlModel = controlModel;
  }

  public void executeInstruction(Instruction21 instruction) {

    if (instruction == Instruction21.FF_RH) {

      execute(Instruction21.FF);
      // NOTE: Delay between executions as measured by scope (@MANC Cave) is ~6ms.
      execute(Instruction21.RHbdn);

      return;
    } else if (instruction == Instruction21.FF_RL) {

      execute(Instruction21.FF);
      // NOTE: Delay between executions as measured by scope (@MANC Cave) is ~6ms
      execute(Instruction21.RLadn);

      return;
    } else if (instruction == Instruction21.FF_RA) {

      execute(Instruction21.FF);

      if (vy >= 0) {
        execute(Instruction21.RLadn);
      } else {
        execute(Instruction21.RHbdn);
      }

      return;
    } else if (instruction == Instruction21.FF_RU) {

      execute(Instruction21.FF);

      if (vy >= 0) {
        execute(Instruction21.RHbdn);
      } else {
        execute(Instruction21.RLadn);
      }

      return;
    } else {
      execute(instruction);
    }
  }

  private void execute(Instruction21 instruction) {

    getControlModel()
        .swingPropertyChangeSupport
        .firePropertyChange(
            Model.EVENT_NEW_CONSOLE_LOG, null, "Executing Instruction: " + instruction);

    // 1. the IO-bits are set
    dWFProxy.setUpper8IOStates(instruction.getBits());

    double W1Amplitude = controlModel.getAmplitude() * instruction.getW1PulseAmplitudeMultiplier();

    // hard-set the FFLV amplitude, as this is used for reads and should never change and be low
    // that adaptation never occures.
    if (instruction == Instruction21.FFLV) {
      W1Amplitude = .1f;
    } else if (instruction == Instruction21.RFLV) {
      W1Amplitude = -.1f;
    }

    double[] W1 =
        WaveformUtils.generateCustomWaveform(
            controlModel.getWaveform(), W1Amplitude, controlModel.getCalculatedFrequency());

    if (instruction == Instruction21.FFLV) {

      dWFProxy
          .getDwf()
          .startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(
              DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency() * 300, 300 * 1, true);
      dWFProxy.waitUntilArmed();
      dWFProxy
          .getDwf()
          .setCustomPulseTrain(
              DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, W1);
      dWFProxy.getDwf().startPulseTrain(DWF.WAVEFORM_CHANNEL_1);

      boolean success = dWFProxy.capturePulseData(controlModel.getCalculatedFrequency(), 1);
      if (success) {
        setVy(W1Amplitude);
      } else {
        getControlModel()
            .swingPropertyChangeSupport
            .firePropertyChange(
                Model.EVENT_NEW_CONSOLE_LOG,
                null,
                "Capture has failed! This is usually due to noise/interference. Try a shorter cable or use a magnetic choke.");
        // System.out.println("capture failed!");
      }

    } else {
      dWFProxy
          .getDwf()
          .setCustomPulseTrain(
              DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, W1);
      // note w2 amplitude is zero (gnd).
      double[] W2 =
          WaveformUtils.generateCustomWaveform(
              controlModel.getWaveform(), 0.0, controlModel.getCalculatedFrequency());
      dWFProxy
          .getDwf()
          .setCustomPulseTrain(
              DWF.WAVEFORM_CHANNEL_2, controlModel.getCalculatedFrequency(), 0, 1, W2);
      dWFProxy.getDwf().startPulseTrain(DWF.WAVEFORM_CHANNEL_BOTH);
    }

    /*
     * must wait here to allow pulses from AWG to finish. Should take no more that one ms...
     */
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {

    }
  }

  public double getVy() {

    // this is a quick way to deal with measurement noise.
    if (vy > .5) {
      return .5;
    } else if (vy < -.5) {
      return -.5;
    }

    return vy;
  }

  private void setVy(double W1Amplitude) {

    int validSamples = dWFProxy.getDwf().FDwfAnalogInStatusSamplesValid();
    double peakV1 =
        Util.maxAbs(
            dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples));
    double peakV2 =
        Util.maxAbs(
            dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples));

    // note: if V1 is less than resolution of scope, the measurments will be useless
    double vb = peakV2;
    this.vy = (vb - peakV1 - (W1Amplitude - peakV1) / 2.0) / (W1Amplitude - peakV1);

    // System.out.println("vy=" + vy);

    if (peakV1 > MIN_V_RESOLUTION) {

      double I = peakV1 / controlModel.getSeriesResistance();
      double va = W1Amplitude - ExperimentPreferences.R_SWITCH * I;
      double vc = peakV1 + ExperimentPreferences.R_SWITCH * I;

      //      System.out.println("va=" + va);
      //      System.out.println("vb=" + vb);
      //      System.out.println("vc=" + vc);
      //      System.out.println("I=" + I);

      if ((va - vb > MIN_V_RESOLUTION)) {
        this.ga = I / (va - vb);

      } else {
        getControlModel()
            .swingPropertyChangeSupport
            .firePropertyChange(
                Model.EVENT_NEW_CONSOLE_LOG, null, "Voltage drop across Ma too small too measure.");
        // System.out.println("voltage drop across Ma too small too measure.");
        this.ga = Double.NaN;
      }

      if (((vb - vc) > MIN_V_RESOLUTION)) {
        this.gb = 1 / ((vb - vc) / I);

      } else {
        getControlModel()
            .swingPropertyChangeSupport
            .firePropertyChange(
                Model.EVENT_NEW_CONSOLE_LOG, null, "Voltage drop across Mb too small too measure.");
        // System.out.println("voltage drop across Mb too small too measure.");
        this.gb = Double.NaN;
      }

    } else {
      getControlModel()
          .swingPropertyChangeSupport
          .firePropertyChange(
              Model.EVENT_NEW_CONSOLE_LOG,
              null,
              "Current too low to measure. peakV1=" + String.format("%.4f", (double) peakV1));
      // System.out.println("Current too low to measure. peakV1=" + peakV1);
      this.ga = Double.NaN;
      this.gb = Double.NaN;
    }
  }

  public DWFProxy getdWFProxy() {

    return dWFProxy;
  }

  public void setdWFProxy(DWFProxy dWFProxy) {

    this.dWFProxy = dWFProxy;
  }

  /** @return the controlModel */
  public ControlModel getControlModel() {

    return controlModel;
  }

  /** @return the ga */
  public double getGa() {

    return ga;
  }

  /** @param ga the ga to set */
  public void setGa(double ga) {

    this.ga = ga;
  }

  /** @return the gb */
  public double getGb() {

    return gb;
  }

  /** @param gb the gb to set */
  public void setGb(double gb) {

    this.gb = gb;
  }

  public enum Instruction21 {

    /*
     * NOTE: Charge injection from the MUX can affect voltage across memristors when routing the scopes. The scope configuration [1011] is chosen
     * because this is the configuration used to measure the state of the synapse, resulting in minor charge injection. It is unclear if charge
     * injection for waveform generators is an issue, but currently (7/29/2017) appears to not be.
     */

    // Order ==> W2, W1, 2+, 1+
    // 00 None
    // 10 Y
    // 01 A
    // 11 B

    // @formatter:off
    FFLV(0b0001_1011_0000_0000, .1f),
    FF_RL(0b1001_1011_0000_0000, -1),
    FF_RH(0b1011_1011_0000_0000, 1f),
    FF_RU(0b1011_1011_0000_0000, 1f),
    FF_RA(0b1011_1011_0000_0000, 1f),
    FF(0b1101_1011_0000_0000, 1.0f),
    RHbdn(0b1011_1011_0000_0000, 1f), // w2-->Y, w1-->B
    RLadn(0b1001_1011_0000_0000, -1f), // w2-->Y, w1-->A
    RFLV(0b0001_1011_0000_0000, -.1f),
    RF(0b1101_1011_0000_0000, -1.0f),
    RHaup(0b1001_1011_0000_0000, 1),
    RLbup(0b1011_1011_0000_0000, -1);

    // @formatter:on
    // RZ;

    private final int bits;
    private final float pulseAmplitudeMultiplier;

    Instruction21(int bits, float pulseAmplitudeMultiplier) {

      this.bits = bits;
      this.pulseAmplitudeMultiplier = pulseAmplitudeMultiplier;
    }

    public int getBits() {

      return bits;
    }

    public float getW1PulseAmplitudeMultiplier() {

      return pulseAmplitudeMultiplier;
    }
  }

  // public double getAmplitude() {
  //
  // return amplitude;
  // }
  //
  // public void setAmplitude(double amplitude) {
  //
  // this.amplitude = amplitude;
  // }
}
