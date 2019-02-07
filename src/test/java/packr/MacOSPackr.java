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
package packr;

import com.badlogicgames.packr.Packr;
import com.badlogicgames.packr.PackrConfig;
import com.badlogicgames.packr.PackrConfig.Platform;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * OpenJDKs - http://jdk.java.net/11/
 * https://iconverticons.com/online/
 *
 */
public class MacOSPackr {

  private static final String JDK_LOCATION = "/Users/timmolter/Downloads/JDK/MacOS/jdk-11.0.2.jdk.zip";

  public static void main(String[] args) throws IOException {

    PackrConfig config = new PackrConfig();
    config.platform = Platform.MacOS;
    config.jdk = JDK_LOCATION;
    config.executable = "Memristor-Discovery";
    config.classpath = Arrays.asList("target/memristor-discovery-0.0.7.jar");
    //    config.removePlatformLibs = config.classpath;
    config.mainClass = "org.knowm.memristor.discovery.MemristorDiscovery";
    config.vmArgs = Arrays.asList("Xmx2G");
    config.iconResource = new File("_exe/icons.icns");
    config.minimizeJre = "hard";
    config.outDir = new java.io.File("target/Memristor-Discovery.app");

    new Packr().pack(config);
  }
}
