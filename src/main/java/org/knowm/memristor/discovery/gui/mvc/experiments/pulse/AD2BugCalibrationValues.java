package org.knowm.memristor.discovery.gui.mvc.experiments.pulse;

public class AD2BugCalibrationValues {


  private float readPulseZeroOffset = 0.0f;
  private float readPulseOffset = 0.0f;
  private float readPulseVoltage = 0.0f;
  private float readPulseInitialVoltage = 0.0f;

  public float getReadPulseZeroOffset() {
    return readPulseZeroOffset;
  }

  public void setReadPulseZeroOffset(float readPulseZeroOffset) {
    this.readPulseZeroOffset = readPulseZeroOffset;
  }

  public float getReadPulseOffset() {
    return readPulseOffset;
  }

  public void setReadPulseOffset(float readPulseOffset) {
    this.readPulseOffset = readPulseOffset;
  }

  public float getReadPulseVoltage() {
    return readPulseVoltage;
  }

  public void setReadPulseVoltage(float readPulseVoltage) {
    this.readPulseVoltage = readPulseVoltage;
  }

  public float getReadPulseInitialVoltage() {
    return readPulseInitialVoltage;
  }

  public void setReadPulseInitialVoltage(float readPulseInitialVoltage) {
    this.readPulseInitialVoltage = readPulseInitialVoltage;
  }

  @Override
  public String toString() {
    return "AD2BugCalibrationValues{" +
        "readPulseZeroOffset=" + readPulseZeroOffset +
        ", readPulseOffset=" + readPulseOffset +
        ", readPulseVoltage=" + readPulseVoltage +
        ", readPulseInitialVoltage=" + readPulseInitialVoltage +
        '}';
  }
}
