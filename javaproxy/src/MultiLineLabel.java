/**
 * @(#)Log.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS
 * SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES
 * THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK. Modifier :
 * Steve Yeong-Ching Hsueh
 */

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.StringTokenizer;



/**
 * MultiLineLabel is partly copied from the example program in Java in a
 * NutShell
 */
public class MultiLineLabel extends Canvas
{

    public static final int LEFT = 0;

    public static final int CENTER = 1;

    public static final int RIGHT = 2;

    protected String[] lines;

    protected int num_lines;

    protected int margin_width;

    protected int margin_height;

    protected int line_height;

    protected int line_ascent;

    protected int[] line_widths;

    protected int max_width;

    protected int alignment = LEFT;



    /**
     * set new label
     */
    protected void newLabel(String label)
    {
        StringTokenizer t = new StringTokenizer(label,"\n");
        num_lines = t.countTokens();
        lines = new String[num_lines];
        line_widths = new int[num_lines];
        for (int i = 0; i < num_lines; i++)
            lines[i] = t.nextToken();
    }


    /**
     * calculate the max size of the line
     */
    protected void measure()
    {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        if (fm == null)
            return;

        line_height = fm.getHeight();
        line_ascent = fm.getAscent();
        max_width = 0;
        for (int i = 0; i < num_lines; i++)
        {
            line_widths[i] = fm.stringWidth(lines[i]);
            if (line_widths[i] > max_width)
                max_width = line_widths[i];
        }
    }


    /**
     * constructor
     */
    public MultiLineLabel(String label, int margin_width, int margin_height, int alignment)
    {

        newLabel(label);
        this.margin_width = margin_width;
        this.margin_height = margin_height;
        this.alignment = alignment;

    }

    /**
     * constructor
     */
    public MultiLineLabel(String label, int margin_width, int margin_height)
    {
        this(label,margin_width,margin_height,LEFT);
    }

    /**
     * constructor
     */
    public MultiLineLabel(String label)
    {
        this(label,10,10,LEFT);
    }

    /**
     * set lable
     */
    public void setLabel(String label)
    {
        newLabel(label);
        measure();
        repaint();
    }

    /**
     * add notify
     */
    public void addNotify()
    {
        super.addNotify();
        measure();
    }

    /**
     * return preferred size
     */
    public Dimension preferredSize()
    {
        return new Dimension(max_width + margin_width * 2,num_lines * line_height + margin_height * 2);
    }

    /**
     * return minimum size
     */
    public Dimension minimumSize()
    {
        return new Dimension(max_width,num_lines * line_height);
    }


    /**
     * paint
     */
    public void paint(Graphics g)
    {
        int x, y;
        Dimension d = this.size();
        x = 0;
        y = line_ascent + (d.height - num_lines * line_height) / 2;
        for (int i = 0; i < num_lines; i++, y += line_height)
        {
            switch (alignment)
            {
                case LEFT:
                    x = margin_width;
                break;
                case CENTER:
                    x = (d.width - line_widths[i]) / 2;
                break;
                case RIGHT:
                    x = d.width - margin_width - line_widths[i];
                break;
            }
            g.drawString(lines[i],x,y);
        }

    }

}