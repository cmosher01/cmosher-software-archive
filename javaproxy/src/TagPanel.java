/**
 * @(#)TagPanel.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF
 * THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;



/**
 * TagPanel is a panel that contains a tag list used in configuration window
 */
class TagPanel extends Panel implements MouseListener
{
    final static int MAX_TAG = 20;

    private Vector taglist = new Vector();

    private int PanelWidth, PanelHeight;

    private Tag upperTag; // the currently selected Tag

    Folder controler; // Folder is the controler to the TagPanel



    /**
     * constructor
     */
    public TagPanel(Folder fd)
    {
        controler = fd;
        setLayout(null);
        addMouseListener(this);
    }

    /**
     * constructor
     */
    public TagPanel(Folder fd, int width, int height)
    {
        controler = fd;
        setSize(width,height);
        PanelWidth = width;
        PanelHeight = height;
        setLayout(null);
        addMouseListener(this);
    }


    /**
     * draw 3D cap, a 3D cap is a square without a top (north)
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
        //if( x == 2 && y == 2 ) g.drawLine(x, y, x, y + ih + 2);
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
     * patch the Tag bottom line, xs = starting x, xe = end x, cy = common y
     */
    public static void patch3DTagLine(Graphics g, Tag t, int xs, int xe, int cy)
    {
        Color org_color; // save original color
        Rectangle r = t.getPositionSize();

        //        System.out.println( xs + ":" + xe );
        //        System.out.println( r.x + ":" + r.y + ":" + r.width + ":"+ r.height);

        g.drawLine(xs,cy - 2,r.x,cy - 2); // draw left line, use default color
        g.drawLine(r.x + r.width,cy - 2,xe,cy - 2); // draw right line, use
                                                    // default color

        org_color = g.getColor();
        g.setColor(Color.white); // set white
        g.drawLine(xs + 1,cy - 1,r.x + 1,cy - 1); // draw left line
        g.drawLine(r.x + r.width - 1,cy - 1,xe,cy - 1); // draw right line

        g.setColor(org_color);
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


    public void update()
    {
    }

    /**
     * paint
     */
    public void paint(Graphics g)
    {

        int TAG1_Y_POSTION = 2; // Y position of the 1st tag(left upper corner)
        int TAG1_X_POSTION = 2; // X position of the 1st tag(left upper corner)
        int TAG_Y_CLEARANCE = 20; // Y clearance(uppder clearance)
        int TAG_X_CLEARANCE = 10; // X clearance(left + right)

        Enumeration e = taglist.elements();
        int width, height, x, y;
        Tag nexttag;
        Rectangle r;

        FontMetrics fm = g.getFontMetrics();

        x = TAG1_X_POSTION;
        y = TAG1_Y_POSTION;
        while (e.hasMoreElements())
        {
            nexttag = (Tag)e.nextElement();
            String tagname = nexttag.getName();

            if (tagname != null)
            {

                if (nexttag.getPositionSize() == null)
                {

                    // calculate the positon and size
                    width = fm.stringWidth(tagname) + TAG_X_CLEARANCE;
                    //height = fm.getHeight();
                    height = PanelHeight;
                    y = TAG1_Y_POSTION;
                    nexttag.setPositionSize(new Rectangle(x,y,width,height));
                    x += width;
                    if (x > PanelWidth)
                        return; // can't display the reset
                }

                r = nexttag.getPositionSize();
                g.drawString(tagname,r.x + TAG_X_CLEARANCE / 2,r.y + TAG_Y_CLEARANCE);
                draw3DCap(g,r.x,r.y,r.width,PanelHeight);
            }
        }

        patch3DTagLine(g,upperTag,2,PanelWidth - 10,PanelHeight);

    }


    /**
     * add tag to this panel
     */
    public boolean addTag(String tag)
    {

        Tag tmptag = new Tag(tag);

        if (taglist.size() >= MAX_TAG)
            return false;
        taglist.addElement(tmptag);
        upperTag = tmptag;
        return true;
    }


    /**
     * mouse event handler
     */
    public void mouseClicked(MouseEvent msevt)
    {
        Enumeration e = taglist.elements();
        Tag ctag;
        Rectangle r;
        Point p = msevt.getPoint();
        String tagname;

        while (e.hasMoreElements())
        {
            ctag = (Tag)e.nextElement();
            r = ctag.getPositionSize();
            if (r.contains(p))
            {
                tagname = ctag.getName();
                //System.out.println("You clicked on " + tagname );
                if (upperTag == ctag)
                    return; // clicked on upper tag
                upperTag = ctag;
                repaint();
                controler.updateSetupPage(tagname);
                return;
            }
        }

    }

    // various mouse event handlers
    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }


    public void actionPerformed(ActionEvent evt)
    {


    } // end of public void actionPerformed(ActionEvent evt)


}