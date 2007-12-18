/*
 * Created on Dec 17, 2007
 */
package video;

import java.awt.Color;

public class A2Monochrome
{
	public static final int[] WHITE = new int[16];
	public static final int[] GREEN = new int[16];
	public static final int[] ORANGE = new int[16];
	static
	{
		for (int i = 0; i < WHITE.length; ++i)
		{
			int rgb = A2Colors.COLOR[i];
			double y = brightness((rgb >> 16) & 0xFF,(rgb >> 8) & 0xFF,rgb & 0xFF);
			WHITE[i] = Color.HSBtoRGB(0,0,(float)y);
			GREEN[i] = Color.HSBtoRGB(123F/360,1,(float)y);
			ORANGE[i] = Color.HSBtoRGB(33F/360,1,(float)y);
		}
	}

	static double brightness(int red, int green, int blue)
	{
	    final double r = red/255D;
	    final double g = green/255D;
	    final double b = blue/255D;

	    return 0.299*r + 0.587*g + 0.114*b;
	}
}
