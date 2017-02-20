package org.knowm.memristor.discovery.utils;

/**
 * Created by timmolter on 2/20/17.
 */
public class PostProcessDataUtils {

  public static double[][] trimIdleData(double[] v1, double[] v2) {

    // The data is a bit weird, as what's captured is a long window of "idle" voltage before the pulses. We clean that now...
    int startIndex = 0;
    for (int i = 0; i < v1.length; i++) {
      if (Math.abs(v1[i]) > .02) {
        startIndex = i;
        break;
      }
    }
    int endIndex = v1.length - 1;
    for (int i = v1.length - 1; i > 0; i--) {
      if (Math.abs(v1[i]) > .02) {
        endIndex = i;
        break;
      }
    }
    int bufferLength = endIndex - startIndex;



    double[] V1Cleaned = new double[bufferLength];
    double[] V2Cleaned = new double[bufferLength];
    for (int i = 0; i < bufferLength; i++) {
      V1Cleaned[i] = v1[i + startIndex];
      V2Cleaned[i] = v2[i + startIndex];
    }
    return new double[][]{V1Cleaned, V2Cleaned};
  }
}
