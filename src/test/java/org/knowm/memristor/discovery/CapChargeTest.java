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
package org.knowm.memristor.discovery;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class CapChargeTest {

  public static void main(String[] args) {

    double[] time = new double[500];
    double[] signal = new double[500];

    double Rs = 220E3;
    // double Rs = 5E3;

    double Rm = 382E3;
    // double Rm = 38.7E3;
    double C = .160E-9; // 350pF
    double rc = Rm * C;

    double Vmax = .07 - .07 * (Rm / (Rs + Rm));
    // double Vmax = .07;

    for (int i = 0; i < time.length; i++) {
      double t = i * .5E-7;
      time[i] = (t) / 1E-6;
      signal[i] = Vmax * (1 - Math.exp(-t / (rc)));
    }

    XYChart chart = QuickChart.getChart("C=" + C + ", R=" + Rm, "X", "Y", "y(x)", time, signal);

    // Show it
    new SwingWrapper(chart).displayChart();
  }
}
