/*
 * Created on Apr 29, 2006
 */
package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class DrawableFamily implements Drawable
{
	private static final int BAR_HEIGHT = 4;
	private static final int CHILD_LINE_DISTANCE = 10;
	private static final int CHILD_HEIGHT = 10;
	private static final int MARRIAGE_SPACING = 20;
//	private static final int CHILD_VSPACING = 150;

	private final DrawablePersona parentLeft;
	private final DrawablePersona parentRight;
	private final List<DrawablePersona> rChild;

	private List<Shape> rShape = new ArrayList<Shape>();

	/*
	 *  parentLeft {pt1}====={pt2} parentRight
	 *                  {ptP}
	 *                    |
	 *                    |
	 *          __________|_____________
	 *       {ptC1}   |            |   {ptC2}
	 *         |      |            |    |
	 *         |      |            |    |
	 *       {ptc0    3            1    2}
	 *        child0 child3     child1  child2
	 */
	/**
	 * @param parentLeft
	 * @param parentRight
	 * @param rChild 
	 */
	public DrawableFamily(final DrawablePersona parentLeft, final DrawablePersona parentRight, final Collection<DrawablePersona> rChild)
	{
		this.parentLeft = parentLeft;
		this.parentRight = parentRight;
		this.rChild = Collections.<DrawablePersona>unmodifiableList(new ArrayList<DrawablePersona>(rChild));

		if (this.parentLeft == null && this.parentRight == null && this.rChild.isEmpty())
		{
			throw new IllegalArgumentException("Must have at least one parent or child.");
		}

		if (this.parentLeft != null)
		{
			this.parentLeft.addParentInFamily(this);
		}
		if (this.parentRight != null)
		{
			this.parentRight.addParentInFamily(this);
		}
		for (final DrawablePersona child: rChild)
		{
			child.setChildInFamily(this);
		}
	}

	public void draw(final Graphics2D graphics, final DrawingOptions options)
	{
		graphics.setPaint(Color.BLUE.darker());
		for (final Shape shape : this.rShape)
		{
			graphics.draw(shape);
		}
	}

	public void calc(final Graphics2D graphics)
	{
		this.rShape.clear();

		Point2D.Double ptP = new Point2D.Double();
		if (this.parentLeft != null || this.parentRight != null)
		{
			final Rectangle2D.Double rect1 = new Rectangle2D.Double();
			final Rectangle2D.Double rect2 = new Rectangle2D.Double();
			if (this.parentRight == null)
			{
				rect1.setFrame(this.parentLeft.getBounds2D());
				rect2.setFrame(this.parentLeft.getBounds2D());
				rect2.x = rect1.getMaxX()+MARRIAGE_SPACING;
			}
			else if (this.parentLeft == null)
			{
				rect1.setFrame(this.parentRight.getBounds2D());
				rect2.setFrame(this.parentRight.getBounds2D());
				rect1.x = rect2.x-MARRIAGE_SPACING-rect2.getWidth();
			}
			else
			{
				rect1.setFrame(this.parentLeft.getBounds2D());
				rect2.setFrame(this.parentRight.getBounds2D());
			}
	
			final boolean bHusbandOnRight = (rect1.getCenterX() > rect2.getCenterX());
			if (bHusbandOnRight)
			{
				Rectangle2D rectTemp = rect1.getFrame();
				rect1.setFrame(rect2);
				rect2.setFrame(rectTemp);
			}
			final Point2D.Double pt1 = new Point2D.Double(rect1.getMaxX(),rect1.getCenterY());
			final Point2D.Double pt2 = new Point2D.Double(rect2.getMinX(),rect2.getCenterY());
	
			if (bHusbandOnRight)
			{
				ptP = calcParPt(pt2.x,pt2.y+BAR_HEIGHT/2,pt1.x,pt1.y+BAR_HEIGHT/2);
			}
			else
			{
				ptP = calcParPt(pt1.x,pt1.y+BAR_HEIGHT/2,pt2.x,pt2.y+BAR_HEIGHT/2);
			}
			this.rShape.add(new Line2D.Double(pt1.x,pt1.y-BAR_HEIGHT/2,pt2.x,pt2.y-BAR_HEIGHT/2));
			this.rShape.add(new Line2D.Double(pt1.x,pt1.y+BAR_HEIGHT/2,pt2.x,pt2.y+BAR_HEIGHT/2));
		}

		if (!this.rChild.isEmpty())
		{
			double nTop = Double.MAX_VALUE;
			double nLeft = Double.MAX_VALUE;
			double nRight = Double.MIN_VALUE;
			for (final DrawablePersona shapeChild : this.rChild)
			{
				final Rectangle2D rect = shapeChild.getBounds2D();

				if (rect.getMinY() < nTop)
				{
					nTop = rect.getMinY();
				}
				if (rect.getCenterX() < nLeft)
				{
					nLeft = rect.getCenterX();
				}
				if (nRight < rect.getCenterX())
				{
					nRight = rect.getCenterX();
				}
			}

			final Point2D.Double ptC1 = new Point2D.Double(nLeft,nTop-CHILD_HEIGHT);
			final Point2D.Double ptC2 = new Point2D.Double(nRight,nTop-CHILD_HEIGHT);

			this.rShape.add(new Line2D.Double(ptC1,ptC2));

			for (final DrawablePersona shapeChild : this.rChild)
			{
				final Rectangle2D rect = shapeChild.getBounds2D();
				this.rShape.add(new Line2D.Double(rect.getCenterX(),rect.getCenterY(),rect.getCenterX(),ptC1.y));
			}
			if (this.parentLeft != null || this.parentRight != null)
			{
				if (ptC1.x < ptP.x && ptP.x < ptC2.x)
				{
					final Point2D.Double pt2 = new Point2D.Double(ptP.x,ptC1.y);
					this.rShape.add(new Line2D.Double(pt2,ptP));
				}
				else
				{
					final Point2D.Double pt2 = new Point2D.Double(ptP.x,ptC1.y-CHILD_HEIGHT);
					final Point2D.Double pt3 = new Point2D.Double((ptC1.x+ptC2.x)/2,ptC1.y-CHILD_HEIGHT);
					final Point2D.Double pt4 = new Point2D.Double((ptC1.x+ptC2.x)/2,ptC1.y);
					this.rShape.add(new Line2D.Double(ptP,pt2));
					this.rShape.add(new Line2D.Double(pt2,pt3));
					this.rShape.add(new Line2D.Double(pt3,pt4));
				}
			}
		}
	}

	private Point2D.Double calcParPt(final double ax, final double ay, final double bx, final double by)
	{
		final double dx = bx-ax;
		final double dy = by-ay;
		final double dist = Math.sqrt(dx*dx+dy*dy);

		// avoid division by zero
		if (-1e-6 <= dist && dist <= 1e-6)
		{
			return new Point2D.Double(ax,ay);
		}

		return new Point2D.Double(ax+CHILD_LINE_DISTANCE*dx/dist,ay+CHILD_LINE_DISTANCE*dy/dist);
	}
}
