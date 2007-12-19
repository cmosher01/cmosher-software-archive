/*
 * Created on Dec 15, 2007
 */
package video;

import java.awt.Color;

/**
 * Defines Apple ][ colors, as captured and measured
 * from my Apple //c.
 *
 * @author Chris Mosher
 */
class A2ColorsObserved
{
/*

These HSV were read from an image captured
from my Apple //c, through Pansonic PV-GS400 camcorder,
converted using AviSynth ConvertToRGB24(matrix="PC.601")
Exported to PNG with ImageWriter, then used GIMP to
change the levels so WHITE was 0,0,100 HSV, and BLACK
was 0,0,0 HSV. Then read the actual HSV values of the
other colors and put the nominal values in this table:
354  98  66 magenta
344  59 100 pink
300  99 100 purple
256  98  99 dark blue
276  55 100 light blue
212  99 100 medium blue
169 100  41 dark green
170  92  98 aqua
120 100  93 light green
 99 100  40 brown
 80 100  95 yellow
 28 100  91 orange
n/a   0  41 grey
n/a   1  41 grey (tweaked to 0 sat)
n/a   0 100 white
n/a   0   1 black (tweaked to 0 val)

*/



	// Columns from table above:

	// N:
	private static final int[] clr = { 0x1, 0xB, 0x3, 0x2, 0x7, 0x6, 0x4, 0xE, 0xC, 0x8, 0xD, 0x9, 0x5, 0xA, 0xF, 0x0 };
	// COLOR CIRCLE POSITION (above center of crest in SIGNAL WAVEFORM)
	private static final int[] hue = { 354, 344, 300, 256, 276, 212, 169, 170, 120,  99,  80,  23,  -1,  -1,  -1,  -1 };
	// SAT:
	private static final int[] sat = {  98,  59,  99,  98,  55,  99, 100,  92, 100, 100, 100, 100,   0,   0,   0,   0 };
	// VAL:
	private static final int[] val = {  66, 100, 100,  99, 100, 100,  41,  98,  93,  40,  95,  91,  41,  41, 100,   0 };
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
