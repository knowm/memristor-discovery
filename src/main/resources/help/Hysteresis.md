# Hysteresis App

This app allows you to drive a memristor in series with a resistor with a sinusoidal or triangle waveform and observe the response as either a time series (V1+ vs T) and (V2+ vs T), (I vs V) or (G vs V) plot, revealing the signature hysteresis behavior of the memristor. The user can adjust the input signal voltage, frequency and offset and observe the response in real-time.

![](file://help/Hysteresis.png)

## Connections

1.  Arbitrary Waveform Generator W1 (AD2's W1 and Gnd connectors) is connected across the memristor (M) and series resistor (R).
2.  Oscilloscope Probe V1+ (AD2's 1+ and 1- connectors) is connected across the memristor (M) and series resistor (Rs).
3.  Oscilloscope Probe V2+ (AD2's 2+ and 2- connectors) is connected across the series resistor ( R ).

note: AD2 = Analog Discovery 2

## V1.X Jumper Connections

![](file://help/HysteresisV1Board.png)

## Memristor Selection

Use the toggle switches near the top of the app window to connect one or more individual memristors into the circuit.

## Series Resistor

The series resistor provides two important functions:

1.  Limits the maximum current through the memristor to prevent device damage (see memristor datasheet).
2.  Allows for a current measurement to be made with the oscilloscope (I = V2+/R).

## Controls

The control panel can be used to adjust the driver waveform of W1. The series resistor value must correspond to the actual series resistance value used in the experimental setup, otherwise the calculated values will be invalid.

## Conductance Plot Averaging

The conductance plot (G-V) uses a running average value, k, to smooth the data and reduce measurment noise. A k value of 1 eliminates averaging. The smaller k is, the more averaging will occur. If k is too small you may unknowingly hide important memristor behavior so it is best to keep k as close to 1 as possible. 

Gave(t)=k*Gave(t-1)+(1-k)G(t)

notes:
 
1. You can change the default k value in the preferences.
2. When changing the k value in the control above the G-V plot, it will only accept values between 0 and 1.  

## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." option, a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.
