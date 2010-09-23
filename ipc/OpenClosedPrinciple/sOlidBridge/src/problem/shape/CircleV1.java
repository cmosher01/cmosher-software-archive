package problem.shape;

import support.DP1;

public class CircleV1 extends Circle
{
	public CircleV1(double x, double y, double r)
	{
		super(x,y,r);
	}

	@Override
	protected void drawCircle(double x, double y, double r)
	{
		DP1.paintCircle(x,y,r);
	}
}
