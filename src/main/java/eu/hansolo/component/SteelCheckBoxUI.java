package eu.hansolo.component;

/** @author hansolo */
public class SteelCheckBoxUI extends javax.swing.plaf.basic.BasicCheckBoxUI
    implements java.beans.PropertyChangeListener,
        java.awt.event.ComponentListener,
        java.awt.event.MouseListener {

  private static final java.awt.Dimension SIZE = new java.awt.Dimension(26, 13);
  private final SteelCheckBox CHECKBOX;
  private final float[] FOREGROUND_FRACTIONS = {0.0f, 0.03f, 0.94f, 1.0f};
  private boolean mouseOver = false;
  private boolean mousePressed = false;
  private java.awt.image.BufferedImage backgroundImage = create_BACKGROUND_Image(SIZE.width);
  private java.awt.image.BufferedImage knobStandardImage = create_KNOB_Image(SIZE.height, false);
  private java.awt.image.BufferedImage knobPressedImage = create_KNOB_Image(SIZE.height, true);
  private java.awt.Point pos = new java.awt.Point(0, 0);
  private java.awt.geom.RoundRectangle2D foreground;
  private java.awt.geom.Point2D foregroundStart;
  private java.awt.geom.Point2D foregroundStop;
  private java.awt.Color[] foregroundColors = {
    new java.awt.Color(241, 242, 242, 255),
    new java.awt.Color(224, 225, 226, 255),
    new java.awt.Color(166, 169, 171, 255),
    new java.awt.Color(124, 124, 124, 255)
  };
  private java.awt.LinearGradientPaint foregroundGradient;

  /**
   * Constructor
   *
   * @param CHECKBOX
   */
  public SteelCheckBoxUI(final SteelCheckBox CHECKBOX) {

    this.CHECKBOX = CHECKBOX;
    this.CHECKBOX.addComponentListener(this);
    this.CHECKBOX.addMouseListener(this);
    this.CHECKBOX.addPropertyChangeListener(this);
    init();
  }

  private void init() {

    foreground =
        new java.awt.geom.RoundRectangle2D.Double(
            pos.x + backgroundImage.getWidth() * 0.03846153989434242,
            pos.y + backgroundImage.getHeight() * 0.0714285746216774,
            backgroundImage.getWidth() * 0.923076868057251,
            backgroundImage.getHeight() * 0.8571428060531616,
            backgroundImage.getHeight() * 0.8571428571,
            backgroundImage.getHeight() * 0.8571428571);
    foregroundStart = new java.awt.geom.Point2D.Double(pos.x, foreground.getBounds2D().getMinY());
    foregroundStop = new java.awt.geom.Point2D.Double(pos.x, foreground.getBounds2D().getMaxY());
    foregroundGradient =
        new java.awt.LinearGradientPaint(
            foregroundStart, foregroundStop, FOREGROUND_FRACTIONS, foregroundColors);
  }

  @Override
  public void installUI(final javax.swing.JComponent COMPONENT) {

    super.installUI(COMPONENT);
    this.CHECKBOX.addComponentListener(this);
  }

  @Override
  public void uninstallUI(final javax.swing.JComponent COMPONENT) {

    super.uninstallUI(COMPONENT);
    this.CHECKBOX.removeComponentListener(this);
  }

  @Override
  public synchronized void paint(java.awt.Graphics g, javax.swing.JComponent component) {

    final java.awt.Graphics2D G2 = (java.awt.Graphics2D) g.create();

    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
    pos.setLocation(0, (this.CHECKBOX.getPreferredSize().height - SIZE.height) / 2.0);

    if (!CHECKBOX.isEnabled()) {
      G2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.5f));
    }

    // Draw the background
    G2.drawImage(backgroundImage, pos.x, pos.y, null);

    // Draw the foreground and knob
    if (CHECKBOX.isSelected()) {
      if (CHECKBOX.isColored()) {
        if (CHECKBOX.isRised()) {
          foregroundColors =
              new java.awt.Color[] {
                CHECKBOX.getSelectedColor().LIGHT.brighter(),
                CHECKBOX.getSelectedColor().LIGHT,
                CHECKBOX.getSelectedColor().MEDIUM,
                CHECKBOX.getSelectedColor().DARK
              };
        } else {
          foregroundColors =
              new java.awt.Color[] {
                CHECKBOX.getSelectedColor().DARK,
                CHECKBOX.getSelectedColor().DARK,
                CHECKBOX.getSelectedColor().LIGHT,
                CHECKBOX.getSelectedColor().MEDIUM
              };
        }
      } else {
        foregroundColors =
            new java.awt.Color[] {
              new java.awt.Color(241, 242, 242, 255),
              new java.awt.Color(224, 225, 226, 255),
              new java.awt.Color(166, 169, 171, 255),
              new java.awt.Color(124, 124, 124, 255)
            };
      }
      foregroundGradient =
          new java.awt.LinearGradientPaint(
              foregroundStart, foregroundStop, FOREGROUND_FRACTIONS, foregroundColors);
      G2.setPaint(foregroundGradient);
      G2.fill(foreground);
      if (mouseOver && mousePressed) {
        G2.drawImage(knobPressedImage, pos.x + backgroundImage.getWidth() / 2, pos.y, null);
      } else {
        G2.drawImage(knobStandardImage, pos.x + backgroundImage.getWidth() / 2, pos.y, null);
      }
    } else {
      if (mouseOver && mousePressed) {
        G2.drawImage(knobPressedImage, pos.x, pos.y, null);
      } else {
        G2.drawImage(knobStandardImage, pos.x, pos.y, null);
      }
    }

    G2.setColor(CHECKBOX.getForeground());
    G2.setFont(CHECKBOX.getFont());
    final java.awt.font.FontRenderContext RENDER_CONTEXT =
        new java.awt.font.FontRenderContext(null, true, true);
    final java.awt.font.TextLayout TEXT_LAYOUT =
        new java.awt.font.TextLayout(CHECKBOX.getText(), G2.getFont(), RENDER_CONTEXT);
    final java.awt.geom.Rectangle2D BOUNDS = TEXT_LAYOUT.getBounds();
    G2.drawString(
        CHECKBOX.getText(),
        backgroundImage.getWidth() + 5,
        (CHECKBOX.getBounds().height - BOUNDS.getBounds().height) / 2 + BOUNDS.getBounds().height);

    G2.dispose();
  }

  private java.awt.image.BufferedImage create_KNOB_Image(final int WIDTH, final boolean PRESSED) {

    if (WIDTH <= 0) {
      return null;
    }

    final java.awt.GraphicsConfiguration GFX_CONF =
        java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();
    final java.awt.image.BufferedImage IMAGE =
        GFX_CONF.createCompatibleImage(WIDTH, WIDTH, java.awt.Transparency.TRANSLUCENT);
    final java.awt.Graphics2D G2 = IMAGE.createGraphics();
    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

    final int IMAGE_WIDTH = IMAGE.getWidth();
    final int IMAGE_HEIGHT = IMAGE.getHeight();

    final java.awt.geom.Ellipse2D KNOB_FRAME =
        new java.awt.geom.Ellipse2D.Double(
            IMAGE_WIDTH * 0.0, IMAGE_HEIGHT * 0.0, IMAGE_WIDTH * 1.0, IMAGE_HEIGHT * 1.0);
    final java.awt.geom.Point2D KNOB_FRAME_START =
        new java.awt.geom.Point2D.Double(0, KNOB_FRAME.getBounds2D().getMinY());
    final java.awt.geom.Point2D KNOB_FRAME_STOP =
        new java.awt.geom.Point2D.Double(0, KNOB_FRAME.getBounds2D().getMaxY());
    final float[] E_KNOB_FRAME_FRACTIONS = {0.0f, 0.25f, 0.51f, 0.76f, 1.0f};
    final java.awt.Color[] KNOB_FRAME_COLORS = {
      new java.awt.Color(90, 91, 92, 255),
      new java.awt.Color(127, 127, 128, 255),
      new java.awt.Color(81, 82, 83, 255),
      new java.awt.Color(104, 105, 105, 255),
      new java.awt.Color(63, 64, 65, 255)
    };
    if (KNOB_FRAME_START.distance(KNOB_FRAME_STOP) > 0) {
      final java.awt.LinearGradientPaint KNOB_FRAME_GRADIENT =
          new java.awt.LinearGradientPaint(
              KNOB_FRAME_START, KNOB_FRAME_STOP, E_KNOB_FRAME_FRACTIONS, KNOB_FRAME_COLORS);
      G2.setPaint(KNOB_FRAME_GRADIENT);
      G2.fill(KNOB_FRAME);
    }

    final java.awt.geom.Ellipse2D KNOB =
        new java.awt.geom.Ellipse2D.Double(
            IMAGE_WIDTH * 0.07692307978868484,
            IMAGE_HEIGHT * 0.07692307978868484,
            IMAGE_WIDTH * 0.8461538553237915,
            IMAGE_HEIGHT * 0.8461538553237915);
    final java.awt.geom.Point2D KNOB_CENTER =
        new java.awt.geom.Point2D.Double(KNOB.getCenterX(), KNOB.getCenterY());
    final float[] KNOB_FRACTIONS = {0.0f, 40.0f, 90.0f, 140.0f, 220.0f, 270.0f, 320.0f};
    final java.awt.Color[] KNOB_COLORS;

    if (PRESSED) {
      KNOB_COLORS =
          new java.awt.Color[] {
            new java.awt.Color(0xC2C2C2),
            new java.awt.Color(0x727678),
            new java.awt.Color(0xC2C2C2),
            new java.awt.Color(0x727678),
            new java.awt.Color(0xC2C2C2),
            new java.awt.Color(0x727678),
            new java.awt.Color(0xC2C2C2)
          };
    } else {
      KNOB_COLORS =
          new java.awt.Color[] {
            new java.awt.Color(0xF2F2F2),
            new java.awt.Color(0x8F9396),
            new java.awt.Color(0xF2F2F2),
            new java.awt.Color(0x8F9396),
            new java.awt.Color(0xF2F2F2),
            new java.awt.Color(0x8F9396),
            new java.awt.Color(0xF2F2F2)
          };
    }
    final eu.hansolo.component.ConicalGradientPaint KNOB_GRADIENT =
        new eu.hansolo.component.ConicalGradientPaint(
            true, KNOB_CENTER, 0f, KNOB_FRACTIONS, KNOB_COLORS);
    G2.setPaint(KNOB_GRADIENT);
    G2.fill(KNOB);

    G2.dispose();

    return IMAGE;
  }

  private java.awt.image.BufferedImage create_BACKGROUND_Image(final int WIDTH) {

    if (WIDTH <= 0) {
      return null;
    }

    final java.awt.GraphicsConfiguration GFX_CONF =
        java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();
    final java.awt.image.BufferedImage IMAGE =
        GFX_CONF.createCompatibleImage(
            WIDTH, (int) (0.5384615384615384 * WIDTH), java.awt.Transparency.TRANSLUCENT);
    final java.awt.Graphics2D G2 = IMAGE.createGraphics();
    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_DITHERING, java.awt.RenderingHints.VALUE_DITHER_ENABLE);
    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION,
        java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_COLOR_RENDERING,
        java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    G2.setRenderingHint(
        java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
    // G2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
    // java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    final int IMAGE_WIDTH = IMAGE.getWidth();
    final int IMAGE_HEIGHT = IMAGE.getHeight();

    final java.awt.geom.RoundRectangle2D BACKGROUND_FRAME =
        new java.awt.geom.RoundRectangle2D.Double(
            IMAGE_WIDTH * 0.0,
            IMAGE_HEIGHT * 0.0,
            IMAGE_WIDTH * 1.0,
            IMAGE_HEIGHT * 1.0,
            IMAGE_HEIGHT,
            IMAGE_HEIGHT);
    final java.awt.geom.Point2D BACKGROUND_FRAME_START =
        new java.awt.geom.Point2D.Double(0, BACKGROUND_FRAME.getBounds2D().getMinY());
    final java.awt.geom.Point2D BACKGROUND_FRAME_STOP =
        new java.awt.geom.Point2D.Double(0, BACKGROUND_FRAME.getBounds2D().getMaxY());
    final float[] BACKGROUND_FRAME_FRACTIONS = {0.0f, 0.51f, 1.0f};
    final java.awt.Color[] BACKGROUND_FRAME_COLORS = {
      new java.awt.Color(68, 68, 68, 255),
      new java.awt.Color(105, 105, 106, 255),
      new java.awt.Color(216, 217, 218, 255)
    };
    final java.awt.LinearGradientPaint BACKGROUND_FRAME_GRADIENT =
        new java.awt.LinearGradientPaint(
            BACKGROUND_FRAME_START,
            BACKGROUND_FRAME_STOP,
            BACKGROUND_FRAME_FRACTIONS,
            BACKGROUND_FRAME_COLORS);
    G2.setPaint(BACKGROUND_FRAME_GRADIENT);
    G2.fill(BACKGROUND_FRAME);

    final java.awt.geom.RoundRectangle2D BACKGROUND =
        new java.awt.geom.RoundRectangle2D.Double(
            IMAGE_WIDTH * 0.03846153989434242,
            IMAGE_HEIGHT * 0.0714285746216774,
            IMAGE_WIDTH * 0.923076868057251,
            IMAGE_HEIGHT * 0.8571428060531616,
            IMAGE_HEIGHT,
            IMAGE_HEIGHT);
    final java.awt.geom.Point2D BACKGROUND_START =
        new java.awt.geom.Point2D.Double(0, BACKGROUND.getBounds2D().getMinY());
    final java.awt.geom.Point2D BACKGROUND_STOP =
        new java.awt.geom.Point2D.Double(0, BACKGROUND.getBounds2D().getMaxY());
    final float[] BACKGROUND_FRACTIONS = {0.0f, 0.96f, 1.0f};
    final java.awt.Color[] BACKGROUND_COLORS = {
      new java.awt.Color(91, 91, 91, 255),
      new java.awt.Color(138, 138, 138, 255),
      new java.awt.Color(124, 124, 124, 255)
    };
    if (BACKGROUND_START.distance(BACKGROUND_STOP) > 0) {
      final java.awt.LinearGradientPaint BACKGROUND_GRADIENT =
          new java.awt.LinearGradientPaint(
              BACKGROUND_START, BACKGROUND_STOP, BACKGROUND_FRACTIONS, BACKGROUND_COLORS);
      G2.setPaint(BACKGROUND_GRADIENT);
      G2.fill(BACKGROUND);
    }

    G2.dispose();

    return IMAGE;
  }

  public java.awt.geom.Point2D getCenteredTextPosition(
      final java.awt.Graphics2D G2,
      final java.awt.geom.Rectangle2D BOUNDARY,
      final java.awt.Font FONT,
      final String TEXT,
      final int ORIENTATION) {

    // Get the visual center of the component.
    final double CENTER_X = BOUNDARY.getWidth() / 2.0;
    final double CENTER_Y = BOUNDARY.getHeight() / 2.0;

    // Get the text boundary
    final java.awt.font.FontRenderContext RENDER_CONTEXT = G2.getFontRenderContext();
    final java.awt.font.TextLayout LAYOUT =
        new java.awt.font.TextLayout(TEXT, FONT, RENDER_CONTEXT);
    final java.awt.geom.Rectangle2D TEXT_BOUNDARY = LAYOUT.getBounds();

    // Calculate the text position
    final double TEXT_X;
    final double TEXT_Y;
    switch (ORIENTATION) {
      case javax.swing.SwingConstants.CENTER:
        TEXT_X = CENTER_X - TEXT_BOUNDARY.getWidth() / 2.0;
        TEXT_Y = CENTER_Y - TEXT_BOUNDARY.getHeight() / 2.0 + TEXT_BOUNDARY.getHeight();
        break;

      case javax.swing.SwingConstants.LEFT:
        TEXT_X = BOUNDARY.getMinX();
        TEXT_Y = CENTER_Y - TEXT_BOUNDARY.getHeight() / 2.0 + TEXT_BOUNDARY.getHeight();
        break;

      case javax.swing.SwingConstants.RIGHT:
        TEXT_X = BOUNDARY.getMaxX() - TEXT_BOUNDARY.getWidth();
        TEXT_Y = CENTER_Y - TEXT_BOUNDARY.getHeight() / 2.0 + TEXT_BOUNDARY.getHeight();
        break;

      default:
        TEXT_X = CENTER_X - TEXT_BOUNDARY.getWidth() / 2.0;
        TEXT_Y = CENTER_Y - TEXT_BOUNDARY.getHeight() / 2.0 + TEXT_BOUNDARY.getHeight();
        break;
    }

    return new java.awt.geom.Point2D.Double(TEXT_X, TEXT_Y);
  }

  @Override
  public void mouseClicked(java.awt.event.MouseEvent event) {

    mousePressed = false;
    CHECKBOX.repaint();
  }

  @Override
  public void mousePressed(java.awt.event.MouseEvent event) {

    mousePressed = true;
    CHECKBOX.repaint();
  }

  @Override
  public void mouseReleased(java.awt.event.MouseEvent event) {

    mousePressed = false;
    CHECKBOX.repaint();
  }

  @Override
  public void mouseEntered(java.awt.event.MouseEvent event) {

    mouseOver = true;
    CHECKBOX.repaint();
  }

  @Override
  public void mouseExited(java.awt.event.MouseEvent event) {

    mouseOver = false;
    CHECKBOX.repaint();
  }

  @Override
  public void componentResized(final java.awt.event.ComponentEvent EVENT) {

    init();
    pos.setLocation(0, (EVENT.getComponent().getHeight() - SIZE.height) / 2.0);
  }

  @Override
  public void componentMoved(final java.awt.event.ComponentEvent EVENT) {}

  @Override
  public void componentShown(final java.awt.event.ComponentEvent EVENT) {}

  @Override
  public void componentHidden(final java.awt.event.ComponentEvent EVENT) {}

  @Override
  public void propertyChange(final java.beans.PropertyChangeEvent EVENT) {

    init();
    CHECKBOX.repaint();
  }

  @Override
  public String toString() {

    return "SteelCheckBoxUI";
  }
}
