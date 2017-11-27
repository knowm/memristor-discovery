# Synapse App

This app allows you to drive a 2-1 AHaH synapse with elemental kT-RAM instructions and observe a continuous response in synaptic state and synaptic pair conductances via repeated `FFLV` read instructions. When the "Start" button is clicked, the selected instruction is executed, followed by continuous read instructions executed at the given sample rate. Read instructions will be read until the "Stop" button is clicked, at which point a new instruction can be selected and the process repeated. The pulse shape, amplitude and width can be varied.

note:

The conductance of each memristor in the synapse is calculated by determining a voltage drop across the series resistor during a FFLV operation as well as the voltage drop across each memristor. If the current is very low or the series resistance is low, the voltage drop may not be sufficient to make a measurement. In this case, the data is not recorded. The synaptic state, Vy, can be measured at all times.

## Connections

This experiment can be run in multiple configurations depending on the users available memristor chips and adapter boards. 

### Method 1: Discrete Memristor Chip

The user must configure the jumpers in such a manner as to form a 2-1 synapse with available memristors on the discrete chip. While the configuration below will form a 2-1 synapse from memristors 1 and 2, the user can adapt this soas to form a synpase from any other two memristors on the discrete chip. 

![](file://help/Synapse.png)

1. The jumper on the left side of memristor #1 connects to node `A`.
1. The jumper on the right side of memristor #2 connects to node `B`.
1. A jumper cable connects the right side of memristor #1 with node `Y`.
1. A jumper cable connects the left side of memristor #2 with node `Y`.


### Method 2: Discrete Adapter Board

The discrete 2-1 X 8 AHaH Node adapter board can be used to form 8 distinct 2-1 synapses from 2, 4 or 6 discrete memristor chips. To use the adapter board, remove all jumpers and plug the adapter board in over the dip socket.

TODO: Diagram/Pic Here

### Method 3: 2-1 X 7 AHaH Node chips

Removed the shown jumpers and wire to the "Y" node. Note that only synapse 1-7 are functional. 

## Synapse Selection

The selection of synapses depends on the connection method used. For methods (2) and (3), simply enable the switch that directly corresponds to each synapses, i.e synapse 1 is selected by enabling switch 1, etc. For method (1), one must select multiple switches. A synapse formed from memristors 1 and 2 must enable switches 1 and 2, for example.

Use the toggle switches near the top of the app window to connect the two individual memristors wired as a synapse into the circuit.

## Series Resistors

Use the jumpers on the right to select the series resistor and route it to ground. The default series resistor is 1kΩ. If you change this, be sure that you change this in the preferences as well.

## Controls

The control panel can be used to adjust the driver waveform, pulse width and kT-RAM instruction. Each instruction applies a voltage pulse across the synapse in a different way given by the table below. 

TODO: A/B/Y Circuit diagram

### Atomic Instructions

A, B and Y nodes are floating unless otherwise specified. "Amp" is the voltage amplitude as set by the control slider.


**FFLV**:  V(A)=.1 V

**RFLV**:  V(A)=-.1 V

**FF**:  V(A)=Amp V

**RF**:  V(A) = -Amp V, V(B) = Gnd

**RL**:  See RLadn.

**RH**: See RHbdn.

**RHbdn**:  V(B) = Amp V, V(Y) = Gnd

**RLadn**: V(A) = - Amp V, V(Y)= Gnd

**RHaup**: V(A) = Amp V, V(Y) = Gnd

**RLbup**: V(B) = - Amp V, V(Y) = Gnd

### Compound Instructions

**FF_RL**: The FF instruction followed by the RL (RLadn) instruction

**FF_RH**: The FF instruction followed by the RH (RHbdn) instruction

### Conditional Compound Instructions

**FF_RU**: FF_RL instruction if the synaptic state evaluted *below* zero on the last FFLV instruction, else FF_RH.

**FF_RA**: FF_RL instruction if the synaptic state evaluted *above* zero on the last FFLV instruction, else FF_RH.


## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.