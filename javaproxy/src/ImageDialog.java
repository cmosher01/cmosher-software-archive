/**
 * @(#)ImageDialog.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.*;
import java.awt.event.*;

/**
 * ImageDialog shows image as the background of a dialog box
 */
class ImageDialog extends Dialog {
    Image image = null;
    String message = null;

    /**
     * constructor
     */
    ImageDialog(Frame p, String title) {
        super(p, title, true);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }

    /**
     * constructor
     */
    ImageDialog(Frame p, String title, Image img, String msg) {
        this(p, title);
        this.image   = img;
        this.message = msg;
    }

    /**
     * set image
     */
    public void setImage(Image img) {
        this.image = img;
    }

    /**
     * set message
     */
    public void setMessage(String msg) {
        this.message = msg;
    }

    /**
     * paint
     */
    public void paint(Graphics g) {
        Insets in = insets();
        Dimension d = size();
        FontMetrics fm = g.getFontMetrics();
        int imgsx = 10;
        int imgsy =  0;
        int msgsx = 10;
        int msgsy = (int) ((d.height - in.top)/2) + in.top;

        if( image != null ) {
            int img_width = image.getWidth(this) ;
            int img_height= image.getHeight(this);

            imgsy = msgsy - (int)(img_height/2);
            msgsx += img_width;
            if(imgsy <= 0) return;
            g.drawImage(image, imgsx, imgsy, this);
        }
        msgsy += fm.getAscent()/2;
        g.drawString(message, msgsx, msgsy);
    }

    /**
     * event handler
     */
    public void processWindowEvent(WindowEvent winevt) {
        if( winevt.getID() == Event.WINDOW_DESTROY ) this.dispose();
    }

}