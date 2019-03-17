package org.knowm.memristor.discovery.core.driver.pulse;

import org.knowm.memristor.discovery.core.driver.PulseDriver;

public class SquareSmoothPulse extends PulseDriver {

  private double dYdt;
  private double riseTime;
  private double fallTime;

  public SquareSmoothPulse(String id, double dcOffset, double pulseWidthInNS, double dutyCycle, double amplitude) {
    super(id, dcOffset, pulseWidthInNS, dutyCycle, amplitude);

    this.riseTime = this.pulseWidth * .1;
    this.dYdt = this.amplitude / riseTime;
    this.fallTime = this.pulseWidth - this.riseTime;

    System.out.println("riseTime=" + riseTime);
    System.out.println("dYdt=" + dYdt);
    System.out.println("fallTime=" + fallTime);
    System.out.println("pulseWidth=" + pulseWidth);

  }

  @Override
  public double getSignal(double time) {
    double t = time % getPeriod();
    if (t < riseTime) {
      return dcOffset + t * dYdt;
    } else if (t > riseTime && t < fallTime) {
      return amplitude + dcOffset;
    } else if (t > fallTime && t < pulseWidth) {
      return dcOffset + amplitude - (t - fallTime) * dYdt;
    } else {
      return dcOffset;
    }

  }

}
