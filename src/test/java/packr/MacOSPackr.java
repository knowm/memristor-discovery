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
