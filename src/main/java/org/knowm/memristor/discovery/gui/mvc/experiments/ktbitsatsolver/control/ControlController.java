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
package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.FileUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.Controller;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;

public class ControlController extends Controller {

  private final ControlPanel controlPanel;
  private final ControlModel controlModel;

  /**
   * Constructor
   *
   * @param controlPanel
   * @param controlModel
   * @param dwf
   */
  public ControlController(ControlPanel controlPanel, ControlModel controlModel, DWFProxy dwf) {

    super(controlPanel, controlModel);

    this.controlPanel = controlPanel;
    this.controlModel = controlModel;
    dwf.addListener(this);

    initGUIComponents();
    setUpViewEvents();

    // register the controller as the listener of the controlModel
    controlModel.addListener(this);
  }

  private void initGUIComponents() {

    controlPanel.getSelectedFileTextField().setText("" + controlModel.getFilePath());

    controlPanel.getWaveformComboBox().setSelectedItem(controlModel.getWaveform());
    controlPanel
        .getWaveformComboBox()
        .setModel(
            new DefaultComboBoxModel<>(
                new Waveform[] {
                  Waveform.SquareSmooth,
                  Waveform.Square,
                  Waveform.QuarterSine,
                  Waveform.HalfSine,
                  Waveform.Triangle
                }));

    controlPanel.getAmplitudeSlider().setValue((int) (controlModel.getForwardAmplitude() * 100));

    controlPanel
        .getAmplitudeSlider()
        .setBorder(
            BorderFactory.createTitledBorder(
                "Forward Amplitude [V] = " + controlModel.getForwardAmplitude()));

    controlPanel
        .getAmplitudeReverseSlider()
        .setValue((int) (controlModel.getReverseAmplitude() * 100));
    controlPanel
        .getAmplitudeReverseSlider()
        .setBorder(
            BorderFactory.createTitledBorder(
                "Reverse Amplitude [V] = " + controlModel.getReverseAmplitude()));

    controlPanel.getPulseWidthSlider().setValue((controlModel.getPulseWidth()));

    controlPanel
        .getPulseWidthSlider()
        .setBorder(
            BorderFactory.createTitledBorder(
                "Pulse Width [µs] = " + controlModel.getPulseWidth() / 1000f));
  }

  /** Here, all the action listeners are attached to the GUI components */
  @Override
  public void doSetUpViewEvents() {

    controlPanel
        .getSelectFileButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                try {
                  String filePath =
                      FileUtils.showPickFileDialog(
                          controlPanel,
                          controlModel.getFilePath(),
                          ControlController.getSATFileFilter());

                  controlModel.setFilePath(filePath);
                  controlPanel.getSelectedFileTextField().setText(filePath);

                } catch (IOException e1) {
                  e1.printStackTrace();
                }
              }
            });

    controlPanel
        .getWaveformComboBox()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                controlModel.setWaveform(
                    controlPanel.getWaveformComboBox().getSelectedItem().toString());
              }
            });

    controlPanel
        .getAmplitudeSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setForwardAmplitude(source.getValue() / (float) 100);
                  controlPanel
                      .getAmplitudeSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Forward Amplitude [V] = " + controlModel.getForwardAmplitude()));
                }
              }
            });

    controlPanel
        .getAmplitudeReverseSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setReverseAmplitude(source.getValue() / (float) 100);
                  controlPanel
                      .getAmplitudeReverseSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Reverse Amplitude [V] = " + controlModel.getReverseAmplitude()));
                }
              }
            });

    controlPanel
        .getPulseWidthSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setPulseWidth(source.getValue());
                  controlPanel
                      .getPulseWidthSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Pulse Width [µs] = "
                                  + (double) controlModel.getPulseWidth() / 1000));
                  // controlPanel.getPulseWidthSliderNs().setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
                }
              }
            });
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying
   * controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    switch (evt.getPropertyName()) {
      case DWFProxy.AD2_STARTUP_CHANGE:
        controlPanel.enableAllChildComponents((Boolean) evt.getNewValue());

        break;

      case Model.EVENT_PREFERENCES_UPDATE:
        initGUIComponents();
        break;

      case Model.EVENT_WAVEFORM_UPDATE:
        controlModel.updateWaveformChartData();
        break;

      default:
        break;
    }
  }

  public static FileFilter getSATFileFilter() {

    FileFilter f =
        new FileFilter() {

          @Override
          public boolean accept(File f) {
            return f.getName().endsWith(".dimacs");
          }

          @Override
          public String getDescription() {
            return ".dimacs files only";
          }
        };

    return f;
  }
}
