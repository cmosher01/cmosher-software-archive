/**
 * @(#)ToolBarCaption.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE
 * SUITABILITY OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE
 * LIABLE FOR ANY DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR
 * OWN RISK. Author : Steve Yeong-Ching Hsueh
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;



/**
 * ToolBarCaption displays a caption window when mouse over a certain area
 */
class ToolBarCaption extends Window
{

    Color foreground = Color.black;

    Color background = Color.yellow;

    String caption;

    int captionx, captiony;



    /**
     * constructor
     */
    public ToolBarCaption(Frame p, String capt)
    {
        super(p);
        setCaption(capt);
    }

    /**
     * set caption
     */
    public void setCaption(String capt)
    {
        if (capt == null)
            caption = " ";
        else
            caption = capt;
    }

    /**
     * show this window
     */
    public void show()
    {
        super.show();
        repaint();
    }

    /**
     * paint
     */
    public void paint(Graphics g)
    {

        FontMetrics fm = g.getFontMetrics();
        int height = fm.getHeight() / 2 + 10;
        int width = fm.stringWidth(caption) + 10;
        this.setSize(new Dimension(width,height));
        captionx = 5;
        captiony = 5 + fm.getHeight() / 2;


        Dimension d = this.getSize();
        g.setColor(background);
        g.fillRect(0,0,d.width - 1,d.height - 1);
        g.setColor(foreground);
        g.drawRect(0,0,d.width - 1,d.height - 1);
        g.drawString(caption,captionx,captiony);
    }

}