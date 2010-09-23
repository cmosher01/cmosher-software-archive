package solution.shape;

import solution.drawing.DrawingPkg;

public class Circle extends Shape
{
	private final double _x, _y, _r;

	public Circle(DrawingPkg dp, double x, double y, double r)
	{
		super(dp);
		_x = x;
		_y = y;
		_r = r;
	}

	@Override
	public void draw()
	{
		_dp.drawCircle(_x,_y,_r);
	}
}
