# Pre-requisites For Running Memristor Discovery

## Calibrate the AD2 Device

Open up Waveforms2015 and select from the Menu `Settings ==> Device Manager`. In the Window that pops up, select `Calibrate`. The rest is self explanatory. Make sure to calibrate "Waveform Generator 1 Low Gain" followed by "Oscilloscope".

## Install DWF Framework on Mac OSX

Move the dwf.framework to `/Library/Frameworks`, as indicated during the install of Waveforms from the DMG:

![](./_img/Framework.png)

## Install Java 8 Runtime Environment

### Option #1: Download and Install From Oracle

Download the Java SE Runtime Environment 8 from [Oracle's Website](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) and run the installer.

### Option #2: Install via Homebrew on Mac OSX
 
    brew update
    brew cask install java

## Design Notes

1. Help images should be 500 x 500 px.

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
    
    
    