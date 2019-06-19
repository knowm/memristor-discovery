package org.knowm.memristor.discovery.circuit;

import java.util.ArrayList;
import java.util.List;
import org.knowm.jspice.JSpice;
import org.knowm.jspice.netlist.Netlist;
import org.knowm.jspice.simulate.SimulationPlotData;
import org.knowm.jspice.simulate.SimulationResult;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;
import org.knowm.jspice.simulate.transientanalysis.driver.DC;
import org.knowm.memristor.discovery.core.rc_engine.MD_V0_V1_Board;
import org.knowm.memristor.discovery.core.rc_engine.MD_V2_Board;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class RC_ResistanceComputerCalibration {

  public static void main(String[] args) {

    // V0_V1();
    V2();
  }

  public static void V2() {

    double seriesResistor = 5000;
    double memristorResistance = 10100;

    double parasiticCapacitance = 140E-12;

    double readPulseWidth = 25E-6;
    double readPulseAmplitude = .1;
    double simStepSize = readPulseWidth / 20;

    TransientConfig transientConfig =
        new TransientConfig(
            "" + readPulseWidth, "" + simStepSize, new DC("V1", -readPulseAmplitude));

    Netlist netlist = new MD_V2_Board(memristorResistance, seriesResistor, parasiticCapacitance);

    netlist.setSimulationConfig(transientConfig);

    SimulationResult simulationResult = JSpice.simulate(netlist);
    SimulationPlotData simulationData = simulationResult.getSimulationPlotDataMap().get("V(2)");

    // V2 board is inverted
    List<Number> invertedData = new ArrayList<>();
    for (int i = 0; i < simulationData.getyData().size(); i++) {
      invertedData.add(-simulationData.getyData().get(i).doubleValue());
    }

    // plot it

    // Create Chart
    XYChart chart =
        QuickChart.getChart(
            "Sample Chart", "X", "Y", "y(x)", simulationData.getxData(), invertedData);
    chart.getStyler().setYAxisMax(.1);
    chart.getStyler().setYAxisMin(0.0);

    // Show it
    new SwingWrapper(chart).displayChart();
  }

  public static void V0_V1() {

    double seriesResistor = 5000;
    double memristorResistance = 10000;
    double parasiticCapacitance = 140E-12;

    double readPulseWidth = 25E-6;
    double readPulseAmplitude = .1;
    double simStepSize = readPulseWidth / 20;

    TransientConfig transientConfig =
        new TransientConfig(
            "" + readPulseWidth, "" + simStepSize, new DC("V1", readPulseAmplitude));

    Netlist netlist = new MD_V0_V1_Board(memristorResistance, seriesResistor, parasiticCapacitance);

    netlist.setSimulationConfig(transientConfig);

    SimulationResult simulationResult = JSpice.simulate(netlist);
    SimulationPlotData simulationData = simulationResult.getSimulationPlotDataMap().get("V(2)");

    // plot it

    // Create Chart
    XYChart chart =
        QuickChart.getChart(
            "Sample Chart", "X", "Y", "y(x)", simulationData.getxData(), simulationData.getyData());

    // Show it
    new SwingWrapper(chart).displayChart();
  }
}
