package org.knowm.memristor.discovery.core.driver.pulse;

import org.knowm.memristor.discovery.core.driver.PulseDriver;

public class QuarterSinePulse extends PulseDriver {

  private double freq;

  public QuarterSinePulse(String id, double dcOffset, double pulseWidthInNS, double dutyCycle, double amplitude) {
    super(id, dcOffset, pulseWidthInNS, dutyCycle, amplitude);

    this.freq = 1 / (4 * pulseWidth);
  }

  @Override
  public double getSignal(double time) {
    double t = time % getPeriod();
    if (t < pulseWidth) {

      return amplitude * Math.sin(2 * Math.PI * freq * t) + dcOffset;

    } else {
      return dcOffset;
    }
  }

}
