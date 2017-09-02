/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
 *
 * This package also includes various components that are not part of
 * Memristor-Discovery itself:
 *
 * * `Multibit`: Copyright 2011 multibit.org, MIT License
 * * `SteelCheckBox`: Copyright 2012 Gerrit, BSD license
 *
 * Knowm, Inc. holds copyright
 * and/or sufficient licenses to all components of the Memristor-Discovery
 * package, and therefore can grant, at its sole discretion, the ability
 * for companies, individuals, or organizations to create proprietary or
 * open source (even if not GPL) modules which may be dynamically linked at
 * runtime with the portions of Memristor-Discovery which fall under our
 * copyright/license umbrella, or are distributed under more flexible
 * licenses than GPL.
 *
 * The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * If you have any questions regarding our licensing policy, please
 * contact us at `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck;

import static org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck.V1BoardCheckPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck.consol.ConsolControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck.consol.ConsolController;
import org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck.consol.ConsolPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.v1boardcheck.control.ControlPanel;
import org.knowm.memristor.discovery.utils.Util;
import org.knowm.memristor.discovery.utils.WaveformUtils;
import org.knowm.waveforms4j.DWF;

public class V1BoardCheckExperiment extends Experiment {

 private final ControlModel controlModel = new ControlModel();
  
 private final static float V_MEASURE=1.2345f;
 private final static float V_TOLERANCE=.01F;
 
 
 private ControlPanel controlPanel;
  private ConsolPanel consolPanel;
  private final ConsolControlModel plotModel = new ConsolControlModel();
  private final ConsolController consolController;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public V1BoardCheckExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    consolPanel = new ConsolPanel();
    consolController = new ConsolController(consolPanel, plotModel);
   // new ControlController(controlPanel, controlModel, dwfProxy);
    //System.out.println(controlModel.getInstruction());
  //  dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

  }

  @Override
  public void doCreateAndShowGUI() {

  }
  
  public boolean[] measure(int dWFWaveformChannel,int upper8SetMask) {
    
   
    
    dwfProxy.setUpper8IOStates(upper8SetMask);
    int samplesPerPulse = 300;
    int sampleFrequency = 100 * samplesPerPulse;
    consolPanel.println("Starting Pulse Measurment");

    dwfProxy.getDwf().startAnalogCaptureBothChannelsTriggerOnWaveformGenerator(dWFWaveformChannel, sampleFrequency, samplesPerPulse);

    dwfProxy.waitUntilArmed();
    double[] pulse = WaveformUtils.generateCustomWaveform(Waveform.Square, V_MEASURE, 100);
    dwfProxy.getDwf().startCustomPulseTrain(dWFWaveformChannel, 100, 0, 1, pulse);
    boolean success = dwfProxy.capturePulseData(sampleFrequency, 1);
    if(success) {
     int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
     double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
     double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
     
     /*
      * the output is a pulse with the last half of the measurement data at ground. Taking the first 25% insures we get the pulse amplitude.
      */
     
     //average over the first 25%. 
     
     float aveScope1=0;
     float aveScope2=0;
     for (int i = 0; i < v1.length/4; i++) {
      aveScope1+=v1[i];
      aveScope2+=v2[i];
     }
     
//     System.out.println(Arrays.toString(v1));
//     System.out.println(Arrays.toString(v2));
     
     aveScope1/=v1.length/4;
     aveScope2/=v2.length/4;
     
     float d1=Math.abs(aveScope1-V_MEASURE);
     float d2=Math.abs(aveScope2-V_MEASURE);
     
     consolPanel.println("V(1+)="+aveScope1+", deviation="+d1);
     consolPanel.println("V(2+)="+aveScope2+", deviation="+d2);
 
     boolean[] result=new boolean[] {d1<V_TOLERANCE,d2<V_TOLERANCE};
    // System.out.println(Arrays.toString(result));
     return result ;
    }else {
      consolPanel.println("Pulse Capture Failed. Could not take measurment!");
      return null;
    }
      
  }

  private class CaptureWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // TEST MUXES
      
      /*
       * MUX DIO PINOUT
       * 
       * Order ==> W2, W1, 2+, 1+
       * 00 E
       * 10 Y
       * 01 A
       * 11 B
       */

      consolPanel.println("Testing 1-4 Muxes.");
      boolean abort=false;
      
      /*
       * Procedure: 
       * 1) Route W1 to A.
       * 2) Route 1+ and 2+ to A.
       * 3) Drive 1.234 Volts DC on W1.
       * 4) Measure 1+ and 2+.
       * 5) TEST: Is 1+ and 2+ equal to 1.234 within p percent?
       * 
       * 
       * If both fail, possibly bad W1 mux. Test with W2.
       * If one or the other fail, its a bad scope mux.
       * 
       */
      
      consolPanel.println("Testing W1");
     
      boolean[] measurment=measure(DWF.WAVEFORM_CHANNEL_1,0b0001_0101_0000_0000);
      if(measurment[0] && measurment[1]) {//W1 Good. Scope 1 Good. Scope 2 Good.
        consolPanel.println("W1 pass, 1+ pass, 2+ pass");
      }else if(!measurment[0] && !measurment[1]) {
        consolPanel.println("*!*!*!*!*!*FAIL*!*!*!*!*!*");;
        consolPanel.println("W1 is likely bad.");
      }else if(measurment[0] ^ measurment[1]) {//one or the other but not both. W1 is good, Scope 1 or Scope 2 is bad.
        consolPanel.println("*!*!*!*!*!*FAIL*!*!*!*!*!*");
        if(!measurment[0]) {
          consolPanel.println("1+ Mux is likely bad.");
        }
        if(!measurment[1]) {
          consolPanel.println("2+ Mux is likely bad.");
        }
        abort=true;
      }
      
      if(abort) {
        return false;
      }
     
      consolPanel.println("Testing W2");
      
      measurment=measure(DWF.WAVEFORM_CHANNEL_2,0b0100_0101_0000_0000);
      if(measurment[0] && measurment[1]) {//W1 Good. Scope 1 Good. Scope 2 Good.
        consolPanel.println("W2 pass, 1+ pass, 2+ pass");
      }else if(!measurment[0] && !measurment[1]) {
        consolPanel.println("*!*!*!*!*!*FAIL*!*!*!*!*!*");
        consolPanel.println("W2 is likely bad.");
      }else if(measurment[0] ^ measurment[1]) {//one or the other but not both. W1 is good, Scope 1 or Scope 2 is bad.
        consolPanel.println("*!*!*!*!*!*FAIL*!*!*!*!*!*");
        if(!measurment[0]) {
          consolPanel.println("1+ Mux is likely bad.");
        }
        if(!measurment[1]) {
          consolPanel.println("2+ Mux is likely bad.");
        }
        abort=true;
      }
      
      if(abort) {
        return false;
      }
      

//      try {
//        Thread.sleep(500);
//      } catch (InterruptedException e) {
//        // eat it. caught when interrupt is called
//        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_2);
//        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
//      }
      
      // W2->E,W1-->A, 2+-->Y,1+-->B
//     
//
//      double readVoltage = .1;
//      int samplesPerPulse = 300;
//      int sampleFrequency = 10_000 * samplesPerPulse;
//
//      while (!isCancelled()) {
//
//        try {
//          Thread.sleep(controlModel.getSampleRate() * 1000);
//        } catch (InterruptedException e) {
//          // eat it. caught when interrupt is called
//          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
//          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
//        }
//
//        /*
//         * Apply a pulse across both memristors and the series resistor, measure Y and B to determine conductance of both memristors
//         */
//
//        // ///////////////////////////
//        // Analog In /////////////////
//        // ////////////////////////////
//        // dwfProxy.getDwf().startAnalogCaptureBothChannelsLevelTrigger(sampleFrequency, 0.01, samplesPerPulse * 1);
//        dwfProxy.getDwf().startAnalogCaptureBothChannelsTriggerW1(sampleFrequency, samplesPerPulse * 1);
//        dwfProxy.waitUntilArmed();
//
//        // ////////////////////////////////
//        // Pulse Out /////////////////
//        // ////////////////////////////////
//        double[] customWaveformW1 = WaveformUtils.generateCustomWaveform(Waveform.HalfSine, readVoltage, 10_000);
//        dwfProxy.getDwf().startCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, 10_000, 0, 1, customWaveformW1);
//
//        // //////////////////////////////
//        // Read In /////////////////////
//        // //////////////////////////////
//        boolean success = dwfProxy.capturePulseData(10_000, 1);
//        if (!success) {
//          // Stop Analog In and Out
//          dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
//          dwfProxy.getDwf().stopAnalogCaptureBothChannels();
//          controlPanel.getStartStopButton().doClick();
//          return false;
//        } else {
//
//          // Get Raw Data from Oscilloscope
////          int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
////          double[] v1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
////          double[] v2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);
//
//          // /////////////////////////
//          // Create Chart Data //////
//          // /////////////////////////
//
//          // double[][] trimmedRawData = PostProcessDataUtils.trimIdleData(v1, v2, 0.08, 0);
//          // double[] V1Trimmed = trimmedRawData[0];
//          // double[] V2Trimmed = trimmedRawData[1];
//
////          double peakV1 = Util.maxAbs(v1);
////          double peakV2 = Util.maxAbs(v2);
//          // voltage drop across memristor 1
//   //       double vM1 = readVoltage - peakV2;
//          // voltage drop across memristor 2
//  //        double vM2 = peakV2 - peakV1;
//          // voltage drop across series resistor
//  //        double vR = peakV1;
//
// //         double I = vR / controlModel.getSeriesResistance();
////          double Gm1 = ConductancePreferences.CONDUCTANCE_UNIT.getDivisor() * I / vM1;
////          double Gm2 = ConductancePreferences.CONDUCTANCE_UNIT.getDivisor() * I / vM2;
//
//          // System.out.println("seriesResistance="+controlModel.getSeriesResistance());
//          // System.out.println("I="+I);
//          // System.out.println("Gm1="+Gm1);
//          // System.out.println("Gm2="+Gm2);
//          // System.out.println("ConductancePreferences.CONDUCTANCE_UNIT.getDivisor()"+ConductancePreferences.CONDUCTANCE_UNIT.getDivisor());
//          //
//        //  publish(Gm1, Gm2);
//        }
//        // Stop Analog In and Out
//        dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
//        dwfProxy.getDwf().stopAnalogCaptureBothChannels();
//      }
      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

//      plotController.updateYChartData(chunks.get(0), chunks.get(chunks.size() - 1));
//      plotController.repaintYChart();
    }
  }

  // public void executeInstruction() {
  //
  // // 1. the IO-bits are set
  // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());
  //
  // // 2. set the waveforms ( change this to correct amplitude and sign based on instruction)
  // // Get the waveform for the selected instruction
  // double W2Amplitude = 0;
  // double[] customWaveformW2 = WaveformUtils.generateCustomWaveform(controlModel.getWaveform(), W2Amplitude, controlModel.getCalculatedFrequency());
  // double W1Amplitude = controlModel.getAmplitude() * controlModel.getInstruction().getW1VoltageMultiplier();
  // double[] customWaveformW1 = WaveformUtils.generateCustomWaveform(controlModel.getWaveform(), W1Amplitude, controlModel.getCalculatedFrequency());
  //
  // dwfProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, controlModel.getPulseNumber(), customWaveformW1);
  // dwfProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_2, controlModel.getCalculatedFrequency(), 0, controlModel.getPulseNumber(), customWaveformW2);
  // dwfProxy.getDwf().startPulseTrain(DWF.WAVEFORM_CHANNEL_BOTH);
  //
  // System.out.println("controlModel.getCalculatedFrequency(): " + controlModel.getCalculatedFrequency());
  // System.out.println("pulse width: " + controlModel.getPulseWidth());
  // System.out.println("pulse num: " + controlModel.getPulseNumber());
  //
  // /*
  // * must wait here to allow pulses from AWG to finish.
  // */
  //
  // int ms = controlModel.getPulseNumber() * controlModel.getPulseWidth() * 2 / 1000000;
  // if (ms <= 0) {
  // ms = 1;
  // }
  // System.out.println("sleeping for " + ms + " ms to allow pulse execution");
  // try {
  // Thread.sleep(ms);
  // } catch (InterruptedException e) {
  //
  // }
  //
  // }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    switch (propName) {

      case EVENT_INSTRUCTION_UPDATE:

        // System.out.println(controlModel.getInstruction());
        // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

        break;

      default:
        break;
    }
  }

  // // this is for trigger from the analog out channel instead of analog in
  // public boolean startAnalogCaptureBothChannelsTriggerW1(double sampleFrequency, int bufferSize) {
  //
  // // System.out.println("triggerLevel = " + triggerLevel);
  // if (bufferSize > DWF.AD2_MAX_BUFFER_SIZE) {
  // // logger.error("Buffer size larger than allowed size. Setting to " + DWF.AD2_MAX_BUFFER_SIZE);
  // bufferSize = DWF.AD2_MAX_BUFFER_SIZE;
  // }
  //
  // boolean success = true;
  // success = success && dwfProxy.getDwf().FDwfAnalogInFrequencySet(sampleFrequency);
  // success = success && dwfProxy.getDwf().FDwfAnalogInBufferSizeSet(bufferSize);
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerPositionSet((bufferSize / 2) / sampleFrequency); // no buffer prefill
  //
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, 2.5);
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, 2.5);
  // success = success && dwfProxy.getDwf().FDwfAnalogInAcquisitionModeSet(AcquisitionMode.Single.getId());
  // // Trigger single capture on rising edge of analog signal pulse
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerAutoTimeoutSet(0); // disable auto trigger
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcAnalogOut1.getId()); // one of the analog in channels
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerTypeSet(AnalogTriggerType.trigtypeEdge.getId());
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerChannelSet(0); // first channel
  // // Trigger Level
  // // / if (triggerLevel > 0) {
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondRisingPositive.getId());
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(triggerLevel);
  // // } else {
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondFallingNegative.getId());
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(triggerLevel);
  // // }
  //
  // // arm the capture
  // success = success && dwfProxy.getDwf().FDwfAnalogInConfigure(true, true);
  // if (!success) {
  // dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
  // dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
  // dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
  // throw new DWFException(dwfProxy.getDwf().FDwfGetLastErrorMsg());
  // }
  // return true;
  // }

  @Override
  public ExperimentControlModel getControlModel() {

    return controlModel;
  }

  @Override
  public ExperimentControlPanel getControlPanel() {

    return controlPanel;
  }

  @Override
  public ExperimentPlotPanel getPlotPanel() {

    return consolPanel;
  }

  @Override
  public SwingWorker getCaptureWorker() {

    return new CaptureWorker();
  }
}
