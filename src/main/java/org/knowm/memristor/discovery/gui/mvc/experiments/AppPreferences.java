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
package org.knowm.memristor.discovery.gui.mvc.experiments;

import java.util.prefs.Preferences;

public abstract class AppPreferences {

  protected Preferences preferences;

  /**
   * Constructor
   *
   * @param c
   */
  public AppPreferences(Class<?> c) {

    preferences = Preferences.userNodeForPackage(c);
  }

  public enum Waveform {

    Sine, Triangle, TriangleUpDown, Square, SquareUpDown, SawtoothUpDown, Sawtooth;
  }

  public enum CurrentUnits {

    Amps(1, "A"), Milliamps(1000, "mA"), MicroAmps(1_000_000, "µA");

    private final double divisor;
    private final String label;

    private CurrentUnits(double divisor, String label) {

      this.divisor = divisor;
      this.label = label;
    }

    public double getDivisor() {

      return divisor;
    }

    public String getLabel() {

      return label;
    }
  }

  public enum ResistanceUnits {

    Ohms(1, "Ω"), KiloOhms(1000, "kΩ"), MegaOhms(1_000_000, "mΩ");

    private final double divisor;
    private final String label;

    private ResistanceUnits(double divisor, String label) {

      this.divisor = divisor;
      this.label = label;
    }

    public double getDivisor() {

      return divisor;
    }

    public String getLabel() {

      return label;
    }
  }

  public enum ConductanceUnits {

    Siemens(1, "S"), MilliSiemens(1000, "mS"), MicroSiemens(1_000_000, "µS");

    private final double divisor;
    private final String label;

    private ConductanceUnits(double divisor, String label) {

      this.divisor = divisor;
      this.label = label;
    }

    public double getDivisor() {

      return divisor;
    }

    public String getLabel() {

      return label;
    }
  }
  public enum TimeUnits {

    Seconds(1, "s"), MilliSeconds(1000, "ms"), MicroSeconds(1_000_000, "µs");

    private final double divisor;
    private final String label;

    private TimeUnits(double divisor, String label) {

      this.divisor = divisor;
      this.label = label;
    }

    public double getDivisor() {

      return divisor;
    }

    public String getLabel() {

      return label;
    }
  }
  public void setString(String key, String value) {

    this.preferences.put(key, value.trim());
  }

  public void setInteger(String key, int value) {

    this.preferences.putInt(key, value);
  }

  public void setFloat(String key, float value) {

    this.preferences.putFloat(key, value);
  }

  public void setDouble(String key, double value) {

    this.preferences.putDouble(key, value);
  }

  public void setLong(String key, long value) {

    this.preferences.putLong(key, value);
  }

  public void setBoolean(String key, boolean value) {

    this.preferences.putBoolean(key, value);
  }

  public String getString(String key, String defaultValue) {

    return this.preferences.get(key, defaultValue);
  }

  public int getInteger(String key, int defaultValue) {

    return this.preferences.getInt(key, defaultValue);
  }

  public float getFloat(String key, float defaultValue) {

    return this.preferences.getFloat(key, defaultValue);
  }

  public double getDouble(String key, double defaultValue) {

    return this.preferences.getDouble(key, defaultValue);
  }

  public long getLong(String key, long defaultValue) {

    return this.preferences.getLong(key, defaultValue);
  }

  public boolean getBoolean(String key, boolean defaultValue) {

    return this.preferences.getBoolean(key, defaultValue);
  }
}
