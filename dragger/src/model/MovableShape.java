/*
 * Created on Apr 25, 2006
 */
package model;

import java.awt.Shape;

public class MovableShape
{
    private Shape shape;
    private Shape shapeMoved;
    private boolean moving;

    public MovableShape(final Shape shape)
    {
        this.shape = shape;
        this.shapeMoved = shape;
    }

    public void setShape(final Shape shape)
    {
        this.shapeMoved = shape;
        this.moving = true;
    }

    public Shape getOriginalShape()
    {
        return this.shape;
    }

    public Shape getMovedShape()
    {
        return this.shapeMoved;
    }

    public void commit()
    {
        this.shape = this.shapeMoved;
        this.moving = false;
    }

    public void rollBack()
    {
        this.shapeMoved = this.shape;
        this.moving = false;
    }

    public boolean isMoving()
    {
        return this.moving;
    }
}
