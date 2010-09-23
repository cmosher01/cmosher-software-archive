package problem.shape;

import support.DP2;

public class CircleV2 extends Circle
{
	public CircleV2(double x, double y, double r)
	{
		super(x,y,r);
	}

	@Override
	protected void drawCircle(double x, double y, double r)
	{
		DP2.paint_arc(x,y,r,0,360);
	}
}
