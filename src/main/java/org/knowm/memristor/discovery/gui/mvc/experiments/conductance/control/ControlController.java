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
package org.knowm.memristor.discovery.gui.mvc.experiments.conductance.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Controller;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;

public class ControlController extends Controller {

  private final ControlPanel controlPanel;
  private final ControlModel controlModel;

  ActionListener waveformRadioButtonActionListener =
      new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

          for (Enumeration<AbstractButton> buttons =
                  controlPanel.getResetPulseTypeRadioButtonGroup().getElements();
              buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
              controlModel.setWaveform(button.getText());
            }
          }
        }
      };

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

    // RESET
    switch (controlModel.getResetPulseType()) {
      case Sawtooth:
        controlPanel.getSawToothRadioButton().setSelected(true);
        break;
      case Triangle:
        controlPanel.getSquareRadioButton().setSelected(true);
        break;
      default:
        controlPanel.getSawToothRadioButton().setSelected(true);
        break;
    }

    controlPanel.getResetAmplitudeSlider().setValue((int) (controlModel.getResetAmplitude() * 100));
    controlPanel
        .getResetAmplitudeSlider()
        .setBorder(
            BorderFactory.createTitledBorder(
                "Reset Amplitude [V] = " + controlModel.getResetAmplitude()));
    controlPanel.getResetPulseWidthSlider().setValue((controlModel.getResetPulseWidth()));
    controlPanel
        .getResetPulseWidthSlider()
        .setBorder(
            BorderFactory.createTitledBorder(
                "Reset Period [µs] = " + controlModel.getResetPulseWidth() / 1000));

    // SET

    controlPanel.getSetAmplitudeSlider().setValue((int) (controlModel.getSetAmplitude() * 100));
    controlPanel
        .getSetAmplitudeSlider()
        .setBorder(
            BorderFactory.createTitledBorder(
                "Set Amplitude [V] = " + controlModel.getSetAmplitude()));
    controlPanel.getSetPulseWidthSlider().setValue((controlModel.getSetPulseWidth()));
    controlPanel
        .getSetPulseWidthSlider()
        .setBorder(
            BorderFactory.createTitledBorder(
                "Set Period [µs] = " + controlModel.getSetPulseWidth() / 1000));

    controlPanel.getSeriesTextField().setText("" + controlModel.getSeriesResistance());
  }

  /** Here, all the action listeners are attached to the GUI components */
  public void doSetUpViewEvents() {

    controlPanel.getSawToothRadioButton().addActionListener(waveformRadioButtonActionListener);
    controlPanel.getSquareRadioButton().addActionListener(waveformRadioButtonActionListener);

    controlPanel
        .getResetAmplitudeSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setResetAmplitude(source.getValue() / (float) 100);
                  controlPanel
                      .getResetAmplitudeSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Set Amplitude [V] = " + controlModel.getResetAmplitude()));
                }
              }
            });

    controlPanel
        .getResetPulseWidthSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setResetPulseWidth(source.getValue());
                  controlPanel
                      .getResetPulseWidthSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Set Pulse Width [µs] = "
                                  + (double) controlModel.getResetPulseWidth() / 1000));
                }
              }
            });

    // SET

    controlPanel
        .getSetConductanceSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setSetConductance(source.getValue() / (float) 1000);
                  controlPanel
                      .getSetConductanceSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Set Conductance [mS] = " + controlModel.getSetConductance()));
                }
              }
            });
    controlPanel
        .getSetAmplitudeSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setSetAmplitude(source.getValue() / (float) 100);
                  controlPanel
                      .getSetAmplitudeSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Set Amplitude [V] = " + controlModel.getSetAmplitude()));
                }
              }
            });

    controlPanel
        .getSetPulseWidthSlider()
        .addChangeListener(
            new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent e) {

                JSlider source = (JSlider) e.getSource();
                if (!(source.getValueIsAdjusting())) {
                  controlModel.setSetPulseWidth(source.getValue());
                  controlPanel
                      .getSetPulseWidthSlider()
                      .setBorder(
                          BorderFactory.createTitledBorder(
                              "Set Pulse Width [µs] = "
                                  + (double) controlModel.getSetPulseWidth() / 1000));
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
        break;

      default:
        break;
    }
  }
}
