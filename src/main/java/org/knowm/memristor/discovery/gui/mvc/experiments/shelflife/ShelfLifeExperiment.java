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
package org.knowm.memristor.discovery.gui.mvc.experiments.shelflife;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentResultsPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.console.ConsoleControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.console.ConsoleController;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.console.ConsolePanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control.ControlPanel;

public class ShelfLifeExperiment extends Experiment {

  private final ControlModel controlModel = new ControlModel();
  private final ControlPanel controlPanel;

  private final ConsolePanel plotPanel;
  private final ConsoleControlModel plotModel = new ConsoleControlModel();
  private final ConsoleController plotController;

  /** Constructor */
  public ShelfLifeExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    plotPanel = new ConsolePanel();
    plotController = new ConsoleController(plotPanel, plotModel);
    new ControlController(controlPanel, controlModel, dwfProxy);
  }

  /*
   * Here action listeners are attached to the widgets in the control panel and mapped to a worker, also defined here in the experiment.
   */
  @Override
  public void addWorkersToButtonEvents() {

    // don't need anything here as we're leveraging the default Start/Stop button and the default CaptureWorker

  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying
   * controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    // switch (propName) {
    //
    // case EVENT_INSTRUCTION_UPDATE:
    //
    // // System.out.println(controlModel.getInstruction());
    // // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());
    //
    // break;
    //
    // default:
    // break;
    // }
  }

  @Override
  public ExperimentControlModel getControlModel() {

    return controlModel;
  }

  @Override
  public ExperimentControlPanel getControlPanel() {

    return controlPanel;
  }

  @Override
  public ExperimentResultsPanel getPlotPanel() {

    return plotPanel;
  }

  @Override
  public SwingWorker getCaptureWorker() {

    return null;
  }
}
