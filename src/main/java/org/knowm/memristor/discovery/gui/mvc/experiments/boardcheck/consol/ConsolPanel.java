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
package org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.consol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;

public class ConsolPanel extends ExperimentPlotPanel {

  JTextArea consol;
  private ArrayList<String> lines = new ArrayList<>();

  /**
   * Constructor
   */
  public ConsolPanel() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

    consol = new JTextArea();
    consol.setLineWrap(true);
    consol.setWrapStyleWord(true);
    // consol.setAutoscrolls(true);

    // Font font = new Font("Courier", Font.PLAIN, 12);

    consol.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

    consol.setForeground(Color.green);
    consol.setBackground(Color.black);

    DefaultCaret caret = (DefaultCaret) consol.getCaret();
    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    Date now = new Date();
    lines.add("" + now.toString());
    lines.add("");

    loadLinesToColsol();

    consol.setLineWrap(true);
    consol.setWrapStyleWord(true);

    JScrollPane scroll = new JScrollPane(consol, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    add(scroll);
  }

  public void loadLinesToColsol() {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lines.size(); i++) {
      sb.append(lines.get(i) + "\n");
    }

    consol.setText(sb.toString());
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
