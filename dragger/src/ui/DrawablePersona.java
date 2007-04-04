/*
 * Created on May 27, 2006
 */
package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.MovableShape;



public class DrawablePersona implements Drawable
{
	private static final int LABEL_MARGIN_X = 4;
	private static final int LABEL_MARGIN_Y = 2;

	private Point2D upperLeft;
	private List<AttributedString> rLabel = new ArrayList<AttributedString>();

	private Font font = new Font("Helvetica",Font.PLAIN,9);
	private Font font2 = new Font("Helvetica",Font.PLAIN,9);
    private BasicStroke stroke = new BasicStroke(2);
    private Paint paint = Color.BLACK;
    private Paint paintText = Color.BLACK;

	private List<Layout> rLayoutLabel;
	private Rectangle2D bounds;

	private MovableShape movable;

	private DrawableFamily childInFamily;
	private Set<DrawableFamily> rParentInFamily = new HashSet<DrawableFamily>();

	/**
	 * @param upperLeft
	 * @param rLabel
	 */
	public DrawablePersona(final Point2D upperLeft, final List<AttributedCharacterIterator> rLabel)
	{
		this.upperLeft = (Point2D)upperLeft.clone();

		for (final AttributedCharacterIterator iChar: rLabel)
		{
			this.rLabel.add(new AttributedString((AttributedCharacterIterator)iChar.clone()));
		}
	}

	void setChildInFamily(final DrawableFamily family)
	{
		this.childInFamily = family;
	}

	void addParentInFamily(final DrawableFamily family)
	{
		this.rParentInFamily.add(family);
	}

	/**
	 * @return Returns the childInFamily.
	 */
	public DrawableFamily getChildInFamily()
	{
		return this.childInFamily;
	}

	/**
	 * @param append 
	 */
	public void getParentInFamilies(final Collection<DrawableFamily> append)
	{
		append.addAll(this.rParentInFamily);
	}

	public void draw(final Graphics2D graphics, final DrawingOptions options)
	{
        // Optimization (this "if" block could be removed).
        // don't draw the shape if it's not in the clipping area
        if (!graphics.getClip().intersects(this.bounds))
        {
            return;
        }

        graphics.setStroke(this.stroke);
		graphics.setPaint(this.paint);
		graphics.draw(this.bounds);

		if (options.isSelected())
		{
			graphics.setPaint(new Color(200,255,200));
		}
        else
        {
        	graphics.setPaint(new Color(255,255,204));
        }
		graphics.fill(this.bounds);

		graphics.setPaint(this.paintText);
        for (final Layout layout: this.rLayoutLabel)
		{
			layout.draw(graphics);
		}
	}

	public void calc(final Graphics2D graphics)
	{
		boolean first = true;
		for (final AttributedString as: this.rLabel)
		{
			if (first)
			{
				first = false;
	            as.addAttribute(TextAttribute.FONT,this.font);
			}
			else
			{
	            as.addAttribute(TextAttribute.FONT,this.font2);
			}
		}

		final FontRenderContext frc = graphics.getFontRenderContext();

    	final double maxAdvance = getMaxAdvance(frc);
    	final double maxY = calcTextLayouts(frc,maxAdvance);

    	calcBounds(maxAdvance,maxY);
	}

	/**
	 * 
	 */
	public void place()
	{
		this.movable = new MovableShape(this.bounds);
	}

	private void calcBounds(final double maxAdvance, final double maxY)
	{
		final double x = this.upperLeft.getX();
		final double y = this.upperLeft.getY();

		final double width = maxAdvance+2*LABEL_MARGIN_X;
		final double height = maxY-this.upperLeft.getY()+LABEL_MARGIN_Y;

		this.bounds = new Rectangle2D.Double(x,y,width,height);
	}

	private double calcTextLayouts(final FontRenderContext frc, final double maxAdvance)
	{
    	this.rLayoutLabel = new ArrayList<Layout>();

    	double y = this.upperLeft.getY()+LABEL_MARGIN_Y;

    	boolean first = true;
        for (final AttributedString str: this.rLabel)
		{
        	final TextLayout tl = new TextLayout(str.getIterator(),frc);

        	if (first)
        	{
        		first = false;
        	}
        	else
        	{
        		y += tl.getLeading();
        	}

        	y += tl.getAscent();

        	double x = this.upperLeft.getX()+LABEL_MARGIN_X;
            if (!tl.isLeftToRight())
            {
            	x += maxAdvance - tl.getAdvance();
            }

        	this.rLayoutLabel.add(new Layout(tl,(float)x,(float)y));

        	y += tl.getDescent();
		}

        return y;
	}

	private double getMaxAdvance(final FontRenderContext frc)
	{
		double maxAdvance = 0;
        for (final AttributedString str: this.rLabel)
        {
            final TextLayout tl = new TextLayout(str.getIterator(),frc);
            if (tl.getAdvance() > maxAdvance) 
            {
            	maxAdvance = tl.getAdvance();
            }
        }
		return maxAdvance;
	}

	private static final class Layout
	{
		final TextLayout layout;
		final float x;
		final float y;
		Layout(final TextLayout layout, final float x, final float y)
		{
			this.layout = layout;
			this.x = x;
			this.y = y;
		}
		void draw(final Graphics2D graphics)
		{
			this.layout.draw(graphics,this.x,this.y);
		}
	}

	public Rectangle2D getBounds2D()
    {
		if (this.bounds == null)
		{
			throw new IllegalStateException("Must call calc before getting bounds.");
		}
		return (Rectangle2D)this.bounds.clone();
    }

	public MovableShape getMovable()
	{
		if (this.movable == null)
		{
			throw new IllegalStateException("Must call place before getting movable.");
		}
		return this.movable;
	}

	public void move(final Shape movedTo)
	{
		this.movable.setShape(movedTo);
		final Rectangle2D bounds = movedTo.getBounds2D();
		this.upperLeft = new Point2D.Double(bounds.getX(),bounds.getY());
	}
}
