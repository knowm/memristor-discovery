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
package org.knowm.memristor.discovery.utils.driver;

/**
 * @author timmolter
 */
public abstract class Driver {

  protected final String id;
  protected final double dcOffset;
  protected final double phase;
  protected final double amplitude;
  protected final double frequency;

  /**
   * Constructor
   *
   * @param id
   * @param dcOffset
   * @param phase
   * @param amplitude
   * @param frequency
   */
  public Driver(String id, double dcOffset, double phase, double amplitude, double frequency) {

    this.id = id;
    this.dcOffset = dcOffset;
    this.phase = phase;
    this.amplitude = amplitude;
    this.frequency = frequency;
  }

  public String getId() {

    return id;
  }

  public double getDcOffset() {

    return dcOffset;
  }

  public double getPhase() {

    return phase;
  }

  public double getAmplitude() {

    return amplitude;
  }

  public double getFrequency() {

    return frequency;
  }

  public abstract double getSignal(double time);

}
