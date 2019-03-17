package org.knowm.memristor.discovery.core.driver.pulse;

import org.knowm.memristor.discovery.core.driver.PulseDriver;

public class SquareDecayPulse extends PulseDriver {

  private double d;
  private double halfPulseWidth;

  public SquareDecayPulse(String id, double dcOffset, double pulseWidthInNS, double dutyCycle, double amplitude) {
    super(id, dcOffset, pulseWidthInNS, dutyCycle, amplitude);

    halfPulseWidth = this.pulseWidth / 2;
    d = 4.605 / (pulseWidth);

  }

  @Override
  public double getSignal(double time) {
    double t = time % getPeriod();
    if (t < halfPulseWidth) {
      return dcOffset + amplitude;
    } else if (t > halfPulseWidth) {

      return amplitude * Math.exp(-d * (t - halfPulseWidth));
    }

    else {
      return dcOffset;
    }

  }

}
