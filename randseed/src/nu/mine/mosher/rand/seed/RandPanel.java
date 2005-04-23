/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RandPanel extends JPanel
{
	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public RandPanel(LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout,isDoubleBuffered);
	}

	/**
	 * @param layout
	 */
	public RandPanel(LayoutManager layout)
	{
		super(layout);
	}

	/**
	 * @param isDoubleBuffered
	 */
	public RandPanel(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
	}

	/**
	 * 
	 */
	public RandPanel()
	{
		super();
	}

	public void init()
	{
		JLabel x = new JLabel("Test", SwingConstants.LEFT);
		add(x);
	}
}
