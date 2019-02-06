/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2018 Knowm Inc. www.knowm.org
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
package org.knowm.memristor.discovery;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.knowm.memristor.discovery.gui.AboutDialog;
import org.knowm.memristor.discovery.gui.ConsoleDialog;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentHelpDialog;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.BoardCheckExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.boardcheck.BoardCheckPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.ClassifyExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.classify.ClassifyPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.conductance.ConductancePreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.dc.DCPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.HysteresisExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.hysteresis.HysteresisPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.logic.LogicPreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.PulseExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.pulse.PulsePreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.qc.QCExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.SynapseExperiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.SynapsePreferencesPanel;
import org.knowm.memristor.discovery.gui.mvc.footer.FooterController;
import org.knowm.memristor.discovery.gui.mvc.footer.FooterPanel;
import org.knowm.memristor.discovery.gui.mvc.header.HeaderController;
import org.knowm.memristor.discovery.gui.mvc.header.HeaderPanel;
import org.knowm.waveforms4j.DWFException;
import org.multibit.platform.GenericApplication;
import org.multibit.platform.GenericApplicationFactory;
import org.multibit.platform.GenericApplicationSpecification;
import org.multibit.platform.builder.mac.MacApplication;
import org.multibit.platform.listener.GenericAboutEvent;
import org.multibit.platform.listener.GenericAboutEventListener;
import org.multibit.platform.listener.GenericPreferencesEvent;
import org.multibit.platform.listener.GenericPreferencesEventListener;
import org.multibit.platform.listener.GenericQuitEvent;
import org.multibit.platform.listener.GenericQuitEventListener;
import org.multibit.platform.listener.GenericQuitResponse;

