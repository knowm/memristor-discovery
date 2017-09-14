package org.knowm.memristor.discovery.gui.mvc.experiments.synapse;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.utils.Util;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;

/**
 * Created by timmolter on 5/25/17.
 */
public class AHaHController {

  private DWFProxy dWFProxy;
  private SynapsePreferences.Waveform waveform;
  private double calculatedFrequency;
  private double amplitude;

  private double vy;// last read value

  public AHaHController() {

  }

  public void executeInstruction(Instruction instruction) {

    // 1. the IO-bits are set
    dWFProxy.setUpper8IOStates(instruction.getBits());

    boolean isReadInstruction = false;
    if (instruction == Instruction.FFLV || instruction == Instruction.RFLV || instruction == Instruction.FF || instruction == Instruction.RF) {
      isReadInstruction = true;
    }

    // capture read data
    if (isReadInstruction) {
      dWFProxy.getDwf().startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(DWF.WAVEFORM_CHANNEL_1, calculatedFrequency * 300, 300 * 1);
      dWFProxy.waitUntilArmed();
    }

    // 2. set the waveforms ( change this to correct amplitude and sign based on instruction)
    // Get the waveform for the selected instruction
    double W2Amplitude = 0;
    double[] customWaveformW2 = WaveformUtils.generateCustomWaveform(waveform, W2Amplitude, calculatedFrequency);
    double W1Amplitude = amplitude * instruction.getW1VoltageMultiplier();
    double[] customWaveformW1 = WaveformUtils.generateCustomWaveform(waveform, W1Amplitude, calculatedFrequency);

    dWFProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, calculatedFrequency, 0, 1, customWaveformW1);
    dWFProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_2, calculatedFrequency, 0, 1, customWaveformW2);
    dWFProxy.getDwf().startPulseTrain(DWF.WAVEFORM_CHANNEL_BOTH);

    if (isReadInstruction) {
      boolean success = dWFProxy.capturePulseData(calculatedFrequency, 1);
      if (success) {
        int validSamples = dWFProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        double[] v1 = dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
        double[] v2 = dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
        double peakV1 = Util.maxAbs(v1);
        double peakV2 = Util.maxAbs(v2);

        // because of the series resistor...
        vy = (peakV2 - .5 * (W1Amplitude - peakV1)) / (W1Amplitude - peakV1);

        // System.out.println("V1=" + peakV1);
        // System.out.println("V2=" + peakV2);
        // System.out.println("vy=" + vy);

      } else {
        System.out.println("did not capture!");
      }

    }

    /*
     * must wait here to allow pulses from AWG to finish. Should take no more that one ms...
     */
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {

    }

  }

  // Order ==> W2, W1, 2+, 1+
  // 00 None
  // 10 Y
  // 01 A
  // 11 B

  public enum Instruction {

    /*
     * NOTE: Charge injection from the MUX can affect voltage across memristors when routing the scopes.
     * The scope configuration [1011] is chosen because this is the configuration used to measure the state of the synapse,
     * resulting in minor charge injection. It is unclear if charge injection for waveform generators is an issue, but currently (7/29/2017)
     * appears to not be.
     */
    // @formatter:off
    FFLV(0b1101_1011_0000_0000, .1f),
    FF(0b1101_1011_0000_0000, 1),
    RFLV(0b1101_1011_0000_0000, -.1f),
    RF(0b1101_1011_0000_0000, -1),
    RH_M2_DWN(0b1011_1011_0000_0000, 1), // w2-->Y, w1-->B
    RL_M2_UP(0b1011_1011_0000_0000, -1),
    RL_M1_DWN(0b1001_1011_0000_0000, -1), // w2-->Y, w1-->A
    RH_M1_UP(0b1001_1011_0000_0000, 1);
    // @formatter:on
    // RZ;

    private final int bits;
    private final float w1VoltageMultiplier;

    Instruction(int bits, float w1VoltageMultiplier) {

      this.bits = bits;
      this.w1VoltageMultiplier = w1VoltageMultiplier;
    }

    public int getBits() {

      return bits;
    }

    public float getW1VoltageMultiplier() {

      return w1VoltageMultiplier;
    }

  }

  public SynapsePreferences.Waveform getWaveform() {

    return waveform;
  }

  public void setWaveform(SynapsePreferences.Waveform waveform) {

    this.waveform = waveform;
  }

  public double getCalculatedFrequency() {

    return calculatedFrequency;
  }

  public void setCalculatedFrequency(double calculatedFrequency) {

    this.calculatedFrequency = calculatedFrequency;
  }

  public double getVy() {

    return vy;
  }

  public DWFProxy getdWFProxy() {

    return dWFProxy;
  }

  public void setdWFProxy(DWFProxy dWFProxy) {

    this.dWFProxy = dWFProxy;
  }

  public double getAmplitude() {

    return amplitude;
  }

  public void setAmplitude(double amplitude) {

    this.amplitude = amplitude;
  }
}
