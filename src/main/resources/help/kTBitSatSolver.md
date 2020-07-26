# kTBitSatSolver Experiment

The experiment is described in the following blog article:

 <https://knowm.org/how-to-build-a-memristor-constraint-solver/>

This experiment is used to solve 3SAT problem instances specified in ".dimacs" files. Files can be downloaded from <https://toughsat.appspot.com/>

## Mode 2

**The board must be placed into Mode 2 by moving the switch to the right (2) position.** Mode 2 configures the board for differential-pair access as in the following circuit:

![](_img/Mode2Version2Board.png)

## Series Resistors

Two series resistors of equal value must be placed in the A and B resistor sockets. The board is shipped with 20kΩ resistors in each socket. The resistor value must be specified in the preferences 'Series Resistor' field. 

## Controls

The upper control panel can be used to adjust the pulse driver waveform, forward and reverse voltage amplitudes and pulse width. Lower controls select kT-RAM learning routines and datasets. 

## Synaptic Pairings

Each differential pair memristor is created by pairing the following memristors:

Synapse 1: [1,9]
Synapse 2: [2,10]
Synapse 3: [3,11]
Synapse 4: [4,12]
Synapse 5: [5,13]
Synapse 6: [6,14]
Synapse 7: [7,15]
Synapse 8: [8,16] 


### Clear Plot

Clears the plot.

### Initialize

Executes FAB and ANTI-HEBBIAN instructions to increase and equalize the conductance of each synapse memristor pair.  

### Solve

Uses the instructions as described in [this blog article](<https://knowm.org/how-to-build-a-memristor-constraint-solver/>) to find a solution to the contraint problem.

## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.

## Analog Discovery 2 Scope and Waveform Offsets

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
