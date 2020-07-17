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
package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control.ControlModel;
import org.knowm.waveforms4j.DWF;

public class KTRAM_Controller_12 {

  private DWFProxy dWFProxy;
  private ControlModel controlModel;
  private double vy; // last read value
  private double ga;
  private double gb;

  // 0-7
  private SpikePattern spikePattern;

  public KTRAM_Controller_12(ControlModel controlModel) {

    this.controlModel = controlModel;
  }

  public void executeInstruction(SpikePattern spikePattern, Instruction instruction) {

    this.spikePattern = spikePattern;

    if (instruction == Instruction.HEBBIAN) {
      if (ga >= gb) { // state is positive. make more positive.
        execute(Instruction.FA); // increase conductance of A
        execute(Instruction.RB); // decrease conductance of B
      } else if (ga < gb) { // state is negative. make more negative.
        execute(Instruction.RA); // decrease conductance of A
        execute(Instruction.FB); // increase conductance of B
      }
    } else if (instruction == Instruction.ANTI_HEBBIAN) {
      if (ga >= gb) { // state is positive. Make less positive.
        execute(Instruction.RA); // decrease conductance of A
        execute(Instruction.FB); // increase conductance of B
      } else if (ga < gb) { // state is negative. make less negative.
        execute(Instruction.FA); // increase conductance of A
        execute(Instruction.RB); // decrease conductance of B
      }
    } else {
      execute(instruction);
    }
  }

  private void execute(Instruction instruction) {

    getControlModel()
        .swingPropertyChangeSupport
        .firePropertyChange(
            Model.EVENT_NEW_CONSOLE_LOG, null, "Executing Instruction: " + instruction);

    double W1Amplitude;

    dWFProxy.turnOffAllSwitches(2);

    dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikes, true);
    dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikes, 8, true); // 8 is the offset

    // turn off switches and invert pulse if necessary
    if (instruction == Instruction.FLV) {
      W1Amplitude = -.08f;
    } else if (instruction == Instruction.RLV) {
      W1Amplitude = .08f;
    } else if (instruction == Instruction.FAB) {
      W1Amplitude = -controlModel.getForwardAmplitude();
    } else if (instruction == Instruction.RAB) {
      W1Amplitude = controlModel.getReverseAmplitude();
    } else if (instruction == Instruction.FA) {
      W1Amplitude = -controlModel.getForwardAmplitude();

      // turn off B spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikes, 8, false);

    } else if (instruction == Instruction.FB) {
      W1Amplitude = -controlModel.getForwardAmplitude();
      // turn off A spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikes, false);

    } else if (instruction == Instruction.RA) {
      W1Amplitude = controlModel.getReverseAmplitude();
      // turn off B spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikes, 8, false);

    } else if (instruction == Instruction.RB) {
      W1Amplitude = controlModel.getReverseAmplitude();
      // turn off A spikes
      dWFProxy.update2DigitalIOStatesAtOnce(spikePattern.spikes, false);

    } else { // default is forward read.
      W1Amplitude = -.08f;
    }

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

      if (instruction == Instruction.FLV || instruction == Instruction.RLV) {
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

  public enum Instruction {

    // @formatter:off
    FLV,
    FA,
    FB,
    FAB,
    RLV,
    RA,
    RB,
    RAB,
    HEBBIAN,
    ANTI_HEBBIAN;

    Instruction() {}
  }
}
