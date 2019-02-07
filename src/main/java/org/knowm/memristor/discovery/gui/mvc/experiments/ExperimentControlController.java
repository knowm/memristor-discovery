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
package org.knowm.memristor.discovery.gui.mvc.experiments;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Created by timmolter on 3/7/17.
 */
public abstract class ExperimentControlController implements PropertyChangeListener {

  protected abstract void doSetUpViewEvents();

  protected final ExperimentControlPanel experimentControlPanel;
  private final ExperimentControlModel experimentControlModel;

  /**
   * Constuctor
   *
   * @param experimentControlPanel
   * @param experimentControlModel
   */
  public ExperimentControlController(ExperimentControlPanel experimentControlPanel, ExperimentControlModel experimentControlModel) {

    this.experimentControlPanel = experimentControlPanel;
    this.experimentControlModel = experimentControlModel;
  }

  public void setUpViewEvents() {

    doSetUpViewEvents();

    experimentControlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "startstop");
    experimentControlPanel.getActionMap().put("startstop", new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {

        experimentControlPanel.getStartStopButton().doClick();
      }
    });
  }
}
