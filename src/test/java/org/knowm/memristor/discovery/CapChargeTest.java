package org.knowm.memristor.discovery;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class CapChargeTest {

  public static void main(String[] args) {

    double[] time = new double[500];
    double[] signal = new double[500];

    double Rs = 220E3;
    // double Rs = 5E3;

    double Rm = 382E3;
    // double Rm = 38.7E3;
    double C = .160E-9;//350pF
    double rc = Rm * C;

    double Vmax = .07 - .07 * (Rm / (Rs + Rm));
    // double Vmax = .07;

    for (int i = 0; i < time.length; i++) {
      double t = i * .5E-7;
      time[i] = (t) / 1E-6;
      signal[i] = Vmax * (1 - Math.exp(-t / (rc)));
    }

    XYChart chart = QuickChart.getChart("C=" + C + ", R=" + Rm, "X", "Y", "y(x)", time, signal);

    // Show it
    new SwingWrapper(chart).displayChart();

  }

}
