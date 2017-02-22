# Pulse App

This app allows you to drive a memristor in series with a series resistor with a sinusoidal or triangle waveform and observe the response as either a time series, I/V or G/V plot, revealing the signature hysteresis behavior of the memristor.

## Connections

1.  Arbitrary Waveform Generator W1 (Analog Discovery 2's "W1" and "gnd" connectors) is connected across the memristor (M) and series resistor (R).
2.  Oscilloscope Probe V1+ (Analog Discovery 2's "1+" and "1-" connectors) is connected across the memristor (M) and series resistor (Rs).
3.  Oscilloscope Probe V2+ (Analog Discovery 2's "2+" and "2-" connectors) is connected across the series resistor (R).

## Series Resistor

The series resistor provide two important functions:

1.  Limits the maximum current through the memristor to prevent device damage (see memristor datasheet).
2.  Allows for a current measurement to be made with the oscilloscope (I = V2+/R).

## Controls

The control panel can be used to adjust the driver waveform of W1. The series resistor value control should correspond to the actual series resistance value used in the experimental setup in order to calculate an accurate current value.

## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.