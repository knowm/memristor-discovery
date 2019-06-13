# BoardCheck Experiment

This app allows you to self-test the functionality of your board and memristor chips. Paramount to measurement is validated equipment. This app enables you to check the functionality of:

1. Bilateral Switches via **Switch Board Test** Button 
2. 1-4 Muxes  via **1-4 Mux Board Test** Button 
3. Knowm Discrete Memristor chips. via **Mem-Inline Chip** Button 

All boards and chips shipped by Knowm Inc have passed these tests. 

## Switch Board Test

This test activates each switch in order and applies a pulse across the chip and a series resistor. The voltage drop across the series resistor is used to calculate the resistance in the memristor sockets. 

The test is passed if the displayed resistance for each switch selection matches reality to within 2%. 

## 1-4 Mux Board Test

This test allow for the test of each of the 1-4 muxes. It does this by routing a voltage of a specific value to each of the three junctions while also routing the oscilloscopes. If any of the tests fail, it is reported and the whole test will fail. The measured voltage is compared to the generated voltage for each route and oscilloscope channel and the deviation is shown.

## Mem-Inline Chip

This test applies a sequence of pulses across each memristor, using the voltage drop across the series resistor to measure the memristor resistance. The sequence is as follows:

1. Write
2. Reset 
3. Read-->R0
4. Write
5. Read-->R1
6. Reset
7. Read-->R2

Default Constants:

V_READ = .1f;
V_WRITE = 1.0f;
V_RESET = -2f;

MEMINLINE_MIN_Q = 2;
MEMINLINE_MIN_R = 10;//kΩ
MEMINLINE_MAX_R = 100;//kΩ
MEMINLINE_MIN_SWITCH_OFF = 1000;//kΩ

Define Values: 

q1 = R0 / R1;
q2 = R2 / R1;

Test will terminate if the measured resistance with all switches off is less than MEMINLINE_MIN_SWITCH_OFF. This condition indicates that the switches are bad and thus measurement if futile. 

A memristor will fail the test if any of the following conditions are found:

R0 < MEMINLINE_MIN_R && R1 < MEMINLINE_MIN_R && R2 < MEMINLINE_MIN_R
                  
R0 > MEMINLINE_MAX_R && R1 > MEMINLINE_MAX_R && R2 > MEMINLINE_MAX_R        
          
q1 < MEMINLINE_MIN_Q
          
q2 < MEMINLINE_MIN_Q


## Preferences

The preferences window allows you to save your preferred experimental control parameters between sessions of using the app.
