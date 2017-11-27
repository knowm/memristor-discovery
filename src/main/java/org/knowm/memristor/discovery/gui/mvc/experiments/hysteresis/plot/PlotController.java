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
package org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.plot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextField;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.HysteresisPreferences;
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

    plotPanel.getkTextFieldGV().setText("" + plotModel.getK());
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

    plotPanel.getkTextFieldGV().addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {

        JTextField textField = (JTextField) e.getSource();
        String text = textField.getText();

        if (text.equalsIgnoreCase(".")) {
          //do nothing
        } else {
          try {
            double newKValue = Double.parseDouble(text);

            plotModel.setK(newKValue);
            plotPanel.getkTextFieldGV().setText(Double.toString(plotModel.getK()));//this reverts back to acceptable range (0-1)
          } catch (Exception ex) {
            // parsing error, default back to previous value
            plotPanel.getkTextFieldGV().setText(Double.toString(plotModel.getK()));
          }
        }

      }
    });
  }

  public void udpateWaveformChart(double[] waveformTimeData, double[] waveformAmplitudeData, double amplitude, int frequency, double offset) {

    plotPanel.getWaveformChart().setTitle(getWaveformChartTitle(amplitude, frequency, offset));
    plotPanel.getWaveformChart().updateXYSeries("waveform", waveformTimeData, waveformAmplitudeData, null);
    plotPanel.getWaveformChartPanel().revalidate();
    plotPanel.getWaveformChartPanel().repaint();
  }

  public void udpateVtChartData(double[] captureAmplitudeData1, double[] captureAmplitudeData2, double[] timeData, int frequency, double amplitude,
      double offset) {

    plotPanel.getCaptureChart().setTitle(getVtChartTitle(amplitude, frequency, offset));
    plotPanel.getCaptureChart().updateXYSeries("V1", timeData, captureAmplitudeData1, null);
    plotPanel.getCaptureChart().updateXYSeries("V2", timeData, captureAmplitudeData2, null);
    plotPanel.getCaptureChartPanel().revalidate();
    plotPanel.getCaptureChartPanel().repaint();
  }

  public void udpateIVChartData(double[] captureAmplitudeData1, double[] vMemristor, double[] current, int frequency, double amplitude, double offset) {

    plotPanel.getIvChart().getStyler().setYAxisMax(plotModel.getyMaxIV());
    plotPanel.getIvChart().getStyler().setYAxisMin(plotModel.getyMinIV());

    plotPanel.getIvChart().setTitle(getIVChartTitle(amplitude, frequency, offset));
    if (!HysteresisPreferences.IS_VIN) {
      plotPanel.getIvChart().updateXYSeries("iv", vMemristor, current, null);
    } else {
      plotPanel.getIvChart().updateXYSeries("iv", captureAmplitudeData1, current, null);
    }
    plotPanel.getIvChartPanel().revalidate();
    plotPanel.getIvChartPanel().repaint();
  }

  public void updateGVChartData(double[] captureAmplitudeData1, double[] vMemristor, double[] conductance, int frequency, double amplitude,
      double offset) {

    plotPanel.getGvChart().getStyler().setYAxisMax(plotModel.getyMaxGV());
    plotPanel.getGvChart().setTitle(getGVChartTitle(amplitude, frequency, offset));
    plotPanel.getGvChart().updateXYSeries("V1", captureAmplitudeData1, conductance, null);
    plotPanel.getGvChart().updateXYSeries("Memristor", vMemristor, conductance, null);
    plotPanel.getGvChartPanel().revalidate();
    plotPanel.getGvChartPanel().repaint();
  }

  private String getWaveformChartTitle(double amplitude, int frequency, double offset) {

    return "Waveform: " + getWaveform(amplitude, frequency, offset);
  }

  private String getVtChartTitle(double amplitude, int frequency, double offset) {

    return "Capture: " + getWaveform(amplitude, frequency, offset);
  }

  private String getIVChartTitle(double amplitude, int frequency, double offset) {

    return "I-V: " + getWaveform(amplitude, frequency, offset);
  }

  private String getGVChartTitle(double amplitude, int frequency, double offset) {

    return "G-V: " + getWaveform(amplitude, frequency, offset);
  }

  private String getWaveform(double amplitude, int frequency, double offset) {

    return "Amplitude = " + getFormattedAmplitude(amplitude) + " V, Frequency = " + frequency + " Hz, Offset = " + getFormattedAmplitude(offset)
        + " V";
  }

  private double getFormattedAmplitude(double amplitude) {

    return Util.round(amplitude, 2);
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to
   * those events and make sure the corresponding GUI components get updated.
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
