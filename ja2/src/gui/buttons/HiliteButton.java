/*
 * Created on Sep 18, 2007
 */
package gui.buttons;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

class HiliteButton extends JButton //implements ActionListener
{
	private boolean mouseOver;
	private Border borderEnabled;
	private Border borderDisabled;
	private Font font;
	private static final int FONT_HEIGHT = 7;

	public HiliteButton(String text, int width, int height)
	{
		super(text);
		setOpaque(true);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		this.borderEnabled = BorderFactory.createLineBorder(Color.BLACK);
		this.borderDisabled = BorderFactory.createLineBorder(Color.GRAY);
		this.font = new Font("Arial",Font.PLAIN,10);
		this.setPreferredSize(new Dimension(width,height));
	}

	@Override
	public void processMouseEvent(MouseEvent evt)
	{
		switch (evt.getID())
		{
			case MouseEvent.MOUSE_ENTERED:
				mouseEntered();
			break;
			case MouseEvent.MOUSE_EXITED:
				mouseExited();
			break;
		}
		super.processMouseEvent(evt);
	}

	public void mouseExited()
	{
		this.mouseOver = false;
		repaint();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void mouseEntered()
	{
		this.mouseOver = true;
		repaint();
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	protected void paintComponent(Graphics g)
	{
        final Graphics2D g2d = (Graphics2D)g.create();
        if (isOpaque())
		{
            if (this.mouseOver && isEnabled())
            {
            	g2d.setColor(Color.GREEN);
            }
            else
            {
            	g2d.setColor(Color.WHITE);
            }
            g2d.fillRect(0,0,getWidth(),getHeight());
		}
        if (isEnabled())
        {
    		setBorder(this.borderEnabled);
        }
        else
        {
    		setBorder(this.borderDisabled);
        }
        if (isEnabled())
        {
        	g2d.setColor(Color.BLACK);
        }
        else
        {
        	g2d.setColor(Color.GRAY);
        }
		g2d.setFont(this.font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D bndText = fontMetrics.getStringBounds(getText(),g2d);
		double y = FONT_HEIGHT+(this.getHeight()-FONT_HEIGHT)/2;
		double x = (this.getWidth()-bndText.getWidth())/2;
		g2d.drawString(getText(),Math.round(x),Math.round(y));
		g2d.dispose();
	}
}
