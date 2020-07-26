# Synapse 1-2 Experiment

*Requires Version 2.0 Memristor Discovery Board in Mode 2 with a 1X16 linear array chip*

This app allows you to drive kT-RAM differential-pair memristor synapses with elemental kT-RAM instructions and observe a continuous response in synaptic state and synaptic pair conductances via repeated `FLV` read instructions. When the "Start" button is clicked or the "s" key is pressed, the selected instruction is executed, followed by continuous read instructions executed at the given sample rate. Read instructions will be read until the "Stop" button is clicked or the "s" key is pressed, at which point a new instruction can be selected and the process repeated. The pulse shape, amplitude and width can be varied.

## Synapse Selection

A synapse is formed by selecting two memristors to form a differential pair. Memristor A is selected from 1-8 in the top memristor selection menu, while memristor B is selected from 8-16. Two and only two memristors can be selected. The series resistors in each of the A and B resistor sockets must match each other and the value set in the preferences menu. It is recommended to use a precision resistor to minimize measurement variance. 

## Circuit Configuration and Voltage Polarity Conventions

![](_img/12SynapseCircuit.png)

The above circuit depicts the synapse circuit where memristor A is selected from memristor 2 on the chip, while memristor B is selected from memristor 13 on the chip. Note that all of the memristor anodes share a common ground. To increase the conductance of the selected memristors it is necessary to lower the driver voltage W1 below zero (ground). While the applied pulse on W1 is *negative*, the voltage drop across the memristor is defined to be *positive*, since in this case the cathode is the lower potential. 

## Instructions

![](_img/instructions12.png)

Notes:

1. The conductance of each memristor in the synapse is calculated by determining a voltage drop across the A and B precision series resistors during a FLV operation. If the voltage drop is not sufficient to make a measurement (because it falls below the resolution of the AD2),  the data is not recorded in the chart and a message is printed to the console (Menu-->Help--Consol).

## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.

## Analog Discovery 2 Scope and Waveform Offsets

Some AD2 units have offsets of a few millivolts that can cause significant measurement issues. While waveforms software resolves this through calibration, and while the calibration data is stored on the device itself, the unit does not actually apply the calibration. Rather, Waveforms software uses the stored parameters to correct the acquired data and generated signals. To add calibration to your measurements, follow the below procedure:

1. Remove the Memristor Discovery Board from the AD2.
2. Open the Synapse 1-2 Experiment in Memristor Discovery software.
3. Connect the AD2 1+, 2+, 1- and 2- terminals to ground.
4. Select two memristors (otherwise the software will complain) and hit "start".
5. Open the console by going to Menu Bar--> Window-->Console. Look at the logged messages for V(1+), V(2+) and V(W1). Stop the measurements by clicking on "Stop". Copy the console information. For example:  

```
V(1+): -0.001160290631058606
V(2+): -0.04096653596287886
V(W1): -0.07999999821186066
```
	
6. Connect 1+ and 2+ inputs to W1. You may need a breadboard for this. Click "Start". Look at the logged messages for V(1+), V(2+) and V(W1). Click "Stop". Copy the console information again:

```
V(1+): -0.10088136722427613
V(2+): -0.14046089059718023
V(W1): -0.07999999821186066
```

7. Record scope offsets from measurements in step (5). V(1+) is the Scope (1+) Offset and V(2+) is the Scope (2+) Offset. 
	
```
Scope (1+) Offset: -0.00116
Scope (2+) Offset: -0.04096
```

8. Compute waveform offset from equation using data recorded in step 6 and scope offsets.

```
W1 Offset = V(1+) - [Scope (1+) Offset] - V(W1)

For example: W1 Offset = -0.101 - (-0.001) - (-.08) = -.02
```
	
9. Open the experiment preferences (Menu --> Window --> Preferences) and enter the measured offsets into the preferences. 

10. Restart the experiment. The calibration information will now be used for measurements. 


