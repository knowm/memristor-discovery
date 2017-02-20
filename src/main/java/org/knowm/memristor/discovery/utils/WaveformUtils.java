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
package org.knowm.memristor.discovery.utils;

import org.knowm.memristor.discovery.gui.mvc.apps.AppPreferences.Waveform;
import org.knowm.waveforms4j.DWF;

/**
 * Created by timmolter on 2/15/17.
 */
public class WaveformUtils {

  public static double[] generateSquarePulseWithReadPulses(double amplitude) {

    // read pulses
    double readPulseMagnitude = .1 / 5.0;
    int size = 1024; // could go up to 4096
    double[] rgdData = new double[size];
    for (int i = size / 4; i < size * 3 / 4; i++) {
      rgdData[i] = amplitude / 5.0;
    }
    for (int i = 0; i < size / 8; i++) {
      rgdData[i] = readPulseMagnitude;
    }
    for (int i = size * 7 / 8; i < size; i++) {
      rgdData[i] = readPulseMagnitude;
    }

    return rgdData; // weird name, but that's what Waveforms SDK calls it.
  }

  // public static double[] generatePositiveAndNegativeDCRamps(double amplitude) {
  //
  //   // read pulses
  //   double readPulseMagnitude = .1 / 5.0;
  //   int size = 1024; // could go up to 4096
  //   double[] rgdData = new double[size];
  //
  //   for (int i = 0; i < size / 2; i++) {
  //     rgdData[i] = i * -1.0 * amplitude / 5.0 / (size / 2);
  //   }
  //   for (int i = size / 2; i < size; i++) {
  //     rgdData[i] = i * 1.0 * amplitude / 5.0 / (size / 2) - amplitude / 5.0;
  //   }
  //
  //   // leading read pulse
  //   for (int i = 0; i < size / 90; i++) {
  //     rgdData[i] = readPulseMagnitude;
  //   }
  //   // // trailing read pulse
  //   // for (int i = size * 9 / 10; i < size; i++) {
  //   //   rgdData[i] = readPulseMagnitude;
  //   // }
  //
  //   return rgdData; // weird name, but that's what Waveforms SDK calls it.
  // }
  //
  // public static double[] generatePositiveAndNegativeDCSquares(double amplitude) {
  //
  //   // read pulses
  //   double readPulseMagnitude = .1 / 5.0;
  //   int size = 1024; // could go up to 4096
  //   double[] rgdData = new double[size];
  //
  //   for (int i = 0; i < size / 2; i++) {
  //     rgdData[i] = amplitude / 5.0;
  //   }
  //   for (int i = size / 2; i < size; i++) {
  //     rgdData[i] = -1.0 * amplitude / 5.0;
  //   }
  //
  //   // leading read pulse
  //   for (int i = 0; i < size / 90; i++) {
  //     rgdData[i] = readPulseMagnitude;
  //   }
  //   // // trailing read pulse
  //   // for (int i = size * 9 / 10; i < size; i++) {
  //   //   rgdData[i] = readPulseMagnitude;
  //   // }
  //
  //   return rgdData; // weird name, but that's what Waveforms SDK calls it.
  // }

  public static DWF.Waveform getDWFWaveform(Waveform waveform) {

    switch (waveform) {
      case Sine:
        return DWF.Waveform.Sine;
      case Triangle:
        return DWF.Waveform.Triangle;
      case TriangleUpDown:
        return DWF.Waveform.Triangle;
      case Square:
        return DWF.Waveform.Square;
      case SawtoothUpDown:
        return DWF.Waveform.Custom;
      case Sawtooth:
        return DWF.Waveform.Custom;
      default:
        return DWF.Waveform.Sine;
    }
  }
}
