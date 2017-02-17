/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

/**
 * App Preferences
 * 
 * @author timmolter
 */
public abstract class AppPreferencesPanel extends JDialog {

  public abstract void doCreateAndShowGUI(JPanel preferencesPanel);

  public abstract void doSavePreferences();

  public abstract AppPreferences initAppPreferences();

  public abstract String getAppName();

  public static final int ID_OK = 1;
  public static final int ID_CANCEL = 0;
  private int exitCode = ID_CANCEL;

  protected AppPreferences appPreferences;

  /**
   * Constructor
   *
   * @param owner
   */
  public AppPreferencesPanel(JFrame owner) {

    super(owner);
    this.appPreferences = initAppPreferences();
    createAndShowGUI();
  }

  private void createAndShowGUI() {

    setPreferredSize(new Dimension(600, 400));
    setTitle("Preferences - " + getAppName());

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
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {

        exitCode = ID_CANCEL;
        AppPreferencesPanel.this.setVisible(false);
        AppPreferencesPanel.this.dispatchEvent(new WindowEvent(AppPreferencesPanel.this, WindowEvent.WINDOW_CLOSING));
      }
    });
    buttonPane.add(cancelButton);

    JButton okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {

        // save any changes to preferences
        try {
          doSavePreferences();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(null, "Error saving preference! Invalid Number entered.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        exitCode = ID_OK;
        AppPreferencesPanel.this.setVisible(false);
        AppPreferencesPanel.this.dispatchEvent(new WindowEvent(AppPreferencesPanel.this, WindowEvent.WINDOW_CLOSING));
      }
    });
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);

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
