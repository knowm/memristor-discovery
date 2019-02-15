package eu.hansolo.component;

/** @author hansolo */
public class SteelCheckBox extends javax.swing.JCheckBox {

  private final int id;

  private boolean colored = false;
  private boolean rised = false;
  private eu.hansolo.component.ColorDef selectedColor = eu.hansolo.component.ColorDef.JUG_GREEN;
  protected static final String COLORED_PROPERTY = "colored";
  protected static final String COLOR_PROPERTY = "color";
  protected static final String RISED_PROPERTY = "rised";

  /** Constructor */
  public SteelCheckBox(int id) {

    this("" + id, id);
  }

  /**
   * Constructor
   *
   * @param label
   * @param id
   */
  public SteelCheckBox(String label, int id) {

    this(label, id, 50, 26);
  }

  /**
   * Constructor
   *
   * @param label
   * @param id
   * @param width
   * @param height
   */
  public SteelCheckBox(String label, int id, int width, int height) {

    super(label);
    this.id = id;
    setPreferredSize(new java.awt.Dimension(width, height));
  }

  public int getId() {

    return id;
  }

  public boolean isColored() {

    return this.colored;
  }

  public void setColored(final boolean COLORED) {

    final boolean OLD_STATE = this.colored;
    this.colored = COLORED;
    firePropertyChange(COLORED_PROPERTY, OLD_STATE, COLORED);
    repaint();
  }

  public boolean isRised() {

    return this.rised;
  }

  public void setRised(final boolean RISED) {

    final boolean OLD_VALUE = this.rised;
    this.rised = RISED;
    firePropertyChange(RISED_PROPERTY, OLD_VALUE, RISED);
  }

  public eu.hansolo.component.ColorDef getSelectedColor() {

    return this.selectedColor;
  }

  public void setSelectedColor(final eu.hansolo.component.ColorDef SELECTED_COLOR) {

    final eu.hansolo.component.ColorDef OLD_COLOR = this.selectedColor;
    this.selectedColor = SELECTED_COLOR;
    firePropertyChange(COLOR_PROPERTY, OLD_COLOR, SELECTED_COLOR);
    repaint();
  }

  @Override
  public void setUI(final javax.swing.plaf.ButtonUI BUI) {

    super.setUI(new SteelCheckBoxUI(this));
  }

  public void setUi(final javax.swing.plaf.ComponentUI UI) {

    this.ui = new SteelCheckBoxUI(this);
  }

  @Override
  protected void setUI(final javax.swing.plaf.ComponentUI UI) {

    super.setUI(new SteelCheckBoxUI(this));
  }

  @Override
  public String toString() {

    return "SteelCheckBox";
  }
}
