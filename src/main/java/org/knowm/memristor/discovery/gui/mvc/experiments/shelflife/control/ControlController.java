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
package org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.FileUtils;
import org.knowm.memristor.discovery.gui.mvc.experiments.Controller;
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

    controlPanel.getSaveDirectoryTextField().setText("" + controlModel.getSaveDirectory());
    //controlPanel.getTimeunitComboBox().setModel(new DefaultComboBoxModel<>(TimeUnit.values()));

    //    controlPanel.getTimeunitComboBox().setModel(new DefaultComboBoxModel<>(new TimeUnit[]{TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.DAYS}));
    //
    //    controlPanel.getTimeunitComboBox().setSelectedItem(controlModel.getTimeUnit());
    //    //controlPanel.getSeriesTextField().setText("" + controlModel.getSeriesResistance());
    //    controlPanel.getIntervalTextField().setText("" + controlModel.getRepeatInterval());
  }

  /** Here, all the action listeners are attached to the GUI components */
  @Override
  public void doSetUpViewEvents() {

    controlPanel.getSaveDirectoryButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          String saveDirectory = FileUtils.showSaveAsDialog(controlPanel, controlModel.getSaveDirectory());
          controlModel.setSaveDirectory(saveDirectory);

          controlPanel.getSaveDirectoryTextField().setText(saveDirectory);

        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });

    //    controlPanel.getTimeunitComboBox().addActionListener(new ActionListener() {
    //      @Override
    //      public void actionPerformed(ActionEvent e) {
    //
    //        controlModel.setTimeUnit(controlPanel.getTimeunitComboBox().getSelectedItem().toString());
    //      }
    //    });
    //
    //    controlPanel.getIntervalTextField().addKeyListener(new KeyAdapter() {
    //
    //      @Override
    //      public void keyReleased(KeyEvent e) {
    //
    //        JTextField textField = (JTextField) e.getSource();
    //        String text = textField.getText();
    //
    //        try {
    //          int newInterval = Integer.parseInt(text);
    //          controlModel.setRepeatInterval(newInterval);
    //        } catch (Exception ex) {
    //          // parsing error, default back to previous value
    //          textField.setText(Integer.toString(controlModel.getRepeatInterval()));
    //        }
    //      }
    //    });

    //    controlPanel.getSeriesTextField().addKeyListener(new KeyAdapter() {
    //
    //      @Override
    //      public void keyReleased(KeyEvent e) {
    //
    //        JTextField textField = (JTextField) e.getSource();
    //        String text = textField.getText();
    //
    //        try {
    //          int newSeriesValue = Integer.parseInt(text);
    //          controlModel.setSeriesResistance(newSeriesValue);
    //        } catch (Exception ex) {
    //          // parsing error, default back to previous value
    //          textField.setText(Integer.toString(controlModel.getSeriesResistance()));
    //        }
    //      }
    //    });

    controlView.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "startstop");
    controlView.getActionMap().put("startstop", new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        controlPanel.getStartStopButton().doClick();
      }
    });
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can
   * respond to those events and make sure the corresponding GUI components get updated.
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

      default:
        break;
    }
  }
}
