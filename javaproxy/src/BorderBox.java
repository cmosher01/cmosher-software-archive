/**
 * @(#)BorderBox.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.Component;
import java.awt.Rectangle;



/**
 * BorderBox is an object that sets the border properties in the configuration
 * window
 */
class BorderBox extends Component
{
    public String title;

    public Rectangle border;



    /**
     * constructor
     */
    public BorderBox(String t, int x, int y, int w, int h)
    {
        title = t;
        border = new Rectangle(x,y,w,h);
    }

    /**
     * constructor
     */
    public BorderBox(String t, Rectangle b)
    {
        title = t;
        border = b;
    }

    /**
     * set title
     */
    public void setTitle(String t)
    {
        title = t;
    }

    /**
     * set border
     */
    public void setBorder(Rectangle b)
    {
        border = b;
    }

    /**
     * set title and border
     */
    public void setTitleBorder(String t, Rectangle b)
    {
        title = t;
        border = b;
    }
}