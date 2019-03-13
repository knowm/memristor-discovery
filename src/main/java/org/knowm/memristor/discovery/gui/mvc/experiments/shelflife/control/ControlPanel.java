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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;

/**
 * Provides controls for running the control
 *
 * @author timmolter
 */
public class ControlPanel extends ControlView {

  private final JLabel saveDirectoryLabel;
  private final JButton saveDirectoryButton;
  private final JTextField saveDirectoryTextField;

  private final JLabel timeUnitLabel;
  private final JComboBox<TimeUnit> timeunitComboBox;

  private final JLabel intervalLabel;
  private final JTextField intervalTextField;

  private final JLabel seriesLabel;
  private final JTextField seriesTextField;

  private final JButton startStopButton;

  /** Constructor */
  public ControlPanel() {

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    saveDirectoryLabel = new JLabel("Save Directory");
    saveDirectoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(saveDirectoryLabel, c);

    saveDirectoryButton = new JButton("Choose");
    saveDirectoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(saveDirectoryButton, c);

    saveDirectoryTextField = new JTextField();
    saveDirectoryTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(saveDirectoryTextField, c);

    timeUnitLabel = new JLabel("Time Unit");
    timeUnitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(timeUnitLabel, c);

    this.timeunitComboBox = new JComboBox<>();
    timeunitComboBox.setFocusable(false);
    c.gridy++;
    c.insets = new Insets(0, 0, 14, 6);
    add(timeunitComboBox, c);

    intervalLabel = new JLabel("Repeat Interval");
    intervalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(intervalLabel, c);

    intervalTextField = new JTextField();
    intervalTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(intervalTextField, c);

    seriesLabel = new JLabel("Series R [Ohm]");
    seriesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 10, 4, 0);
    add(seriesLabel, c);

    seriesTextField = new JTextField();
    seriesTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 5, 14, 5);
    add(seriesTextField, c);

    startStopButton = new JButton("Start");
    startStopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.gridy++;
    c.insets = new Insets(0, 0, 0, 0);
    add(startStopButton, c);

    c.gridy++;
    add(logoLabel, c);
  }

  public void enableAllChildComponents(boolean enabled) {

    saveDirectoryButton.setEnabled(enabled);
    saveDirectoryTextField.setEnabled(enabled);
    timeunitComboBox.setEnabled(enabled);
    intervalTextField.setEnabled(enabled);
    seriesTextField.setEnabled(enabled);
    startStopButton.setEnabled(enabled);
  }

  public JButton getSaveDirectoryButton() {
    return saveDirectoryButton;
  }

  public JTextField getSaveDirectoryTextField() {
    return saveDirectoryTextField;
  }

  public JTextField getIntervalTextField() {
    return intervalTextField;
  }

  public JComboBox<TimeUnit> getTimeunitComboBox() {
    return timeunitComboBox;
  }

  public JTextField getSeriesTextField() {

    return seriesTextField;
  }

  public JButton getStartStopButton() {
    return startStopButton;
  }
}
