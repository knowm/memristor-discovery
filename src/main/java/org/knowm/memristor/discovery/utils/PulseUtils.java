package org.knowm.memristor.discovery.utils;

/**
 * Created by timmolter on 2/15/17.
 */
public class PulseUtils {

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

  public static double[] generatePositiveAndNegativeDCRamps(double amplitude) {

    // read pulses
    double readPulseMagnitude = .1 / 5.0;
    int size = 1024; // could go up to 4096
    double[] rgdData = new double[size];

    for (int i = 0; i < size / 2; i++) {
      rgdData[i] = i * -1.0 * amplitude / 5.0 / (size / 2);
    }
    for (int i = size / 2; i < size; i++) {
      rgdData[i] = i * 1.0 * amplitude / 5.0 / (size / 2) - amplitude / 5.0;
    }

    // leading read pulse
    for (int i = 0; i < size / 90; i++) {
      rgdData[i] = readPulseMagnitude;
    }
    // // trailing read pulse
    // for (int i = size * 9 / 10; i < size; i++) {
    //   rgdData[i] = readPulseMagnitude;
    // }

    return rgdData; // weird name, but that's what Waveforms SDK calls it.
  }

  public static double[] generatePositiveAndNegativeDCSquares(double amplitude) {

    // read pulses
    double readPulseMagnitude = .1 / 5.0;
    int size = 1024; // could go up to 4096
    double[] rgdData = new double[size];

    for (int i = 0; i < size / 2; i++) {
      rgdData[i] = amplitude / 5.0;
    }
    for (int i = size / 2; i < size; i++) {
      rgdData[i] = -1.0 * amplitude / 5.0;
    }

    // leading read pulse
    for (int i = 0; i < size / 90; i++) {
      rgdData[i] = readPulseMagnitude;
    }
    // // trailing read pulse
    // for (int i = size * 9 / 10; i < size; i++) {
    //   rgdData[i] = readPulseMagnitude;
    // }

    return rgdData; // weird name, but that's what Waveforms SDK calls it.
  }
}
