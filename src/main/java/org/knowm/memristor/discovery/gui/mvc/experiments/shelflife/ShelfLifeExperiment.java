/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3 and is also
 * available under alternative licenses negotiated directly with Knowm, Inc.
 *
 * <p>Copyright (c) 2016-2019 Knowm Inc. www.knowm.org
 *
 * <p>This package also includes various components that are not part of Memristor-Discovery itself:
 *
 * <p>* `Multibit`: Copyright 2011 multibit.org, MIT License * `SteelCheckBox`: Copyright 2012
 * Gerrit, BSD license
 *
 * <p>Knowm, Inc. holds copyright and/or sufficient licenses to all components of the
 * Memristor-Discovery package, and therefore can grant, at its sole discretion, the ability for
 * companies, individuals, or organizations to create proprietary or open source (even if not GPL)
 * modules which may be dynamically linked at runtime with the portions of Memristor-Discovery which
 * fall under our copyright/license umbrella, or are distributed under more flexible licenses than
 * GPL.
 *
 * <p>The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * <p>If you have any questions regarding our licensing policy, please contact us at
 * `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.gui.mvc.experiments.shelflife;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.core.PostProcessDataUtils;
import org.knowm.memristor.discovery.core.Util;
import org.knowm.memristor.discovery.core.PostProcessDataUtils.MemristorTestResult;
import org.knowm.memristor.discovery.core.experiment_common.PulseUtility;
import org.knowm.memristor.discovery.core.gpio.MuxController;
import org.knowm.memristor.discovery.gui.mvc.experiments.ControlView;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences;
import org.knowm.memristor.discovery.gui.mvc.experiments.Model;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPreferences.Waveform;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.result.ResultController;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.result.ResultModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.shelflife.result.ResultPanel;
import java.util.concurrent.TimeUnit;

public class ShelfLifeExperiment extends Experiment {

  private static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private String cSVFileName;

  // Control and Result MVC
  private final ControlModel controlModel;
  private final ControlPanel controlPanel;
  private final ResultPanel resultPanel;
  private final ResultModel resultModel;
  private final ResultController resultController;

  // SwingWorkers
  private SwingWorker experimentCaptureWorker;

  private MuxController muxController;
  private PulseUtility pulseUtility;

  private static final float VOLTAGE_READ_NOISE_FLOOR = .001f;//if the measured voltage across series resistor is less than this, you are getting noisy. 

  private DecimalFormat kOhmFormat = new DecimalFormat("0.00");

  /** Constructor */
  public ShelfLifeExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlModel = new ControlModel();
    controlPanel = new ControlPanel();
    resultModel = new ResultModel();
    resultPanel = new ResultPanel();

    refreshModelsFromPreferences();
    new ControlController(controlPanel, controlModel, dwfProxy);
    resultController = new ResultController(resultPanel, resultModel);

    muxController = new MuxController();
    pulseUtility = new PulseUtility(controlModel, dwfProxy, muxController, VOLTAGE_READ_NOISE_FLOOR);

  }

  @Override
  public void doCreateAndShowGUI() {
  }

  /*
   * Here action listeners are attached to the widgets in the control panel and mapped to a worker, also defined here in the experiment.
   */
  @Override
  public void addWorkersToButtonEvents() {

    controlPanel.getStartStopButton().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (!controlModel.isStartToggled()) {

          controlModel.setStartToggled(true);
          controlPanel.getStartStopButton().setText("Stop");

          // clear the console
          resultController.clear();

          // start capture
          experimentCaptureWorker = new CaptureWorker();
          experimentCaptureWorker.execute();
        } else {

          controlModel.setStartToggled(false);
          controlPanel.getStartStopButton().setText("Start");

          // cancel the worker
          experimentCaptureWorker.cancel(true);
        }
      }
    });
  }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can
   * respond to those events and make sure the corresponding GUI components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
  }

  @Override
  public Model getControlModel() {

    return controlModel;
  }

  @Override
  public ControlView getControlPanel() {

    return controlPanel;
  }

  @Override
  public Model getResultModel() {
    return resultModel;
  }

  @Override
  public JPanel getResultPanel() {

    return resultPanel;
  }

  @Override
  public ExperimentPreferences initAppPreferences() {

    return new ShelfLifePreferences();
  }

  private class CaptureWorker extends SwingWorker<Boolean, double[][]> {

    @Override
    protected Boolean doInBackground() throws Exception {

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm a z");
      String timeString = dateFormat.format(new Date());

      String dataFileName = "MD_Shelf_Life_Data_" + timeString + ".csv";
      String infoFileName = "MD_Shelf_Life_Info_" + timeString + ".csv";

      //cycle devices in case they have not yet been formed
      float V_READ = controlModel.getReadVoltageAmplitude();
      float V_WRITE = controlModel.getWriteVoltageAmplitude();
      float V_ERASE = controlModel.getEraseVoltageAmplitude();
      int PULSE_WIDTH_READ = controlModel.getReadPulseWidthInMicroSeconds();
      int PULSE_WIDTH_WRITE = controlModel.getWritePulseWidthInMicroSeconds();
      int PULSE_WIDTH_ERASE = controlModel.getErasePulseWidthInMicroSeconds();
      int seriesResistor = controlModel.getSeriesResistance();
      TimeUnit timeUnit = controlModel.getTimeUnit();
      int repeatInterval = controlModel.getRepeatInterval();

      float maxWriteResistance = controlModel.getMaxWriteResistance();
      float minEraseResistance = controlModel.getMinEraseResistance();

      //information file-->

      String saveInfoFilePath = controlModel.getSaveDirectory() + "/" + infoFileName;
      try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(saveInfoFilePath, true)))) {

        printWriter.println("Memristor Discovery Shelf Life Experiment");
        printWriter.println("");
        printWriter.println("Memristor Discovery Version: " + Util.getVersionNumber());
        printWriter.println("");
        printWriter.println("EXPERIMENT INFO");
        printWriter.println(" DataFile: " + dataFileName);
        printWriter.println(" Start Date : " + timeString);
        printWriter.println(" Series Resistor : " + seriesResistor + "Ω");
        printWriter.println(" Measurment Interval : " + repeatInterval + " " + timeUnit);
        printWriter.println(" Read Voltage : " + V_READ + "V");
        printWriter.println(" Write Voltage : " + V_WRITE + "V");
        printWriter.println(" Erase Voltage : " + V_ERASE + "V");
        printWriter.println(" Read Pulse Width : " + PULSE_WIDTH_READ + "μs");
        printWriter.println(" Write Pulse Width : " + PULSE_WIDTH_WRITE + "μs");
        printWriter.println(" Erase Pulse Width : " + PULSE_WIDTH_ERASE + "μs");
        printWriter.println(" Max Write Resistance : " + maxWriteResistance + "kΩ");
        printWriter.println(" Min Erase Resistance : " + minEraseResistance + "kΩ");
        printWriter.println("");
        printWriter.println("SYSTEM INFO");
        printWriter.println(" java.home: " + System.getProperty("java.home"));
        printWriter.println(" java.vendor: " + System.getProperty("java.vendor"));
        printWriter.println(" java.version: " + System.getProperty("java.version"));
        printWriter.println(" os.name: " + System.getProperty("os.name"));
        printWriter.println(" os.version: " + System.getProperty("os.version"));
        printWriter.println(" user.name: " + System.getProperty("user.name"));

        printWriter.flush();

      }

      String saveDataFilePath = controlModel.getSaveDirectory() + "/" + dataFileName;

      try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(saveDataFilePath, true)))) {

        dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

        //CVS FILE HEADERS--->
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Time");

        for (int i = 1; i < 9; i++) {
          csvBuilder.append(",");
          csvBuilder.append("M" + i + "_E0");
          csvBuilder.append(",");
          csvBuilder.append("M" + i + "_W0");
          csvBuilder.append(",");
          csvBuilder.append("M" + i + "_E1");
        }

        String csvString = csvBuilder.toString();
        resultController.addNewLine(csvString);
        printWriter.println(csvString);
        printWriter.flush();

        while (!isCancelled()) {

          float[][] reads = pulseUtility.testMeminline(V_WRITE, V_ERASE, V_READ, PULSE_WIDTH_READ, PULSE_WIDTH_WRITE, PULSE_WIDTH_ERASE);

          csvBuilder = new StringBuilder();
          csvBuilder.append(dateFormat.format(new Date()));

          for (int i = 1; i < 9; i++) {
            csvBuilder.append(",");
            csvBuilder.append(formatResistance(reads[0][i]));
            csvBuilder.append(",");
            csvBuilder.append(formatResistance(reads[1][i]));
            csvBuilder.append(",");
            csvBuilder.append(formatResistance(reads[2][i]));
          }

          csvString = csvBuilder.toString();

          // CSV to console
          resultController.addNewLine(csvString);

          MemristorTestResult[] result = PostProcessDataUtils.categorizeMemristorTestReads(reads, minEraseResistance, maxWriteResistance, 1000f);

          resultController.addNewLine(Arrays.toString(result));

          // CSV to file
          printWriter.println(csvString);
          printWriter.flush();

          Thread.sleep(controlModel.getTimeUnit().toMillis(controlModel.getRepeatInterval()));
        }
      }
      return true;
    }
  }

  private String formatResistance(float resistanceInKiloOhms) {

    if (resistanceInKiloOhms == Float.POSITIVE_INFINITY) {
      return "∞";
    } else {
      return kOhmFormat.format(resistanceInKiloOhms);
    }

  }

  private String getDateTimeString(Date value) {
    return dateTimeFormat.format(value);
  }
}
