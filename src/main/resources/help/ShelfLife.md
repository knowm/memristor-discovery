# Shelf Life Experiment

## Overview

This experiment is used to measure the switching function of memristors over long durations of time, recording the measured data into a CSV file.

## Operation

Switch function for each memristor is measured in intervals set by the *Repeat Interval* and *Time Unit* parameters on the control (or preferences) panels. To take a measurement every 30 minutes, for example, set the Time Unit to *MINUTES* and the Repeat Interval to *30*.

Switch function is determined by applying a pulse sequence to each memristor consisting of the following:

1. ERASE PULSE
2. READ PULSE (R1)
3. WRITE PULSE
4. READ PULSE (R2)
5. ERASE PULSE
6. READ PULSE (R3)

Each WRITE and ERASE pulse waveform shape is of the *HALF SINE* type.
Each READ pulse waveform shape is of the *SQUARE* type

The resistance of each memristor is measured during the READ pulse. The voltage amplitude and pulse duration of READ, WRITE and ERASE pulses can be set in the preferences menu. 

The experiment will start after pressing the Start button and will continue until the *Stop* button is pressed or the program terminated.

Two files will be created after the experiment begins and saved to the specified directory, which can be set in the control and preferences panels. 

## Information File

The "info" file contains specifications for the experiment and looks like the following:

///MD_Shelf_Life_Info_2019-03-13 17-42 PM MDT.csv  ---->

Memristor Discovery Shelf Life Experiment

Memristor Discovery Version: 0.0.9-SNAPSHOT

EXPERIMENT INFO
DataFile: MD_Shelf_Life_Data_2019-03-13 17-42 PM MDT.csv
Start Date : 2019-03-13 17-42 PM MDT
Series Resistor : 5000Ω
Measurment Interval : 5 SECONDS
Read Voltage : 0.1V
Write Voltage : 2.0V
Erase Voltage : -2.0V
Read Pulse Width : 1000μs
Write Pulse Width : 20000μs
Erase Pulse Width : 20000μs
Max Write Resistance : 10.0kΩ
Min Erase Resistance : 20.0kΩ

SYSTEM INFO
java.home: /Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home
java.vendor: Oracle Corporation
java.version: 11.0.2
os.name: Mac OS X
os.version: 10.14
user.name: alexnugent

## Data File


The "data" file contains data organized as Comma Separated Values (CSV), with one measurement for all memristors to each line. Its contents look like this:

///MD_Shelf_Life_Data_2019-03-13 17-42 PM MDT.csv  ---->

SHELF LIFE TEST START

data file: /Users/alexnugent/Desktop/MD_Shelf_Life_Data_2019-03-13 17-42 PM MDT.csv
info file: /Users/alexnugent/Desktop/MD_Shelf_Life_Info_2019-03-13 17-42 PM MDT.csv

Time,SwitchTest,1_E0,1_W,1_E1,1C,2_E0,2_W,2_E1,2C,3_E0,3_W,3_E1,3C,4_E0,4_W,4_E1,4C, ... 
2019.03.13 17:42:21 MDT,S_GOOD,12.1,4.1,11.4,E_FAIL,32.3,1.2,37.0,PASS,22.0,1.5,41.7,PASS,∞,∞,∞,...
2019.03.13 17:42:28 MDT,S_GOOD,12.5,3.4,15.3,E_FAIL,31.1,1.2,30.0,PASS,74.1,1.6,37.3,PASS,∞,∞,∞,...
2019.03.13 17:42:34 MDT,S_GOOD,14.5,3.8,12.4,E_FAIL,32.5,1.4,30.8,PASS,53.3,1.6,41.3,PASS,∞,∞,∞,...

Each column is as follows:

Time: The date and time in the following formate:  year-month-day hour:minute:second timezone
SwitchTest: S_GOOD if switches test good. S_BAD if switches test bad.
1_E0: The measured resistance (kΩ), after the **first** ERASE pulse on memristor 1.
1_W: The measured resistance (kΩ), after the **first** WRITE pulse on memristor 1.
1_E1: The measured resistance (kΩ), after the **second** ERASE pulse on memristor 1.
1C: The evaluated category of memristor 1 based on its resistances measured during each READ pulse. 

The follow code is used to determine the category (C) of each memristor.

1. Let R1, R2 and R3 be the measured resistance on the first, second and third read pulse.
2. Let *maxWriteResistance* be the maximum allowed resistance after application of the WRITE pulse, which should decrease resistance of the memristor. 
3. Let *minEraseResistance* be the minimum allowed resistance after application of the ERASE pulse, which should increase resistance of the memristor. 

if (R1 < maxWriteResistance && R2 < maxWriteResistance && R3 < maxWriteResistance) {

     C = STK_LO;
     
} 
else if (R1 > minEraseResistance && R2 > minEraseResistance && R3 > minEraseResistance) {

     C = STK_HI;
     
} 
else if (R2 > maxWriteResistance) {

     C = W_FAIL;
     
} 
else if (R3 < minEraseResistance) {

     C = E_FAIL;
     
} 
else {

     C = PASS;
     

}


### Failures

STK_LO = "Stuck Low", The resistance is stuck low. 
STK_HI = "Stuck High", The resistance is stuck high. 
W_FAIL = "Write Fail", The resistance failed to fall below *maxWriteResistance* after WRITE pulse. 
E_FAIL = "Erase Fail", The resistance failed to rise above * minEraseResistance* after ERASE pulse. 

It is possible that a functioning memristor is labels as failed. For example, in the example data file above, memristor 1 reliably switches between ~4kΩ and 12kΩ. However, the Min Erase Resistance is set to 20kΩ and thus this memristor is determined to have an erase fail. 

When resistance is measured as ∞, this means that the voltage drop across the series resistor has fallen below .001V and thus the resistance measurement is approaching the measurement accuracy of the AD2.


## Experiment Consol

The experiment consol will update to mirror the data being placed in the 'info' file. 




