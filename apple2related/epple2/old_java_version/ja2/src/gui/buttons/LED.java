/*
 * Created on Sep 19, 2007
 */
package gui.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;

public class LED extends JLabel
{
	private final Color color;
	private volatile boolean on;
	private Font font;
	private static final int FONT_HEIGHT = 9;

	public LED(String label, Color color, int width, int height)
	{
		super(label);
		setOpaque(true);
		this.color = color;
		this.font = new Font("Arial",Font.PLAIN,10);
		this.setPreferredSize(new Dimension(width,height));
	}

	public void setOn(boolean on)
	{
		this.on = on;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
        final Graphics2D g2d = (Graphics2D)g.create();
        if (isOpaque())
		{
        	g2d.setColor(Color.WHITE);
            g2d.fillRect(0,0,getWidth(),getHeight());
		}
		g2d.setFont(this.font);
		g2d.setColor(Color.BLACK);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D bndText = fontMetrics.getStringBounds(getText(),g2d);
		g2d.drawString(getText(),Math.round(10-bndText.getWidth()),Math.round(FONT_HEIGHT));


		if (this.on)
		{
			g2d.setPaint(this.color);
			g2d.fillOval(11,1,9,9);
		}
		g2d.setPaint(Color.BLACK);
		g2d.drawOval(11,1,8,8);


		g2d.dispose();
	}
}
