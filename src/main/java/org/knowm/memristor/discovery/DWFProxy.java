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

import java.beans.PropertyChangeListener;

import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;

import org.knowm.memristor.discovery.gui.mvc.header.HeaderPanel;
import org.knowm.waveforms4j.DWF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DWFProxy {

  final DWF dwf;
  private SwingPropertyChangeSupport swingPropertyChangeSupport;

  /**
   * Constructor
   */
  public DWFProxy() {

    dwf = new DWF();
    swingPropertyChangeSupport = new SwingPropertyChangeSupport(this);
  }

  /**
   * Here is where the Controller registers itself as a listener to model changes.
   *
   * @param listener
   */
  public void addListener(PropertyChangeListener listener) {

    swingPropertyChangeSupport.addPropertyChangeListener(listener);
  }

  private final Logger logger = LoggerFactory.getLogger(DWFProxy.class);

  public static final String AD2_STARTUP_CHANGE = "AD2_START_UP";
  public static final String DIGITAL_IO_READ = "DIGITAL_IO_READ";

  /////////////////////////////////////////////////////////////
  // State Variables //////////////////////////////////////////
  /////////////////////////////////////////////////////////////

  private boolean isAD2Running = false;
  private boolean isAD2Capturing = false;
  private int digitalIOStates = 0;

  /////////////////////////////////////////////////////////////
  // Device ///////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////

  /**
   * This is called by the main app once on start up or during a switch-triggered shut off event. Here, the AD2 is started up and the GUI will reflect its startup state.
   */
  public void startupAD2() {

    new AD2StartupWorker().execute();
  }

  private class AD2StartupWorker extends SwingWorker<Boolean, Void> {

    @Override
    protected Boolean doInBackground() throws Exception {

      /////////////////////////////////////////////////////////////
      // Device ///////////////////////////////////////////////////
      /////////////////////////////////////////////////////////////
      isAD2Running = dwf.FDwfDeviceOpen();

      if (isAD2Running) {

        // // Some device read out stuff
        // System.out.println("Analog Out Custom Waveform Buffer Size Channel 1: "+Arrays.toString(dwf.FDwfAnalogOutNodeDataInfo(DWF.WAVEFORM_CHANNEL_1)));
        // System.out.println("Analog Out Custom Waveform Buffer Size Channel 2: "+Arrays.toString(dwf.FDwfAnalogOutNodeDataInfo(DWF.WAVEFORM_CHANNEL_2)));


        /////////////////////////////////////////////////////////////
        // Digital I/O //////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        int oldValDigitalIO = digitalIOStates;
        dwf.FDwfDigitalIOOutputEnableSet(HeaderPanel.SWITCHES_MASK);
        digitalIOStates = dwf.getDigitalIOStatus();
        swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);

        /////////////////////////////////////////////////////////////
        // Analog I/O //////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        dwf.setPowerSupply(0, 5.0);
        dwf.setPowerSupply(1, -5.0);

        /////////////////////////////////////////////////////////////
        // Analog Out //////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        // set analog out offset to zero, as it seems like it's not quite there by default
        dwf.FDwfAnalogOutNodeOffsetSet(DWF.WAVEFORM_CHANNEL_1, 0);
        dwf.FDwfAnalogOutNodeOffsetSet(DWF.WAVEFORM_CHANNEL_2, 0);
        // dwf.FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, true);

        /////////////////////////////////////////////////////////////
        // Analog In //////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        dwf.FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
        dwf.FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, 2.5);
        dwf.FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
        dwf.FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, 2.5);

        // Set this to false (default=true). Need to call FDwfAnalogOutConfigure(true), FDwfAnalogInConfigure(true) in order for *Set* methods to take effect.
        dwf.FDwfDeviceAutoConfigureSet(false);

      }else{

        System.out.println(dwf.FDwfGetLastErrorMsg());
      }
      return isAD2Running;
    }

    @Override
    protected void done() {

      swingPropertyChangeSupport.firePropertyChange(DWFProxy.AD2_STARTUP_CHANGE, !isAD2Running, isAD2Running);
    }
  }

  /**
   * This is called when the main board on/off toggle switch is switched to the off position.
   */
  public void shutdownAD2() {

    /////////////////////////////////////////////////////////////
    // Digital I/O //////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    int oldValDigitalIO = digitalIOStates;
    dwf.FDwfDigitalIOReset();
    dwf.FDwfDigitalOutReset();
    digitalIOStates = dwf.getDigitalIOStatus();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);

    /////////////////////////////////////////////////////////////
    // Analog Out ///////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    dwf.FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false);

    /////////////////////////////////////////////////////////////
    // Analog In ///////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    dwf.FDwfAnalogInConfigure(false, false);

    /////////////////////////////////////////////////////////////
    // Analog I/O //////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    dwf.setPowerSupply(0, 0.0);
    dwf.setPowerSupply(1, 0.0);

    /////////////////////////////////////////////////////////////
    // Device //////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    boolean oldValDevice = isAD2Running;
    isAD2Running = false;
    dwf.FDwfDeviceCloseAll();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.AD2_STARTUP_CHANGE, oldValDevice, isAD2Running);

    // try {
    // Thread.sleep(500);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
  }

  /**
   * A GUI element was clicked, so we need to update the model. Don't need to fire a property change for the GUI since the change came from the GUI.
   *
   * @param toggleClickedID
   * @param isOn
   */
  public void updateDigitalIOState(int toggleClickedID, boolean isOn) {

    // logger.debug("toggleClickedID: " + toggleClickedID);
    int oldValDigitalIO = digitalIOStates;

    // Update model
    if (isOn) {
      digitalIOStates = digitalIOStates | (1 << toggleClickedID);
    }
    else {
      digitalIOStates = digitalIOStates & ~(1 << toggleClickedID);
    }

    // logger.debug("new state: " + digitalIOStates);

    boolean successful = dwf.FDwfDigitalIOOutputSet(digitalIOStates);
    // logger.debug("AD2 Device Digital I/O Written: " + successful);
    dwf.FDwfDigitalIOConfigure();

    digitalIOStates = dwf.getDigitalIOStatus();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);
  }

  public void setAllIOStates(int outputSetMask) {

    // logger.debug("outputSetMask: " + outputSetMask);
    int oldValDigitalIO = digitalIOStates;

    // Update model
    digitalIOStates = outputSetMask;

    // logger.debug("new state: " + digitalIOStates);

    boolean successful = dwf.FDwfDigitalIOOutputSet(digitalIOStates);
    // logger.debug("AD2 Device Digital I/O Written: " + successful);
    dwf.FDwfDigitalIOConfigure();

    digitalIOStates = dwf.getDigitalIOStatus();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);
  }

  /////////////////////////////////////////////////////////////
  // Getters and Setters //////////////////////////////////////
  /////////////////////////////////////////////////////////////

  public int getDigitalIOStates() {

    return digitalIOStates;
  }

  public boolean isAD2Running() {

    return isAD2Running;
  }

  public boolean isAD2Capturing() {

    return isAD2Capturing;
  }

  public void setAD2Capturing(boolean isAD2Capturing) {

    this.isAD2Capturing = isAD2Capturing;
  }

  public DWF getDwf() {

    return dwf;
  }
}
