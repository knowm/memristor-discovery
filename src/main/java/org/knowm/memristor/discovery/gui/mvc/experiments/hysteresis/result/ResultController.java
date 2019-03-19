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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.HysteresisPreferences;

public class ResultController implements PropertyChangeListener {

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

    // register the controller as the listener of the model
    resultModel.addListener(this);
  }

  public void initGUIComponents() {

    resultPanel.getCaptureButton().setSelected(true);
    resultPanel.getFreezeYAxisCheckBoxIV().setSelected(false);
    resultPanel.getFreezeYAxisCheckBoxGV().setSelected(false);
    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    resultPanel.getkTextFieldGV().setText("" + Util.round(resultModel.getK(), 2));
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
    resultPanel
        .getFreezeYAxisCheckBoxIV()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                if (resultPanel.getFreezeYAxisCheckBoxIV().isSelected()) {
                  resultModel.setyMaxIV(resultPanel.getIVChartMax());
                  resultModel.setyMinIV(resultPanel.getIVChartMin());
                } else {
                  resultModel.setyMaxIV(null);
                  resultModel.setyMinIV(null);
                }
              }
            });
    resultPanel
        .getFreezeYAxisCheckBoxGV()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                if (resultPanel.getFreezeYAxisCheckBoxGV().isSelected()) {
                  resultModel.setyMaxGV(resultPanel.getGVChartMax());
                  resultModel.setyMinGV(resultPanel.getGVChartMin());
                } else {
                  resultModel.setyMaxGV(null);
                  resultModel.setyMinGV(null);
                }
              }
            });

    resultPanel
        .getkTextFieldGV()
        .addKeyListener(
            new KeyAdapter() {

              @Override
              public void keyReleased(KeyEvent e) {

                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();

                if (text.equalsIgnoreCase(".")) {
                  // do nothing
                } else {
                  try {
                    double newKValue = Double.parseDouble(text);

                    resultModel.setK(newKValue);
                    resultPanel
                        .getkTextFieldGV()
                        .setText(
                            Double.toString(
                                resultModel.getK())); // this reverts back to acceptable range (0-1)
                  } catch (Exception ex) {
                    // parsing error, default back to previous value
                    resultPanel.getkTextFieldGV().setText(Double.toString(resultModel.getK()));
                  }
                }
              }
            });
  }

  public void udpateWaveformChart(
      double[] waveformTimeData,
      double[] waveformAmplitudeData,
      double amplitude,
      int frequency,
      double offset) {

    resultPanel.getWaveformChart().setTitle(getWaveformChartTitle(amplitude, frequency, offset));
    resultPanel
        .getWaveformChart()
        .updateXYSeries("waveform", waveformTimeData, waveformAmplitudeData, null);
    resultPanel.getWaveformChartPanel().revalidate();
    resultPanel.getWaveformChartPanel().repaint();
  }

  public void udpateVtChartData(
      double[] captureAmplitudeData1,
      double[] captureAmplitudeData2,
      double[] timeData,
      int frequency,
      double amplitude,
      double offset) {

    resultPanel.getCaptureChart().setTitle(getVtChartTitle(amplitude, frequency, offset));
    resultPanel.getCaptureChart().updateXYSeries("V1", timeData, captureAmplitudeData1, null);
    resultPanel.getCaptureChart().updateXYSeries("V2", timeData, captureAmplitudeData2, null);
    resultPanel.getCaptureChartPanel().revalidate();
    resultPanel.getCaptureChartPanel().repaint();
  }

  public void udpateIVChartData(
      double[] captureAmplitudeData1,
      double[] vMemristor,
      double[] current,
      int frequency,
      double amplitude,
      double offset) {

    resultPanel.getIvChart().getStyler().setYAxisMax(resultModel.getyMaxIV());
    resultPanel.getIvChart().getStyler().setYAxisMin(resultModel.getyMinIV());

    resultPanel.getIvChart().setTitle(getIVChartTitle(amplitude, frequency, offset));
    if (!HysteresisPreferences.IS_VIN) {
      resultPanel.getIvChart().updateXYSeries("iv", vMemristor, current, null);
    } else {
      resultPanel.getIvChart().updateXYSeries("iv", captureAmplitudeData1, current, null);
    }
    resultPanel.getIvChartPanel().revalidate();
    resultPanel.getIvChartPanel().repaint();
  }

  public void updateGVChartData(
      double[] captureAmplitudeData1,
      double[] vMemristor,
      double[] conductance,
      int frequency,
      double amplitude,
      double offset) {

    resultPanel.getGvChart().getStyler().setYAxisMax(resultModel.getyMaxGV());
    resultPanel.getGvChart().setTitle(getGVChartTitle(amplitude, frequency, offset));
    resultPanel.getGvChart().updateXYSeries("V1", captureAmplitudeData1, conductance, null);
    resultPanel.getGvChart().updateXYSeries("Memristor", vMemristor, conductance, null);
    resultPanel.getGvChartPanel().revalidate();
    resultPanel.getGvChartPanel().repaint();
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

    return "Amplitude = "
        + getFormattedAmplitude(amplitude)
        + " V, Frequency = "
        + frequency
        + " Hz, Offset = "
        + getFormattedAmplitude(offset)
        + " V";
  }

  private double getFormattedAmplitude(double amplitude) {

    return Util.round(amplitude, 2);
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model
   * is updated. Here, the controller can respond to those events and make sure the corresponding
   * GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {
      case Model.EVENT_PREFERENCES_UPDATE:
        initGUIComponentsFromModel();
        break;

      default:
        break;
    }
  }
}
