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

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.PulsePreferences;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class ResultPanel extends JPanel {

  private final JPanel radioPanel;
  private final ButtonGroup radioButtonGroup;
  private final JRadioButton captureButton;
  private final JRadioButton ivButton;
  private final JRadioButton readPulseCaptureButton;

  private final JPanel chartsPanel;

  // private final JCheckBox freezeYAxisCheckBoxIV;

  // private final JPanel gvChartControlPanel;

  // private final JCheckBox freezeYAxisCheckBoxGV;

  XYChart waveformChart;
  XChartPanel<XYChart> waveformChartPanel;

  XYChart captureChart;
  XChartPanel<XYChart> captureChartPanel;

  XYChart iTChart;
  XChartPanel<XYChart> iTChartPanel;

  XYChart readCaptureChart;
  XChartPanel<XYChart> readCaptureChartPanel;

  XYChart gChart;
  XChartPanel<XYChart> gChartPanel;

  /** Constructor */
  public ResultPanel() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    // ///////////////////////////////////////////////////////////
    // Waveform Chart ///////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    waveformChart =
        new XYChartBuilder()
            .width(400)
            .height(300)
            .title("Waveform")
            .yAxisTitle("Voltage [V]")
            .xAxisTitle("Time [µs]")
            .build();
    waveformChart.getStyler().setLegendVisible(false);
    XYSeries series = waveformChart.addSeries("waveform", new double[] {0}, new double[] {0});
    series.setMarker(
        SeriesMarkers
            .NONE); // waveformChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
    waveformChartPanel = new XChartPanel<>(waveformChart);

    // ///////////////////////////////////////////////////////////
    // Capture Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    captureChart =
        new XYChartBuilder()
            .width(600)
            .title("Capture")
            .height(300)
            .yAxisTitle("Voltage [V]")
            .xAxisTitle("Time [µs]")
            .build();
    captureChart.getStyler().setLegendPosition(LegendPosition.InsideNE);

    series = captureChart.addSeries("V1(1+)", new double[] {0}, new double[] {0});
    series.setMarker(SeriesMarkers.NONE);
    series = captureChart.addSeries("V2(2+)", new double[] {0}, new double[] {0});
    series.setMarker(SeriesMarkers.NONE);
    series = captureChart.addSeries("V_Memristor", new double[] {0}, new double[] {0});
    series.setMarker(SeriesMarkers.NONE);
    captureChartPanel = new XChartPanel<>(captureChart);

    // ///////////////////////////////////////////////////////////
    // I-T Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    iTChart =
        new XYChartBuilder()
            .width(600)
            .title("I-T")
            .height(300)
            .yAxisTitle("Current [" + PulsePreferences.CURRENT_UNIT.getLabel() + "]")
            .xAxisTitle("Time [µs]")
            .build();
    iTChart.getStyler().setLegendVisible(false);
    iTChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
    iTChart.addSeries("it", new double[] {0}, new double[] {0});
    iTChartPanel = new XChartPanel<>(iTChart);

    /////////////////////////////////////////////////////////////
    // ReadCaptureChart  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////

    readCaptureChart =
        new XYChartBuilder()
            .width(600)
            .title("Read Pulse Capture")
            .height(300)
            .yAxisTitle("Voltage [V]")
            .xAxisTitle("Time [µs]")
            .build();
    readCaptureChart.getStyler().setLegendPosition(LegendPosition.InsideNE);
    // readCaptureChart.getStyler().setYAxisMin(0.0);
    readCaptureChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

    series = readCaptureChart.addSeries("V1(1+)", new double[] {0}, new double[] {0});
    series.setMarker(SeriesMarkers.NONE);
    series = readCaptureChart.addSeries("V2(2+)", new double[] {0}, new double[] {0});
    series.setMarker(SeriesMarkers.NONE);
    series = readCaptureChart.addSeries("V_Memristor", new double[] {0}, new double[] {0});
    series.setMarker(SeriesMarkers.NONE);

    readCaptureChartPanel = new XChartPanel<>(readCaptureChart);

    // ///////////////////////////////////////////////////////////
    // G Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    gChart =
        new XYChartBuilder()
            .width(600)
            .height(300)
            .title("G")
            .xAxisTitle("Read Pulse Number")
            .yAxisTitle("Conductance [" + PulsePreferences.CONDUCTANCE_UNIT.getLabel() + "]")
            .build();
    gChart.getStyler().setLegendVisible(false);
    // gChart.getStyler().setYAxisMin(0.0);

    gChart.getStyler().setYAxisLogarithmic(true);

    series = gChart.addSeries("g", new double[] {0}, new double[] {1E-10});
    series.setMarker(SeriesMarkers.NONE);
    series = gChart.addSeries("glast", new double[] {0}, new double[] {1E-10});
    series.setMarker(SeriesMarkers.NONE);
    gChartPanel = new XChartPanel<>(gChart);

    // ///////////////////////////////////////////////////////////
    // Charts Panel ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    chartsPanel = new JPanel();
    chartsPanel.setLayout(new BorderLayout());
    add(chartsPanel, BorderLayout.CENTER);

    // ///////////////////////////////////////////////////////////
    // Radio Buttons ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    radioPanel = new JPanel();
    captureButton = new JRadioButton("Write-Erase Pulse Capture");
    ivButton = new JRadioButton("I-T");
    readPulseCaptureButton = new JRadioButton("Read Pulse Capture");
    radioButtonGroup = new ButtonGroup();
    addRadioButtons();

    // ///////////////////////////////////////////////////////////
    // Check Box ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    // freezeYAxisCheckBoxIV = new JCheckBox("Freeze Y-Axis");

    // gvChartControlPanel = new JPanel();
    // freezeYAxisCheckBoxGV = new JCheckBox("Freeze Y-Axis");

    // gvChartControlPanel.add(freezeYAxisCheckBoxGV);
  }

  private void addRadioButtons() {

    radioButtonGroup.add(captureButton);
    radioButtonGroup.add(readPulseCaptureButton);
    radioButtonGroup.add(ivButton);

    radioPanel.add(captureButton);
    radioPanel.add(readPulseCaptureButton);
    radioPanel.add(ivButton);

    add(radioPanel, BorderLayout.SOUTH);
  }

  //  private void addYAxisFreezeCheckBoxIV() {
  //
  //    add(freezeYAxisCheckBoxIV, BorderLayout.NORTH);
  //  }

  //  private void addChartControlGV() {
  //
  //    add(gvChartControlPanel, BorderLayout.NORTH);
  //  }

  public void switch2WaveformChart() {

    if (!waveformChartPanel.isShowing()) {
      // System.out.println("switch2WaveformChart");
      removeAll();
      chartsPanel.removeAll();
      chartsPanel.add(waveformChartPanel, BorderLayout.CENTER);
      chartsPanel.add(gChartPanel, BorderLayout.SOUTH);
      add(chartsPanel, BorderLayout.CENTER);
      addRadioButtons();
      revalidate();
      repaint();
    }
  }

  public void switch2CaptureChart() {

    if (!captureChartPanel.isShowing()) {
      // System.out.println("switch2CaptureChart");
      removeAll();
      chartsPanel.removeAll();

      chartsPanel.add(captureChartPanel, BorderLayout.CENTER);
      chartsPanel.add(gChartPanel, BorderLayout.SOUTH);
      add(chartsPanel, BorderLayout.CENTER);
      addRadioButtons();
      revalidate();
      repaint();
    }
  }

  public void switch2IVChart() {

    if (!iTChartPanel.isShowing()) {
      // System.out.println("switch2IVChart");
      removeAll();
      chartsPanel.removeAll();

      chartsPanel.add(iTChartPanel, BorderLayout.CENTER);
      chartsPanel.add(gChartPanel, BorderLayout.SOUTH);
      add(chartsPanel, BorderLayout.CENTER);
      addRadioButtons();
      // addYAxisFreezeCheckBoxIV();
      revalidate();
      repaint();
    }
  }

  public void switchReadPulseCaptureChart() {

    if (!readCaptureChartPanel.isShowing()) {
      // System.out.println("switch2GVChart");
      removeAll();
      chartsPanel.removeAll();
      chartsPanel.add(readCaptureChartPanel, BorderLayout.CENTER);
      chartsPanel.add(gChartPanel, BorderLayout.SOUTH);
      add(chartsPanel, BorderLayout.CENTER);
      addRadioButtons();
      // addChartControlGV();
      revalidate();
      repaint();
    }
  }

  public JRadioButton getCaptureButton() {

    return captureButton;
  }

  public JRadioButton getIVButton() {

    return ivButton;
  }

  public JRadioButton getGVButton() {

    return readPulseCaptureButton;
  }

  //  public JCheckBox getFreezeYAxisCheckBoxIV() {
  //
  //    return freezeYAxisCheckBoxIV;
  //  }
  //
  //  public JCheckBox getFreezeYAxisCheckBoxGV() {
  //
  //    return freezeYAxisCheckBoxGV;
  //  }

  public double getITChartMax() {

    return iTChart.getSeriesMap().get("it").getYMax();
  }

  public double getITChartMin() {

    return iTChart.getSeriesMap().get("it").getYMin();
  }

  //  public double getGVChartMax() {
  //
  //    return readCaptureChart.getSeriesMap().get("gv").getYMax();
  //  }
  //
  //  public double getGVChartMin() {
  //
  //    return readCaptureChart.getSeriesMap().get("gv").getYMin();
  //  }

  public JPanel getRadioPanel() {

    return radioPanel;
  }

  public XYChart getWaveformChart() {

    return waveformChart;
  }

  public XChartPanel<XYChart> getWaveformChartPanel() {

    return waveformChartPanel;
  }

  public XYChart getCaptureChart() {

    return captureChart;
  }

  public XChartPanel<XYChart> getCaptureChartPanel() {

    return captureChartPanel;
  }

  public XYChart getITChart() {

    return iTChart;
  }

  public XChartPanel<XYChart> getITChartPanel() {

    return iTChartPanel;
  }

  public XYChart getReadPulseCaptureChart() {

    return readCaptureChart;
  }

  public XChartPanel<XYChart> getReadPulseCaptureChartPanel() {

    return readCaptureChartPanel;
  }

  public XYChart getGChart() {

    return gChart;
  }

  public XChartPanel<XYChart> getGChartPanel() {

    return gChartPanel;
  }
}
