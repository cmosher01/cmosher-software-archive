/**
 * @(#)UpperLineLayout.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE
 * SUITABILITY OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE
 * LIABLE FOR ANY DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR
 * OWN RISK. Author : Steve Yeong-Ching Hsueh
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.Serializable;



/**
 * UpperLineLayout is the LayoutManager for the buttons on the upper part of the
 * jproxy main window
 */
public class UpperLineLayout implements LayoutManager, Serializable
{

    private int minWidth = 0;

    private int minHeight = 0;

    private int preferredWidth = 0;

    private int preferredHeight = 0;

    private boolean sizeset = false;

    private int maxComponentWidth = 0;

    private int maxComponentHeight = 0;

    private int vgap, hgap, rgap, lgap, cgap;



    /**
     * constructor
     */
    public UpperLineLayout()
    {
        vgap = hgap = rgap = lgap = cgap = 5;
    }

    /**
     * addLayoutComponent
     */
    public void addLayoutComponent(String name, Component comp)
    {
    }

    /**
     * removeLayoutComponent
     */
    public void removeLayoutComponent(Component comp)
    {
    }

    /**
     * set sizes
     */
    public void setSizes(Container parent)
    {
        int n, i;
        Component comp;
        Dimension dim;

        if (sizeset)
            return;
        n = parent.getComponentCount();

        for (i = 0; i < n; i++)
        {
            comp = parent.getComponent(i);
            if (comp.isVisible())
            {
                dim = comp.getPreferredSize();
                maxComponentWidth = Math.max(maxComponentWidth,dim.width);
                maxComponentHeight = Math.max(maxComponentHeight,dim.height);
                preferredHeight += dim.height;
            }
        }

        preferredWidth = maxComponentWidth;
        preferredHeight += maxComponentHeight;
        minWidth = preferredWidth;
        minHeight = preferredHeight;
        sizeset = true;
    }

    /**
     * calculate the preferred layout size
     */
    public Dimension preferredLayoutSize(Container parent)
    {
        Dimension dim = new Dimension(0,0);
        setSizes(parent);

        Rectangle rec = parent.getBounds();
        Insets insets = parent.getInsets();
        if (rec.width <= 0 || rec.height <= 0)
        {
            dim.width = preferredWidth + insets.left + insets.right;
            dim.height = preferredHeight + insets.top + insets.bottom;
            return dim;
        }

        dim.width = rec.width;
        dim.height = rec.height;
        //System.out.println(insets.left + ":" + insets.right + ":" +
        // insets.top + ":" + insets.bottom );
        //System.out.println("width = " + dim.width + "height = " +
        // dim.height);
        return dim;
    }

    /**
     * calculate the minimum layout size
     */
    public Dimension minimumLayoutSize(Container parent)
    {
        Dimension dim = new Dimension(0,0);
        setSizes(parent);
        Insets insets = parent.getInsets();
        dim.width = minWidth + insets.left + insets.right;
        dim.height = minHeight + insets.top + insets.bottom;
        return dim;
    }

    /**
     * layout container
     */
    public void layoutContainer(Container parent)
    {
        Insets insets = parent.getInsets();
        int n, i, cxpos = 0, cypos = 0; // component's x/y position

        setSizes(parent);
        cypos = insets.top + vgap;
        if ((n = parent.getComponentCount()) <= 0)
            return;
        for (i = 0; i < n; i++)
        {
            Component comp = parent.getComponent(i);
            if (comp.isVisible())
            {

                Dimension d = comp.getPreferredSize();
                //comp.setSize(d.width, d.height);

                //System.out.println("drawing " + cxpos +":"+ cypos
                // +":"+d.width+":"+d.height);
                cxpos += (d.width + cgap);
                if (d.width <= 1)
                    continue;
                comp.setBounds(cxpos,cypos,d.width,d.height);
            }
        }


    }

}