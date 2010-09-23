package solution.drawing;

import support.DP1;

public class DrawingV1 implements DrawingPkg
{
	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		DP1.paintLine(x1,y1,x2,y2);
	}

	@Override
	public void drawCircle(double x, double y, double r)
	{
		DP1.paintCircle(x,y,r);
	}
}
