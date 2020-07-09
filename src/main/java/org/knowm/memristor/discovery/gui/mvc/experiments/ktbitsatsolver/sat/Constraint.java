package org.knowm.memristor.discovery.gui.mvc.experiments.ktbitsatsolver.sat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Constraint {

  private int a;
  private int b;
  private int c;

  private boolean satisfied = false;

  public Constraint(String line) {
    String[] v = line.split(" ");
    a = Integer.parseInt(v[0]);
    b = Integer.parseInt(v[1]);
    c = Integer.parseInt(v[2]);
  }

  @Override
  public String toString() {
    return "C(" + a + "," + b + "," + c + ")";
  }

  public void loadMap(Map<Integer, List<Constraint>> map) {

    List<Constraint> l1 = map.get(Math.abs(a));
    if (l1 == null) {
      l1 = new ArrayList<Constraint>();
    }

    l1.add(this);
    map.put(Math.abs(a), l1);

    List<Constraint> l2 = map.get(Math.abs(b));
    if (l2 == null) {
      l2 = new ArrayList<Constraint>();
    }

    l2.add(this);
    map.put(Math.abs(b), l2);

    List<Constraint> l3 = map.get(Math.abs(c));
    if (l3 == null) {
      l3 = new ArrayList<Constraint>();
    }

    l3.add(this);
    map.put(Math.abs(c), l3);
  }

  public boolean isSatisfied() {
    return satisfied;
  }

  public void setSatisfied(float[] kTBits) {

    //    System.out.println("setSatisfied");
    //    System.out.println("(a,b,c) = " + a + "," + b + "," + c);

    // constraints are '1' indexed not '0' indexed...
    if (kTBits[Math.abs(a) - 1] * a > 0) {
      satisfied = true;
    } else if (kTBits[Math.abs(b) - 1] * b > 0) {
      satisfied = true;
    } else if (kTBits[Math.abs(c) - 1] * c > 0) {
      satisfied = true;
    } else {
      satisfied = false;
    }
  }
}
