/*
 * Created on Jan 26, 2008
 */
package gui.buttons;

//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.geom.Rectangle2D;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Button;

public class PowerLight extends Button
{
	private boolean on;
//	private Font font;
//	private static final int FONT_HEIGHT = 7;

	public PowerLight(Context context)
	{
		super(context);
//		super("POWER");
//		setOpaque(true);

//		this.font = new Font("Arial",Font.PLAIN,10);
//		this.setPreferredSize(new Dimension(50,50));
//		this.setEnabled(false);
	}

	public void turnOn(final boolean powerOn)
	{
		this.on = powerOn;
	}

	@Override
	protected void onDraw(final Canvas canvas)
	{
		super.onDraw(canvas);
		final Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		if (this.on) {
			paint.setColor(Color.rgb(255, 240, 120));
		} else {
			paint.setColor(Color.GRAY);
		}
		canvas.drawRect(new Rect(0,0,getWidth(),getHeight()), paint);

		// TODO ANDROID draw "POWER"
//		g2d.setColor(Color.BLACK);
//		g2d.setFont(this.font);
//		FontMetrics fontMetrics = g2d.getFontMetrics();
//		Rectangle2D bndText = fontMetrics.getStringBounds(getText(),g2d);
//		double y = FONT_HEIGHT+(this.getHeight()-FONT_HEIGHT)/2;
//		double x = (this.getWidth()-bndText.getWidth())/2;
//		g2d.drawString(getText(),Math.round(x),Math.round(y));
//		g2d.dispose();
	}
}
