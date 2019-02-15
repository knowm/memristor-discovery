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
package org.knowm.memristor.discovery.utils;

/** Created by timmolter on 2/20/17. */
public class PostProcessDataUtils {

  /**
   * The data is a bit weird, as what's captured is a long window of "idle" voltage before and after
   * the pulses. We clean that now...
   *
   * @param v1
   * @param v2
   * @param v1Threshold
   * @param windowBuffer - how many data points outside the window should be included
   * @return
   */
  public static double[][] trimIdleData(
      double[] v1, double[] v2, double v1Threshold, int windowBuffer) {

    double vThresholdAbs = Math.abs(v1Threshold);
    int startIndex = 0;
    for (int i = 0; i < v1.length; i++) {
      if (Math.abs(v1[i]) > vThresholdAbs) {
        startIndex = Math.max(startIndex, i - windowBuffer);
        break;
      }
    }
    // System.out.println("startIndex = " + startIndex);
    int endIndex = v1.length - 1;
    for (int i = v1.length - 1; i > 0; i--) {
      if (Math.abs(v1[i]) > vThresholdAbs) {
        endIndex = Math.min(endIndex, i + windowBuffer);
        break;
      }
    }
    // System.out.println("endIndex = " + endIndex);

    int bufferLength = endIndex - startIndex;

    double[] V1Cleaned = new double[bufferLength];
    double[] V2Cleaned = new double[bufferLength];
    for (int i = 0; i < bufferLength; i++) {
      V1Cleaned[i] = v1[i + startIndex];
      V2Cleaned[i] = v2[i + startIndex];
    }
    return new double[][] {V1Cleaned, V2Cleaned};
  }

  /**
   * Set all V2 data to zero where V1 is less than a given threshold
   *
   * @param v1
   * @param v2
   * @return
   */
  public static double[] zeroIdleData(double[] v1, double[] v2, double v1Threshold) {

    double vThresholdAbs = Math.abs(v1Threshold);

    double[] V2Zeroed = new double[v1.length];
    V2Zeroed[0] = 0;
    V2Zeroed[V2Zeroed.length - 1] = 0;
    for (int i = 1; i < V2Zeroed.length - 1; i++) {
      if (Math.abs(v1[i]) > vThresholdAbs) {
        V2Zeroed[i] = v2[i];
      } else {
        V2Zeroed[i - 1] = 0;
        V2Zeroed[i] = 0;
        V2Zeroed[i + 1] = 0;
      }
    }
    return V2Zeroed;
  }

  public static double[] getV1MinusV2(double[] v1, double[] v2) {

    double[] V2MinusV1 = new double[v1.length];
    for (int i = 0; i < V2MinusV1.length; i++) {
      V2MinusV1[i] = v1[i] - v2[i];
    }
    return V2MinusV1;
  }
}
