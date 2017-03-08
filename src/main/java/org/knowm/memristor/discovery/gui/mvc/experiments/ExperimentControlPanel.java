package org.knowm.memristor.discovery.gui.mvc.experiments;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Created by timmolter on 3/7/17.
 */
public abstract class ExperimentControlPanel extends JPanel {

  // TODO set this button up more, reduce copypasta
  public JButton startStopButton;

  public JButton getStartStopButton() {

    return startStopButton;
  }


}
