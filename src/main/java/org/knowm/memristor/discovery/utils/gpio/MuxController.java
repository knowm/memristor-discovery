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
package org.knowm.memristor.discovery.utils.gpio;

/**
 * @author alexnugent
 */
public class MuxController {

  public enum Destination {

    A, B, Y, OUT
  }

  // default configuration set up to measure voltage drop across selected memristors and the series resistor.
  private Destination w1 = Destination.A;
  private Destination w2 = Destination.OUT;
  private Destination scope1 = Destination.A;
  private Destination scope2 = Destination.B;

  @Override
  public String toString() {

    return "MuxConfig: w1->" + w1 + ", w2->" + w2 + ", scope1->" + scope1 + ", scope2->" + scope2;
  }

  /*
   * MUX DIO PINOUT
   * Order ==> W2, W1, 2+, 1+
   * 00 OUT
   * 10 Y
   * 01 A
   * 11 B
   */
  public int getGPIOConfig() {

    String bits = toBits(w2) + toBits(w1) + toBits(scope2) + toBits(scope1) + "00000000";

    return Integer.parseInt(bits, 2);
  }

  private String toBits(Destination d) {

    if (d == Destination.A) {
      return "01";
    } else if (d == Destination.B) {
      return "11";
    } else if (d == Destination.Y) {
      return "10";
    } else if (d == Destination.OUT) {
      return "00";
    }
    return "00";

  }

  public Destination getW1() {

    return w1;
  }

  public void setW1(Destination w1) {

    this.w1 = w1;
  }

  public Destination getW2() {

    return w2;
  }

  public void setW2(Destination w2) {

    this.w2 = w2;
  }

  public Destination getScope1() {

    return scope1;
  }

  public void setScope1(Destination scope1) {

    this.scope1 = scope1;
  }

  public Destination getScope2() {

    return scope2;
  }

  public void setScope2(Destination scope2) {

    this.scope2 = scope2;
  }

}
