/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2020 Knowm Inc. www.knowm.org
 *
 * <p>This package also includes various components that are not part of Memristor-Discovery itself:
 *
 * <p>* `Multibit`: Copyright 2011 multibit.org, MIT License * `SteelCheckBox`: Copyright 2012
 * Gerrit, BSD license
 *
 * <p>Knowm, Inc. holds copyright and/or sufficient licenses to all components of the
 * Memristor-Discovery package, and therefore can grant, at its sole discretion, the ability for
 * companies, individuals, or organizations to create proprietary or open source (even if not GPL)
 * modules which may be dynamically linked at runtime with the portions of Memristor-Discovery which
 * fall under our copyright/license umbrella, or are distributed under more flexible licenses than
 * GPL.
 *
 * <p>The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * <p>If you have any questions regarding our licensing policy, please contact us at
 * `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.core.driver.pulse;

import org.knowm.memristor.discovery.core.driver.PulseDriver;

public class SquareLongDecayPulse extends PulseDriver {

  private double d;
  private double halfPulseWidth;

  public SquareLongDecayPulse(
      String id, double dcOffset, double pulseWidthInNS, double dutyCycle, double amplitude) {
    super(id, dcOffset, pulseWidthInNS, dutyCycle, amplitude);

    halfPulseWidth = this.pulseWidth / 2;
    d = 4.605 / (10 * pulseWidth);
  }

  @Override
  public double getSignal(double time) {
    double t = time % getPeriod();
    if (t < halfPulseWidth) {
      return dcOffset + amplitude;
    } else if (t > halfPulseWidth) {

      return amplitude * Math.exp(-d * (t - halfPulseWidth));
    } else {
      return dcOffset;
    }
  }
}
