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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.result;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.HysteresisPreferences;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class ResultPanel extends JPanel {

  private final JPanel radioPanel;
  private final ButtonGroup radioButtonGroup;
  private final JRadioButton captureButton;
  private final JRadioButton ivButton;
  private final JRadioButton gvButton;
  private final JCheckBox freezeYAxisCheckBoxIV;
  private final JPanel gvChartControlPanel;
  private final JCheckBox freezeYAxisCheckBoxGV;
  private final JLabel kLabelGV;
  private final JTextField kTextFieldGV;
  private XYChart waveformChart;
  private XChartPanel<XYChart> waveformChartPanel;
  private XYChart captureChart;
  private XChartPanel<XYChart> captureChartPanel;
  private XYChart ivChart;
  private XChartPanel<XYChart> ivChartPanel;
  private XYChart gvChart;
  private XChartPanel<XYChart> gvChartPanel;

  /** Constructor */
  public ResultPanel() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    // ///////////////////////////////////////////////////////////
    // Waveform Chart ///////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    waveformChart = new XYChartBuilder().width(400).height(400).title("Waveform").yAxisTitle("Voltage [V]").xAxisTitle("Time [s]").build();
    waveformChart.getStyler().setLegendVisible(false);
    XYSeries series = waveformChart.addSeries("waveform", new double[]{0}, new double[]{0});
    series.setMarker(SeriesMarkers.NONE); // waveformChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
    waveformChartPanel = new XChartPanel<>(waveformChart);
    add(waveformChartPanel, BorderLayout.CENTER);

    // ///////////////////////////////////////////////////////////
    // Capture Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    captureChart = new XYChartBuilder().width(600).title("Capture").height(400).yAxisTitle("Voltage [V]").xAxisTitle("Time [s]").build();
    captureChart.getStyler().setLegendPosition(LegendPosition.InsideNE);

    series = captureChart.addSeries("V1", new double[]{0}, new double[]{0});
    series.setMarker(SeriesMarkers.NONE);

    series = captureChart.addSeries("V2", new double[]{0}, new double[]{0});
    series.setMarker(SeriesMarkers.NONE);

    captureChartPanel = new XChartPanel<>(captureChart);

    // ///////////////////////////////////////////////////////////
    // I-V Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    ivChart = new XYChartBuilder().width(600).title("I-V").height(400).xAxisTitle("Voltage [V]")
        .yAxisTitle("Current [" + HysteresisPreferences.CURRENT_UNIT.getLabel() + "]").build();
    ivChart.getStyler().setLegendVisible(false);
    // ivChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);

    series = ivChart.addSeries("iv", new double[]{0}, new double[]{0});

    ivChartPanel = new XChartPanel<>(ivChart);

    // ///////////////////////////////////////////////////////////
    // G-V Chart ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    gvChart = new XYChartBuilder().width(100).title("G-V").height(100).xAxisTitle("Voltage [V]")
        .yAxisTitle("Conductance [" + HysteresisPreferences.CONDUCTANCE_UNIT.getLabel() + "]").build();
    gvChart.getStyler().setLegendVisible(true);
    gvChart.getStyler().setLegendPosition(LegendPosition.InsideNW);
    gvChart.getStyler().setYAxisMin(0.0);
    // gvChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
    gvChart.addSeries("V1", new double[]{0}, new double[]{0});
    gvChart.addSeries("Memristor", new double[]{0}, new double[]{0});
    gvChart.getStyler().setYAxisMin(0.0);
    gvChart.getStyler().setXAxisMin(-1.0);
    gvChart.getStyler().setXAxisMax(1.0);
    gvChartPanel = new XChartPanel<>(gvChart);

    // ///////////////////////////////////////////////////////////
    // Radio Buttons ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    radioPanel = new JPanel();
    captureButton = new JRadioButton("Capture");
    ivButton = new JRadioButton("I-V");
    gvButton = new JRadioButton("G-V");
    radioButtonGroup = new ButtonGroup();
    addRadioButtons();

    // ///////////////////////////////////////////////////////////
    // Check Box ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    freezeYAxisCheckBoxIV = new JCheckBox("Freeze Y-Axis");

    gvChartControlPanel = new JPanel();
    freezeYAxisCheckBoxGV = new JCheckBox("Freeze Y-Axis");
    kLabelGV = new JLabel("K: ");
    kTextFieldGV = new JTextField(4);

    gvChartControlPanel.add(freezeYAxisCheckBoxGV);
    gvChartControlPanel.add(kLabelGV);
    gvChartControlPanel.add(kTextFieldGV);
  }

  private void addRadioButtons() {

    radioButtonGroup.add(captureButton);
    radioButtonGroup.add(ivButton);
    radioButtonGroup.add(gvButton);
    radioPanel.add(captureButton);
    radioPanel.add(ivButton);
    radioPanel.add(gvButton);
    add(radioPanel, BorderLayout.SOUTH);
  }

  private void addYAxisFreezeCheckBoxIV() {

    add(freezeYAxisCheckBoxIV, BorderLayout.NORTH);
  }

  private void addChartControlGV() {

    // add(freezeYAxisCheckBoxGV, BorderLayout.NORTH);
    add(gvChartControlPanel, BorderLayout.NORTH);
  }

  public void switch2WaveformChart() {

    if (!waveformChartPanel.isShowing()) {
      // System.out.println("switch2WaveformChart");
      removeAll();
      add(waveformChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      revalidate();
      repaint();
    }
  }

  public void switch2CaptureChart() {

    if (!captureChartPanel.isShowing()) {
      // System.out.println("switch2CaptureChart");
      removeAll();
      add(captureChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      revalidate();
      repaint();
    }
  }

  public void switch2IVChart() {

    if (!ivChartPanel.isShowing()) {
      // System.out.println("switch2IVChart");
      removeAll();
      add(ivChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      addYAxisFreezeCheckBoxIV();
      revalidate();
      repaint();
    }
  }

  public void switch2GVChart() {

    if (!gvChartPanel.isShowing()) {
      // System.out.println("switch2GVChart");
      removeAll();
      add(gvChartPanel, BorderLayout.CENTER);
      addRadioButtons();
      addChartControlGV();
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

    return gvButton;
  }

  public JCheckBox getFreezeYAxisCheckBoxIV() {

    return freezeYAxisCheckBoxIV;
  }

  public JCheckBox getFreezeYAxisCheckBoxGV() {

    return freezeYAxisCheckBoxGV;
  }

  public JTextField getkTextFieldGV() {

    return kTextFieldGV;
  }

  public double getIVChartMax() {

    return ivChart.getSeriesMap().get("iv").getYMax();
  }

  public double getIVChartMin() {

    return ivChart.getSeriesMap().get("iv").getYMin();
  }

  public double getGVChartMax() {

    return gvChart.getSeriesMap().get("V1").getYMax();
  }

  public double getGVChartMin() {

    return gvChart.getSeriesMap().get("V1").getYMin();
  }

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

  public XYChart getIvChart() {

    return ivChart;
  }

  public XChartPanel<XYChart> getIvChartPanel() {

    return ivChartPanel;
  }

  public XYChart getGvChart() {

    return gvChart;
  }

  public XChartPanel<XYChart> getGvChartPanel() {

    return gvChartPanel;
  }
}
