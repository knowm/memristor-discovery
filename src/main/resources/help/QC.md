# QC App (Quality Control)

This app allows you to drive 8 memristors in series with a series resistor with a sinusoidal waveform and record the response as an I/V plot, revealing the signature hysteresis behavior of the memristor.

## Connections

1.  Voltage Source Vs (Analog Discovery 2's "1+" and "1-" connectors) is connected across the memristor (M) and series resistor (Rs).
2.  Oscilloscope Probe V1 (Analog Discovery 2's "W1" and "gnd" connectors) is connected across the memristor (M) and series resistor (Rs).
3.  Oscilloscope Probe V2 (Analog Discovery 2's "W2" and "gnd" connectors) is connected across the series resistor (Rs).

## Series Resistor

The series resistor provide two important functions:

1.  Limits the maximum current through the memristor to prevent device damage (see datasheet).
2.  Allows for a current measurement to be made with the oscilloscope.

## Controls

The control panel can be used to adjust the driver waveform of Vs. The series resistor value should correspond to the actual series resistance value used in the experimental setup.

## Exporting Data

The field "Report Directory" defines where the resulting QC report will be exported to.