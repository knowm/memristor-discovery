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
package org.knowm.memristor.discovery.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  /**
   * Given a File from the classpath, return the content of the file as a String
   *
   * @param fileName
   * @return
   */
  public static String readFileFromClasspathToString(String fileName) {

    //    System.out.println("fileName=" + fileName);

    BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(FileUtils.class.getClassLoader().getResourceAsStream(fileName)));
    String result = readerToString(reader);

    // show file contents here
    return result;
  }

  private static String readerToString(BufferedReader reader) {

    StringBuffer sb = new StringBuffer();

    try {
      String text = null;

      // repeat until all lines are read
      while ((text = reader.readLine()) != null) {
        sb.append(text).append(System.getProperty("line.separator"));
      }
    } catch (FileNotFoundException e) {
      logger.error("ERROR IN READFILETOSTRING!!!", e);
    } catch (IOException e) {
      logger.error("ERROR IN READFILETOSTRING!!!", e);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        logger.error("ERROR IN READFILETOSTRING!!!", e);
      }
    }

    // show file contents here
    return sb.toString();
  }

  /**
   * Makes a dir, if it doesn't already exist
   *
   * @param filePath
   */
  public static void mkDirIfNotExists(String filePath) {

    File f = new File(filePath);
    if (!f.exists()) {
      f.mkdir();
    }
  }
}
