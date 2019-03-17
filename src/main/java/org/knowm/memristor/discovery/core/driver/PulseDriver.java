package org.knowm.memristor.discovery.core.driver;

public abstract class PulseDriver extends Driver {

  protected final double pulseWidth;
  protected final double dutyCycle;

  public PulseDriver(String id, double dcOffset, double pulseWidthInNS, double dutyCycle, double amplitude) {
    super(id, dcOffset, amplitude, 1 / (pulseWidthInNS * 1E-9 / dutyCycle));
    this.pulseWidth = pulseWidthInNS * 1E-9;
    this.dutyCycle = dutyCycle;

  }

  public double getPulseWidth() {
    return pulseWidth;
  }

  public double getDutyCycle() {
    return dutyCycle;
  }

}
