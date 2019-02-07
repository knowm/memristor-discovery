/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments.dc.plot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCPreferences;
import org.knowm.memristor.discovery.utils.Util;

public class PlotController implements PropertyChangeListener {

  private final PlotPanel plotPanel;
  private final PlotControlModel plotModel;

  /**
   * Constructor
   *
   * @param plotPanel
   * @param plotModel
   */
  public PlotController(PlotPanel plotPanel, PlotControlModel plotModel) {

    this.plotPanel = plotPanel;
    this.plotModel = plotModel;

    initGUIComponents();
    setUpViewEvents();

    // init waveform chart
    plotPanel.switch2WaveformChart();
  }

  public void initGUIComponents() {

    plotPanel.getCaptureButton().setSelected(true);
    plotPanel.getFreezeYAxisCheckBoxIV().setSelected(false);
    plotPanel.getFreezeYAxisCheckBoxGV().setSelected(false);
    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

  }

  private void setUpViewEvents() {

    plotPanel.getCaptureButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        plotPanel.switch2CaptureChart();
      }
    });
    plotPanel.getIVButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        plotPanel.switch2IVChart();
      }
    });
    plotPanel.getGVButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        plotPanel.switch2GVChart();
      }
    });
    plotPanel.getFreezeYAxisCheckBoxIV().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (plotPanel.getFreezeYAxisCheckBoxIV().isSelected()) {
          plotModel.setyMaxIV(plotPanel.getIVChartMax());
          plotModel.setyMinIV(plotPanel.getIVChartMin());
        } else {
          plotModel.setyMaxIV(null);
          plotModel.setyMinIV(null);
        }
      }
    });
    plotPanel.getFreezeYAxisCheckBoxGV().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (plotPanel.getFreezeYAxisCheckBoxGV().isSelected()) {
          plotModel.setyMaxGV(plotPanel.getGVChartMax());
          plotModel.setyMinGV(plotPanel.getGVChartMin());
        } else {
          plotModel.setyMaxGV(null);
          plotModel.setyMinGV(null);
        }
      }
    });
  }

  public void updateWaveformChart(double[] timeData, double[] waveformAmplitudeData, double amplitude, int pulseWidth) {

    plotPanel.getWaveformChart().setTitle(getWaveformChartTitle(amplitude, pulseWidth));
    plotPanel.getWaveformChart().updateXYSeries("waveform", timeData, waveformAmplitudeData, null);
    plotPanel.getWaveformChartPanel().revalidate();
    plotPanel.getWaveformChartPanel().repaint();
  }

  public void updateCaptureChartData(double[] timeData, double[] v1, double[] v2, double[] v1Minusv2, int pulseWidth, double amplitude) {

    plotPanel.getCaptureChart().setTitle(getVtChartTitle(amplitude, pulseWidth));
    plotPanel.getCaptureChart().updateXYSeries("V1", timeData, v1, null);
    plotPanel.getCaptureChart().updateXYSeries("V2", timeData, v2, null);
    plotPanel.getCaptureChart().updateXYSeries("V1-V2", timeData, v1Minusv2, null);
  }

  public void updateIVChartData(double[] v1, double[] current, int pulseWidth, double amplitude) {

    plotPanel.getIvChart().getStyler().setYAxisMax(plotModel.getyMaxIV());
    plotPanel.getIvChart().getStyler().setYAxisMin(plotModel.getyMinIV());

    plotPanel.getIvChart().setTitle(getIVChartTitle(amplitude, pulseWidth));
    plotPanel.getIvChart().updateXYSeries("iv", v1, current, null);
  }

  public void updateGVChartData(double[] v1, double[] conductance, int pulseWidth, double amplitude) {

    plotPanel.getGvChart().getStyler().setYAxisMax(plotModel.getyMaxGV());
    plotPanel.getGvChart().getStyler().setYAxisMin(0.0);
    plotPanel.getGvChart().setTitle(getGVChartTitle(amplitude, pulseWidth));
    plotPanel.getGvChart().updateXYSeries("gv", v1, conductance, null);
  }

  public void repaintCaptureChart() {

    plotPanel.getCaptureChartPanel().revalidate();
    plotPanel.getCaptureChartPanel().repaint();
  }

  public void repaintItChart() {

    plotPanel.getIvChartPanel().revalidate();
    plotPanel.getIvChartPanel().repaint();
  }

  public void repaintRtChart() {

    plotPanel.getGvChartPanel().revalidate();
    plotPanel.getGvChartPanel().repaint();
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

    return "Amplitude = " + getFormattedAmplitude(amplitude) + " V, Period = " + (double) period + " " + DCPreferences.TIME_UNIT.getLabel();
  }

  private double getFormattedAmplitude(double amplitude) {

    return Util.round(amplitude, 2);
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {

      case ExperimentControlModel.EVENT_PREFERENCES_UPDATE:

        initGUIComponentsFromModel();
        break;

      default:
        break;
    }
  }
}
