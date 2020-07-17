/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2020 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.rightbar.RightBarController;
import org.knowm.memristor.discovery.gui.mvc.rightbar.RightBarPanel;

public abstract class Experiment implements PropertyChangeListener {

  public final DWFProxy dwfProxy;

  public final Container mainFrameContainer;

  protected final int boardVersion;

  protected ExperimentPreferences experimentPreferences;

  /** Constructor */
  public Experiment(DWFProxy dwfProxy, Container mainFrameContainer, int boardVersion) {

    this.dwfProxy = dwfProxy;
    this.mainFrameContainer = mainFrameContainer;
    this.boardVersion = boardVersion;

    this.experimentPreferences = initAppPreferences();
  }

  public abstract ExperimentPreferences initAppPreferences();

  public abstract Model getControlModel();

  public abstract ControlView getControlPanel();

  public abstract Model getResultModel();

  public abstract JPanel getResultPanel();

  public abstract void addWorkersToButtonEvents();

  public abstract void doCreateAndShowGUI();

  public void createAndShowGUI() {

    // //////////////////////
    // Control Panel ///////
    // //////////////////////

    JScrollPane jScrollPane =
        new JScrollPane(
            getControlPanel(),
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane.setBorder(createEmptyBorder());
    mainFrameContainer.add(jScrollPane, BorderLayout.WEST);

    // //////////////////////
    // RESULTS Panel //////////
    // //////////////////////

    mainFrameContainer.add(getResultPanel(), BorderLayout.CENTER);

    // //////////////////////
    // Right Bar Panel //////////
    // //////////////////////

    if (boardVersion == 1) {
      RightBarPanel rightBarPanel = new RightBarPanel();
      RightBarController rightBarController = new RightBarController(rightBarPanel, dwfProxy);
      mainFrameContainer.add(rightBarPanel, BorderLayout.EAST);
    }

    // take care of anything else that may need to ccur after the main GUI structure has been setup
    doCreateAndShowGUI();

    // Here additional CaptureWorkers are added to addition buttons needed for the experiment
    addWorkersToButtonEvents(); // after adding the default start/stop action so actions can be
    // overridden.
  }

  /**
   * This is called from `MemristorDiscovery` when the Preferences window is closed and by the
   * Experiments when their models are created
   */
  public void refreshModelsFromPreferences() {

    getControlModel().loadModelFromPrefs(experimentPreferences);
    getResultModel().loadModelFromPrefs(experimentPreferences);
  }
}
