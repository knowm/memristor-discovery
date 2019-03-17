package org.knowm.memristor.discovery.core.driver.pulse;

import org.knowm.memristor.discovery.core.driver.PulseDriver;

public class SquarePulse extends PulseDriver {

  public SquarePulse(String id, double dcOffset, double pulseWidth, double dutyCycle, double amplitude) {
    super(id, dcOffset, pulseWidth, dutyCycle, amplitude);

  }

  @Override
  public double getSignal(double time) {
    double t = time % getPeriod();
    if (t < pulseWidth) {
      return dcOffset + amplitude;
    } else {
      return dcOffset;
    }

  }

}
