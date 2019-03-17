/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.core.driver.waveform;

import org.knowm.memristor.discovery.core.driver.Driver;

/** @author timmolter */
public class TriangleUpDown extends WaveformDriver {

  /**
   * Constructor
   *
   * @param name
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   */
  public TriangleUpDown(String name, double dcOffset, double phase, double amplitude, double frequency) {

    super(name, dcOffset, phase, amplitude, frequency);
  }

  @Override
  public double getSignal(double time) {

    double T = 1 / frequency;
    double remainderTime = (time + phase) % T;

    // up phase
    if (0 <= (remainderTime) && (remainderTime) * T < .25 / frequency * T) {
      return 4 * frequency * amplitude * (remainderTime) + dcOffset;
    }

    // up phase
    else if (.75 / frequency * T <= (remainderTime) * T && (remainderTime) * T < 1.0 / frequency * T) {
      return 4 * frequency * amplitude * (remainderTime) - 4 * amplitude + dcOffset;
    }

    // down phase
    else {
      return -4 * frequency * amplitude * (remainderTime) + 2 * amplitude + dcOffset;
    }
  }
}
