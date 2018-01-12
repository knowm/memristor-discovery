# Logic Experiment

This app performs unsupervised (FFRU or 'converge' ) and anti-unsuperved (FFRA 'reset') AHaH operations over a two-input AHaH node. The synaptic state is plotted as a point on the plane (x,y)=(Synapse A, Synapse B). The state of a synapse is defined as the output voltage Vy when the synapse is selected in isolation and the FFLV instruction is executed.

## Connections

This experiment requires the discrete 2-1 X 8 AHaH Node adapter board and at least (2) discrete 16-Pin DIP memristor chips.

![](MDV1X_21AHaHX3R_Adaptor.png)

The adapter board can be used to form 8 distinct 2-1 synapses from 2, 4 or 6 discrete memristor chips. Use 4 or 6 chips for 2X and 3X redundancy, respectively. To use the adapter board, remove all jumpers and plug the adapter board in over the dip socket.

## Controls

### Input A and B mask selection

Each input mask allows for the use of one or more synapse for each input. The user can use this to increase the redundancy of the synapses or to select arbitrary devices from which to form the AHaH node.

### Input Bias selection

A bias is a synapse which receives the FF-RA instruction instead of the FF-RU instruction. It can help in some situations to avoid the null-state, provided that its incremental. If this means nothing to you, just leave it alone. 

### NumExecutions (NE)

This is the number of time that the FF-RU or FF-RA instruction will be applied for a single trace when the *[FFRU X NE]*, *[FFRA X NE]* or *[Reset->[FFRU X NE] X 25]* buttons are pressed.

### Button Actions

#### FFRU X NE

Applies the FF-RU instruction NE times. Plots the result as a green circle (starting value), blue line (path) and red circle (end value)

#### FFRA X NE

Applies the FF-RA instruction NE times. Plots the result as a thin gray line, where each vertex is a measured state of the synapse. 

#### Reset

Applies 20 FF-RA instructions with a voltage of 1.5V and pulse width of 500us. 

#### Reset->[FFRU X NE] X 25

Performs twenty five [Reset]-->[FFRU X NE] cycles. 

#### Clear Plot

Clears the the plot.

## Series Resistor

Use the jumpers on the right to select the series resistor and route it to ground. The default series resistor is 1kΩ. If you change this, be sure that you change this in the preferences as well.

## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.