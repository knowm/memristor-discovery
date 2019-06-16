# DC Experiment

The DC app allows you to drive a memristor in series with a resistor with various ramping functions including sawtooth, sawtoothupdown, triangle and triangleupdown *at time scale from 10 to 1000ms*. The number of applied ramping signals can be selected and the response can be observed as either as a time series, current-vs-voltage (I-V) or conductance-vs-voltage (G-V) plot, revealing the behavior of the memristor at slow timescales. The circuit configuration depends on your board version:


![](_img/BasicCircuit.png)

**W1**: Arbitrary Waveform Generator W1 on the Analog Discovery 2.
**1+**: Oscilloscope Probe 1+ on the Analog Discovery 2.
**2+**: Oscilloscope Probe 2+ on the Analog Discovery 2.


## V2.0 Mode 1 Selection

Memristor Discovery V2.0 boards must be set to "Mode 1" by moving selection switch on board to the '1' position. 

## V1.X (Deprecated) Jumper Connections

![](_img/HysteresisV1Board.png)

## Memristor Selection

Use the toggle switches near the top of the app window to connect one or more individual memristors into the circuit. 

## Series Resistor

The series resistor provides two important functions:

1.  Limits the maximum current through the memristor to prevent device damage (see memristor datasheet).
2.  Allows for a current measurement to be made with the oscilloscope.

Version 2.0 Memristor Discovery boards have two series resistors, A and B. These resistors are connected in parallel when the board is in Mode 1. The series resistance of the circuit is thus half of the value of the resistors in the socket, which needs to be reflected in the preferences menu. Boards are shipped with 20kΩ precision resistors. Alternately, you may remove either resistor A or B and record the value of the remaining resistor in the preferences. Whatever you do, the value of the series resistor in the preferences must match the actual series resistance for measurements to be accurate.


## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.