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
package org.knowm.memristor.discovery.gui.mvc.apps.qc;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class QCMainPanel extends JPanel {

  XYChart waveformChart;
  XChartPanel<XYChart> waveformChartPanel;

  XYChart ivChart;
  XChartPanel<XYChart> ivChartPanel;

  /**
   * Constructor
   */
  public QCMainPanel() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    // ///////////////////////////////////////////////////////////
    // Waveform Chart ///////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    waveformChart = new XYChartBuilder().width(600).height(400).title("Waveform").yAxisTitle("Voltage [V]").xAxisTitle("Time [s]").build();
    waveformChart.getStyler().setLegendVisible(false);
    XYSeries series = waveformChart.addSeries("waveform", new double[] { 0 }, new double[] { 0 });
    series.setMarker(SeriesMarkers.NONE); // waveformChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
    waveformChartPanel = new XChartPanel<>(waveformChart);
    add(waveformChartPanel, BorderLayout.CENTER);

    // ///////////////////////////////////////////////////////////
    // I-V Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    ivChart = new XYChartBuilder().width(600).title("I-V").height(400).xAxisTitle("Voltage [V]").yAxisTitle("Current [" + QCPreferences.CURRENT_UNIT_DEFAULT_VALUE.getLabel() + "]").build();
    ivChart.getStyler().setLegendVisible(false);
    ivChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
    ivChart.getStyler().setMarkerSize(0);
    series = ivChart.addSeries("iv", new double[] { 0 }, new double[] { 0 });

    ivChartPanel = new XChartPanel<>(ivChart);

  }

  public void switch2WaveformChart() {

    if (!waveformChartPanel.isShowing()) {
      removeAll();
      add(waveformChartPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
    }
  }

  public void switch2IVChart() {

    if (!ivChartPanel.isShowing()) {
      removeAll();
      add(ivChartPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
    }
  }

  public void udpateWaveformChart(double[] waveformTimeData, double[] waveformAmplitudeData, double amplitude, int frequency) {

    waveformChart.setTitle(getWaveformChartTitle(amplitude, frequency));
    waveformChart.updateXYSeries("waveform", waveformTimeData, waveformAmplitudeData, null);
    waveformChartPanel.revalidate();
    waveformChartPanel.repaint();
  }

  public void udpateIVChart(double[] captureAmplitudeData1, double[] vMemristor, double[] current, int frequency, double amplitude) {

    ivChart.setTitle(getIVChartTitle(amplitude, frequency));
    if (!QCPreferences.IS_VIN) {
      ivChart.updateXYSeries("iv", vMemristor, current, null);
    }
    else {
      ivChart.updateXYSeries("iv", captureAmplitudeData1, current, null);
    }
    ivChartPanel.revalidate();
    ivChartPanel.repaint();

  }

  private String getWaveformChartTitle(double amplitude, int frequency) {

    return "Waveform: Amplitude = " + amplitude + " V, Frequency = " + frequency + " Hz";
  }

  private String getIVChartTitle(double amplitude, int frequency) {

    return "I-V: Amplitude = " + amplitude + " V, Frequency = " + frequency + " Hz";
  }

  public void saveIVChart(String savePath, int memristorID) throws IOException {

    BitmapEncoder.saveBitmap(ivChart, savePath + File.separator + memristorID, BitmapFormat.PNG);
  }

}
