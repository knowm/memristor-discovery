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
public class LinuxPackr {

  private static final String JDK_LOCATION = "/Users/timmolter/Downloads/JDK/Linux/jdk-11.0.2.zip";
//  private static final String JDK_LOCATION = "/Users/timmolter/Downloads/jre1.7.0_80.zip";


  public static void main(String[] args) throws IOException {

    PackrConfig config = new PackrConfig();
    config.platform = Platform.Linux64;
    config.jdk = JDK_LOCATION;
    config.executable = "Memristor-Discovery";
    config.classpath = Arrays.asList("target/memristor-discovery-0.0.6.jar");
    //    config.removePlatformLibs = config.classpath;
    config.mainClass = "org.knowm.memristor.discovery.MemristorDiscovery";
    config.vmArgs = Arrays.asList("Xmx2G");
    config.iconResource = new File("_img/icons.icns");
    config.minimizeJre = "soft";
    config.outDir = new File("target/Memristor-Discovery_Linux");

    new Packr().pack(config);
  }
}