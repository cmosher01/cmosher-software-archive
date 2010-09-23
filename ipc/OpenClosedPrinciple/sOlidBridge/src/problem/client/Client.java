package problem.client;

import problem.shape.CircleV1;
import problem.shape.CircleV2;
import problem.shape.RectangleV1;
import problem.shape.RectangleV2;
import problem.shape.Shape;


public class Client
{
	public static void main(String[] args)
	{
		String dptype = args[0];

		// draw rectangle
		Shape sh1;
		if (dptype.equalsIgnoreCase("DP1"))
		{
			sh1 = new RectangleV1(10,10,50,30);
		}
		else if (dptype.equalsIgnoreCase("DP2"))
		{
			sh1 = new RectangleV2(10,10,50,30);
		}
		else
		{
			throw new IllegalArgumentException(args[0]);
		}
		sh1.draw();

		// draw circle
		Shape sh2;
		if (dptype.equalsIgnoreCase("DP1"))
		{
			sh2 = new CircleV1(30,30,5);
		}
		else if (dptype.equalsIgnoreCase("DP2"))
		{
			sh2 = new CircleV1(30,30,5);
		}
		else
		{
			throw new IllegalArgumentException(args[0]);
		}
		sh2.draw();
	}
}
