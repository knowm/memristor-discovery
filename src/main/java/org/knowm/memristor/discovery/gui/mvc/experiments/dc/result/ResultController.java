/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.dc.result;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.knowm.memristor.discovery.MemristorDiscoveryPreferences;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCPreferences;

public class ResultController {

  private final ResultPanel resultPanel;
  private final ResultModel resultModel;

  /**
   * Constructor
   *
   * @param resultPanel
   * @param resultModel
   */
  public ResultController(ResultPanel resultPanel, ResultModel resultModel) {

    this.resultPanel = resultPanel;
    this.resultModel = resultModel;

    initGUIComponents();
    setUpViewEvents();

    // init waveform chart
    resultPanel.switch2WaveformChart();
  }

  public void initGUIComponents() {

    resultPanel.getCaptureButton().setSelected(true);
    //    resultPanel.getFreezeYAxisCheckBoxIV().setSelected(false);
    //    resultPanel.getFreezeYAxisCheckBoxGV().setSelected(false);
  }

  private void setUpViewEvents() {

    resultPanel
        .getCaptureButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                resultPanel.switch2CaptureChart();
              }
            });
    resultPanel
        .getIVButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                resultPanel.switch2IVChart();
              }
            });
    resultPanel
        .getGVButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                resultPanel.switch2GVChart();
              }
            });
    //    resultPanel.getFreezeYAxisCheckBoxIV().addActionListener(new ActionListener() {
    //
    //      @Override
    //      public void actionPerformed(ActionEvent e) {
    //
    //        if (resultPanel.getFreezeYAxisCheckBoxIV().isSelected()) {
    //          resultModel.setyMaxIV(resultPanel.getIVChartMax());
    //          resultModel.setyMinIV(resultPanel.getIVChartMin());
    //        } else {
    //          resultModel.setyMaxIV(null);
    //          resultModel.setyMinIV(null);
    //        }
    //      }
    //    });
    //    resultPanel.getFreezeYAxisCheckBoxGV().addActionListener(new ActionListener() {
    //
    //      @Override
    //      public void actionPerformed(ActionEvent e) {
    //
    //        if (resultPanel.getFreezeYAxisCheckBoxGV().isSelected()) {
    //          resultModel.setyMaxGV(resultPanel.getGVChartMax());
    //          resultModel.setyMinGV(resultPanel.getGVChartMin());
    //        } else {
    //          resultModel.setyMaxGV(null);
    //          resultModel.setyMinGV(null);
    //        }
    //      }
    //    });
    resultPanel
        .getCaptureButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                resultPanel.switch2CaptureChart();
              }
            });
  }

  public void updateWaveformChart(
      double[] timeData, double[] waveformAmplitudeData, double amplitude, int pulseWidth) {

    resultPanel.getWaveformChart().setTitle(getWaveformChartTitle(amplitude, pulseWidth));
    resultPanel
        .getWaveformChart()
        .updateXYSeries("waveform", timeData, waveformAmplitudeData, null);
    resultPanel.getWaveformChartPanel().revalidate();
    resultPanel.getWaveformChartPanel().repaint();
  }

  public void updateCaptureChartData(
      double[] timeData,
      double[] v1,
      double[] v2,
      double[] v1Minusv2,
      int pulseWidth,
      double amplitude) {

    resultPanel.getCaptureChart().setTitle(getVtChartTitle(amplitude, pulseWidth));
    resultPanel.getCaptureChart().updateXYSeries("V1(1+)", timeData, v1, null);
    resultPanel.getCaptureChart().updateXYSeries("V2(2+)", timeData, v2, null);
    resultPanel.getCaptureChart().updateXYSeries("V1-V2", timeData, v1Minusv2, null);
  }

  public void updateIVChartData(
      double[] vRM, double[] vM, double[] current, int pulseWidth, double amplitude) {

    resultPanel.getIvChart().getStyler().setYAxisMax(resultModel.getyMaxIV());
    resultPanel.getIvChart().getStyler().setYAxisMin(resultModel.getyMinIV());

    resultPanel.getIvChart().setTitle(getIVChartTitle(amplitude, pulseWidth));
    resultPanel.getIvChart().updateXYSeries("Resistor+Memristor", vRM, current, null);
    resultPanel.getIvChart().updateXYSeries("Memristor", vM, current, null);
  }

  public void updateGVChartData(
      double[] vRM, double[] vM, double[] conductance, int pulseWidth, double amplitude) {

    resultPanel.getGvChart().getStyler().setYAxisMax(resultModel.getyMaxGV());
    resultPanel.getGvChart().getStyler().setYAxisMin(0.0);
    resultPanel.getGvChart().setTitle(getGVChartTitle(amplitude, pulseWidth));

    // filter out all conductance measurments less than .02V.
    List<Number> v_rm = new ArrayList<>();
    List<Number> v_m = new ArrayList<>();
    List<Number> g = new ArrayList<>();

    for (int i = 0; i < conductance.length; i++) {
      if (Math.abs(vRM[i]) > MemristorDiscoveryPreferences.MIN_VOLTAGE_MEASURE_AMPLITUDE) {
        v_rm.add(vRM[i]);
        v_m.add(vM[i]);
        g.add(conductance[i]);
      }
    }

    // resultPanel.getGvChart().updateXYSeries("gv", v1, conductance, null);
    resultPanel.getGvChart().updateXYSeries("Resistor+Memristor", v_rm, g, null);
    resultPanel.getGvChart().updateXYSeries("Memristor", v_m, g, null);
  }

  public void repaintCaptureChart() {

    resultPanel.getCaptureChartPanel().revalidate();
    resultPanel.getCaptureChartPanel().repaint();
  }

  public void repaintIVChart() {

    resultPanel.getIvChartPanel().revalidate();
    resultPanel.getIvChartPanel().repaint();
  }

  public void repaintGVChart() {

    resultPanel.getGvChartPanel().revalidate();
    resultPanel.getGvChartPanel().repaint();
  }

  private String getWaveformChartTitle(double amplitude, int pulseWidth) {

    return "Waveform: " + getWaveform(amplitude, pulseWidth);
  }

  private String getVtChartTitle(double amplitude, int pulseWidth) {

    return "Capture: " + getWaveform(amplitude, pulseWidth);
  }

  private String getIVChartTitle(double amplitude, int pulseWidth) {

    return "I-V: " + getWaveform(amplitude, pulseWidth);
  }

  private String getGVChartTitle(double amplitude, int pulseWidth) {

    return "G-V: " + getWaveform(amplitude, pulseWidth);
  }

  private String getWaveform(double amplitude, int period) {

    return "Amplitude = "
        + getFormattedAmplitude(amplitude)
        + " V, Period = "
        + (double) period
        + " "
        + DCPreferences.TIME_UNIT.getLabel();
  }

  private double getFormattedAmplitude(double amplitude) {

    return Util.round(amplitude, 2);
  }
}
