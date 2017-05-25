package org.knowm.memristor.discovery.gui.mvc.experiments.synapse;

/**
 * Created by timmolter on 5/25/17.
 */
public class AHaHController {

  // Order ==> W2, W1, 2+, 1+
  // 00 None
  // 10 Y
  // 01 A
  // 11 B

  public enum Instruction {

    // TODO complete the entire instruction set
    FFLV(0b0001_0010_0000_0000),
    FF(0b0001_0010_0000_0000),
    RH(0b1110_0000_0000_0000),
    RL(0b1011_0000_0000_0000);
    // RH,
    // RU,
    // RL,
    // RZ;

    private final int bits;

    Instruction(int bits) {

      this.bits = bits;
    }

    public int getBits() {

      return bits;
    }
  }
}
