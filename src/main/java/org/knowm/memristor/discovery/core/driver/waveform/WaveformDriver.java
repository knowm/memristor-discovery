package org.knowm.memristor.discovery.core.driver.waveform;

import org.knowm.memristor.discovery.core.driver.Driver;

public abstract class WaveformDriver extends Driver {

  protected final double phase;
  protected final double frequency;

  public WaveformDriver(String id, double dcOffset, double phase, double amplitude, double frequency) {
    super(id, dcOffset, amplitude, frequency);
    this.phase = phase;
    this.frequency = frequency;

  }

  public double getPhase() {
    return phase;
  }

  public double getFrequency() {
    return frequency;
  }

}
