package to.go.gro.groapplet;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class FamilyChart extends JPanel
{
    private final IndiSet mIndis;
    private final FamiSet mFamis;
    private boolean mInitialized;

    public FamilyChart(IndiSet indis, FamiSet famis)
    {
        mIndis = indis;
        mFamis = famis;
		GROMouseListener ml = new GROMouseListener();
		addMouseListener(ml);
		addMouseMotionListener(ml);
    }

    public void paint(Graphics g)
    {
        if (!mInitialized)
            init(g);

        Rectangle clip = g.getClipBounds();
        g.clearRect(clip.x,clip.y,clip.width,clip.height);

        mFamis.paint(g);
        mIndis.paint(g);
    }

    protected void init(Graphics g)
    {
        Rectangle bounds = mIndis.init(g);
        mFamis.init(g);

        Dimension dimBounds = new Dimension(bounds.width,bounds.height);
        setSize(dimBounds);
        setPreferredSize(dimBounds);

        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);

        mInitialized = true;
    }

    public Indi hitIndi(Point point)
    {
        return mIndis.isOnIndi(point);
    }

    protected void processMouseMotionEvent(MouseEvent e)
    {
//        if (isOnIndi(e.getPoint()))
//            setCursor();
        super.processMouseMotionEvent(e);
    }
}
