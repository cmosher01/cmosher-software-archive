/*
 * Created on Aug 31, 2004
 */
package nu.mine.mosher;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

/**
 * TODO
 * 
 * @author chrism
 */
public class JTextPaneNoWrap extends JTextPane
{
    /**
     * 
     */
    public JTextPaneNoWrap()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param doc
     */
    public JTextPaneNoWrap(StyledDocument doc)
    {
        super(doc);
        // TODO Auto-generated constructor stub
    }

    /**
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth()
    {
        Component parent = getParent();
        if (parent == null)
        {
            return true;
        }

        int uiWidth = getUI().getPreferredSize(this).width;
        int parentWidth = parent.getSize().width;

        return uiWidth < parentWidth;
    }

    /**
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    public void setBounds(int x, int y, int width, int height)
    {
        Dimension size = getPreferredSize();
        super.setBounds(x,y,Math.max(size.width,width),Math.max(size.height,height));
    }
}
