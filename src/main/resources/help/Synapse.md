# Synapse App

This app allows you to drive a 2-1 synapse with base kT-RAM instructions and observe a continuous response in synaptic conductances via repeated `FFLV` instructions. It does this by measuring the voltage drops across the two memristors and the series resistor. 

## Connections

1. The jumper on the left side of memristor #1 connects to node `A`.
1. The jumper on the right side of memristor #2 connects to node `B`.
1. A jumper cable connects the right side of memristor #1 with node `Y`.
1. A jumper cable connects the left side of memristor #2 with node `Y`.

## Memristor Selection

Use the toggle switches near the top of the app window to connect the two individual memristors wired as a synapse into the circuit.

## Series Resistors

Use the jumpers on the right to select the series resistor and route it to ground. The default series resistor is 1kΩ. If you change this, be sure that you change this in the preferences as well.

## Controls

The control panel can be used to adjust the driver waveform and pulse width. 


## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.