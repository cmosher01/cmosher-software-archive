package solution.drawing;

import support.DP2;

public class DrawingV2 implements DrawingPkg
{
	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		DP2.paint_straight_line(x1,x2,y1,y2);
	}

	@Override
	public void drawCircle(double x, double y, double r)
	{
		DP2.paint_arc(x,y,r,0,360);
	}
}
