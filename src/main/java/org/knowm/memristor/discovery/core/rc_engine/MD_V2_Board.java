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
package org.knowm.memristor.discovery.core.rc_engine;

import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.netlist.NetlistCapacitor;
import org.knowm.jspice.netlist.NetlistDCVoltage;
import org.knowm.jspice.netlist.NetlistResistor;

public class MD_V2_Board extends Netlist {

  public MD_V2_Board(double initV2, double Rm, double Rs) {

    //    addNetListComponent(new NetlistDCVoltage("V1", 0.0, "1", "0"));
    //    addNetListComponent(new NetlistResistor("Rm", Rm, "1", "2"));
    //    addNetListComponent(new NetlistCapacitor("C_B2Gnd", C_B2Gnd, 0, "2", "0"));
    //    addNetListComponent(new NetlistResistor("Rs", Rs, "2", "0"));
    //    addNetListComponent(new NetlistResistor("R_1XScope", 1_000_000, "2", "0"));

    addNetListComponent(new NetlistDCVoltage("V1", 0, "1", "0"));
    addNetListComponent(new NetlistResistor("Rm", Rm, "0", "2"));
    addNetListComponent(new NetlistResistor("Rs", Rs, "2", "1"));
    addNetListComponent(new NetlistResistor("R_1XScope1", 1_000_000, "2", "0")); // scope
    addNetListComponent(new NetlistResistor("R_1XScope2", 1_000_000, "1", "0")); // scope
    addNetListComponent(new NetlistCapacitor("C_B2Gnd", 180E-12, initV2, "2", "0"));

    setInitialConditions(true);
  }
}
