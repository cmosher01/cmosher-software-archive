/**
 * @(#)DarkenFilter.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

/**
 * DarkenFilter darkens the color of a pixel
 */
class DarkenFilter extends RGBImageFilter {
    public DarkenFilter() {

        canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
        DirectColorModel cm = (DirectColorModel) ColorModel.getRGBdefault();
        int alpha = cm.getAlpha(rgb);
        int red   = (int)(cm.getRed(rgb) * .6);      // bit 17~24
        int green = (int)(cm.getGreen(rgb) * .6);    // bit  9~16
        int blue  = (int)(cm.getBlue(rgb) * .6);     // bit  1~ 8

        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}

/**
 * ImageButton is a lightweight component
 */
public class ImageButton extends Component {
  Image image, grayimage;
  ImageFilter colorfilter = new DarkenFilter();
  int width, height;
  String label, name;
  private boolean clicked = false;   // true if the button is detented.
  private boolean entered = false;   // true if mouse enters the image area
  ActionListener actionListener = null;
  private static final String base = "ImageButton";
  private static int nameCounter = 0;

  private boolean enabled = true;

  /**
   * Constructs a ImageButton with the specified label.
   * @param label the label of the button
   */
  public ImageButton(String label, Image img) {

      this.label = label;
      this.image = img;
      this.name  = base + nameCounter++;
      super.setName(this.name);

      width  = image.getWidth(this);
      height = image.getHeight(this);
      grayimage = createImage(new FilteredImageSource(image.getSource(), colorfilter));
      enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }


  /**
   * gets the label
   * @see setLabel
   */
  public String getLabel() {
    return label;
  }


  /**
   * sets the label
   * @see getLabel
   */
  public void setLabel(String label) {
      this.label = label;
      invalidate();
      repaint();
  }


  public void setEnabled(boolean b) {
        enabled = b;
        if(!b) entered = false;
        repaint();
  }


  /**
   * paints the ImageButton
   */
  public void paint(Graphics g) {
      Color oldColor = g.getColor();

      if( !enabled ) {
        g.drawImage(grayimage, 2, 2, this);
        g.drawRect(1, 1, width, height);
        g.setColor(Color.white);
        g.drawLine(1, 1, width+1, 1);
        g.drawLine(1, 1, 1, height+1);
        g.setColor(oldColor);
        return;
      }


      if(enabled && clicked) {
        g.drawImage(image, 3, 3, this);
        g.drawRect(1, 1, width, height);
        g.drawLine(2, 2, width+2, 2);
        g.drawLine(2, 2, 2, height+2);

        g.setColor(Color.white);
        g.drawLine(width+1, 2, width+1, height+1);
        g.drawLine(2, height+1, width+1, height+1);
      }
      else {
        g.drawImage(image, 2, 2, this);
        g.drawRect(1, 1, width, height);
        g.setColor(Color.white);
        g.drawLine(1, 1, width+1, 1);
        g.drawLine(1, 1, 1, height+1);
      }

      if(enabled && entered) {
        g.setColor(Color.red);
        g.drawRect(0, 0, width+2, height+2);
      }

      g.setColor(oldColor);
  }

  /**
   * The preferred size of the button.
   */
  public Dimension getPreferredSize() {
    return new Dimension(width + 4, height + 4);
  }

  /**
   * The minimum size of the button.
   */
  public Dimension getMinimumSize() {
    return new Dimension(width + 4, height + 4);
  }


  /**
   * Returns the parameter String of this button.
   */
  protected String paramString() {
    return super.paramString() + ",label=" + label;
  }

  /**
   * Determine if mouse was inside button.
   */
   public boolean contains(int x, int y) {

       int mx = getSize().width;
       int my = getSize().height;

       //System.out.println(mx + ":" + my+":" + x + ":" + y);
       return (x>=0 && mx>x && y>=0 && my>y);
   }


  public void addActionListener(ActionListener l) {
        actionListener = AWTEventMulticaster.add(actionListener, l);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  public void removeActionListener(ActionListener l) {
        actionListener = AWTEventMulticaster.remove(actionListener, l);
  }


   /**
    * Paints the button and distribute an action event to all listeners.
    */
   public void processMouseEvent(MouseEvent e) {

       if(!enabled) {
            super.processMouseEvent(e);
            return;
       }

       switch(e.getID()) {
          case MouseEvent.MOUSE_PRESSED:
            //System.out.println("mouse pressed");
            // render myself inverted....
            clicked = true;
            repaint();
            break;
          case MouseEvent.MOUSE_RELEASED:
            //System.out.println("mouse released");
            // render myself normal again

            if(clicked) {
                if( entered  && actionListener != null) {
                    actionListener.actionPerformed(
                            new ActionEvent(this,
                                            ActionEvent.ACTION_PERFORMED,
                                            label));
                }
                clicked = false;
                // Repaint might flicker a bit. To avoid this, you can use
                // double buffering (see the Gauge example).
                repaint();
            }
            break;
          case MouseEvent.MOUSE_ENTERED:
            //System.out.println(e.getX() + ":" + e.getY() + ":" + getBounds());
            entered = true;
            repaint();
            break;
          case MouseEvent.MOUSE_EXITED:
            //System.out.println("mouse exited");
            entered = false;
            repaint();
            break;
       }

       super.processMouseEvent(e);

   }
}