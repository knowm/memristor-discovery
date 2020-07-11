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
