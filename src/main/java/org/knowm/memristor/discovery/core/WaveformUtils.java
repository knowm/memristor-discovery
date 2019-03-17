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
package org.knowm.memristor.discovery.core;

import org.knowm.memristor.discovery.core.driver.Driver;
import org.knowm.memristor.discovery.core.driver.pulse.HalfSinePulse;
import org.knowm.memristor.discovery.core.driver.pulse.QuarterSinePulse;
import org.knowm.memristor.discovery.core.driver.pulse.SquareDecayPulse;
import org.knowm.memristor.discovery.core.driver.pulse.SquarePulse;
import org.knowm.memristor.discovery.core.driver.pulse.SquareSmoothPulse;
import org.knowm.memristor.discovery.core.driver.pulse.TrianglePulse;
import org.knowm.memristor.discovery.core.driver.waveform.HalfSine;
import org.knowm.memristor.discovery.core.driver.waveform.QuarterSine;
import org.knowm.memristor.discovery.core.driver.waveform.Sawtooth;
import org.knowm.memristor.discovery.core.driver.waveform.SawtoothUpDown;
import org.knowm.memristor.discovery.core.driver.waveform.Square;
import org.knowm.memristor.discovery.core.driver.waveform.SquareSmooth;
import org.knowm.memristor.discovery.core.driver.waveform.Triangle;
import org.knowm.memristor.discovery.core.driver.waveform.TriangleUpDown;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.waveforms4j.DWF;

/** Created by timmolter on 2/15/17. */
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

  public static double[] generateCustomPulse(Waveform waveform, double amplitude, double pulseWidthInNS, double dutyCycle) {

    System.out.println("generateCustomPulse");
    System.out.println("pulseWidth=" + pulseWidthInNS);
    System.out.println("dutyCycle=" + dutyCycle);
    System.out.println("amplitude=" + amplitude);
    System.out.println("waveform=" + waveform);

    Driver driver;
    switch (waveform) {
      //      case Sawtooth:
      //        driver = new Sawtooth("Sawtooth", 0, 0, amplitude, frequency);
      //        break;
      case SquareDecay:
        driver = new SquareDecayPulse("SquareDecay", 0, pulseWidthInNS, dutyCycle, amplitude);
        break;
      case Triangle:

        driver = new TrianglePulse("Triangle", 0, pulseWidthInNS, dutyCycle, amplitude);
        break;
      //      case TriangleUpDown:
      //        driver = new TriangleUpDown("TriangleUpDown", 0, 0, amplitude, frequency);
      //        break;
      case Square:
        driver = new SquarePulse("Square", 0, pulseWidthInNS, dutyCycle, amplitude);
        break;
      //      case SquareUpDown:
      //        driver = new Square("SquareUpDown", 0, 0, amplitude, frequency);
      //        break;
      case QuarterSine:
        driver = new QuarterSinePulse("QuarterSine", 0, pulseWidthInNS, dutyCycle, amplitude);
        break;
      case HalfSine:
        driver = new HalfSinePulse("HalfSine", 0, pulseWidthInNS, dutyCycle, amplitude);
        break;
      case SquareSmooth:
        driver = new SquareSmoothPulse("SquareSmooth", 0, pulseWidthInNS, dutyCycle, amplitude);
        break;
      default:
        driver = new SquarePulse("Square", 0, pulseWidthInNS, dutyCycle, amplitude);
        break;
    }

    int counter = 0;
    double[] customWaveform = new double[4096];
    double timeInc = driver.getPeriod() / 4096;

    do {
      double time = counter * timeInc;
      customWaveform[counter] = driver.getSignal(time) / 5.0; // / 5.0 to scale between 1 and -1  HUH???

    } while (++counter < 4096);
    return customWaveform;

  }

  public static double[] generateCustomWaveform(Waveform waveform, double amplitude, double frequency) {

    Driver driver;
    switch (waveform) {
      case Sawtooth:
        driver = new Sawtooth("Sawtooth", 0, 0, amplitude, frequency);
        break;
      case SawtoothUpDown:
        driver = new SawtoothUpDown("SawtoothUpDown", 0, 0, amplitude, frequency);
        break;
      case Triangle:
        driver = new Triangle("Triangle", 0, 0, amplitude, frequency);
        break;
      case TriangleUpDown:
        driver = new TriangleUpDown("TriangleUpDown", 0, 0, amplitude, frequency);
        break;
      case Square:
        driver = new Square("Square", amplitude / 2, 0, amplitude / 2, frequency);
        break;
      case SquareUpDown:
        driver = new Square("SquareUpDown", 0, 0, amplitude, frequency);
        break;
      case QuarterSine:
        driver = new QuarterSine("QuarterSine", 0, 0, amplitude, frequency);
        break;
      case HalfSine:
        driver = new HalfSine("HalfSine", 0, 0, amplitude, frequency);
        break;
      case SquareSmooth:
        driver = new SquareSmooth("SquareSmooth", 0, 0, amplitude, frequency);
        break;
      default:
        driver = new SawtoothUpDown("SawtoothUpDown", 0, 0, amplitude, frequency);
        break;
    }

    int counter = 0;
    double[] customWaveform = new double[4096];
    double timeInc = 1.0 / frequency / 4096;

    do {
      double time = counter * timeInc;
      customWaveform[counter] = driver.getSignal(time) / 5.0; // / 5.0 to scale between 1 and -1
    } while (++counter < 4096);
    return customWaveform;
  }

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

  public static double[] concat(double[] a, double[] b) {

    int aLen = a.length;
    int bLen = b.length;
    double[] c = new double[aLen + bLen];
    System.arraycopy(a, 0, c, 0, aLen);
    System.arraycopy(b, 0, c, aLen, bLen);
    return c;
  }
}
