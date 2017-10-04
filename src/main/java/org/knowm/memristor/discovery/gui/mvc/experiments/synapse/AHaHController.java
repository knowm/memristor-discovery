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
public class AHaHController {

  private DWFProxy dWFProxy;

  ControlModel controlModel;

  // private SynapsePreferences.Waveform waveform;
  // private double calculatedFrequency;
  // private double amplitude;

  private double vy;// last read value
  private double ga;
  private double gb;

  public AHaHController(ControlModel controlModel) {

    this.controlModel = controlModel;
  }

  public void executeInstruction(Instruction instruction) {

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

    if (instruction == Instruction.FFLV || instruction == Instruction.RFLV) {

      dWFProxy.getDwf().startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency() * 300, 300 * 1);
      dWFProxy.waitUntilArmed();
      dWFProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, 1, W1);
      dWFProxy.getDwf().startPulseTrain(DWF.WAVEFORM_CHANNEL_1);

      boolean success = dWFProxy.capturePulseData(controlModel.getCalculatedFrequency(), 1);
      if (success) {
        int validSamples = dWFProxy.getDwf().FDwfAnalogInStatusSamplesValid();
        double[] v1 = dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
        double[] v2 = dWFProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
        double peakV1 = Util.maxAbs(v1);
        double peakV2 = Util.maxAbs(v2);

        double I = peakV1 / controlModel.getSeriesResistance();

        double va = W1Amplitude - ExperimentPreferences.R_SWITCH * I;
        double vb = peakV2;
        // double vc = vb - ExperimentPreferences.R_SWITCH * I;
        double vc = peakV1;

        this.ga = I / (va - vb);

        double r_mb = (vb - vc) / I - ExperimentPreferences.R_SWITCH;

        this.gb = 1 / (r_mb);
        this.vy = vb - vc;

        System.out.println("va=" + va);
        System.out.println("vb=" + vb);
        System.out.println("vc=" + vc);
        System.out.println("I=" + I);

      }
      else {
        System.out.println("did not capture!");
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
    FF(0b1101_1011_0000_0000, 1),
    RFLV(0b0001_1011_0000_0000, -.1f),
    RF(0b1101_1011_0000_0000, -1),

    RH_A_UP(0b1001_1011_0000_0000, 1),
    RH_B_DN(0b1011_1011_0000_0000, 1), // w2-->Y, w1-->B
    RL_B_UP(0b1011_1011_0000_0000, -1),
    RL_A_DN(0b1001_1011_0000_0000, -1); // w2-->Y, w1-->A



    ;
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
