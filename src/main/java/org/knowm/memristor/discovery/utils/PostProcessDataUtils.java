package org.knowm.memristor.discovery.utils;

/**
 * Created by timmolter on 2/20/17.
 */
public class PostProcessDataUtils {

  /**
   * The data is a bit weird, as what's captured is a long window of "idle" voltage before and after the pulses. We clean that now...
   *
   * @param v1
   * @param v2
   * @param v1Threshold
   * @return
   */
  public static double[][] trimIdleData(double[] v1, double[] v2, double v1Threshold) {

    double vThresholdAbs = Math.abs(v1Threshold);
    int startIndex = 0;
    for (int i = 0; i < v1.length; i++) {
      if (Math.abs(v1[i]) > vThresholdAbs) {
        startIndex = i;
        break;
      }
    }
    int endIndex = v1.length - 1;
    for (int i = v1.length - 1; i > 0; i--) {
      if (Math.abs(v1[i]) > vThresholdAbs) {
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

  /**
   * Set all V2 data to zero where V1 is less than a given threshold
   *
   * @param v1
   * @param v2
   * @return
   */
  public static double[] zeroIdleData(double[] v1, double[] v2, double v1Threshold) {

    double vThresholdAbs = Math.abs(v1Threshold);

    double[] V2Zeroed = new double[v1.length];
    for (int i = 0; i < V2Zeroed.length; i++) {
      if (Math.abs(v1[i]) > vThresholdAbs) {
        V2Zeroed[i] = v2[i];
      }
      else {
        V2Zeroed[i] = 0;
      }
    }
    return V2Zeroed;
  }
}
