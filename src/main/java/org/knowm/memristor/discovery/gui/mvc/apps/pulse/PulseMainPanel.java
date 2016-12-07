/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016 Knowm Inc. www.knowm.org
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

package org.knowm.memristor.discovery.gui.mvc.apps.pulse;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PulseMainPanel extends JPanel {

  private final JPanel radioPanel;
  private final ButtonGroup radioButtonGroup;
  private final JRadioButton captureButton;
  private final JRadioButton itButton;
  private final JRadioButton rtButton;

  XYChart waveformChart;
  XChartPanel<XYChart> waveformChartPanel;

  XYChart captureChart;
  XChartPanel<XYChart> captureChartPanel;

  XYChart ivChart;
  XChartPanel<XYChart> itChartPanel;

  XYChart rtChart;
  XChartPanel<XYChart> rtChartPanel;

  /**
   * Constructor
   */
  public PulseMainPanel() {

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
    captureChart.getStyler().setLegendVisible(false);

    series = captureChart.addSeries("capture1", new double[] { 0 }, new double[] { 0 });
    series.setMarker(SeriesMarkers.NONE);

    series = captureChart.addSeries("capture2", new double[] { 0 }, new double[] { 0 });
    series.setMarker(SeriesMarkers.NONE);

    captureChartPanel = new XChartPanel<>(captureChart);

    /////////////////////////////////////////////////////////////
    // I-T Chart ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    ivChart = new XYChartBuilder().width(600).title("Current").height(400).xAxisTitle("Time [µs]").yAxisTitle("Current [" + PulsePreferences.CURRENT_UNIT.getLabel() + "]").build();
    ivChart.getStyler().setLegendVisible(false);
    ivChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);

    series = ivChart.addSeries("it", new double[] { 0 }, new double[] { 0 });

    itChartPanel = new XChartPanel<>(ivChart);

    /////////////////////////////////////////////////////////////
    // R-T Chart ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    rtChart = new XYChartBuilder().width(100).title("Resistance").height(100).xAxisTitle("Time [µs]").yAxisTitle("Resistance [" + PulsePreferences.RESISTANCE_UNIT.getLabel() + "]").build();
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
      revalidate();
      repaint();
    }
  }

  public void switch2RTChart() {

    if (!rtChartPanel.isShowing()) {
      removeAll();
      add(rtChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      revalidate();
      repaint();
    }
  }

  public void initControlStates() {

    captureButton.setSelected(true);
  }

  public void udpateWaveformChart(double[] waveformTimeData, double[] waveformAmplitudeData, double amplitude, int pulseWidth) {

    waveformChart.setTitle(getWaveformChartTitle(amplitude, pulseWidth));
    waveformChart.updateXYSeries("waveform", waveformTimeData, waveformAmplitudeData, null);
    waveformChartPanel.revalidate();
    waveformChartPanel.repaint();
  }

  public void udpateVtChart(double[] captureAmplitudeData1, double[] captureAmplitudeData2, double[] timeData, int pulseWidth, double amplitude) {

    // System.out.println(Arrays.toString(captureAmplitudeData1));
    // System.out.println(Arrays.toString(captureAmplitudeData2));
    // System.out.println(Arrays.toString(current));
    captureChart.setTitle(getVtChartTitle(amplitude, pulseWidth));
    captureChart.updateXYSeries("capture1", timeData, captureAmplitudeData1, null);
    captureChart.updateXYSeries("capture2", timeData, captureAmplitudeData2, null);
    captureChartPanel.revalidate();
    captureChartPanel.repaint();
  }

  public void udpateITChart(double[] captureAmplitudeData1, double[] current, double[] time, int pulseWidth, double amplitude) {

    // System.out.println(Arrays.toString(captureAmplitudeData1));
    // System.out.println(Arrays.toString(vMemristor));
    // System.out.println(Arrays.toString(current));
    ivChart.setTitle(getIVChartTitle(amplitude, pulseWidth));
    ivChart.updateXYSeries("it", time, current, null);
    itChartPanel.revalidate();
    itChartPanel.repaint();
  }

  public void udpateRTChart(double[] captureAmplitudeData1, double[] resistance, double[] time, int pulseWidth, double amplitude) {

    // System.out.println(Arrays.toString(captureAmplitudeData1));
    // System.out.println(Arrays.toString(vMemristor));
    // System.out.println(Arrays.toString(resistance));

    rtChart.setTitle(getRVChartTitle(amplitude, pulseWidth));
    rtChart.updateXYSeries("rt", time, resistance, null);
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

    return "Amplitude = " + getFormattedeAmplitude(amplitude) + " V, Pulse Width = " + pulseWidth + " µs";
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
}
