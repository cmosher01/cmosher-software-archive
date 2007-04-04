/*
 * Created on Apr 19, 2006
 */
package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import model.MovableShape;

class DragPanel extends JPanel implements Scrollable
{
	private final List<DrawablePersona> rDrawPersona = new ArrayList<DrawablePersona>();
	private final List<DrawableFamily> rDrawFamily = new ArrayList<DrawableFamily>();

    private final Set<DrawablePersona> selection = new HashSet<DrawablePersona>();

//    private BasicStroke strokePartner = new BasicStroke(1);
//    private Paint paintPartner = Color.BLUE;

    private BasicStroke strokeSelecting = new BasicStroke(1);//,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);//,3f,new float[] {1f},0f);
    private Paint paintSelecting = Color.RED.darker();

    private boolean moving;
    private boolean selecting;
    private Point2D.Double pressedAt;
    private Point2D.Double draggingTo;

//    private final int deltaStrokeMin;
//    private final int deltaStrokeMax;

    private Rectangle2D.Double rectDrag;
    private Rectangle2D.Double rectDragNormal;

    public DragPanel()
    {
        setOpaque(true);

//        setPreferredSize(new Dimension(16000,12000));

        setAutoscrolls(true);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(final MouseEvent e)
            {
                pressed(getAt(e),e.isControlDown() || e.isShiftDown());
            }
            @Override
            public void mouseReleased(final MouseEvent e)
            {
                released();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(final MouseEvent e)
            {
                dragged(getAt(e),e.isControlDown() || e.isShiftDown());
            }
        });

//        final int sizeStroke = getStrokeSize();
//        this.deltaStrokeMin = sizeStroke / 2;
//        this.deltaStrokeMax = sizeStroke-this.deltaStrokeMin;
    }


	public void set(final List<DrawablePersona> rPersona, final List<DrawableFamily> rFamily)
	{
		this.rDrawPersona.clear();
		this.rDrawPersona.addAll(rPersona);

		this.rDrawFamily.clear();
		this.rDrawFamily.addAll(rFamily);
	}

	public void calc()
	{
        final Graphics gr = getGraphics();
        final Graphics2D graphics = (Graphics2D)gr.create();

        for (final Drawable drawable: this.rDrawPersona)
		{
			drawable.calc(graphics);
		}

        for (final Drawable drawable: this.rDrawFamily)
		{
			drawable.calc(graphics);
		}

        graphics.dispose();

        Rectangle2D union = null;
        for (final DrawablePersona persona: this.rDrawPersona)
        {
        	persona.place();
        	final Rectangle2D bnd = persona.getBounds2D();
        	if (union == null)
        	{
        		union = bnd;
        	}
        	else
        	{
        		union = union.createUnion(bnd);
        	}
        }
        setPreferredSize(new Dimension((int)(union.getMaxX()),(int)(union.getMaxY())));
	}

	@Override
    public void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g.create();

//        final AffineTransform scaler = AffineTransform.getScaleInstance(2,2);
//        g2.transform(scaler);

        for (final Drawable drawable : this.rDrawFamily)
		{
			drawable.draw(g2,null);
		}

        for (final DrawablePersona persona : this.rDrawPersona)
        {
            final DrawingOptions opt = new DrawingOptions();
            opt.setSelected(this.selection.contains(persona));
            persona.draw(g2,opt);
        }

        if (this.selecting)
        {
            g2.setStroke(this.strokeSelecting);
            g2.setPaint(this.paintSelecting);
            g2.draw(this.rectDragNormal);
        }

        g2.dispose();
    }

//    private int getStrokeSize()
//    {
//        int sizeStroke = (int)Math.rint(Math.round(this.stroke.getLineWidth()));
//        if (sizeStroke < 1)
//        {
//            sizeStroke = 1;
//        }
//        return sizeStroke;
//    }

    private void pressed(final Point2D.Double press, final boolean extending)
    {
        this.pressedAt = press;

        int iHit = -1;
        for (int iPersona = this.rDrawPersona.size()-1; iPersona >= 0; --iPersona)
		{
			final DrawablePersona persona = this.rDrawPersona.get(iPersona);
			if (persona.getMovable().getOriginalShape().contains(press))
			{
				iHit = iPersona;
				break;
			}
		}
        if (iHit >= 0)
        {
            this.moving = true;
            final DrawablePersona persona = this.rDrawPersona.get(iHit);
            if (!this.selection.contains(persona))
            {
                if (!extending)
                {
                    this.selection.clear();
                }
                this.selection.add(persona);
            }
            else
            {
                if (extending)
                {
                    this.selection.remove(persona);
                    this.moving = false;
                }
            }
            repaint();
        }
        else
        {
            this.selecting = true;
            dragged(press,extending);
        }
    }

    private void dragged(final Point2D.Double draggedTo, final boolean extending)
    {
    	assert this.moving || this.selecting;

    	this.draggingTo = draggedTo;
        this.rectDrag = createRectFromPoints(this.pressedAt,this.draggingTo);
        this.rectDragNormal = normalizeRect(this.rectDrag);
        scrollRectToVisible(pointToRect(this.draggingTo));

        if (this.moving)
        {
            moveSelectedShapes();
        }
        else if (this.selecting)
        {
            if (!extending)
            {
                this.selection.clear();
            }
            for (final DrawablePersona persona : this.rDrawPersona)
            {
                // TODO account for the stroke when checking for hit shapes
                if (persona.getMovable().getOriginalShape().intersects(this.rectDragNormal))
                {
                    this.selection.add(persona);
                }
            }
        }
        repaint();
    }

    private void released()
    {
        this.moving = false;
        this.selecting = false;
        this.pressedAt = null;
        this.draggingTo = null;
        this.rectDrag = null;
        this.rectDragNormal = null;
        for (final DrawablePersona persona : this.selection)
        {
            persona.getMovable().commit();
        }

        repaint();
    }

