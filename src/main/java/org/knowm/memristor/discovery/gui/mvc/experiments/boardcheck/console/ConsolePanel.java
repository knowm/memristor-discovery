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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentResultsPanel;

public class ConsolePanel extends ExperimentResultsPanel {

  JTextArea console;
  private ArrayList<String> lines = new ArrayList<>();

  /** Constructor */
  public ConsolePanel() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    console = new JTextArea();
    console.setLineWrap(true);
    console.setWrapStyleWord(true);
    // console.setAutoscrolls(true);

    // Font font = new Font("Courier", Font.PLAIN, 12);

    console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

    console.setForeground(Color.green);
    console.setBackground(Color.black);

    DefaultCaret caret = (DefaultCaret) console.getCaret();
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    Date now = new Date();
    lines.add("" + now.toString());
    lines.add("");

    loadLinesToColsol();

    console.setLineWrap(true);
    console.setWrapStyleWord(true);

    JScrollPane scroll =
        new JScrollPane(
            console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    add(scroll);
  }

  public void clear() {

    lines.clear();
    loadLinesToColsol();
  }

  public void loadLinesToColsol() {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lines.size(); i++) {
      sb.append(lines.get(i) + "\n");
    }

    console.setText(sb.toString());
  }

  public void println(String newLine) {

    lines.add(newLine);
    loadLinesToColsol();
  }

  public ArrayList<String> getLines() {

    return lines;
  }

  public void setLines(ArrayList<String> lines) {

    this.lines = lines;
  }
}
