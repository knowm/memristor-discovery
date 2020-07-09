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

public class Arbitrary extends WaveformDriver {

  private final double[] activePhases;

  /**
   * Constructor
   *
   * @param matchingSourceId
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   * @param activePhases
   */
  public Arbitrary(
      String matchingSourceId,
      double dcOffset,
      double phase,
      double amplitude,
      double frequency,
      double[] activePhases) {

    super(matchingSourceId, dcOffset, phase, amplitude, frequency);
    this.activePhases = activePhases;
  }

  @Override
  public double getSignal(double time) {

    double T = 1 / frequency;
    double remainderTime = (time + phase) % T;
    boolean isActive = false;
    for (int i = 0; i < activePhases.length; i = i + 2) {
      double start = activePhases[i];
      double end = activePhases[i + 1];
      if (remainderTime >= T * start && remainderTime < T * end) {
        isActive = true;
      }
    }
    if (isActive) {

      return dcOffset + amplitude;
    } else {
      return 0.0;
    }
  }
}
