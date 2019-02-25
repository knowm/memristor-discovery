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
package org.knowm.memristor.discovery.gui.mvc.experiments;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.rightbar.RightBarController;
import org.knowm.memristor.discovery.gui.mvc.rightbar.RightBarPanel;

public abstract class Experiment implements PropertyChangeListener {

  public final DWFProxy dwfProxy;

  public final Container mainFrameContainer;

  private final boolean isV1Board;

  private SwingWorker experimentCaptureWorker;

  /**
   * Constructor
   *
   * @param dwfProxy
   */
  public Experiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    this.dwfProxy = dwfProxy;
    this.mainFrameContainer = mainFrameContainer;
    this.isV1Board = isV1Board;
  }

  public abstract ExperimentControlModel getControlModel();

  public abstract ExperimentControlPanel getControlPanel();

  public abstract ExperimentResultsPanel getResultPanel();

  public abstract SwingWorker getCaptureWorker();

  public abstract void addWorkersToButtonEvents();

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

    // trigger result of waveform
    PropertyChangeEvent evt =
        new PropertyChangeEvent(this, ExperimentControlModel.EVENT_WAVEFORM_UPDATE, true, false);
    propertyChange(evt);

    getControlModel().addListener(this);

    // Most experiments have a Start/Stop button, so we attach the default CaptureWorker to it's
    // click events here. This doesn't mean that the experiment HAS to use the default CaptureWorker
    getControlPanel()
        .getStartStopButton()
        .addActionListener(
            new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {

                if (!getControlModel().isStartToggled()) {

                  getControlModel().setStartToggled(true);
                  getControlPanel().getStartStopButton().setText("Stop");

                  // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
                  experimentCaptureWorker = getCaptureWorker();
                  experimentCaptureWorker.execute();
                } else {

                  getControlModel().setStartToggled(false);
                  getControlPanel().getStartStopButton().setText("Start");

                  // cancel the worker
                  experimentCaptureWorker.cancel(true);
                }
              }
            });

    // Here additional CaptureWorkers are added to addition buttons needed for the experiment
    addWorkersToButtonEvents(); // after adding the default start/stop action so actions can be
    // overridden.

    // //////////////////////
    // Plot Panel //////////
    // //////////////////////

    mainFrameContainer.add(getResultPanel(), BorderLayout.CENTER);

    // //////////////////////
    // Plot Panel //////////
    // //////////////////////

    if (isV1Board) {
      RightBarPanel rightBarPanel = new RightBarPanel();
      RightBarController rightBarController = new RightBarController(rightBarPanel, dwfProxy);
      mainFrameContainer.add(rightBarPanel, BorderLayout.EAST);
    }
  }

  public void refreshModelFromPreferences() {

    getControlModel().loadModelFromPrefs();
  }
}
