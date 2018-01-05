# Pulse App

The Pulse App allows you to drive a memristor in series with a resistor with one or more pulse waveforms and observe the instantaneous and post-pulse response. The instantaneous response is displayed on the upper chart as a time series of voltage (V1+ vs T) and (V2+ vs T), current (I vs T) or conductance (G vs T). The lower plot shows the conductance of the memristor, as measured by a low-voltage (.1V) pulse. The sampling rate of these measurements can be set on the control panel. 

*note:*

1. A memristor undergoes a dynamic internal change after receiving an impulse, and this response can extend *beyond* the timescale of the impulse. We have observed 'cascades', where the conductance of the memristor continues to fall (or climb) *after* application of a pulse. In addition, non-linearities in equipment and devices can result in measurement deviations if two different read voltages are used to measure conductance at two moments in time. Hence, the changes in a memristor is revealed when measurments are performed over time scales longer than the impulse and constant read voltages are used. 

2. The energy estimations are calculated from the following equation: 

AppliedEnergy = AppliedAmplitude * AppliedAmplitude / (MemristorResistance + SeriesResistorResistance + SwitchResistances) * PulseNumber * PulseWidth;



![](file://Pulse.png)

## Connections

1.  Arbitrary Waveform Generator W1 (AD2's W1 and Gnd connectors) are connected across the memristor M and series resistor R.
2.  Oscilloscope Probe V1+ (AD2's 1+ and 1- connectors) is connected across the memristor M and series resistor Rs.
3.  Oscilloscope Probe V2+ (Analog Discovery 2's "2+" and "2-" connectors) is connected across the series resistor (R).


## V1.X Jumper Connections

![](file://HysteresisV1Board.png)

## Memristor Selection

Use the toggle switches near the top of the app window to connect one or more individual memristors into the circuit.

## Series Resistor

The series resistor provide two important functions:

1.  Limits the maximum current through the memristor to prevent device damage (see memristor datasheet).
2.  Allows for a current measurement to be made with the oscilloscope (I = V2+/R).

## Controls

The control panel can be used to adjust the driver waveform of W1. The series resistor value control should correspond to the actual series resistance value used in the experimental setup in order to calculate an accurate current value.

The Sample Rate determines how many seconds elapse between each read measurement. 

## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.