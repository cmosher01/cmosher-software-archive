package solution.client;

import solution.drawing.DrawingPkg;
import solution.drawing.DrawingV1;
import solution.drawing.DrawingV2;
import solution.shape.Circle;
import solution.shape.Rectangle;
import solution.shape.Shape;

public class Client
{
	public static void main(String[] args)
	{
		String dptype = args[0];

		DrawingPkg dp;
		if (dptype.equalsIgnoreCase("DP1"))
		{
			dp = new DrawingV1();
		}
		else if (dptype.equalsIgnoreCase("DP2"))
		{
			dp = new DrawingV2();
		}
		else
		{
			throw new IllegalArgumentException(args[0]);
		}

		Shape sh1 = new Rectangle(dp,10,10,50,30);
		sh1.draw();
		Shape sh2 = new Circle(dp,30,30,5);
		sh2.draw();
	}
}
