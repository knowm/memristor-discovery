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