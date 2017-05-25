# Synapse App

This app allows you to drive a 2-1 synapse with the kT-RAM instructions set and observe a continuous response in synaptic value on node `y` via repaeted `FFLV` instructions.

## Connections

1. The jumper on the left side of memristor #1 connects to node `A`.
1. The jumper on the right side of memristor #2 connects to node `B`.
1. A jumper cable (white) connects the right side of memristor #1 with the left side of memristor #2.
1. A jumper cable (yellow) connects the left side of memristor #2 with node `Y`.

## Memristor Selection

Use the toggle switches near the top of the app window to connect the two individual memristors wired as a synapse into the circuit.

## Series Resistors

The jumpers on the left and right bypass the resistors. There is no serial resistor.

## Controls

The control panel can be used to adjust the driver waveform of W1. The series resistor value control should correspond to the actual series resistance value used in the experimental setup in order to calculate an accurate current value.

## Conductance Plot

The conductance plot (G-V) uses a running average value, k, to smooth the data. A k value of 0 will eliminate all averaging. The larger k is, the more averaging will occur. If k is too big you may unknowingly hide important memristor behavior so it is best to keep k as low as possible.

## Exporting Data

Any plot can be right-clicked to export the data in either chart format (save As...) or comma-separated-values (Export As...), which can be opened in spreadsheet software. For "Export As..." a directory needs to be selected. In that directory, an individual CSV file will be created for each series in the plot.

## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.