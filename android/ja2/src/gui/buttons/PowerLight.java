/*
 * Created on Jan 26, 2008
 */
package gui.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JButton;

public class PowerLight extends JButton
{
	private boolean on;
	private Font font;
	private static final int FONT_HEIGHT = 7;

	public PowerLight()
	{
		super("POWER");
		setOpaque(true);

		this.font = new Font("Arial",Font.PLAIN,10);
		this.setPreferredSize(new Dimension(50,50));
		this.setEnabled(false);
	}

	public void turnOn(final boolean powerOn)
	{
		this.on = powerOn;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
        final Graphics2D g2d = (Graphics2D)g.create();
        if (isOpaque())
		{
            if (this.on)
            {
            	g2d.setColor(new Color(255,240,120));
            }
            else
            {
            	g2d.setColor(Color.GRAY);
            }
            g2d.fillRect(0,0,getWidth(),getHeight());
		}
    	g2d.setColor(Color.BLACK);
		g2d.setFont(this.font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D bndText = fontMetrics.getStringBounds(getText(),g2d);
		double y = FONT_HEIGHT+(this.getHeight()-FONT_HEIGHT)/2;
		double x = (this.getWidth()-bndText.getWidth())/2;
		g2d.drawString(getText(),Math.round(x),Math.round(y));
		g2d.dispose();
	}
}
