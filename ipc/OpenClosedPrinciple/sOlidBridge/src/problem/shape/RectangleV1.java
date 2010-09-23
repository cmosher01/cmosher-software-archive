package problem.shape;

import support.DP1;

public class RectangleV1 extends Rectangle
{
	public RectangleV1(double x1, double y1, double x2, double y2)
	{
		super(x1,y1,x2,y2);
	}

	@Override
	protected void drawLine(double x1, double y1, double x2, double y2)
	{
		DP1.paintLine(x1,y1,x2,y2);
	}
}
