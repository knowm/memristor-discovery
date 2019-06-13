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
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse.result;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.knowm.memristor.discovery.core.Util;

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

    resultPanel.getCaptureButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        resultPanel.switch2CaptureChart();
      }
    });

    resultPanel.getIVButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        resultPanel.switch2IVChart();
      }
    });
    resultPanel.getGVButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        resultPanel.switchReadPulseCaptureChart();
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

  }

  public void updateWaveformChart(double[] timeData, double[] waveformAmplitudeData, double amplitude, int pulseWidth) {

    resultPanel.getWaveformChart().setTitle(getWaveformChartTitle(amplitude, pulseWidth));
    resultPanel.getWaveformChart().updateXYSeries("waveform", timeData, waveformAmplitudeData, null);
    resultPanel.getWaveformChartPanel().revalidate();
    resultPanel.getWaveformChartPanel().repaint();
  }

  public void updateCaptureChartData(double[] timeData, double[] v1, double[] v2, double[] v1Minusv2, int pulseWidth, double amplitude) {

    resultPanel.getCaptureChart().setTitle(getVtChartTitle(amplitude, pulseWidth));
    resultPanel.getCaptureChart().updateXYSeries("V1(1+)", timeData, v1, null);
    resultPanel.getCaptureChart().updateXYSeries("V2(2+)", timeData, v2, null);
    resultPanel.getCaptureChart().updateXYSeries("V_Memristor", timeData, v1Minusv2, null);
  }

  public void updateIVChartData(double[] timeData, double[] current, int pulseWidth, double amplitude) {

    resultPanel.getITChart().getStyler().setYAxisMax(resultModel.getyMaxIV());
    resultPanel.getITChart().getStyler().setYAxisMin(resultModel.getyMinIV());

    resultPanel.getITChart().setTitle(getIVChartTitle(amplitude, pulseWidth));
    resultPanel.getITChart().updateXYSeries("it", timeData, current, null);
  }

  public void updateReadPulseCaptureChartData(double[] timeData, double[] v1, double[] v2, double[] vMemristor, int pulseWidth, double amplitude) {

    //  System.out.println("ResultController().updateReadPulseCaptureChartData()");

    resultPanel.getReadPulseCaptureChart().updateXYSeries("V1(1+)", timeData, v1, null);
    resultPanel.getReadPulseCaptureChart().updateXYSeries("V2(2+)", timeData, v2, null);
    resultPanel.getReadPulseCaptureChart().updateXYSeries("V_Memristor", timeData, vMemristor, null);
  }

  public void updateGChartData(double conductance, String resistance) {

    // System.out.println("conductance=" + conductance);

    if (conductance <= 0) {
      return; //
    }

    //  System.out.println("resistance=" + resistance);
    resultModel.getGData().add(conductance);
    resultPanel.getGChart().getStyler().setYAxisMax(resultModel.getyMaxGV());
    // resultPanel.getGChart().getStyler().setYAxisMin(0.0);

    // System.out.println("g: " + resultModel.getGData());

    resultPanel.getGChart().setTitle("G (R = " + resistance + ")");
    resultPanel.getGChart().updateXYSeries("g", null, resultModel.getGData(), null);
    resultPanel.getGChart().updateXYSeries("glast", new double[]{1, resultModel.getGData().size()}, new double[]{conductance, conductance}, null);
  }

  public void repaintVtChart() {

    resultPanel.getCaptureChartPanel().revalidate();
    resultPanel.getCaptureChartPanel().repaint();
  }

  public void repaintItChart() {

    resultPanel.getITChartPanel().revalidate();
    resultPanel.getITChartPanel().repaint();
  }

  public void repaintReadPulseCaptureChart() {

    resultPanel.getReadPulseCaptureChartPanel().revalidate();
    resultPanel.getReadPulseCaptureChartPanel().repaint();
  }

  public void repaintGChart() {

    resultPanel.getGChartPanel().revalidate();
    resultPanel.getGChartPanel().repaint();
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

  private String getWaveform(double amplitude, int pulseWidth) {

    return "Amplitude = " + getFormattedAmplitude(amplitude) + " V, Pulse Width = " + (double) pulseWidth / 1000 + " µs";
  }

  private double getFormattedAmplitude(double amplitude) {

    return Util.round(amplitude, 2);
  }
}
