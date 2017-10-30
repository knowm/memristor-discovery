/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.logic;

/**
 * @author alexnugent
 */
public class TraceDatum {

  public final double vy_a;
  public final double ga_a;
  public final double gb_a;

  public final double vy_b;
  public final double ga_b;
  public final double gb_b;

  public TraceDatum(double vy_a, double ga_a, double gb_a, double vy_b, double ga_b, double gb_b) {

    this.vy_a = vy_a;
    this.ga_a = ga_a;
    this.gb_a = gb_a;
    this.vy_b = vy_b;
    this.ga_b = ga_b;
    this.gb_b = gb_b;
  }

}
