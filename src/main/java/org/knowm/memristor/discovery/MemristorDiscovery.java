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
package org.knowm.memristor.discovery;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.*;

import org.knowm.memristor.discovery.gui.AboutDialog;
import org.knowm.memristor.discovery.gui.mvc.apps.App;
import org.knowm.memristor.discovery.gui.mvc.apps.AppHelpDialog;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.HysteresisApp;
import org.knowm.memristor.discovery.gui.mvc.apps.hysteresis.HysteresisPrefencesPanel;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse.PulseApp;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse.PulsePrefencesPanel;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.PulseApp2;
import org.knowm.memristor.discovery.gui.mvc.apps.pulse2.PulsePrefencesPanel2;
import org.knowm.memristor.discovery.gui.mvc.apps.qc.QCApp;
import org.knowm.memristor.discovery.gui.mvc.apps.qc.QCPrefencesPanel;
import org.knowm.memristor.discovery.gui.mvc.footer.FooterController;
import org.knowm.memristor.discovery.gui.mvc.footer.FooterPanel;
import org.knowm.memristor.discovery.gui.mvc.header.HeaderController;
import org.knowm.memristor.discovery.gui.mvc.header.HeaderPanel;
import org.multibit.platform.GenericApplication;
import org.multibit.platform.GenericApplicationFactory;
import org.multibit.platform.GenericApplicationSpecification;
import org.multibit.platform.listener.GenericAboutEvent;
import org.multibit.platform.listener.GenericAboutEventListener;
import org.multibit.platform.listener.GenericPreferencesEvent;
import org.multibit.platform.listener.GenericPreferencesEventListener;
import org.multibit.platform.listener.GenericQuitEvent;
import org.multibit.platform.listener.GenericQuitEventListener;
import org.multibit.platform.listener.GenericQuitResponse;

public class MemristorDiscovery implements GenericQuitEventListener, GenericPreferencesEventListener, GenericAboutEventListener {

  private final static String FRAME_TITLE_BASE = "Knowm Memristor Discovery - ";

  private final DWFProxy dwf = new DWFProxy();

  // private final String[] apps = new String[] { "Hysteresis", "Pulse", "Pulse2", "QC" };
  private final String[] apps = new String[] { "Hysteresis", "QC" };
  private String appID;
  // private String appID = apps[0];
  private App app;

  // Swing Stuff
  private JFrame mainFrame;
  private HeaderPanel headerPanel;
  private FooterPanel footerPanel;

  public static void main(String[] args) {

    System.setProperty("apple.awt.application.name", "Knowm Memristor Discovery");

    //Set the look and feel to users OS LaF.
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }

    final MemristorDiscovery memristorDiscovery = new MemristorDiscovery();

    // Schedule a job for the event dispatch thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

        memristorDiscovery.createAndShowGUI();
      }
    });
  }

  public void createAndShowGUI() {

    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");

    GenericApplicationSpecification specification = new GenericApplicationSpecification();
    specification.getQuitEventListeners().add(this);
    specification.getPreferencesEventListeners().add(this);
    specification.getAboutEventListeners().add(this);
    GenericApplication genericApplication = GenericApplicationFactory.INSTANCE.buildGenericApplication(specification);

    // Create and set up the window.
    mainFrame = new JFrame(FRAME_TITLE_BASE + appID);
    mainFrame.setResizable(true);
    mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent winEvt) {

        System.out.println("windowClosing");
        quit();
      }
    });
    Container mainFrameContainer = mainFrame.getContentPane();
    mainFrameContainer.setLayout(new BorderLayout(12, 0));

    // app menu
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Applications");
    menu.setMnemonic(KeyEvent.VK_A);
    menuBar.add(menu);
    for (int i = 0; i < apps.length; i++) {

      JMenuItem appMenuItem = new JMenuItem(new AbstractAction(apps[i]) {

        @Override
        public void actionPerformed(ActionEvent e) {

          dwf.shutdownAD2();

          Container mainFrameContainer = mainFrame.getContentPane();
          mainFrameContainer.removeAll();
          mainFrameContainer.revalidate();
          mainFrameContainer.add(headerPanel, BorderLayout.NORTH);
          mainFrameContainer.add(footerPanel, BorderLayout.SOUTH);

          app = null;
          appID = e.getActionCommand();
          System.out.println(appID);

          switch (e.getActionCommand()) {
          case "Hysteresis":
            app = new HysteresisApp(dwf, mainFrame.getContentPane());
            break;
          case "Pulse":
            app = new PulseApp(dwf, mainFrame.getContentPane());
            break;
          case "Pulse2":
            app = new PulseApp2(dwf, mainFrame.getContentPane());
            break;
          case "QC":
            app = new QCApp(dwf, mainFrame.getContentPane());
            break;

          default:
            break;
          }

          dwf.startupAD2();

          mainFrame.setTitle(FRAME_TITLE_BASE + e.getActionCommand());
        }

      });
      appMenuItem.setActionCommand(appMenuItem.getName());
      menu.add(appMenuItem);
    }

    // Window menu
    menu = new JMenu("Window");
    menu.setMnemonic(KeyEvent.VK_W);
    menuBar.add(menu);

    JMenuItem helpMenuItem = new JMenuItem(new AbstractAction("Help") {

      @Override
      public void actionPerformed(ActionEvent e) {

        new AppHelpDialog(mainFrame, appID);
      }

    });
    helpMenuItem.setActionCommand(helpMenuItem.getName());
    menu.add(helpMenuItem);

    if (!genericApplication.isMac()) {

      JMenuItem prefsMenuItem = new JMenuItem(new AbstractAction("Preferences") {

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

    // default app injected here

    app = new HysteresisApp(dwf, mainFrameContainer);
    appID = "Hysteresis";
    mainFrame.setTitle(FRAME_TITLE_BASE + "Hysteresis");

    // app = new PulseApp2(dwf, mainFrameContainer);
    // appID = "Pulse2";
    // mainFrame.setTitle(FRAME_TITLE_BASE + appID);

    // app = new QCApp(dwf, mainFrameContainer);

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

  @Override
  public void onQuitEvent(GenericQuitEvent event, GenericQuitResponse response) {

    quit();
  }

  private void quit() {

    // System.out.println("here0");
    dwf.shutdownAD2();
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

  @Override
  public void onPreferencesEvent(GenericPreferencesEvent event) {

    showPreferences();
  }

  private void showPreferences() {

    int result = 0;
    // System.out.println("appID= " + appID);
    switch (appID) {
    case "Hysteresis":
      result = new HysteresisPrefencesPanel(mainFrame).doModal();
      break;
    case "Pulse":
      result = new PulsePrefencesPanel(mainFrame).doModal();
      break;
    case "Pulse2":
      result = new PulsePrefencesPanel2(mainFrame).doModal();
      break;
    case "QC":
      result = new QCPrefencesPanel(mainFrame).doModal();
      break;

    default:
      break;
    }
    // if (result == AppPrefencesPanel.ID_OK) {
    // app.refreshModelFromPreferences();
    // }

  }

  @Override
  public void onAboutEvent(GenericAboutEvent event) {

    new AboutDialog(mainFrame);
  }

}
