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
