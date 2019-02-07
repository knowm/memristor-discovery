/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
 *
 * This package also includes various components that are not part of
 * Memristor-Discovery itself:
 *
 * * `Multibit`: Copyright 2011 multibit.org, MIT License
 * * `SteelCheckBox`: Copyright 2012 Gerrit, BSD license
 *
 * Knowm, Inc. holds copyright
 * and/or sufficient licenses to all components of the Memristor-Discovery
 * package, and therefore can grant, at its sole discretion, the ability
 * for companies, individuals, or organizations to create proprietary or
 * open source (even if not GPL) modules which may be dynamically linked at
 * runtime with the portions of Memristor-Discovery which fall under our
 * copyright/license umbrella, or are distributed under more flexible
 * licenses than GPL.
 *
 * The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * If you have any questions regarding our licensing policy, please
 * contact us at `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.utils;

import java.util.List;

/**
 * @author alexnugent
 */
public class AveMaxMinVar {

  private float max = 0;
  private int max_idx = 0;

  private float min = 0;
  private int min_idx = 0;

  private float ave = 0;
  private float var = 0;

  public AveMaxMinVar(List<Double> values) {

    float[] floatValues = new float[values.size()];
    for (int i = 0; i < floatValues.length; i++) {
      floatValues[i] = values.get(i).floatValue();
    }

    init(floatValues);

  }

  public AveMaxMinVar(float[] values) {

    init(values);
  }

  private void init(float[] values) {

    max = values[0];
    min = values[0];

    for (int i = 0; i < values.length; i++) {

      if (values[i] > max) {
        max = values[i];
        max_idx = i;
      }

      if (values[i] < min) {
        min = values[i];
        min_idx = i;
      }

      ave += values[i];
    }
    ave /= values.length;

    for (int i = 0; i < values.length; i++) {
      var += Math.pow(values[i] - ave, 2);
    }
    var /= values.length;

  }

  public float getMax() {

    return max;
  }

  public float getMin() {

    return min;
  }

  public float getAve() {

    return ave;
  }

  public float getVar() {

    return var;
  }

  public float getStd() {

    return (float) Math.sqrt(getVar());
  }

  public int getMaxIndex() {

    return max_idx;
  }

  public int getMinIndex() {

    return min_idx;
  }

}
