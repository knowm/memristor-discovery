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