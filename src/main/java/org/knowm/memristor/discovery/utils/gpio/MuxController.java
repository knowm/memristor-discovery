/**
 * Copyright (C) 2013 Knowmtech  http://knowmtech.com
 *
 * ***IMPORTANT*** THIS CODE IS PROPRIETARY!!! ABSOLUTELY NO DUPLICATION OR DISTRIBUTION IS PERMITTED WITHOUT EXPRESS WRITTEN PERMISSION FROM:
 * M. ALEXANDER NUGENT CONSULTING 22B STACY RD, SANTA FE NM 87585 (505)-988-7016 i@alexnugent.name
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
    }
    else if (d == Destination.B) {
      return "11";
    }
    else if (d == Destination.Y) {
      return "10";
    }
    else if (d == Destination.OUT) {
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
