/*
 * Created on Apr 19, 2006
 */
package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import model.MovableShape;

class DragPanel extends JPanel
{
    private final Set<MovableShape> model = new HashSet<MovableShape>();
    private final Set<MovableShape> selection = new HashSet<MovableShape>();

    private BasicStroke stroke = new BasicStroke(4);
    private Paint paint = Color.BLACK;

    private BasicStroke strokeSelecting = new BasicStroke(1);//,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1f,new float[] {1f},0f);
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

        setPreferredSize(new Dimension(1024,768));
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

//        this.rModel = new ShapeModel();
//        this.rModel = this.rModel.addShape(new Rectangle2D.Double(10,10,100,50));
//        this.rModel = this.rModel.addShape(new Rectangle2D.Double(160,70,100,50));
        this.model.add(new MovableShape(new Rectangle2D.Double(10,10,100,50)));
        this.model.add(new MovableShape(new Rectangle2D.Double(160,70,100,50)));
    }

    @Override
    public void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g.create();

//        final AffineTransform scaler = AffineTransform.getScaleInstance(2,2);
//        g2.transform(scaler);


        for (final MovableShape shape : this.model)
        {
            drawShape(shape,this.selection.contains(shape),g2);
        }
//        drawShape(this.rect,this.rectIsSelected,g2);
//        drawShape(this.rect2,this.rectIsSelected2,g2);



        if (this.selecting)
        {
            g2.setStroke(this.strokeSelecting);
            g2.setPaint(this.paintSelecting);
            g2.draw(this.rectDragNormal);
        }

        g2.dispose();
    }

    private void drawShape(final MovableShape shapeMovable, final boolean selected, final Graphics2D g2)
    {
        final Shape shape = shapeMovable.getMovedShape();

        // Optimization (this "if" block could be removed).
        // don't draw the shape if it's not in the clipping area
        if (!g2.getClip().intersects(shape.getBounds2D()))
        {
            return;
        }
        g2.setStroke(this.stroke);
        g2.setPaint(this.paint);
        g2.draw(shape);

        if (selected)
        {
            g2.setPaint(new Color(200,255,200));
        }
        else
        {
            g2.setPaint(Color.WHITE);
        }
        g2.fill(shape);
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

        MovableShape shapeHit = null;
        for (final MovableShape shape : this.model)
        {
            // TODO account for the stroke when checking for hit shapes
            if (shape.getOriginalShape().contains(press))
            {
                shapeHit = shape;
                break;
            }
        }

        if (shapeHit != null)
        {
            this.moving = true;
            if (!this.selection.contains(shapeHit))
            {
                if (!extending)
                {
                    this.selection.clear();
                }
                this.selection.add(shapeHit);
            }
            else
            {
                if (extending)
                {
                    this.selection.remove(shapeHit);
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
        this.draggingTo = draggedTo;
        this.rectDrag = createRectFromPoints(this.pressedAt,this.draggingTo);
        this.rectDragNormal = normalizeRect(this.rectDrag);
        final Rectangle scrollTo = pointToRect(this.draggingTo);

        if (this.moving)
        {
            scrollRectToVisible(scrollTo);
            moveSelectedShapes();
            repaint();
        }
        else if (this.selecting)
        {
            scrollRectToVisible(scrollTo);
            if (!extending)
            {
                this.selection.clear();
            }
            for (final MovableShape shape : this.model)
            {
                // TODO account for the stroke when checking for hit shapes
                if (shape.getOriginalShape().intersects(this.rectDragNormal))
                {
                    this.selection.add(shape);
                }
            }
            repaint();
        }
    }

    private void released()
    {
        this.moving = false;
        this.selecting = false;
        this.pressedAt = null;
        this.draggingTo = null;
        this.rectDrag = null;
        this.rectDragNormal = null;
        for (final MovableShape shape : this.model)
        {
            shape.commit();
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

        Rectangle2D boundsShapes = null;
        for (final MovableShape shape : this.selection)
        {
            final Shape moved = move.createTransformedShape(shape.getOriginalShape());
            shape.setShape(moved);

            if (boundsShapes == null)
            {
                boundsShapes = moved.getBounds2D();
            }
            else
            {
                boundsShapes.add(moved.getBounds2D());
            }
        }
        constrainMovement(boundsShapes);
    }

    private void constrainMovement(Rectangle2D boundsShapes)
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
            for (final MovableShape shape : this.selection)
            {
                shape.setShape(moveBack.createTransformedShape(shape.getMovedShape()));
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
}
