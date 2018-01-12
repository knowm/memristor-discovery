/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2018 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery.gui.mvc.experiments;

import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.knowm.memristor.discovery.utils.FileUtils;
import org.knowm.memristor.discovery.utils.Util;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

public class ExperimentHelpDialog {

  /**
   * Constructor
   *
   * @param parentFrame
   * @param appName
   */
  public ExperimentHelpDialog(JFrame parentFrame, String appName) {


    String markdownString = FileUtils.readFileFromClasspathToString("help" + File.separatorChar + appName + ".md");
    Parser parser = Parser.builder().build();
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    Node document = parser.parse(markdownString);
    String htmlString = renderer.render(document);
//    System.out.println("htmlString = " + htmlString);

    String helpResourceFullPath = Util.getResourceFullPath("help");
    String replacedHTMLString = htmlString.replaceAll("file://help/", helpResourceFullPath + File.separatorChar);
//    System.out.println("replacedHTMLString = " + replacedHTMLString);

    JLabel textlabel = new JLabel("<html>" + replacedHTMLString + "</html>");
    textlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextPane textPane = new JTextPane();
    textPane.insertComponent(textlabel);

    JScrollPane sp = new JScrollPane(textPane);
    sp.setBorder(null);
    sp.setPreferredSize(new Dimension(1000, 800));


    final JDialog dialog = new JDialog(parentFrame, appName + " Help", false);
    dialog.getContentPane().add(sp);
    dialog.pack();
    dialog.setVisible(true);
  }
}
