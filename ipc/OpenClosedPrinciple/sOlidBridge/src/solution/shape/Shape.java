package solution.shape;

import solution.drawing.DrawingPkg;

public abstract class Shape
{
	protected final DrawingPkg _dp;

	protected Shape(DrawingPkg dp)
	{
		_dp = dp;
	}

	abstract public void draw();
}
