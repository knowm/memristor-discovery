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
package org.knowm.memristor.discovery.gui.mvc.experiments.qc;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.utils.AveMaxMinVar;
import org.knowm.waveforms4j.DWF;
import org.knowm.waveforms4j.DWF.AcquisitionMode;

public class QCExperiment extends Experiment implements PropertyChangeListener {

  private final QCControlModel model = new QCControlModel();
  private QCControlPanel controlPanel;
  private QCMainPanel mainPanel;

  private QCCaptureWorker captureWorker;

  private List<String> reportLines = new ArrayList<>();

  private DecimalFormat f = new DecimalFormat("#,###.## kΩ");

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public QCExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);
    createAndShowGUI(mainFrameContainer);
  }

  private void createAndShowGUI(Container mainFrameContainer) {

    controlPanel = new QCControlPanel();
    JScrollPane jScrollPane = new JScrollPane(controlPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane.setBorder(createEmptyBorder());
    mainFrameContainer.add(jScrollPane, BorderLayout.WEST);

    // ///////////////////////////////////////////////////////////
    // START BUTTON ////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    controlPanel.getStartButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (model.getSerialNumber() == null || model.getSerialNumber().trim().equalsIgnoreCase("")) {
          JOptionPane.showMessageDialog(mainFrameContainer, "Please enter the serial number first!", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        try {
          reportLines.clear();
          String fileName = model.getBasePath() + "report.md";
          Path filePath = Paths.get(fileName);
          System.out.println(filePath);

          Path parentDir = filePath.getParent();
          if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
          }
          else {
            JOptionPane.showMessageDialog(mainFrameContainer, "Folder for this serial number already exixts!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
        } catch (IOException ioException) {

          JOptionPane.showMessageDialog(mainFrameContainer, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
          ioException.printStackTrace();
        }

        // switchPanel.enableAllRadioButtons(false);
        // controlPanel.enableAllChildComponents(false);
        controlPanel.getStartButton().setEnabled(false);
        controlPanel.getStopButton().setEnabled(true);

        // switch to iv view
        mainPanel.switch2IVChart();

        // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
        captureWorker = new QCCaptureWorker();
        captureWorker.execute();
      }
    });

    // ///////////////////////////////////////////////////////////
    // STOP BUTTON //////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////

    controlPanel.getStopButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        // switchPanel.enableAllRadioButtons(true);
        // controlPanel.enableAllChildComponents(true);
        controlPanel.getStartButton().setEnabled(true);
        controlPanel.getStopButton().setEnabled(false);

        // stop AD2 waveform 1 and stop AD2 capture on channel 1 and 2
        captureWorker.cancel(true);
      }
    });

    mainPanel = new QCMainPanel();
    mainFrameContainer.add(mainPanel, BorderLayout.CENTER);

    QCController hysteresisController = new QCController(controlPanel, mainPanel, model, dwfProxy);

    // register this as the listener of the model
    model.addListener(this);
  }

  private class QCCaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      try {
        reportLines.add("# Knowm Memristor Quality Control Test");

        reportLines.add("## Chip Information");
        reportLines.add("|Type|Serial|Date|");
        reportLines.add("|:--:|:--:|:--:|");
        reportLines.add("|" + model.getChipType() + "|" + model.getSerialNumber() + "|" + (new Date()).toLocaleString() + "|");
        reportLines.add("");

        reportLines.add("## Testing Conditions");
        reportLines.add("|Waveform|Amplitude|Offset|Frequency|Series Resistor|");
        reportLines.add("|:--:|:--:|:--:|:--:|");
        reportLines.add("|" + model.getWaveform() + "|" + model.getAmplitude() + "V|" + model.getOffset() + " V|" + model.getFrequency() + " Hz|" + f.format(model.getSeriesResistance() / 1000.0)
            + "|");
        reportLines.add("");

        reportLines.add("## Test Result");
        reportLines.add("|Device|LRS|HRS|Q|RESULT|");
        reportLines.add("|:--:|:--:|:--:|:--:|:--:|");

        dwfProxy.setAllIOStates(0b0000_0000);

        int numpass = 0;

        // for each memristor
        for (int j = 0; j < 8; j++) {

          // switch memristor
          dwfProxy.update2DigitalIOStatesAtOnce(j, true);

          // start capture
          dwfProxy.getDwf().startWave(DWF.WAVEFORM_CHANNEL_1, model.getWaveform(), model.getFrequency(), model.getAmplitude(), model.getOffset(), 50);
          dwfProxy.getDwf().startAnalogCaptureBothChannelsImmediately(model.getFrequency() * QCPreferences.CAPTURE_BUFFER_SIZE / QCPreferences.CAPTURE_PERIOD_COUNT, QCPreferences.CAPTURE_BUFFER_SIZE,
              AcquisitionMode.ScanShift);

          int captureCount = 0;

          List<Double> high_resistance_measurments = new ArrayList<Double>();
          List<Double> low_resistance_measurments = new ArrayList<Double>();

          while (!isCancelled()) {

            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              // eat it. caught when interrupt is called
              dwfProxy.setAllIOStates(0b0000_0000);
              dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
              dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false);
            }

            // Read In Data
            boolean success = dwfProxy.capturePulseData(model.getFrequency(), 1);
            if (!success) {
              continue;
            }

            int validSamples = dwfProxy.getDwf().FDwfAnalogInStatusSamplesValid();
            // System.out.println("validSamples: " + validSamples);

            if (validSamples > 0) {

              // captureAmplitudeData = dwf.FDwfAnalogInStatusData(OSCILLOSCOPE_CHANNEL_1, validSamples);
              double[] rawdata1 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_1, validSamples);
              double[] rawdata2 = dwfProxy.getDwf().FDwfAnalogInStatusData(DWF.OSCILLOSCOPE_CHANNEL_2, validSamples);

              // create current data
              double[] current = new double[rawdata2.length];
              double[] voltage = new double[rawdata1.length];

              for (int i = 0; i < current.length; i++) {
                current[i] = rawdata2[i] / model.getSeriesResistance() * QCPreferences.CURRENT_UNIT_DEFAULT_VALUE.getDivisor();
              }

              double maxV = 0;
              double minV = 0;

              if (!QCPreferences.IS_VIN) {
                for (int i = 0; i < current.length; i++) {
                  voltage[i] = rawdata1[i] - rawdata2[i];
                  maxV = voltage[i] > maxV ? voltage[i] : maxV;
                  minV = voltage[i] < minV ? voltage[i] : minV;
                }
              }

              publish(new double[][] { rawdata1, voltage, current });

              // System.out.println("voltage: " + Arrays.toString(voltage));
              // System.out.println("current: " + Arrays.toString(current));
              // System.out.println("resistance: " + Arrays.toString(resistance));

              if (captureCount > QCPreferences.START_QC_CAPTURE_COUNT) {
                List<Double> resistance = new ArrayList<Double>();
                for (int i = 0; i < current.length; i++) {

                  if (voltage[i] > maxV * QCPreferences.P_BELOW_MAX_MIN_V) {
                    resistance.add(Math.abs(voltage[i] / (rawdata2[i] / model.getSeriesResistance())));
                  }
                  else if (voltage[i] < minV * QCPreferences.P_BELOW_MAX_MIN_V) {
                    resistance.add(Math.abs(voltage[i] / (rawdata2[i] / model.getSeriesResistance())));
                  }
                }

                if (resistance.size() > 0) {
                  AveMaxMinVar maxMinVar = new AveMaxMinVar(resistance);
                  high_resistance_measurments.add((double) maxMinVar.getMax());
                  low_resistance_measurments.add((double) maxMinVar.getMin());
                }
              }
            }

            // go to next memristor
            if (captureCount++ > QCPreferences.MAX_CAPTURE_COUNT) {

              // stop capture
              dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
              dwfProxy.getDwf().FDwfAnalogOutConfigure(DWF.WAVEFORM_CHANNEL_1, false);

              if (high_resistance_measurments.size() > 0 && low_resistance_measurments.size() > 0) {
                AveMaxMinVar hrs = new AveMaxMinVar(high_resistance_measurments);
                AveMaxMinVar lrs = new AveMaxMinVar(low_resistance_measurments);
                double q = hrs.getAve() / lrs.getAve();
                boolean pass = false;
                if (lrs.getAve() < QCPreferences.R_TARGET && hrs.getAve() > QCPreferences.R_TARGET && q > QCPreferences.MIN_Q) {
                  pass = true;
                }

                if (pass) {
                  numpass++;
                }

                reportLines.add("|" + (j) + "|" + f.format(lrs.getAve() / 1000.0) + "|" + f.format(hrs.getAve() / 1000.0) + "|" + q + "|" + (pass ? "PASS" : "FAIL") + "|");
              }
              else {
                reportLines.add("|" + (j) + "| null | null | null | null |");
              }

              // save plot
              try {
                mainPanel.saveIVChart(model.getBasePath(), j);
              } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Problem saving chart", "Error", JOptionPane.ERROR_MESSAGE);
              }
              dwfProxy.setAllIOStates(0b0000_0000);

              break;
            }
          }
        }
        controlPanel.getStopButton().doClick();

        String classification = "";
        if (numpass == 8) {
          reportLines.add(1, "# Classification: Tier 1");
          classification = "TIER 1";
        }
        else if (numpass == 7) {
          reportLines.add(1, "# Classification: Tier 2");
          classification = "TIER 2";
        }
        else if (numpass == 6 | numpass == 5 | numpass == 4) {
          reportLines.add(1, "# Classification: Burn and Learn");
          classification = "BURN & LEARN";
        }
        else {
          reportLines.add(1, "# Classification: REJECT");
          classification = "REJECT";
        }

        reportLines.add(" ");

        reportLines.add("|0|1|2|3|");
        reportLines.add("|:--:|:--:|:--:|:--:|");
        reportLines.add("|![](0.png)|![](1.png)|![](2.png)|![](3.png)|");
        reportLines.add("|4|5|6|7|");
        reportLines.add("|![](4.png)|![](5.png)|![](6.png)|![](7.png)|");

        reportLines.add(" ");
        reportLines.add("## Notes");
        reportLines.add(" Device passes QC IFF: Q > " + QCPreferences.MIN_Q + " & HRS>r & LRS<r, where r=" + f.format(QCPreferences.R_TARGET));

        // must save report on its own thread or it will get an interupt exceptions
        class SaveReportThread extends Thread {

          @Override
          public void run() {

            // save report
            try {
              String fileName = model.getBasePath() + "report.md";
              Path filePath = Paths.get(fileName);
              Files.write(filePath, reportLines, Charset.forName("UTF-8"));
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }

        (new SaveReportThread()).start();

        JOptionPane.showMessageDialog(null, "Classification: " + classification);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return true;
    }

    @Override
    protected void process(List<double[][]> chunks) {

      long start = System.currentTimeMillis();

      // System.out.println("" + chunks.size());

      // Messages received from the doInBackground() (when invoking the publish() method). See: http://www.javacreed.com/swing-worker-example/

      mainPanel.udpateIVChart(chunks.get(chunks.size() - 1)[0], chunks.get(chunks.size() - 1)[1], chunks.get(chunks.size() - 1)[2], model.getFrequency(), model.getAmplitude());

      long duration = System.currentTimeMillis() - start;
      // System.out.println("duration" + duration);

      // System.out.println("capturedData: " + Arrays.toString(captureAmplitudeData));
      // swingPropertyChangeSupport.firePropertyChange(Events.CAPTURE_UPDATE, true, false);
      try {
        Thread.sleep(40 - duration); // 40 ms ==> 25fps
      } catch (InterruptedException e) {
      }
    }
  }

  /**
   * These property change events are triggered in the model in the case where the underlying model is updated. Here, the controller can respond to those events and make sure the corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    // System.out.println("propName: " + propName);

    switch (propName) {

    case ExperimentControlModel.EVENT_WAVEFORM_UPDATE:

      if (true) { // TODO fix this when converting to new experiment abstraction

        // stop AD2 waveform 1 and stop AD2 capture on channel 1 and 2
        captureWorker.cancel(true);

        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // start AD2 waveform 1 and start AD2 capture on channel 1 and 2
        captureWorker = new QCCaptureWorker();
        captureWorker.execute();

        mainPanel.switch2IVChart();
      }
      break;

    default:
      break;
    }
  }

  @Override
  public ExperimentControlModel getControlModel() {

    return model;
  }

  @Override
  public ExperimentControlPanel getControlPanel() {

    return controlPanel;
  }

  @Override
  public SwingWorker getCaptureWorker() {

    return new QCCaptureWorker();
  }

  @Override
  public void doCreateAndShowGUI() {

  }

  @Override
  public ExperimentPlotPanel getPlotPanel() {

    return null;
  }
}
