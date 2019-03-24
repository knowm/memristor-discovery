package org.knowm.memristor.discovery.core.rc_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knowm.jspice.JSpice;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.SimulationPlotData;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.DC;
import org.knowm.jspice.simulate.transientanalysis.driver.Square;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

/**
 * used to measure memristor resistance at low currents, when pulse capture results in capacitive charge/discharge instead of steady-state voltage
 * divider. Given board series resistor and parasitic capacitance, will return the estimated resistance given the read square pulse trace.
 * 
 * @author alexnugent
 */
public class RC_ResistanceComputer {

  private double parasiticCapacitance;
  private double seriesResistor;
  private double readPulseAmplitude;
  private double readPulseWidth;

  private double[] voltage;
  private double[] resistance;

  //  public static void main(String[] args) {
  //
  //    RC_ResistanceComputer rc = new RC_ResistanceComputer(.1, 25E-6, 50_000, 140E-12);
  //    rc.loadTrace();
  //
  //    long startTime = System.currentTimeMillis();
  //
  //    System.out.println(".003-->" + rc.getRFromV(.003));
  //    System.out.println("search time = " + (System.currentTimeMillis() - startTime));
  //
  //  }

  public RC_ResistanceComputer(double readPulseAmplitude, double readPulseWidth, double seriesResistance, double parasiticCapacitance) {

    this.parasiticCapacitance = parasiticCapacitance;
    this.seriesResistor = seriesResistance;
    this.readPulseAmplitude = readPulseAmplitude;
    this.readPulseWidth = readPulseWidth;

    loadTrace();

  }

  public double getRFromV(double v) {

    for (int i = 0; i < voltage.length; i++) {
      if (this.voltage[i] <= v) {
        if (i == 0) {//edge case
          return resistance[0];
        }
        //linear interpolation between i and i-1.
        double dv = voltage[i - 1] - voltage[i];
        double r = (voltage[i - 1] - v) / dv;
        double dR = (resistance[i] - resistance[i - 1]) * r;
        double interpolation = resistance[i - 1] + dR;
        return interpolation;
      }
    }

    return resistance[resistance.length - 1];

  }

  public void loadTrace() {

    double Rinit = 1E2;
    double Rfinal = 1E8;

    List<Number> voltage = new ArrayList<>();
    List<Number> resistance = new ArrayList<>();

    double simStepSize = readPulseWidth / 20;
    TransientConfig transientConfig = new TransientConfig("" + readPulseWidth, "" + simStepSize, new DC("V1", readPulseAmplitude));
    for (double Rm = Rinit; Rm < Rfinal; Rm *= 1.05) {
      Netlist netlist = new MDV1Board(Rm, seriesResistor, parasiticCapacitance);
      netlist.setSimulationConfig(transientConfig);
      SimulationResult simulationResult = JSpice.simulate(netlist);
      SimulationPlotData simulationData = simulationResult.getSimulationPlotDataMap().get("V(2)");
      voltage.add(simulationData.getyData().get(simulationData.getyData().size() - 1));
      resistance.add(Rm);
    }

    this.voltage = new double[voltage.size()];
    this.resistance = new double[resistance.size()];

    for (int i = 0; i < this.resistance.length; i++) {
      this.voltage[i] = voltage.get(i).doubleValue();
      this.resistance[i] = resistance.get(i).doubleValue();
    }

  }

}
