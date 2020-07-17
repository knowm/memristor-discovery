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

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import org.knowm.memristor.discovery.core.FileUtils;
import org.knowm.memristor.discovery.core.Util;

public class ExperimentHelpDialog {

  /**
   * Constructor
   *
   * @param parentFrame
   * @param experimentName
   */
  public ExperimentHelpDialog(JFrame parentFrame, String experimentName) {

    String markdownString =
        FileUtils.readFileFromClasspathToString("help" + "/" + experimentName + ".md");
    Parser parser = Parser.builder().build();
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    Node document = parser.parse(markdownString);
    String htmlString = renderer.render(document);
    //    System.out.println("htmlString = " + htmlString);

    String helpResourceFullPath = Util.getResourceFullPath("help");
    String replacedHTMLString =
        htmlString.replace("img src=\"", "img src=\"" + helpResourceFullPath + "/");
    //    System.out.println("replacedHTMLString = " + replacedHTMLString);

    //    JLabel textlabel = new JLabel("<html>" + replacedHTMLString + "</html>");
    //    textlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    //
    //    JTextPane textPane = new JTextPane();
    //    textPane.insertComponent(textlabel);
    JEditorPane editorPane = new JEditorPane();
    editorPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    HTMLEditorKit kit = new HTMLEditorKit();
    editorPane.setEditorKit(kit);
    Document doc = kit.createDefaultDocument();
    editorPane.setDocument(doc);

    editorPane.setEditable(false);
    editorPane.setText("<html>" + replacedHTMLString + "</html>");
    //    editorPane.setContentType("text/html");

    JScrollPane sp = new JScrollPane(editorPane);
    sp.setBorder(null);
    sp.setPreferredSize(new Dimension(1000, 800));

    final JDialog dialog = new JDialog(parentFrame, experimentName + " Help", true);
    dialog.getContentPane().add(sp);
    dialog.pack();
    dialog.setVisible(true);
  }
}