public class MemristorDiscovery
    implements GenericQuitEventListener,
        GenericPreferencesEventListener,
        GenericAboutEventListener,
        PropertyChangeListener {

  private static final String FRAME_TITLE_BASE = "Knowm Memristor Discovery - ";

  private MemristorDiscoveryPreferences memristorDiscoveryPreferences;
  private boolean isV1Board;

  private DWFProxy dwf;

  private final String[] appsV0;
  private final String[] appsV1;
  private String appID;
  private Experiment experiment;

  // Swing Stuff
  private JFrame mainFrame;
  private HeaderPanel headerPanel;
  private FooterPanel footerPanel;
  private ConsoleDialog consoleDialog;

  public static void main(String[] args) {

    System.setProperty("apple.awt.application.name", "Knowm Memristor Discovery");

    // Set the look and feel to users OS LaF.
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException
        | InstantiationException
        | UnsupportedLookAndFeelException
        | IllegalAccessException e) {
      e.printStackTrace();
    }

    final MemristorDiscovery memristorDiscovery = new MemristorDiscovery();

    // Schedule a job for the event dispatch thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(
        new Runnable() {

          @Override
          public void run() {

            memristorDiscovery.createAndShowGUI();
          }
        });
  }

  public MemristorDiscovery() {

    memristorDiscoveryPreferences = new MemristorDiscoveryPreferences();
    isV1Board = memristorDiscoveryPreferences.getBoardVersion().equalsIgnoreCase("v1");
    this.appsV1 = new String[] {"Synapse", "Logic", "Classify"};
    this.appsV0 =
        new String[] {
          "BoardCheck", "Hysteresis", "DC", "Pulse",
        };
  }

  public void createAndShowGUI() {

    // kill main frame if we're switching board version from menu
    if (mainFrame != null) {
      //      mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
      mainFrame.setVisible(false); // you can't see me!
      mainFrame.dispose(); // Destroy the JFrame object
      shutdownDWF();
    }
    this.dwf = new DWFProxy(isV1Board);

    GenericApplicationSpecification specification = new GenericApplicationSpecification();
    specification.getQuitEventListeners().add(this);
    specification.getPreferencesEventListeners().add(this);
    specification.getAboutEventListeners().add(this);
    GenericApplication genericApplication =
        GenericApplicationFactory.INSTANCE.buildGenericApplication(specification);

    if (genericApplication.isMac()) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
      URL iconURL = MemristorDiscovery.class.getResource("/img/logo_square_256.png");
      Image image = Toolkit.getDefaultToolkit().getImage(iconURL);
      MacApplication macApplication = (MacApplication) genericApplication;
      macApplication.setDockIconImage(image);
    }

    // Create and set up the window.
    mainFrame = new JFrame(FRAME_TITLE_BASE + appID);
    mainFrame.setResizable(true);
    mainFrame.addWindowListener(
        new java.awt.event.WindowAdapter() {

          @Override
          public void windowClosing(WindowEvent winEvt) {

            //            System.out.println("windowClosing");
            quit();
          }
        });
    Container mainFrameContainer = mainFrame.getContentPane();
    mainFrameContainer.setLayout(new BorderLayout(12, 0));

    URL iconURL = getClass().getResource("/img/logo_square_48.png");
    ImageIcon icon = new ImageIcon(iconURL);
    mainFrame.setIconImage(icon.getImage());

    // menu bar
    JMenuBar menuBar = new JMenuBar();

    // Board menu
    JMenu menu = new JMenu("Board");
    menu.setMnemonic(KeyEvent.VK_B);
    menuBar.add(menu);

    ButtonGroup group = new ButtonGroup();

    JRadioButtonMenuItem boardMenuItem =
        new JRadioButtonMenuItem(
            new AbstractAction("V0") {

              @Override
              public void actionPerformed(ActionEvent e) {

                System.out.println("V0");
                updateBoardPreferences("V0");
              }
            });
    boardMenuItem.setActionCommand(boardMenuItem.getName());
    if (isV1Board) {
      boardMenuItem.setSelected(false);
    } else {
      boardMenuItem.setSelected(true);
    }
    group.add(boardMenuItem);
    menu.add(boardMenuItem);

    boardMenuItem =
        new JRadioButtonMenuItem(
            new AbstractAction("V1") {

              @Override
              public void actionPerformed(ActionEvent e) {

                System.out.println("V1");
                updateBoardPreferences("V1");
              }
            });
    boardMenuItem.setActionCommand(boardMenuItem.getName());
    if (isV1Board) {
      boardMenuItem.setSelected(true);
    } else {
      boardMenuItem.setSelected(false);
    }
    group.add(boardMenuItem);
    menu.add(boardMenuItem);

    // Experiments menu
    menu = new JMenu("Experiments");
    menu.setMnemonic(KeyEvent.VK_E);
    menuBar.add(menu);
    for (int i = 0; i < appsV0.length; i++) {

      JMenuItem appMenuItem =
          new JMenuItem(
              new AbstractAction(appsV0[i]) {

                @Override
                public void actionPerformed(ActionEvent e) {

                  try {
                    dwf.shutdownAD2();
                  } catch (DWFException e1) {
                    e1.printStackTrace();
                  }
                  Container mainFrameContainer = mainFrame.getContentPane();
                  mainFrameContainer.removeAll();
                  mainFrameContainer.revalidate();
                  mainFrameContainer.repaint();
                  mainFrameContainer.add(headerPanel, BorderLayout.NORTH);
                  mainFrameContainer.add(footerPanel, BorderLayout.SOUTH);

                  experiment = null;
                  appID = e.getActionCommand();
                  // System.out.println(appID);

                  switch (e.getActionCommand()) {
                    case "Hysteresis":
                      experiment =
                          new HysteresisExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                      break;
                    case "Pulse":
                      experiment = new PulseExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                      break;
                    case "DC":
                      experiment = new DCExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                      break;
                    case "QC":
                      experiment = new QCExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                      break;
                    case "BoardCheck":
                      experiment =
                          new BoardCheckExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                      break;

                    default:
                      break;
                  }

                  experiment.createAndShowGUI();
                  // for console message from experiments
                  experiment.getControlModel().addListener(MemristorDiscovery.this);

                  dwf.startupAD2();

                  mainFrame.setTitle(FRAME_TITLE_BASE + e.getActionCommand());
                }
              });
      appMenuItem.setActionCommand(appMenuItem.getName());
      menu.add(appMenuItem);
    }

    if (isV1Board) {
      for (int i = 0; i < appsV1.length; i++) {

        JMenuItem appMenuItem =
            new JMenuItem(
                new AbstractAction(appsV1[i]) {

                  @Override
                  public void actionPerformed(ActionEvent e) {

                    try {
                      dwf.shutdownAD2();
                    } catch (DWFException e1) {
                      e1.printStackTrace();
                    }
                    Container mainFrameContainer = mainFrame.getContentPane();
                    mainFrameContainer.removeAll();
                    mainFrameContainer.revalidate();
                    mainFrameContainer.repaint();
                    mainFrameContainer.add(headerPanel, BorderLayout.NORTH);
                    mainFrameContainer.add(footerPanel, BorderLayout.SOUTH);

                    experiment = null;
                    appID = e.getActionCommand();
                    // System.out.println(appID);

                    switch (e.getActionCommand()) {
                      case "Synapse":
                        experiment =
                            new SynapseExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                        break;
                      case "Logic":
                        experiment =
                            new LogicExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                        break;
                      case "Classify":
                        experiment =
                            new ClassifyExperiment(dwf, mainFrame.getContentPane(), isV1Board);
                        break;
                      case "BoardCheck":
                        experiment = new BoardCheckExperiment(dwf, mainFrameContainer, isV1Board);
                        break;

                      default:
                        break;
                    }
                    experiment.createAndShowGUI();
                    // for console message from experiments
                    experiment.getControlModel().addListener(MemristorDiscovery.this);

                    dwf.startupAD2();

                    mainFrame.setTitle(FRAME_TITLE_BASE + e.getActionCommand());
                  }
                });
        appMenuItem.setActionCommand(appMenuItem.getName());
        menu.add(appMenuItem);
      }
    }

    // Window menu
    menu = new JMenu("Window");
    menu.setMnemonic(KeyEvent.VK_W);
    menuBar.add(menu);

    JMenuItem helpMenuItem =
        new JMenuItem(
            new AbstractAction("Help") {

              @Override
              public void actionPerformed(ActionEvent e) {

                new ExperimentHelpDialog(mainFrame, appID);
              }
            });
    helpMenuItem.setActionCommand(helpMenuItem.getName());
    menu.add(helpMenuItem);

    JMenuItem consoleMenuItem =
        new JMenuItem(
            new AbstractAction("Console") {

              @Override
              public void actionPerformed(ActionEvent e) {

                consoleDialog = new ConsoleDialog();
                consoleDialog.setVisible(true);
              }
            });
    consoleMenuItem.setActionCommand(consoleMenuItem.getName());
    menu.add(consoleMenuItem);

    if (!genericApplication.isMac()) {

      JMenuItem prefsMenuItem =
          new JMenuItem(
              new AbstractAction("Preferences") {

                @Override
                public void actionPerformed(ActionEvent e) {

                  showPreferences();
                }
              });
      prefsMenuItem.setActionCommand(prefsMenuItem.getName());
      menu.add(prefsMenuItem);
    }

    mainFrame.setJMenuBar(menuBar);

    // panels
    headerPanel = new HeaderPanel();
    mainFrameContainer.add(headerPanel, BorderLayout.NORTH);

    footerPanel = new FooterPanel();
    mainFrameContainer.add(footerPanel, BorderLayout.SOUTH);

    new FooterController(footerPanel, dwf);
    new HeaderController(headerPanel, dwf);

    // default control injected here

    if (isV1Board) {

      experiment = new SynapseExperiment(dwf, mainFrameContainer, isV1Board);
      experiment.createAndShowGUI();
      appID = "Synapse";

    } else {

      experiment = new HysteresisExperiment(dwf, mainFrameContainer, isV1Board);
      experiment.createAndShowGUI();
      appID = "Hysteresis";
    }

    // experiment = new DCExperiment(dwf, mainFrameContainer);
    // experiment.createAndShowGUI();
    // appID = "DC";

    // experiment = new ConductanceExperiment(dwf, mainFrameContainer);
    // experiment.createAndShowGUI();
    // appID = "Conductance";

    // experiment = new PulseExperiment(dwf, mainFrameContainer);
    // experiment.createAndShowGUI();
    // appID = "Pulse";

    // for console message from experiments
    experiment.getControlModel().addListener(this);

    mainFrame.setTitle(FRAME_TITLE_BASE + appID);

    // Display the window.
    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    mainFrame.setVisible(true);
    mainFrame.setResizable(true);
    mainFrame.pack();
    // // make the frame half the height and width
    // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // double height = screenSize.height;
    // double width = screenSize.width;
    // mainFrame.setSize(1000, 1200);

    // center the jframe on screen
    mainFrame.setLocationRelativeTo(null);

    dwf.startupAD2();
  }

  private void updateBoardPreferences(String boardVersion) {

    isV1Board = boardVersion.equalsIgnoreCase("v1");
    memristorDiscoveryPreferences.updatePreferences(boardVersion);
    createAndShowGUI();
  }

  @Override
  public void onQuitEvent(GenericQuitEvent event, GenericQuitResponse response) {

    quit();
  }

  private void quit() {

    // System.out.println("here0");
    shutdownDWF();
    // dwf.FDwfDeviceCloseAll();
    //
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // System.out.println("here1");
    // mainFrame.dispose();
    System.exit(0);
  }

  private void shutdownDWF() {
    try {
      dwf.shutdownAD2();
    } catch (DWFException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onPreferencesEvent(GenericPreferencesEvent event) {

    showPreferences();
  }

  private void showPreferences() {

    int result = 0;
    // System.out.println("appID= " + appID);
    switch (appID) {
      case "Hysteresis":
        result = new HysteresisPreferencesPanel(mainFrame).doModal();
        break;
      case "Pulse":
        result = new PulsePreferencesPanel(mainFrame).doModal();
        break;
      case "DC":
        result = new DCPreferencesPanel(mainFrame).doModal();
        break;
      case "Conductance":
        result = new ConductancePreferencesPanel(mainFrame).doModal();
        break;
      case "Synapse":
        result = new SynapsePreferencesPanel(mainFrame).doModal();
        break;
      case "Logic":
        result = new LogicPreferencesPanel(mainFrame).doModal();
        break;
      case "Classify":
        result = new ClassifyPreferencesPanel(mainFrame).doModal();
        break;
      case "BoardCheck":
        result = new BoardCheckPreferencesPanel(mainFrame).doModal();
        break;

      default:
        break;
    }
    if (result == ExperimentPreferencesPanel.ID_OK) {
      experiment.refreshModelFromPreferences();
    }
  }

  @Override
  public void onAboutEvent(GenericAboutEvent event) {

    new AboutDialog(mainFrame);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    //    System.out.println("PC");

    switch (evt.getPropertyName()) {
      case ExperimentControlModel.EVENT_NEW_CONSOLE_LOG:
        if (consoleDialog != null) {
          // System.out.println("evt = " + evt);
          consoleDialog.addConsoleMessage((String) evt.getNewValue());
        }
        break;

      default:
        break;
    }
  }
}
