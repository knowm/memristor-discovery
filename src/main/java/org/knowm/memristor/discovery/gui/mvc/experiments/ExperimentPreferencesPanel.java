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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** Experiment Preferences */
public abstract class ExperimentPreferencesPanel extends JDialog {

  public static final int ID_OK = 1;
  public static final int ID_CANCEL = 0;
  public final String experimentName;
  protected ExperimentPreferences experimentPreferences;
  private int exitCode = ID_CANCEL;

  /**
   * Constructor
   *
   * @param owner
   */
  public ExperimentPreferencesPanel(JFrame owner, String experimentName) {

    super(owner);
    this.experimentName = experimentName;
    this.experimentPreferences = initAppPreferences();
    createAndShowGUI();
  }

  public abstract void doCreateAndShowGUI(JPanel preferencesPanel);

  public abstract void doSavePreferences();

  public abstract ExperimentPreferences initAppPreferences();

  private void createAndShowGUI() {

    //    setPreferredSize(new Dimension(600, 400));
    setTitle("Preferences - " + experimentName);

    getContentPane().setLayout(new BorderLayout());

    JPanel preferencesPanel = new JPanel();
    preferencesPanel.setLayout(new GridBagLayout());

    preferencesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    // load app-specific GUI components
    doCreateAndShowGUI(preferencesPanel);
    getContentPane().add(preferencesPanel, BorderLayout.CENTER);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("Cancel");
    cancelButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent arg0) {

            exitCode = ID_CANCEL;
            ExperimentPreferencesPanel.this.setVisible(false);
            ExperimentPreferencesPanel.this.dispatchEvent(
                new WindowEvent(ExperimentPreferencesPanel.this, WindowEvent.WINDOW_CLOSING));
          }
        });
    buttonPane.add(cancelButton);

    JButton okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    okButton.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent arg0) {

            // save any changes to preferences
            try {
              doSavePreferences();
            } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(
                  null,
                  "Error saving preference! Invalid Number entered.",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            }

            exitCode = ID_OK;
            ExperimentPreferencesPanel.this.setVisible(false);
            ExperimentPreferencesPanel.this.dispatchEvent(
                new WindowEvent(ExperimentPreferencesPanel.this, WindowEvent.WINDOW_CLOSING));
          }
        });
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);

    // setPreferredSize(getPreferredSize());

    pack();
    setLocationRelativeTo(getParent());
  }

  @Override
  public void dispose() {

    super.dispose();
  }

  public int doModal() {

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);
    setVisible(true);

    return exitCode;
  }
}
