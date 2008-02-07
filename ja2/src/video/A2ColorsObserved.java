/*
 * Created on Dec 15, 2007
 */
package video;

import java.awt.Color;

/**
 * Defines Apple ][ colors, as estimated by eye from my Apple ][ plus and Sony CRT TV.
 *
 * @author Chris Mosher
 */
public class A2ColorsObserved
{
	// N:
	private static final int[] clr = { 0x1, 0xB, 0x3, 0x2, 0x7, 0x6, 0x4, 0xE, 0xC, 0x8, 0xD, 0x9, 0x5, 0xA, 0xF, 0x0 };
	// COLOR CIRCLE POSITION (above center of crest in SIGNAL WAVEFORM)
	private static final int[] hue = { 342, 342, 277, 233, 233, 213, 160, 160,  75,  33,  52,  24,  -1,  -1,  -1,  -1 };
	// SAT:
	private static final int[] sat = { 100,  50,  75, 100,  50, 100, 100, 100, 100, 100, 100, 100,   0,   0,   0,   0 };
	// VAL:
	private static final int[] val = {  67, 100, 100,  75, 100, 100,  33, 100,  75,  50, 100, 100,  50,  50, 100,   0 };
	// RRGGBB:
	public static final int[] COLOR = new int[clr.length];
	static
	{
		for (int i = 0; i < COLOR.length; ++i)
		{
			COLOR[clr[i]] = Color.HSBtoRGB(hue[i]/360f,sat[i]/100f,val[i]/100f);
		}
	}
}
