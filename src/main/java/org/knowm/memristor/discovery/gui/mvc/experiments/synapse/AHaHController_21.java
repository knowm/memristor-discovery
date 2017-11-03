package org.knowm.memristor.discovery.gui.mvc.experiments.synapse;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlModel;
import org.knowm.memristor.discovery.utils.Util;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;

/**
 * Created by timmolter on 5/25/17.
 */
public class AHaHController_21 {

  private DWFProxy dWFProxy;
  private ControlModel controlModel;
  private final double MIN_V_RESOLUTION = .0025;

  private double vy;// last read value
  private double ga;
  private double gb;

  public AHaHController_21(ControlModel controlModel) {

    this.controlModel = controlModel;
  }

  public void executeInstruction(Instruction instruction) {

    if (instruction == Instruction.FF_RH) {

      execute(Instruction.FF);
      // NOTE: Delay between executions as measured by scope (@MANC Cave) is ~6ms.
      execute(Instruction.RHbdn);

      return;
    }
    else if (instruction == Instruction.FF_RL) {

      execute(Instruction.FF);
      // NOTE: Delay between executions as measured by scope (@MANC Cave) is ~6ms
      execute(Instruction.RLadn);

      return;
    }
    else if (instruction == Instruction.FF_RA) {

      execute(Instruction.FF);

      if (vy >= 0) {
        execute(Instruction.RLadn);
      }
      else {
        execute(Instruction.RHbdn);
      }

      return;
    }
    else if (instruction == Instruction.FF_RU) {

      execute(Instruction.FF);

      if (vy >= 0) {
        execute(Instruction.RHbdn);
      }
      else {
        execute(Instruction.RLadn);
      }

      return;
    }
    else {
      execute(instruction);
    }
  }

  private void execute(Instruction instruction) {

    System.out.println("Instruction: " + instruction);

    // 1. the IO-bits are set
    dWFProxy.setUpper8IOStates(instruction.getBits());

    double W1Amplitude = controlModel.getAmplitude() * instruction.getW1PulseAmplitudeMultiplier();

    // hard-set the FFLV amplitude, as this is used for reads and should never change and be low that adaptation never occures.
    if (instruction == Instruction.FFLV) {
      W1Amplitude = .1f;
    }
    else if (instruction == Instruction.RFLV) {
      W1Amplitude = -.1f;
    }

    double[] W1 = WaveformUtils.generateCustomWaveform(controlModel.getWaveform(), W1Amplitude, controlModel.getCalculatedFrequency());

    if (instruction == Instruction.FFLV) {

      dWFProxy.getDwf().startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency() * 300, 300 * 1);
      dWFProxy.waitUntilArmed();
      dWFProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, W1);
      dWFProxy.getDwf().startPulseTrain(DWF.WAVEFORM_CHANNEL_1);

      boolean success = dWFProxy.capturePulseData(controlModel.getCalculatedFrequency(), 1);
      if (success) {
        setVy(W1Amplitude);
      }
      else {
        System.out.println("capture failed!");
      }

    }
    else {
      dWFProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, W1);
      // note w2 amplitude is zero (gnd).
      double[] W2 = WaveformUtils.generateCustomWaveform(controlModel.getWaveform(), 0.0, controlModel.getCalculatedFrequency());
      dWFProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_2, controlModel.getCalculatedFrequency(), 0, 1, W2);
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

  private void setVy(double W1Amplitude) {

    int validSamples = dWFProxy.getDwf().FDwfAnalogInStatusSamplesValid();
    double peakV1 = Util.maxAbs(dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples));
    double peakV2 = Util.maxAbs(dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples));

    // note: if V1 is less than resolution of scope, the measurments will be useless
    double vb = peakV2;
    this.vy = (vb - peakV1 - (W1Amplitude - peakV1) / 2.0) / (W1Amplitude - peakV1);

    System.out.println("vy=" + vy);

    if (peakV1 > MIN_V_RESOLUTION) {

      double I = peakV1 / controlModel.getSeriesResistance();
      double va = W1Amplitude - ExperimentPreferences.R_SWITCH * I;
      double vc = peakV1 + ExperimentPreferences.R_SWITCH * I;

      System.out.println("va=" + va);
      System.out.println("vb=" + vb);
      System.out.println("vc=" + vc);
      System.out.println("I=" + I);

      if ((va - vb > MIN_V_RESOLUTION)) {
        this.ga = I / (va - vb);

      }
      else {
        System.out.println("voltage drop across Ma too small too measure.");
        this.ga = Double.NaN;
      }

      if (((vb - vc) > MIN_V_RESOLUTION)) {
        this.gb = 1 / ((vb - vc) / I);

      }
      else {
        System.out.println("voltage drop across Mb too small too measure.");
        this.gb = Double.NaN;
      }

    }
    else {

      System.out.println("Current too low to measure. peakV1=" + peakV1);
      this.ga = Double.NaN;
      this.gb = Double.NaN;
    }

  }

  public enum Instruction {

    /*
     * NOTE: Charge injection from the MUX can affect voltage across memristors when routing the scopes.
     * The scope configuration [1011] is chosen because this is the configuration used to measure the state of the synapse,
     * resulting in minor charge injection. It is unclear if charge injection for waveform generators is an issue, but currently (7/29/2017)
     * appears to not be.
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
    FF(0b1101_1011_0000_0000, 2.0f),
    RHbdn(0b1011_1011_0000_0000, 1f), // w2-->Y, w1-->B
    RLadn(0b1001_1011_0000_0000, -1f), // w2-->Y, w1-->A
    RFLV(0b0001_1011_0000_0000, -.1f),
    RF(0b1101_1011_0000_0000, -2.0f),
    RHaup(0b1001_1011_0000_0000, 1),
    RLbup(0b1011_1011_0000_0000, -1);

    // @formatter:on
    // RZ;

    private final int bits;
    private final float pulseAmplitudeMultiplier;

    Instruction(int bits, float pulseAmplitudeMultiplier) {

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

  public double getVy() {

    // this is a quick way to deal with measurement noise.
    if (vy > .5) {
      return .5;
    }
    else if (vy < -.5) {
      return -.5;
    }

    return vy;
  }

  public DWFProxy getdWFProxy() {

    return dWFProxy;
  }

  public void setdWFProxy(DWFProxy dWFProxy) {

    this.dWFProxy = dWFProxy;
  }

  /**
   * @return the controlModel
   */
  public ControlModel getControlModel() {

    return controlModel;
  }

  /**
   * @return the ga
   */
  public double getGa() {

    return ga;
  }

  /**
   * @param ga the ga to set
   */
  public void setGa(double ga) {

    this.ga = ga;
  }

  /**
   * @return the gb
   */
  public double getGb() {

    return gb;
  }

  /**
   * @param gb the gb to set
   */
  public void setGb(double gb) {

    this.gb = gb;
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
