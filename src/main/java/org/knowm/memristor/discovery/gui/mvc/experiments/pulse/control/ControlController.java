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
package org.knowm.memristor.discovery.gui.mvc.experiments.pulse.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.knowm.memristor.discovery.DWFProxy;
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

    initGUIComponentsFromModel();
  }

  private void initGUIComponentsFromModel() {

    controlPanel.getWaveformComboBox().setSelectedItem(controlModel.getWaveform());
    controlPanel
        .getWaveformComboBox()
        .setModel(
            new DefaultComboBoxModel<>(
                new Waveform[] {
                  Waveform.Square,
                  Waveform.SquareSmooth,
                  Waveform.SquareDecay,
                  Waveform.SquareLongDecay,
                  Waveform.Triangle,
                  Waveform.QuarterSine,
                  Waveform.HalfSine
                }));

    controlPanel.getAmplitudeSlider().setValue((int) (controlModel.getAmplitude() * 100));
    controlPanel
        .getAmplitudeSlider()
        .setBorder(
            BorderFactory.createTitledBorder("Amplitude [V] = " + controlModel.getAmplitude()));
    if (controlModel.getPulseWidth() >= 5000) {
      controlPanel.getPulseWidthSlider().setValue((int) (controlModel.getPulseWidth()));
      controlPanel.getPulseWidthSliderNs().setValue(0);
      controlPanel
          .getPulseWidthSlider()
          .setBorder(
              BorderFactory.createTitledBorder(
                  "Pulse Width [µs] = " + controlModel.getPulseWidth() / 1000));
      controlPanel
          .getPulseWidthSliderNs()
          .setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
    } else {
      controlPanel.getPulseWidthSlider().setValue(0);
      controlPanel.getPulseWidthSliderNs().setValue(controlModel.getPulseWidth());
      controlPanel
          .getPulseWidthSlider()
          .setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
      controlPanel
          .getPulseWidthSliderNs()
          .setBorder(
              BorderFactory.createTitledBorder(
                  "Pulse Width [µs] = " + controlModel.getPulseWidth() / 1000));
    }
    controlPanel
        .getPulseNumberSlider()
        .setBorder(
            BorderFactory.createTitledBorder("Pulse Number = " + controlModel.getPulseNumber()));
    controlPanel.getPulseNumberSlider().setValue(controlModel.getPulseNumber());

    controlPanel
        .getDutyCycleSlider()
        .setBorder(BorderFactory.createTitledBorder("Duty Cycle = " + controlModel.getDutyCycle()));
    controlPanel.getDutyCycleSlider().setValue((int) (100 * controlModel.getDutyCycle()));

    controlPanel.getSeriesTextField().setText("" + controlModel.getSeriesResistance());
    controlPanel.getSampleRateTextField().setText("" + controlModel.getSampleRate());
  }

  /** Here, all the action listeners are attached to the GUI components */
  public void doSetUpViewEvents() {

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
                  controlModel.setAmplitude(source.getValue() / (float) 100);
                  controlPanel
                      .getAmplitudeSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Amplitude [V] = " + controlModel.getAmplitude()));
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
                  controlPanel
                      .getPulseWidthSliderNs()
                      .setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
                }
              }
            });

    controlPanel
        .getPulseWidthSliderNs()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setPulseWidth(source.getValue());
                  controlPanel
                      .getPulseWidthSlider()
                      .setBorder(BorderFactory.createTitledBorder("Pulse Width [µs]"));
                  controlPanel
                      .getPulseWidthSliderNs()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Pulse Width [µs] = "
                                  + (double) controlModel.getPulseWidth() / 1000));
                }
              }
            });

    controlPanel
        .getPulseNumberSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setPulseNumber(source.getValue());
                  controlPanel
                      .getPulseNumberSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Pulse Number = " + controlModel.getPulseNumber()));
                }
              }
            });

    controlPanel
        .getDutyCycleSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {

                  controlModel.setDutyCycle((double) source.getValue() / 100.0);
                  controlPanel
                      .getDutyCycleSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Duty Cycle = " + controlModel.getDutyCycle()));
                }
              }
            });

    controlPanel
        .getSeriesTextField()
        .addKeyListener(
            new KeyAdapter() {

              @Override
              public void keyReleased(KeyEvent e) {

                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();

                try {
                  int newSeriesValue = Integer.parseInt(text);
                  controlModel.setSeriesResistance(newSeriesValue);
                } catch (Exception ex) {
                  // parsing error, default back to previous value
                  textField.setText(Integer.toString(controlModel.getSeriesResistance()));
                }
              }
            });

    controlPanel
        .getSampleRateTextField()
        .addKeyListener(
            new KeyAdapter() {

              @Override
              public void keyReleased(KeyEvent e) {

                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();

                try {
                  int newValue = Integer.parseInt(text);
                  controlModel.setSampleRate(newValue);
                } catch (Exception ex) {
                  // parsing error, default back to previous value
                  textField.setText(Integer.toString(controlModel.getSampleRate()));
                }
              }
            });

    //    controlPanel
    //        .getMemristorVoltageCheckBox()
    //        .addActionListener(
    //            new ActionListener() {
    //
    //              @Override
    //              public void actionPerformed(ActionEvent actionEvent) {
    //
    //                AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
    //                boolean selected = abstractButton.getModel().isSelected();
    //                // System.out.println("selected = " + selected);
    //                controlModel.setMemristorVoltageDropSelected(selected);
    //              }
    //            });

    controlView
        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke("S"), "startstop");
    controlView
        .getActionMap()
        .put(
            "startstop",
            new AbstractAction() {

              @Override
              public void actionPerformed(ActionEvent e) {

                controlPanel.getStartStopButton().doClick();
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
        initGUIComponentsFromModel();
        break;

      case Model.EVENT_WAVEFORM_UPDATE:
        controlModel.updateWaveformChartData();
        controlModel.updateEnergyData();
        controlPanel.updateEnergyGUI(
            controlModel.getAmplitude(),
            controlModel.getAppliedCurrent(),
            controlModel.getAppliedEnergy());
        break;

      default:
        break;
    }
  }
}
