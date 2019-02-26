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
package org.knowm.memristor.discovery;

import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.core.WaveformUtils;
import org.knowm.waveforms4j.DWF;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class PulseTriggerCapture {
  DWF dwf;

  public PulseTriggerCapture() {
    dwf = new DWF();
  }

  public static void main(String[] args) {

    PulseTriggerCapture pulseTriggerCapture = new PulseTriggerCapture();
    pulseTriggerCapture.go();
  }

  public void go() {

    boolean successful = dwf.FDwfDeviceOpen();
    System.out.println("successful: " + successful);

    //    dwf.setPowerSupply(0, 5.0);
    //    dwf.setPowerSupply(1, -5.0);

    dwf.FDwfAnalogOutNodeOffsetSet(DWF.WAVEFORM_CHANNEL_1, 0);
    dwf.FDwfAnalogOutNodeOffsetSet(DWF.WAVEFORM_CHANNEL_2, 0);

    // Order ==> W2, W1, 2+, 1+
    // 00 None
    // 10 Y
    // 01 A
    // 11 B

    // dwf.FDwfDeviceAutoConfigureSet(false);
    //////////////////////////////////
    // Analog In /////////////////
    //////////////////////////////////

    // trigger on 20% the rising .1 V read pulse
    int samplesPerPulse = 300;
    int freq = 10_000;

    int sampleFrequency = freq * samplesPerPulse;
    dwf.startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.1, samplesPerPulse * 1);
    waitUntilArmed();

    //////////////////////////////
    // Pulse Out /////////////////
    //////////////////////////////

    /// without this it does not work. why?
    try {
      // System.out.println("arm oscilloscope now. ");
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // read pulse: 0.1 V, 5 us pulse width
    double[] customWaveform = WaveformUtils.generateCustomWaveform(Waveform.QuarterSine, 1, freq);
    dwf.startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, freq, 0, 1, customWaveform);

    boolean success = capturePulseData(freq, 1);
    if (!success) {
      // Stop Analog In and Out
      dwf.stopWave(DWF.WAVEFORM_CHANNEL_1);
      dwf.stopAnalogCaptureBothChannels();
      System.out.println("Failed to capture pulse");
      return;
    }

    // Get Raw Data from Oscilloscope
    int validSamples = dwf.FDwfAnalogInStatusSamplesValid();
    double[] v1 = dwf.FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
    double[] v2 = dwf.FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
    // System.out.println("validSamples: " + validSamples);

    dwf.stopWave(DWF.WAVEFORM_CHANNEL_1);
    dwf.stopAnalogCaptureBothChannels();
    successful = dwf.FDwfDeviceCloseAll();
    System.out.println("close successful: " + successful);

    XYChart chart =
        new XYChartBuilder()
            .width(600)
            .height(500)
            .title("Pulse Caputre")
            .xAxisTitle("time")
            .yAxisTitle("voltage")
            .build();

    chart.addSeries("V1", v1);
    chart.addSeries("V2", v2);

    // Show it
    new SwingWrapper(chart).displayChart();
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
}
