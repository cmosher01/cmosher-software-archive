package solution.shape;

import solution.drawing.DrawingPkg;

public class Rectangle extends Shape
{
	private final double _x1, _y1, _x2, _y2;

	public Rectangle(DrawingPkg dp, double x1, double y1, double x2, double y2)
	{
		super(dp);
		_x1 = x1;
		_y1 = y1;
		_x2 = x2;
		_y2 = y2;
	}

	@Override
	public void draw()
	{
		_dp.drawLine(_x1,_y1,_x2,_y1);
		_dp.drawLine(_x2,_y1,_x2,_y2);
		_dp.drawLine(_x2,_y2,_x1,_y2);
		_dp.drawLine(_x1,_y2,_x1,_y1);
	}
}
