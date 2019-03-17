package org.knowm.memristor.discovery.core.driver.pulse;

import org.knowm.memristor.discovery.core.driver.PulseDriver;

public class TrianglePulse extends PulseDriver {

  double dYdt;
  double halfPulseWidth;

  public TrianglePulse(String id, double dcOffset, double pulseWidthInNS, double dutyCycle, double amplitude) {
    super(id, dcOffset, pulseWidthInNS, dutyCycle, amplitude);

    this.halfPulseWidth = pulseWidth / 2;
    this.dYdt = amplitude / (pulseWidth / 2);

  }

  @Override
  public double getSignal(double time) {

    double t = time % getPeriod();

    if (t < halfPulseWidth) {

      return dYdt * t + dcOffset;

    } else if (t > halfPulseWidth && t < pulseWidth) {
      return amplitude - (dYdt * (t - halfPulseWidth)) + dcOffset;
    } else {
      return dcOffset;
    }
  }

}
