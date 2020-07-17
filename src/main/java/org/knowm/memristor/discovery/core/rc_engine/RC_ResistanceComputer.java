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

import java.util.ArrayList;
import java.util.List;
import org.knowm.jspice.JSpice;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.SimulationPlotData;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.Pulse;

/**
 * used to measure memristor resistance at low currents, when pulse capture results in capacitive
 * charge/discharge instead of steady-state voltage divider. Given board series resistor and
 * parasitic capacitance, will return the estimated resistance given the read square pulse trace.
 *
 * @author alexnugent
 */
public class RC_ResistanceComputer {

  private double seriesResistor;
  private double readPulseAmplitude;
  private double readPulseWidth;
  private double driverOffset;
  private double readInitVoltage;

  private double[] voltage;
  private double[] resistance;
  int boardVersion;

  // public static void main(String[] args) {
  //
  // RC_ResistanceComputer rc = new RC_ResistanceComputer(.1, 25E-6, 50_000,
  // 140E-12);
  // rc.loadTrace();
  //
  // long startTime = System.currentTimeMillis();
  //
  // System.out.println(".003-->" + rc.getRFromV(.003));
  // System.out.println("search time = " + (System.currentTimeMillis() -
  // startTime));
  //
  // }

  public RC_ResistanceComputer(
      int boardVersion,
      double readPulseAmplitude,
      double readPulseWidth,
      double seriesResistance,
      double driverOffset,
      double readInitVoltage) {

    this.seriesResistor = seriesResistance;
    this.readPulseAmplitude = readPulseAmplitude;
    this.readPulseWidth = readPulseWidth;
    this.boardVersion = boardVersion;
    this.driverOffset = driverOffset;
    this.readInitVoltage = readInitVoltage;

    loadTrace();
  }

  public double getRFromV(double v) {

    if (boardVersion == 2) {
      for (int i = 0; i < voltage.length; i++) {

        // System.out.println(
        // "voltage[i]=" + voltage[i] + ", resistance[i]=" + resistance[i] + ", v=" +
        // v);
        // System.out.println("v=" + v + ", voltage[i]=" + voltage[i]);
        if (voltage[i] <= v) {
          if (i == 0) { // edge case
            return resistance[0];
          }
          // linear interpolation between i and i-1.
          double dv = voltage[i - 1] - voltage[i];
          double r = (voltage[i - 1] - v) / dv;
          double dR = (resistance[i] - resistance[i - 1]) * r;
          double interpolation = resistance[i - 1] + dR;

          // System.out.println("v=" + v + ", voltage[i]=" + voltage[i]);
          // System.out.println("interpolation=" + interpolation);

          return interpolation;
        }
      }

      return resistance[resistance.length - 1];
    } else {

      for (int i = 0; i < voltage.length; i++) {

        //        System.out.println(
        //            "voltage[i]=" + voltage[i] + ", resistance[i]=" + resistance[i] + ", v=" + v);
        //        System.out.println("v=" + v + ", voltage[i]=" + voltage[i]);

        if (voltage[i] <= v) {
          if (i == 0) { // edge case
            System.out.println("HERE");
            return resistance[0];
          }
          // linear interpolation between i and i-1.
          double dv = voltage[i - 1] - voltage[i];
          double r = (voltage[i - 1] - v) / dv;
          double dR = (resistance[i] - resistance[i - 1]) * r;
          double interpolation = resistance[i - 1] + dR;

          //          System.out.println("v=" + v + ", voltage[i]=" + voltage[i]);
          //          System.out.println("interpolation=" + interpolation);

          return interpolation;
        }
      }

      return resistance[resistance.length - 1];
    }
  }

  public void loadTrace() {

    //    double Rinit = 1E2;
    //    double Rfinal = 1E8;
    //
    //    List<Number> voltage = new ArrayList<>();
    //    List<Number> resistance = new ArrayList<>();
    //
    //    double simStepSize = readPulseWidth / 20;
    //    TransientConfig transientConfig =
    //        new TransientConfig(
    //            "" + readPulseWidth, "" + simStepSize, new DC("V1", readPulseAmplitude));
    //    for (double Rm = Rinit; Rm < Rfinal; Rm *= 1.025) {
    //
    //      Netlist netlist;
    //      if (boardVersion == 2) {
    //        netlist = new MD_V2_Board(Rm, seriesResistor, parasiticCapacitance);
    //      } else {
    //        netlist = new MD_V0_V1_Board(Rm, seriesResistor, parasiticCapacitance);
    //      }
    //
    //      netlist.setSimulationConfig(transientConfig);
    //      SimulationResult simulationResult = JSpice.simulate(netlist);
    //      SimulationPlotData simulationData =
    // simulationResult.getSimulationPlotDataMap().get("V(2)");
    //      voltage.add(simulationData.getyData().get(simulationData.getyData().size() - 1));
    //      resistance.add(Rm);
    //    }
    //
    //    this.voltage = new double[voltage.size()];
    //    this.resistance = new double[resistance.size()];
    //
    //    for (int i = 0; i < this.resistance.length; i++) {
    //      this.voltage[i] = voltage.get(i).doubleValue();
    //      this.resistance[i] = resistance.get(i).doubleValue();
    //    }
    ///////////
    double Rinit = 1E1;
    double Rfinal = 1E8;
    double phase = -readPulseWidth / 50;

    List<Number> voltage = new ArrayList<>();
    List<Number> resistance = new ArrayList<>();
    double simStepSize = readPulseWidth / 20;
    double frequency = 1 / readPulseWidth;

    Pulse pulse =
        new Pulse(
            "V1",
            driverOffset + readPulseAmplitude / 2,
            phase + "",
            readPulseAmplitude / 2,
            frequency + "",
            "1");

    TransientConfig transientConfig =
        new TransientConfig("" + (readPulseWidth - phase), "" + simStepSize, pulse);

    for (double Rm = Rinit; Rm < Rfinal; Rm *= 1.05) {

      Netlist netlist;
      if (boardVersion == 2) {
        netlist = new MD_V2_Board(readInitVoltage, Rm, seriesResistor);
      } else {
        netlist = new MD_V0_V1_Board(readInitVoltage, Rm, seriesResistor);
      }
      netlist.setSimulationConfig(transientConfig);
      SimulationResult simulationResult = JSpice.simulate(netlist);
      SimulationPlotData simulationData = simulationResult.getSimulationPlotDataMap().get("V(2)");
      voltage.add(simulationData.getyData().get(simulationData.getyData().size() - 1));
      resistance.add(Rm);
    }

    this.voltage = new double[voltage.size()];
    this.resistance = new double[resistance.size()];

    if (readPulseAmplitude < 0) {
      for (int i = 0; i < this.resistance.length; i++) {
        this.voltage[i] = voltage.get(i).doubleValue();
        this.resistance[i] = resistance.get(i).doubleValue();
      }
    } else { // lookup table needs to be reversed given method of access.
      int idx = 0;
      for (int i = this.resistance.length - 1; i >= 0; i--) {
        this.voltage[idx] = voltage.get(i).doubleValue();
        this.resistance[idx] = resistance.get(i).doubleValue();
        idx++;
      }
    }
  }

  // TODO remove this???
  public double getReadPulseAmplitude() {
    return readPulseAmplitude;
  }
}
