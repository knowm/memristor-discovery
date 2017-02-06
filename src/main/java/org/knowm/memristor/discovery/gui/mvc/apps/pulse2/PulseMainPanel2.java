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

package org.knowm.memristor.discovery.gui.mvc.apps.pulse2;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PulseMainPanel2 extends JPanel {

  private final JPanel radioPanel;
  private final ButtonGroup radioButtonGroup;
  private final JRadioButton captureButton;
  private final JRadioButton itButton;
  private final JRadioButton rtButton;

  XYChart waveformChart;
  XChartPanel<XYChart> waveformChartPanel;

  XYChart captureChart;
  XChartPanel<XYChart> captureChartPanel;

  XYChart itChart;
  XChartPanel<XYChart> itChartPanel;

  XYChart rtChart;
  XChartPanel<XYChart> rtChartPanel;

  private final JCheckBox freezeYAxisCheckBoxIT;
  Double yMaxIT = null;
  Double yMinIT = null;

  private final JCheckBox freezeYAxisCheckBoxRT;
  Double yMaxRT = null;
  Double yMinRT = null;

  /**
   * Constructor
   */
  public PulseMainPanel2() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    /////////////////////////////////////////////////////////////
    // Waveform Chart ///////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    waveformChart = new XYChartBuilder().width(600).height(400).title("Waveform").yAxisTitle("Voltage [V]").xAxisTitle("Time [µs]").build();
    waveformChart.getStyler().setLegendVisible(false);
    XYSeries series = waveformChart.addSeries("waveform", new double[] { 0 }, new double[] { 0 });
    series.setMarker(SeriesMarkers.NONE); // waveformChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
    waveformChartPanel = new XChartPanel<>(waveformChart);
    add(waveformChartPanel, BorderLayout.CENTER);

    /////////////////////////////////////////////////////////////
    // Capture Chart ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    captureChart = new XYChartBuilder().width(600).title("Capture").height(400).yAxisTitle("Voltage [V]").xAxisTitle("Time [µs]").build();
    captureChart.getStyler().setLegendVisible(true);
    captureChart.getStyler().setLegendPosition(LegendPosition.InsideNE);

    series = captureChart.addSeries("v1", new double[] { 0 }, new double[] { 0 });
    series.setMarker(SeriesMarkers.NONE);

    series = captureChart.addSeries("v2", new double[] { 0 }, new double[] { 0 });
    series.setMarker(SeriesMarkers.NONE);

    captureChartPanel = new XChartPanel<>(captureChart);

    /////////////////////////////////////////////////////////////
    // I-T Chart ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    itChart = new XYChartBuilder().width(600).title("Current").height(400).xAxisTitle("Time [µs]").yAxisTitle("Current [" + PulsePreferences2.CURRENT_UNIT.getLabel() + "]").build();
    itChart.getStyler().setLegendVisible(false);
    itChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);

    series = itChart.addSeries("it", new double[] { 0 }, new double[] { 0 });

    itChartPanel = new XChartPanel<>(itChart);

    /////////////////////////////////////////////////////////////
    // R-T Chart ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    rtChart = new XYChartBuilder().width(100).title("Resistance").height(100).xAxisTitle("Time [µs]").yAxisTitle("Resistance [" + PulsePreferences2.RESISTANCE_UNIT.getLabel() + "]").build();
    rtChart.getStyler().setLegendVisible(false);
    rtChart.getStyler().setYAxisMin(0.0);
    rtChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);

    series = rtChart.addSeries("rt", new double[] { 0 }, new double[] { 0 });

    rtChartPanel = new XChartPanel<>(rtChart);

    /////////////////////////////////////////////////////////////
    // Radio Buttons ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    radioPanel = new JPanel();
    // radioPanel.setBackground(Color.blue);
    captureButton = new JRadioButton("Capture");
    itButton = new JRadioButton("Current");
    rtButton = new JRadioButton("Resistance");
    radioButtonGroup = new ButtonGroup();
    addRadioButtons();

    /////////////////////////////////////////////////////////////
    // Check Box ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    freezeYAxisCheckBoxIT = new JCheckBox("Freeze Y-Axis");
    freezeYAxisCheckBoxRT = new JCheckBox("Freeze Y-Axis");

    initControlStates();
  }

  private void addRadioButtons() {

    radioButtonGroup.add(captureButton);
    radioButtonGroup.add(itButton);
    radioButtonGroup.add(rtButton);
    radioPanel.add(captureButton);
    radioPanel.add(itButton);
    radioPanel.add(rtButton);
    add(radioPanel, BorderLayout.SOUTH);
  }

  private void addYAxisFreezeCheckBoxIV() {

    add(freezeYAxisCheckBoxIT, BorderLayout.NORTH);
  }

  private void addYAxisFreezeCheckBoxRV() {

    add(freezeYAxisCheckBoxRT, BorderLayout.NORTH);
  }

  public void switch2WaveformChart() {

    if (!waveformChartPanel.isShowing()) {
      removeAll();
      add(waveformChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      revalidate();
      repaint();
    }
  }

  public void switch2CaptureChart() {

    if (!captureChartPanel.isShowing()) {
      removeAll();
      add(captureChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      revalidate();
      repaint();
    }
  }

  public void switch2ITChart() {

    if (!itChartPanel.isShowing()) {
      removeAll();
      add(itChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      addYAxisFreezeCheckBoxIV();
      revalidate();
      repaint();
    }
  }

  public void switch2RTChart() {

    if (!rtChartPanel.isShowing()) {
      removeAll();
      add(rtChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      addYAxisFreezeCheckBoxRV();
      revalidate();
      repaint();
    }
  }

  public void initControlStates() {

    captureButton.setSelected(true);
  }

  public void udpateVtChart(double[] timeData, double[] vin, double[] vout, int pulseWidth, double amplitude) {

    captureChart.setTitle(getVtChartTitle(amplitude, pulseWidth));
    captureChart.updateXYSeries("v1", timeData, vin, null);
    captureChart.updateXYSeries("v2", timeData, vout, null);
  }

  public void repaintVtChart() {

    captureChartPanel.revalidate();
    captureChartPanel.repaint();
  }

  public void udpateITChart(double[] time, double[] current, int pulseWidth, double amplitude) {

    itChart.getStyler().setYAxisMax(yMaxIT);
    itChart.getStyler().setYAxisMin(yMinIT);

    itChart.setTitle(getIVChartTitle(amplitude, pulseWidth));
    itChart.updateXYSeries("it", time, current, null);
  }

  public void repaintItChart() {

    itChartPanel.revalidate();
    itChartPanel.repaint();
  }

  public void udpateRTChart(double[] time, double[] resistance, int pulseWidth, double amplitude) {

    // System.out.println(Arrays.toString(captureAmplitudeData1));
    // System.out.println(Arrays.toString(vMemristor));
    // System.out.println(Arrays.toString(resistance));

    rtChart.getStyler().setYAxisMax(yMaxRT);
    rtChart.getStyler().setYAxisMin(yMinRT);

    rtChart.setTitle(getRVChartTitle(amplitude, pulseWidth));
    rtChart.updateXYSeries("rt", time, resistance, null);
  }

  public void repaintRtChart() {

    rtChartPanel.revalidate();
    rtChartPanel.repaint();
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

  private String getRVChartTitle(double amplitude, int pulseWidth) {

    return "R-V: " + getWaveform(amplitude, pulseWidth);
  }

  private String getWaveform(double amplitude, int pulseWidth) {

    return "Amplitude = " + getFormattedeAmplitude(amplitude) + " V, Pulse Width = " + getFormattedeAmplitude(pulseWidth / 1000.0) + " µs";
  }

  private double getFormattedeAmplitude(double amplitude) {

    return round(amplitude, 2);
  }

  public double round(double value, int places) {

    if (places < 0)
      throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  public JRadioButton getCaptureButton() {

    return captureButton;
  }

  public JRadioButton getItButton() {

    return itButton;
  }

  public JRadioButton getRtButton() {

    return rtButton;
  }

  public JCheckBox getFreezeYAxisCheckBoxIV() {

    return freezeYAxisCheckBoxIT;
  }

  public void setFreezeYAxisIT(boolean freezeYAxis) {

    if (freezeYAxis) {
      yMaxIT = itChart.getSeriesMap().get("it").getYMax();
      yMinIT = itChart.getSeriesMap().get("it").getYMin();
    }
    else {
      yMaxIT = null;
      yMinIT = null;
    }
  }

  public JCheckBox getFreezeYAxisCheckBoxRV() {

    return freezeYAxisCheckBoxRT;
  }

  public void setFreezeYAxisRT(boolean freezeYAxis) {

    if (freezeYAxis) {
      yMaxRT = rtChart.getSeriesMap().get("rt").getYMax();
      yMinRT = rtChart.getSeriesMap().get("rt").getYMin();
    }
    else {
      yMaxRT = null;
      yMinRT = null;
    }
  }
}
