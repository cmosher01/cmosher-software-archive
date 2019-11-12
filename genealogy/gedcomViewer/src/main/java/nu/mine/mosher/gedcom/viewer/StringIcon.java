package nu.mine.mosher.gedcom.viewer;



import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;



public class StringIcon implements Icon
{
    private final String s;
    private final int x;
    private final int y;
    private final int w;
    private final int h;

    public StringIcon(String s, int x, int y, int w, int h) {
        this.s = s;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public int getIconHeight() {
        return this.h;
    }

    public int getIconWidth() {
        return this.w;
    }

    public void paintIcon(final Component c, final Graphics g, final int tx, final int ty) {
        g.setColor(c.getForeground());
        g.translate(tx, ty);
        g.drawString(s, x, y);
        g.translate(-tx, -ty);
    }
}
