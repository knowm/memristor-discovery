/**
 * jspice is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
 *
 * Knowm, Inc. holds copyright
 * and/or sufficient licenses to all components of the jspice
 * package, and therefore can grant, at its sole discretion, the ability
 * for companies, individuals, or organizations to create proprietary or
 * open source (even if not GPL) modules which may be dynamically linked at
 * runtime with the portions of jspice which fall under our
 * copyright/license umbrella, or are distributed under more flexible
 * licenses than GPL.
 *
 * The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * If you have any questions regarding our licensing policy, please
 * contact us at `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.core.rc_engine;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistCapacitor;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistResistor;

public class MDV1Board extends Netlist {

  public MDV1Board(double Rm, double Rs, double C_B2Gnd) {

    addNetListComponent(new NetlistDCVoltage("V1", 0.0, "1", "0"));
    addNetListComponent(new NetlistResistor("Rm", Rm, "1", "2"));
    addNetListComponent(new NetlistCapacitor("C_B2Gnd", C_B2Gnd, 0, "2", "0"));
    addNetListComponent(new NetlistResistor("Rs", Rs, "2", "0"));

    addNetListComponent(new NetlistResistor("R_1XScope", 1_000_000, "2", "0"));

  }
}