//    private boolean hit(final Point2D.Double press)
//    {
//        return
//            this.rect.getMinX()-this.deltaStrokeMin <= press.getX() && press.getX() < this.rect.getMaxX()+this.deltaStrokeMax &&
//            this.rect.getMinY()-this.deltaStrokeMin <= press.getY() && press.getY() < this.rect.getMaxY()+this.deltaStrokeMax;
//    }

//    private boolean dragHit()
//    {
//        return this.rectSelecting.intersects(this.rect);
//    }

    private void moveSelectedShapes()
    {
        final AffineTransform move = AffineTransform.getTranslateInstance(this.rectDrag.getWidth(),this.rectDrag.getHeight());

        final Graphics gr = this.getGraphics();
        final Graphics2D graphics = (Graphics2D)gr.create();

        Rectangle2D boundsShapes = null;
        for (final DrawablePersona persona : this.selection)
        {
            final Shape moved = move.createTransformedShape(persona.getMovable().getOriginalShape());
            movePersona(persona,moved,graphics);

            if (boundsShapes == null)
            {
                boundsShapes = moved.getBounds2D();
            }
            else
            {
                boundsShapes.add(moved.getBounds2D());
            }
        }
        constrainMovement(boundsShapes,graphics);

        graphics.dispose();
    }


	private void movePersona(final DrawablePersona persona, final Shape moved, final Graphics2D graphics)
	{
		persona.move(moved);
		persona.calc(graphics);
		final DrawableFamily ch = persona.getChildInFamily();
		if (ch != null)
		{
			ch.calc(graphics);
		}
		final List<DrawableFamily> fam = new ArrayList<DrawableFamily>();
		persona.getParentInFamilies(fam);
		for (final DrawableFamily family : fam)
		{
			family.calc(graphics);
		}
	}

    private void constrainMovement(final Rectangle2D boundsShapes,final Graphics2D graphics)
    {
        // TODO account for stroke width when constraining movement
        final Rectangle2D bounds = new Rectangle2D.Double(0,0,getWidth(),getHeight());
        if (!bounds.contains(boundsShapes))
        {
            double dx = 0, dy = 0;
            if (boundsShapes.getMinX() < bounds.getMinX())
            {
                dx = bounds.getMinX()-boundsShapes.getMinX();
            }
            else if (bounds.getMaxX() < boundsShapes.getMaxX())
            {
                dx = bounds.getMaxX()-boundsShapes.getMaxX();
            }
            if (boundsShapes.getMinY() < bounds.getMinY())
            {
                dy = bounds.getMinY()-boundsShapes.getMinY();
            }
            else if (bounds.getMaxY() < boundsShapes.getMaxY())
            {
                dy = bounds.getMaxY()-boundsShapes.getMaxY();
            }

            final AffineTransform moveBack = AffineTransform.getTranslateInstance(dx,dy);
            for (final DrawablePersona persona : this.selection)
            {
                final Shape moved = moveBack.createTransformedShape(persona.getMovable().getMovedShape());
                movePersona(persona,moved,graphics);
            }
        }
    }

//    private void constrainMovement()
//    {
//        if (this.rect.getMinX()-this.deltaStrokeMin < 0)
//        {
//            this.rect.x = this.deltaStrokeMin;
//        }
//        else if (getWidth() < this.rect.getMaxX()+this.deltaStrokeMax)
//        {
//            this.rect.x = getWidth()-this.rect.getWidth()-this.deltaStrokeMax;
//        }
//
//        if (this.rect.getMinY()-this.deltaStrokeMin < 0)
//        {
//            this.rect.y = this.deltaStrokeMin;
//        }
//        else if (getHeight() < this.rect.getMaxY()+this.deltaStrokeMax)
//        {
//            this.rect.y = getHeight()-this.rect.getHeight()-this.deltaStrokeMax;
//        }
//    }

    private static Point2D.Double getAt(final MouseEvent e)
    {
        return new Point2D.Double(e.getX(),e.getY());
    }

    private static Rectangle2D.Double createRectFromPoints(final Point2D a, final Point2D b)
    {
        final double ax = a.getX();
        final double ay = a.getY();
        final double bx = b.getX();
        final double by = b.getY();
        return new Rectangle2D.Double(ax,ay,bx-ax,by-ay);
    }

    private static Rectangle pointToRect(final Point2D point)
    {
        return new Rectangle((int)point.getX(),(int)point.getY(),1,1);
    }

    private static Rectangle2D.Double normalizeRect(final Rectangle2D rect)
    {
        final Rectangle2D.Double rectNormal = new Rectangle2D.Double();
        Rectangle2D.union(rect,rect,rectNormal);
        return rectNormal;
    }

	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(666,360);
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 40;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 240;
	}

	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}

	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}
}
