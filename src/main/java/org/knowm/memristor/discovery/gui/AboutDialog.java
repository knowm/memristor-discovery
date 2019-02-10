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
package org.knowm.memristor.discovery.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.knowm.memristor.discovery.utils.Util;

public class AboutDialog extends JDialog {

  /**
   * Constructor
   *
   * @param parentFrame
   */
  public AboutDialog(JFrame parentFrame) {

    setPreferredSize(new Dimension(400, 300));
    setTitle("About");
    getContentPane().setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBorder(new EmptyBorder(25, 5, 5, 5));

    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(10, 10, 10, 10);
    gc.fill = GridBagConstraints.NONE;
    gc.anchor = GridBagConstraints.CENTER;

    gc.gridy = 0;
    gc.gridx = 0;
    JLabel logoLabel = new JLabel(Util.createImageIcon("img/logo_200.png"));
    panel.add(logoLabel, gc);

    // String markdownString = FileUtils.readFileFromClasspathToString("help" + "/" + appName + ".md");
    // PegDownProcessor processor = new PegDownProcessor();
    // String htmlString = processor.markdownToHtml(markdownString);

    gc.gridy++;
    JLabel textlabel = new JLabel("Memristor Discovery");
    textlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(textlabel, gc);

    gc.gridy++;
    textlabel = new JLabel("Version: " + Util.getVersionNumber());
    textlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(textlabel, gc);

    // JTextPane textPane = new JTextPane();
    // textPane.insertComponent(textlabel);

    // JScrollPane sp = new JScrollPane(textPane);
    // sp.setBorder(null);
    // sp.setPreferredSize(new Dimension(400, 500));
    //
    // helpPanel.add(sp);

    getContentPane().add(panel, BorderLayout.CENTER);
    pack();
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);
    setVisible(true);
  }

}
