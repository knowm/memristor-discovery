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
package org.knowm.memristor.discovery.gui.mvc.experiments.classify12;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify12.control.ControlModel;
import org.knowm.waveforms4j.DWF;

public class KTRAM_Controller_12 {

  private DWFProxy dWFProxy;
  private ControlModel controlModel;
  private double vy; // last read value
  private double ga;
  private double gb;

  // 0-7
  private SupervisedPattern spikePattern;

  public KTRAM_Controller_12(ControlModel controlModel) {

    this.controlModel = controlModel;
  }

  public void executeInstruction(SupervisedPattern spikePattern, Instruction12 instruction) {

    this.spikePattern = spikePattern;

    execute(instruction);
  }

  private void execute(Instruction12 instruction) {

    getControlModel()
        .swingPropertyChangeSupport
        .firePropertyChange(
            Model.EVENT_NEW_CONSOLE_LOG, null, "Executing Instruction: " + instruction);

    double W1Amplitude;

    dWFProxy.turnOffAllSwitches(2);

    // turn on A and B memristors for spikes
    // System.out.println("Instyruction: " + instruction);

    // System.out.println("switches should be off: " +
    // Integer.toBinaryString(dWFProxy.getDigitalIOStates()));

    // System.out.println("spike pattern: " + spikePattern.spikePattern);
    dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, true);
    dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, 8, true); // 8 is the offset
    // System.out.println("A and B switches should be on: " +
    // Integer.toBinaryString(dWFProxy.getDigitalIOStates()));

    // turn off switches and invert pulse if necessary
    if (instruction == Instruction12.FLV) {
      W1Amplitude = -.08f;
    } else if (instruction == Instruction12.RLV) {
      W1Amplitude = .08f;
    } else if (instruction == Instruction12.FAB) {
      W1Amplitude = -controlModel.getForwardAmplitude();
    } else if (instruction == Instruction12.RAB) {
      W1Amplitude = controlModel.getReverseAmplitude();
    } else if (instruction == Instruction12.FA) {
      W1Amplitude = -controlModel.getForwardAmplitude();

      // turn off B spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, 8, false);

    } else if (instruction == Instruction12.FB) {
      W1Amplitude = -controlModel.getForwardAmplitude();
      // turn off A spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, false);

    } else if (instruction == Instruction12.RA) {
      W1Amplitude = controlModel.getReverseAmplitude();
      // turn off B spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, 8, false);

    } else if (instruction == Instruction12.RB) {
      W1Amplitude = controlModel.getReverseAmplitude();
      // turn off A spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, false);

    } else { // default is forward read.
      W1Amplitude = -.08f;
    }

    // System.out.println("Before Instruction " +
    // Integer.toBinaryString(dWFProxy.getDigitalIOStates()));

    double[] W1 =
        WaveformUtils.generateCustomWaveform(
            controlModel.getWaveform(), W1Amplitude, controlModel.getCalculatedFrequency());

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

      if (instruction == Instruction12.FLV || instruction == Instruction12.RLV) {
        setVy(W1Amplitude);
      }

    } else {
      getControlModel()
          .swingPropertyChangeSupport
          .firePropertyChange(
              Model.EVENT_NEW_CONSOLE_LOG,
              null,
              "Capture has failed! This is usually due to noise/interference. Try a shorter cable or use a magnetic choke.");
    }

    //    // turn back on switches and invert pulse if necessary
    //    if (instruction == Instruction12.FA) {
    //      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, 8, true);
    //    } else if (instruction == Instruction12.FB) {
    //      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, true);
    //    } else if (instruction == Instruction12.RA) {
    //      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, 8, true);
    //    } else if (instruction == Instruction12.RB) {
    //      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikePattern, true);
    //    }

    // System.out.println("After Instruction " +
    // Integer.toBinaryString(dWFProxy.getDigitalIOStates()));
    dWFProxy.turnOffAllSwitches(2);
    /*
     * must wait here to allow pulses from AWG to finish. Should take no more that one ms...
     */
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {

    }
  }

  public double getVy() {

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

    this.vy = -(peakV1 - peakV2) / W1Amplitude;

    //    System.out.println("peakV1=" + peakV1);
    //    System.out.println("peakV2=" + peakV2);

    double Ia = (peakV1 - W1Amplitude) / controlModel.getSeriesResistance();
    double Ib = (peakV2 - W1Amplitude) / controlModel.getSeriesResistance();

    double a = -Ia / peakV1;
    double b = -Ib / peakV2;

    // noise correction-->
    this.ga = a < 0 ? 0 : a;
    this.gb = b < 0 ? 0 : b;
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

  public enum Instruction12 {

    // @formatter:off
    FLV,
    FA,
    FB,
    FAB,
    RLV,
    RA,
    RB,
    RAB;

    Instruction12() {}
  }
}
