package org.knowm.memristor.discovery.gui.mvc.experiments.classify;

import java.util.List;

public class SupervisedPattern {

  public final boolean state;
  public final List<Integer> spikePattern;

  public SupervisedPattern(boolean state, List<Integer> spikePattern) {
    this.state = state;
    this.spikePattern = spikePattern;
  }

}
