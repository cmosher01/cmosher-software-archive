package to.go.gro.groapplet;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class GROMouseListener extends MouseInputAdapter
{
	private Component mLastHit;
	private final FamilyChart fc;

	public GROMouseListener(FamilyChart fc)
	{
		this.fc = fc;
	}

    public void mousePressed(MouseEvent evt)
    {
		mLastHit = evt.getComponent();
		evt.getPoint();
        super.mousePressed(evt);
    }

    public void mouseReleased(MouseEvent evt)
    {
        // TODO Auto-generated method stub
        super.mouseReleased(evt);
    }

    public void mouseDragged(MouseEvent evt)
    {
        // TODO Auto-generated method stub
        super.mouseDragged(evt);
    }
}
