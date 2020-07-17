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
package org.knowm.memristor.discovery.core;

import java.io.Serializable;

/** @author alexnugent */
public class ExpRunAve implements Serializable {

  private float x; // the value
  private final float k; // the adaptation rate
  private final float kp; // 1-k

  /**
   * Constructor
   *
   * @param initialValue
   * @param k
   */
  public ExpRunAve(float initialValue, float k) {

    this.x = initialValue;
    this.k = k;
    this.kp = 1 - k;
  }

  public float getValue() {

    return this.x;
  }

  public float getK() {

    return this.k;
  }

  public float update(float input) {

    x = kp * x + k * input;

    return x;
  }
}
