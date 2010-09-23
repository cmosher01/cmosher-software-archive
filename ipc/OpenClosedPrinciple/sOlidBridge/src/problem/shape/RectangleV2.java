package problem.shape;

import support.DP2;

public class RectangleV2 extends Rectangle
{
	public RectangleV2(double x1, double y1, double x2, double y2)
	{
		super(x1,y1,x2,y2);
	}

	@Override
	protected void drawLine(double x1, double y1, double x2, double y2)
	{
		DP2.paint_straight_line(x1,y1,x2,y2);
	}
}
