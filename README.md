# About

Memristor Discovery is a Java application for running memristor "experiments" on the Knowm [Memristor Discovery Board](http://knowm.org/product/memristor-discovery/). It is designed to be cross-platform, meaning it runs on the latest versions of MacOS, Debian-based Linux and Windows 10. Running Memristor-Discovery requires Java and the Digilent waveforms Framework to be installed on your system (see below).

The following screenshots show Memristor-Discovery running on the three supported operating systems.

![MacOS](_img/MD_MacOS_Hysteresis.png)

![Linux](_img/MD_Linux_DC.png)

![Windows 7 & 10](_img/MD_Windows_Pulse.PNG)

# Installing Memristor-Discovery

See [Releases](https://github.com/knowm/memristor-discovery/releases) for downloads and installation instructions.

# Pre-requisites For Running Memristor Discovery

## Install Java 11 Runtime Environment

### MacOS X
 
    brew update
    brew cask install java

### Windows 10

Download the Java 11 OpenJDK from [java.net](http://jdk.java.net/11/). Follow the instructions [here](https://stackoverflow.com/a/52531093/1625820).

### Ubuntu

As of 30.01.2019, Ubuntu 10.04 LTS doesn't upgrade to Java 11, so we do it manually. Otherwise we could do it with `sudo apt-get install default-jdk` if it wasn't already on Java 11. 

Download OpenJDK from [here](https://jdk.java.net/11/)

```
sudo apt-get remove openjdk-*
sudo apt-get purge openjdk-*
sudo apt autoremove

cd /usr/lib/jvm
sudo tar -xvzf ~/Downloads/openjdk-11.0.2_linux-x64_bin.tar.gz
nano ~/.bashrc
```

Add to bottom of fine:

```
export JAVA_HOME=/usr/lib/jvm/jdk-11.0.2
export PATH=${PATH}:${JAVA_HOME}/bin
```

Restart Console.

```
java -version
```
    
## Install DWF Framework on MacOS X

Download Waveforms .dmg file from here: <https://reference.digilentinc.com/reference/software/waveforms/waveforms-3/start>

Move the dwf.framework to `/Library/Frameworks` and Waveforms 2015 to `Applications`, as indicated during the install of Waveforms from the DMG:

![](./_img/Framework.png)

## Install DWF Framework on Windows

Download Waveforms 2015 from here: <https://reference.digilentinc.com/reference/software/waveforms/waveforms-3/start> and run the installer.

## Install DWF Framework on Ubuntu

Download Waveforms .deb file from here: <https://reference.digilentinc.com/reference/software/waveforms/waveforms-3/start>


```
sudo mv ~/Downloads/digilent.waveforms_3.9.1_amd64.deb /var/cache/apt/archives
cd /var/cache/apt/archives
sudo dpkg -i digilent.waveforms_3.9.1_amd64.deb
    
sudo mv ~/Downloads/digilent.adept.runtime_2.19.2-amd64.deb /var/cache/apt/archives
cd /var/cache/apt/archives
sudo dpkg -i digilent.adept.runtime_2.19.2-amd64.deb
```


## Calibrate the AD2 Device

Open up Waveforms 2015 and select from the Menu `Settings ==> Device Manager`. In the Window that pops up, select `Calibrate`. The rest is self explanatory. Make sure to calibrate "Waveform Generator 1 Low Gain" followed by "Oscilloscope".


# For Developers Only

## Building

#### general

    mvn clean package  
    mvn javadoc:javadoc  
    
#### maven-license-plugin

    mvn license:check
    mvn license:format
    mvn license:remove

## Running from Eclipse

Right-click on `MemristorDiscovery.java`, `Run As...` ==> `Java Application`.

## Building Executable Jar

Maven is used to build the executable jar and it will contain all the dependencies within that single jar as well. The jar `memristor-discovery.jar` will be found in the directory `target`.

    mvn clean install
    java -jar memristor-discovery.jar
    

    