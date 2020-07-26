# Classify Experiment

This app performs online supervised classification with an 8 input AHaH node. The top plot displays an exponential moving average of the classification accuracy while the bottom plot displays the measured synaptic states after each training epoch. 

## Connections

This experiment requires the discrete 2-1 X 8 AHaH Node adapter board and at least (2) discrete 16-Pin DIP memristor chips.

![](MDV1X_21AHaHX3R_Adaptor.png)

The adapter board can be used to form an 8 input 2-1 AHaH node from 2, 4 or 6 discrete memristor chips. Use 4 or 6 chips for 2X and 3X redundancy, respectively. To use the adapter board, remove all jumpers and plug the adapter board in over the dip socket.

## Controls

The upper control panel can be used to adjust the driver waveform, voltage amplitude and pulse width.

### AHaH Routine

Will select the AHaH Routine that is used for supervised learning.

#### 'Learn on Mistakes' AHaH Routine 

	if (Vy < 0 && pattern.state) {
      aHaHController.executeInstruction(FF_RH);
    } else if (Vy > 0 && !pattern.state) {//mistake
      aHaHController.executeInstruction(FF_RL);
    }

#### 'Learn Always' AHaH Routine

	if (pattern.state) {
      aHaHController.executeInstruction(FF_RH);
    } else {//mistake
      aHaHController.executeInstruction(FF_RL);
    }

#### 'Learn Combo' AHaH Routine

 	if (pattern.state) {
      aHaHController.executeInstruction(Instruction.FF_RH);
    } else if (Vy > 0) {
      aHaHController.executeInstruction(Instruction.FF_RL);
    }

### Dataset

The dataset defines the supervised learning problem. Each dataset is defines as follows:

[0,1,2]:T denotes that the spike pattern formed by activating synapses 1, 2 and 3 leads to a "True" or "Positive" output. F denotes "False" or "Negative" output. Note that patterns listed below are zero indexed. 

**Ortho2Pattern**

[0, 1, 2, 3]-->true, [4, 5, 6, 7]-->false

**AntiOrtho2Pattern**

[0, 1, 2, 3]-->false, [4, 5, 6, 7]-->true

**Ortho4Pattern**

[0, 1]-->false,[2, 3]-->false,[4, 5]-->true,[6, 7]-->true

**AntiOrtho4Pattern**

[0, 1]-->true,[2, 3]-->true,[4, 5]-->false,[6, 7]-->false

**Ortho8Pattern**

[0]-->true,[1]-->true,[2]-->true,[3]-->true,[4]-->false,[5]-->false,[6]-->false,[7]-->false

**AntiOrtho8Pattern**

[0]-->false,[1]-->false,[2]-->false,[3]-->false,[4]-->true,[5]-->true,[6]-->true,[7]-->true

**TwoPattern25Frustrated**

[0, 1, 2, 3, 5]-->true,[2, 4, 5, 6, 7]-->false

**TwoPattern2345Frustrated**

[0, 1, 2, 3, 4, 5]-->true,[2, 3, 4, 5, 6, 7]-->false


### Train Epochs

The number of times the supervised learning will proceed through the dataset for both the "Scramble" and "Learn" actions.

### Clear Plot

Clears the plot, allowing you to start over.

### Scramble

Applies 10 random two-input spike patterns while executing the FF-RA instruction, for the given number of train epochs.

### Learn

Generates random patterns from the selected dataset and applies the given AHaH Routine for the given number of training epochs. An exponential running average of the training accuracy is displayed in the top plot, while the value of each of the 8 synapses is displayed in the bottom plot.

## Series Resistors

Use the jumpers on the right to select the series resistor and route it to ground. The default series resistor is 1kΩ. If you change this, be sure that you change this in the preferences as well.


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
