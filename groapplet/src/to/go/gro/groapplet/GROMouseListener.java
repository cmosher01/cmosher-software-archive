package to.go.gro.groapplet;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class GROMouseListener extends MouseInputAdapter
{
	private Indi mLastIndi;
	private final FamilyChart fc;

	public GROMouseListener(FamilyChart fc)
	{
		this.fc = fc;
	}

    public void mousePressed(MouseEvent evt)
    {
		mLastIndi = fc.hitIndi(evt.getPoint());
		if (mLastIndi != null)
		{
System.out.println("on");
			mLastIndi.hit(true);
			fc.repaint();
		}
        super.mousePressed(evt);
    }

    public void mouseReleased(MouseEvent evt)
    {
    	if (mLastIndi != null)
    	{
    		if (mLastIndi.isHit())
    		{
    			
    		}
			mLastIndi = null;
    	}
        super.mouseReleased(evt);
    }

    public void mouseDragged(MouseEvent evt)
    {
		Indi indi = fc.hitIndi(evt.getPoint());
		if (indi != mLastIndi && mLastIndi != null && mLastIndi.isHit())
		{
System.out.println("off");
			mLastIndi.hit(false);
			fc.repaint();
		}
		else if (indi == mLastIndi && mLastIndi != null && !mLastIndi.isHit())
		{
System.out.println("on");
			mLastIndi.hit(true);
			fc.repaint();
		}
        super.mouseDragged(evt);
    }
}
