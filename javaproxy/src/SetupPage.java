/**
 * @(#)SetupPage.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;



/**
 * SetupPage
 */
class SetupPage extends Panel
{
    public String PageTitle;

    private Vector broderboxslist = new Vector();



    /**
     * constructor
     */
    public SetupPage(String title)
    {
        super();
        setPageTitle(title);
    }

    /**
     * constructor
     */
    public SetupPage(LayoutManager mgr)
    {
        super(mgr);
    }

    /**
     * set page title
     */
    public void setPageTitle(String title)
    {
        PageTitle = title;
    }

    /**
     * add border box
     */
    public BorderBox add(BorderBox b)
    {
        broderboxslist.addElement(b);
        return b;
    }

    /**
     * add component
     */
    public Component add(Component c)
    {
        return super.add(c);
    }


    /**
     * draw 3d cap, a 3D cap is a square without a top (north)
     */
    public static void draw3DCap(Graphics g, int x, int y, int w, int h)
    {
        int iw, ih; // inner width and height
        Color org_color; // save original color

        if ((iw = w - 2) < 0)
            iw = 0;
        if ((ih = h - 1) < 0)
            ih = 0;

        // draw black line, no south for cap
        g.drawLine(x,y,x + iw,y); // north
        g.drawLine(x,y,x,y + ih); // west
        g.drawLine(x + iw,y + ih,x + iw,y); // east
        org_color = g.getColor();
        g.setColor(Color.white);

        // draw white shade, no south for cap
        g.drawLine(x + 1,y + 1,x + iw - 1,y + 1); // north
        g.drawLine(x + 1,y + 1,x + 1,y + ih - 1); // west
        g.drawLine(x + w,y,x + w,y + h); // east
        g.setColor(org_color);
    }

    /**
     * draw 3D cup, a 3D cup is a square without bottom(south)
     */
    public static void draw3DCup(Graphics g, int x, int y, int w, int h)
    {
        int iw, ih; // inner width and height
        Color org_color; // save original color

        if ((iw = w - 2) < 0)
            iw = 0;
        if ((ih = h - 1) < 0)
            ih = 0;

        // draw black line, no north for cap
        g.drawLine(x,y,x,y + ih); // west
        //if(x == 2 && y == 2 ) g.drawLine(x, y - 1, x, y + ih);
        g.drawLine(x,y + ih,x + iw,y + ih); // south
        g.drawLine(x + iw,y + ih,x + iw,y); // east
        org_color = g.getColor();
        g.setColor(Color.white);

        // draw white shade, no north for cup
        g.drawLine(x + 1,y + 1,x + 1,y + ih - 1); // west
        g.drawLine(x,y + h,x + w,y + h); // south
        g.drawLine(x + w,y,x + w,y + h); // east
        g.setColor(org_color);
    }


    /**
     * draw 3D square
     */
    public static void draw3DSquare(Graphics g, Rectangle r)
    {
        draw3DSquare(g,r.x,r.y,r.width,r.height);
    }

    /**
     * draw 3D square
     */
    public static void draw3DSquare(Graphics g, int x, int y, int w, int h)
    {
        int iw, ih; // inner width and height
        Color org_color; // save original color

        if ((iw = w - 2) < 0)
            iw = 0;
        if ((ih = h - 1) < 0)
            ih = 0;
        g.drawRect(x,y,iw,ih); // draw square
        org_color = g.getColor();
        g.setColor(Color.white);

        // draw white shade
        g.drawLine(x + 1,y + 1,x + iw - 1,y + 1); // north
        g.drawLine(x + 1,y + 1,x + 1,y + ih - 1); // west
        g.drawLine(x,y + h,x + w,y + h); // south
        g.drawLine(x + w,y,x + w,y + h); // east
        g.setColor(org_color);

    }

    /**
     * paint
     */
    public void paint(Graphics g)
    {
        FontMetrics fm = g.getFontMetrics();
        int fontheight = fm.getHeight();

        Dimension d = new Dimension(this.getSize());
        BorderBox nextitem;
        Color backgroundcolor = this.getBackground();
        Color forgroundcolor = this.getForeground();
        int titlewidth;

        // g.drawString(PageTitle, 50, 50);
        draw3DCup(g,2,0,d.width - 6,d.height - 6);

        // draw BorderBox
        Enumeration e = broderboxslist.elements();
        while (e.hasMoreElements())
        {
            nextitem = (BorderBox)e.nextElement();
            titlewidth = fm.stringWidth(nextitem.title);

            // draw the square
            draw3DSquare(g,nextitem.border);

            // erase the line where we will write the title
            g.setColor(backgroundcolor);
            g.drawLine(nextitem.border.x + 20,nextitem.border.y,nextitem.border.x + 20 + titlewidth,nextitem.border.y);
            g.drawLine(nextitem.border.x + 20,nextitem.border.y + 1,nextitem.border.x + 20 + titlewidth,nextitem.border.y + 1);
            g.setColor(forgroundcolor);
            g.drawString(nextitem.title,nextitem.border.x + 20,nextitem.border.y + fontheight / 2);
        }
    }

}