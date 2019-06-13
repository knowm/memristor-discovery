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
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse12.result;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ResultController {

  private final ResultPanel resultPanel;
  private final ResultModel resultModel;

  private Long startTime = null;

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
  }

  public void resetChart() {
    resultModel.clearData();
    startTime = System.currentTimeMillis();
    repaintYChart();
  }

  public void initGUIComponents() {

    resultPanel.getFreezeYAxisCheckBoxIV().setSelected(false);
  }

  private void setUpViewEvents() {

    resultPanel
        .getFreezeYAxisCheckBoxIV()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                if (resultPanel.getFreezeYAxisCheckBoxIV().isSelected()) {
                  resultModel.setyMaxIV(resultPanel.getYChartMax());
                  resultModel.setyMinIV(resultPanel.getYChartMin());
                } else {
                  resultModel.setyMaxIV(null);
                  resultModel.setyMinIV(null);
                }
              }
            });

    resultPanel
        .getResistanceConductanceCheckBox()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                // change data-->
                resultModel.setGr1Data(toOneOver(resultModel.getGr1Data()));
                resultModel.setGr2Data(toOneOver(resultModel.getGr2Data()));

                // update series-->
                resultPanel.getGChart().updateXYSeries("A", null, resultModel.getGr1Data(), null);
                resultPanel.getGChart().updateXYSeries("B", null, resultModel.getGr2Data(), null);

                if (resultPanel.getResistanceConductanceCheckBox().isSelected()) {
                  resultPanel.getGChart().setYAxisGroupTitle(0, "Resistance (Ω)");
                } else {
                  resultPanel.getGChart().setYAxisGroupTitle(0, "Conductance (S)");
                }
                repaintYChart();
              }
            });
  }

  private List<Double> toOneOver(List<Double> array) {

    List<Double> r = new ArrayList<Double>();
    for (int i = 0; i < array.size(); i++) {

      if (array.get(i) > 0) {
        r.add(1 / array.get(i));
      } else {
        r.add(1000000.0); // assume 1MOhm
      }
    }
    return r;
  }

  public void updateYChartData(Double g_a, Double g_b, Double vy) {

    if (startTime == null) {
      startTime = System.currentTimeMillis();
    }

    double timeFromStart = (System.currentTimeMillis() - startTime) / 1000.0;

    if (resultPanel.getResistanceConductanceCheckBox().isSelected()) {

      if (!g_a.isNaN()) {
        resultModel.getGr1Data().add(1 / g_a);
      }

      if (!g_b.isNaN()) {
        resultModel.getGr2Data().add(1 / g_b);
      }

    } else {
      if (!g_a.isNaN()) {
        resultModel.getGr1Data().add(g_a);
      }

      if (!g_b.isNaN()) {
        resultModel.getGr2Data().add(g_b);
      }
    }
    if (!vy.isNaN()) {
      resultModel.getVyData().add(vy);
      resultModel.getTimeVyData().add(timeFromStart);
    }

    if (!g_a.isNaN()) {
      resultModel.getTime1Data().add(timeFromStart);
    }
    if (!g_b.isNaN()) {
      resultModel.getTime2Data().add(timeFromStart);
    }

    //    System.out.println(resultModel.getGr1Data());
    //    System.out.println(resultModel.getGr2Data());

    if (resultModel.getGr1Data() != null && resultModel.getGr1Data().size() > 0) {
      resultPanel
          .getGChart()
          .updateXYSeries("A", resultModel.getTime1Data(), resultModel.getGr1Data(), null);
    }

    if (resultModel.getGr2Data() != null && resultModel.getGr2Data().size() > 0) {
      resultPanel
          .getGChart()
          .updateXYSeries("B", resultModel.getTime2Data(), resultModel.getGr2Data(), null);
    }

    if (resultModel.getVyData() != null && resultModel.getVyData().size() > 0) {
      resultPanel
          .getGChart()
          .updateXYSeries("Vy", resultModel.getTimeVyData(), resultModel.getVyData(), null);
    }
  }

  public void repaintYChart() {

    resultPanel.getGChartPanel().revalidate();
    resultPanel.getGChartPanel().repaint();
  }
}
