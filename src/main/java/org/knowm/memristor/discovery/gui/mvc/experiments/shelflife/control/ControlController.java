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
import java.beans.PropertyChangeEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
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
