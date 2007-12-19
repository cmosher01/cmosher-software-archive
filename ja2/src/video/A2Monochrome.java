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

	public static void main(final String... args)
	{
		for (int i = 0; i < 16; ++i)
		{
			final int rgb = A2Colors.COLOR[i];
			float red = ((rgb >> 16) & 0xFF) / 255F;
			float green = ((rgb >> 8) & 0xFF) / 255F;
			float blue = ((rgb) & 0xFF) / 255F;
			double y = y(red,green,blue);
			double i2 = i(red,green,blue);
			double q = q(red,green,blue);
			System.out.print(A2Colors.clr[i]);
			System.out.print(Integer.toHexString(rgb));
			System.out.print(" Y: "+(int)(y*100));
			System.out.print(" I: "+i2);
			System.out.print(" Q: "+q);
			System.out.println();
//[ Y ]     [ 0.299   0.587   0.114 ] [ R ]
//[ I ]  =  [ 0.596  -0.275  -0.321 ] [ G ]
//[ Q ]     [ 0.212  -0.523   0.311 ] [ B ]

//[ R ]     [ 1   0.956   0.621 ] [ Y ]
//[ G ]  =  [ 1  -0.272  -0.647 ] [ I ]
//[ B ]     [ 1  -1.105   1.702 ] [ Q ]
		}
		for (double i = -.5957; i < .5957; i += 0.29785)
		{
			for (double q = -.5226; q < .5226; q += 0.2613)
			{
				float[] hsbvals = new float[3];
				Color.RGBtoHSB(red(1,i,q),green(1,i,q),blue(1,i,q),hsbvals);
				System.out.print("H: "+hsbvals[0]*360);
				System.out.print(" S: "+hsbvals[0]*100);
				System.out.print(" B: "+hsbvals[0]*100);
				System.out.println();
			}
		}
	}

	private static double y(double r, double g, double b)
	{
	    return 0.299*r + 0.587*g + 0.114*b;
	}
	private static double i(double r, double g, double b)
	{
	    return 0.596*r - 0.275*g - 0.321*b;
	}
	private static double q(double r, double g, double b)
	{
	    return 0.212*r - 0.523*g + 0.311*b;
	}
	//Y in [0,1]
	//I in [-0.5957, 0.5957]
	//Q in [-0.5226, 0.5226]
	private static int red(double y, double i, double q)
	{
		return (int)Math.rint(255*(y + .956D*i + .621D*q));
	}
	private static int green(double y, double i, double q)
	{
		return (int)Math.rint(255*(y - .272D*i - .647D*q));
	}
	private static int blue(double y, double i, double q)
	{
		return (int)Math.rint(255*(y -1.105D*i +1.702D*q));
	}
}
