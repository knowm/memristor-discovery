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
package org.knowm.memristor.discovery;

import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;

import org.knowm.waveforms4j.DWF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DWFProxy {

  public final static int SWITCHES_MASK = 0b1111_1111_1111_1111;
  public final static int ALL_DIO_OFF = 0b0000_0000_0000_0000;
  public final static int DEFAULT_SELECTOR_DIO = 0b0001_1101_0000_0000; // the top 8 bits control the 4 MUXes
  // public final static int DEFAULT_SELECTOR_DIO = 0b0000_0000_0000_0000;

  private final Logger logger = LoggerFactory.getLogger(DWFProxy.class);

  public static final String AD2_STARTUP_CHANGE = "AD2_START_UP";
  public static final String DIGITAL_IO_READ = "DIGITAL_IO_READ";

  // ///////////////////////////////////////////////////////////
  // State Variables //////////////////////////////////////////
  // ///////////////////////////////////////////////////////////

  private boolean isAD2Running = false;
  private int digitalIOStates = ALL_DIO_OFF;
  private final boolean isV1Board;
  final DWF dwf;
  private SwingPropertyChangeSupport swingPropertyChangeSupport;

  /**
   * Constructor
   */
  public DWFProxy(boolean isV1Board) {

    this.isV1Board = isV1Board;
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

  /**
   * This is called by the main app once on start up or during a switch-triggered shut off event. Here, the AD2 is started up and the GUI will reflect
   * its startup state.
   */
  public void startupAD2() {

    new AD2StartupWorker().execute();
  }

  public void waitUntilArmed() {

    // long startTime = System.currentTimeMillis();
    while (true) {
      byte status = dwf.FDwfAnalogInStatus(true);
      // System.out.println("status: " + status);
      if (status == 1) { // armed
        // System.out.println("armed.");
        break;
      }
    }
    // System.out.println("time = " + (System.currentTimeMillis() - startTime));

  }

  public boolean capturePulseData(double frequency, int pulseNumber) {

    // Read In Data
    int bailCount = 0;
    while (true) {
      try {
        long sleepTime = (long) (1 / frequency * pulseNumber * 1000);
        // System.out.println("sleepTime = " + sleepTime);
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      byte status = dwf.FDwfAnalogInStatus(true);
      // System.out.println("status: " + status);
      if (status == 2) { // done capturing
        // System.out.println("bailCount = " + bailCount);
        return true;
      }
      if (bailCount++ > 1000) {
        System.out.println("Bailed!!!");
        return false;
      }
    }
  }

  private class AD2StartupWorker extends SwingWorker<Boolean, Void> {

    @Override protected Boolean doInBackground() {

      // ///////////////////////////////////////////////////////////
      // Device ///////////////////////////////////////////////////
      // ///////////////////////////////////////////////////////////
      isAD2Running = dwf.FDwfDeviceOpen();

      if (isAD2Running) {

        // Some device read out stuff
        // System.out.println("Analog Out Custom Waveform Buffer Size Channel 1: "+Arrays.toString(dwf.FDwfAnalogOutNodeDataInfo(DWF.WAVEFORM_CHANNEL_1)));
        // System.out.println("Analog Out Custom Waveform Buffer Size Channel 2: "+Arrays.toString(dwf.FDwfAnalogOutNodeDataInfo(DWF.WAVEFORM_CHANNEL_2)));
        // System.out.println("Analog In Trigger Position Info: "+ Arrays.toString(dwf.FDwfAnalogInTriggerPositionInfo()));

        // ///////////////////////////////////////////////////////////
        // Digital I/O //////////////////////////////////////////////
        // ///////////////////////////////////////////////////////////
        dwf.FDwfDigitalIOOutputEnableSet(SWITCHES_MASK);
        if (isV1Board) {
          digitalIOStates = DEFAULT_SELECTOR_DIO;
          // System.out.println(Integer.toBinaryString(digitalIOStates));
        } else {
          digitalIOStates = ALL_DIO_OFF;
        }
        dwf.FDwfDigitalIOOutputSet(digitalIOStates);
        dwf.FDwfDigitalIOConfigure();
        digitalIOStates = dwf.getDigitalIOStatus();
        swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, true, false);

        // ///////////////////////////////////////////////////////////
        // Analog I/O //////////////////////////////////////////////
        // ///////////////////////////////////////////////////////////
        dwf.setPowerSupply(0, 5.0);
        dwf.setPowerSupply(1, -5.0);

        // ///////////////////////////////////////////////////////////
        // Analog Out //////////////////////////////////////////////
        // ///////////////////////////////////////////////////////////
        // set analog out offset to zero, as it seems like it's not quite there by default
        dwf.FDwfAnalogOutNodeOffsetSet(DWF.WAVEFORM_CHANNEL_1, 0);
        dwf.FDwfAnalogOutNodeOffsetSet(DWF.WAVEFORM_CHANNEL_2, 0);
        // dwf.FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, true);

        // ///////////////////////////////////////////////////////////
        // Analog In //////////////////////////////////////////////
        // ///////////////////////////////////////////////////////////
        dwf.FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
        dwf.FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, 2.5);
        dwf.FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
        dwf.FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, 2.5);

        // Set this to false (default=true). Need to call FDwfAnalogOutConfigure(true), FDwfAnalogInConfigure(true) in order for *Set* methods to take effect.
        dwf.FDwfDeviceAutoConfigureSet(false);
      } else {

        System.out.println(dwf.FDwfGetLastErrorMsg());
      }
      // swingPropertyChangeSupport.firePropertyChange(DWFProxy.AD2_STARTUP_CHANGE, !isAD2Running, isAD2Running);
      return isAD2Running;
    }

    @Override protected void done() {

      swingPropertyChangeSupport.firePropertyChange(DWFProxy.AD2_STARTUP_CHANGE, !isAD2Running, isAD2Running);
    }
  }

  /**
   * This is called when the main board on/off toggle switch is switched to the off position.
   */
  public void shutdownAD2() {

    // ///////////////////////////////////////////////////////////
    // Digital I/O //////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////
    setAllIOStates(ALL_DIO_OFF);
    dwf.FDwfDigitalIOReset();
    dwf.FDwfDigitalOutReset();

    // ///////////////////////////////////////////////////////////
    // Analog Out ///////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////
    dwf.FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false);

    // ///////////////////////////////////////////////////////////
    // Analog In ///////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////
    dwf.FDwfAnalogInConfigure(false, false);

    // ///////////////////////////////////////////////////////////
    // Analog I/O //////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////
    dwf.setPowerSupply(0, 0.0);
    dwf.setPowerSupply(1, 0.0);

    // ///////////////////////////////////////////////////////////
    // Device //////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////
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

  public void turnOffAllSwitches() {

    int oldValDigitalIO = digitalIOStates;
    Integer mask = Integer.valueOf("1111111100000000", 2);
    digitalIOStates = digitalIOStates & mask;
    boolean successful = dwf.FDwfDigitalIOOutputSet(digitalIOStates);
    dwf.FDwfDigitalIOConfigure();
    digitalIOStates = dwf.getDigitalIOStatus();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);
  }

  public void update2DigitalIOStatesAtOnce(List<Integer> mask, boolean isOn) {

    // logger.debug("toggleClickedID: " + toggleClickedID);
    int oldValDigitalIO = digitalIOStates;

    // Update model
    for (int i = 0; i < mask.size(); i++) {
      if (isOn) {
        digitalIOStates = digitalIOStates | (1 << mask.get(i));
      } else {
        digitalIOStates = digitalIOStates & ~(1 << mask.get(i));
      }
    }

    boolean successful = dwf.FDwfDigitalIOOutputSet(digitalIOStates);
    // logger.debug("AD2 Device Digital I/O Written: " + successful);
    dwf.FDwfDigitalIOConfigure();

    digitalIOStates = dwf.getDigitalIOStatus();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);

    // System.out.println("digitalIO states= " + Integer.toBinaryString(digitalIOStates));

  }

  /**
   * A GUI element was clicked, so we need to update the model. Don't need to fire a property change for the GUI since the change came from the GUI.
   *
   * @param toggleClickedID
   * @param isOn
   */
  public void update2DigitalIOStatesAtOnce(int toggleClickedID, boolean isOn) {

    // logger.debug("toggleClickedID: " + toggleClickedID);
    int oldValDigitalIO = digitalIOStates;

    // Update model
    if (isOn) {
      digitalIOStates = digitalIOStates | (1 << toggleClickedID);
    } else {
      digitalIOStates = digitalIOStates & ~(1 << toggleClickedID);
    }

    // logger.debug("new state: " + digitalIOStates);

    boolean successful = dwf.FDwfDigitalIOOutputSet(digitalIOStates);
    // logger.debug("AD2 Device Digital I/O Written: " + successful);
    dwf.FDwfDigitalIOConfigure();

    digitalIOStates = dwf.getDigitalIOStatus();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);
  }

  public void update2DigitalIOStatesAtOnce(int io1, int io2, boolean value1, boolean value2) {

    // logger.debug("toggleClickedID: " + toggleClickedID);
    int oldValDigitalIO = digitalIOStates;

    // Update model
    if (value1) {
      digitalIOStates = digitalIOStates | (1 << io1);
    } else {
      digitalIOStates = digitalIOStates & ~(1 << io1);
    }
    if (value2) {
      digitalIOStates = digitalIOStates | (1 << io2);
    } else {
      digitalIOStates = digitalIOStates & ~(1 << io2);
    }

    logger.debug("new state: " + Integer.toBinaryString(digitalIOStates));

    boolean successful = dwf.FDwfDigitalIOOutputSet(digitalIOStates);
    logger.debug("AD2 Device Digital I/O Written: " + successful);
    dwf.FDwfDigitalIOConfigure();

    digitalIOStates = dwf.getDigitalIOStatus();
    swingPropertyChangeSupport.firePropertyChange(DWFProxy.DIGITAL_IO_READ, oldValDigitalIO, digitalIOStates);
  }

  public void setUpper8IOStates(int upper8SetMask) {

    logger.debug("upper8SetMask: " + Integer.toBinaryString(upper8SetMask));
    int oldValDigitalIO = digitalIOStates;

    int preserveLower8 = 0b0000_0000_1111_1111;

    int zeroUpper8 = digitalIOStates & preserveLower8;

    int setUpper8 = zeroUpper8 | upper8SetMask;

    // Update model
    digitalIOStates = setUpper8;

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

  // //////////////////////////////////////////////////////////
  // Getters and Setters //////////////////////////////////////
  // //////////////////////////////////////////////////////////

  public int getDigitalIOStates() {

    return digitalIOStates;
  }

  public boolean isAD2Running() {

    return isAD2Running;
  }

  public DWF getDwf() {

    return dwf;
  }

  public boolean isV1Board() {

    return isV1Board;
  }
}
