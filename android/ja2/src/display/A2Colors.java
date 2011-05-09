/*
 * Created on Dec 15, 2007
 */
package display;

import android.graphics.Color;

/**
 * Defines Apple ][ colors, as computed by theory.
 *
 * @author Chris Mosher
 */
class A2Colors
{
/*

This chart shows how to get from the lo-res color number (N) to the
HSV hue value. Also shows saturation, value, and name.

N = lo-res color number
BITS = bits of N
REVERSED = reversal of BITS
SIGNAL WAVEFORM = REVERSED diagrammed as waves (1 = high, 0 = low)
/\ = center of crest in SIGNAL WAVEFORM
POSITION = position of center on color circle (eighths of rotation = 45 degrees)
HUE = HSV hue value (degrees), calculated as ((POSITION * 45 degrees) + 33 degrees)
SAT = HSV saturation (percent)
VAL = HSV value (percent)

Note: I don't know how SAT and VAL are computed; the numbers here are from
actual readings (see A2ColorsObserved) but are rounded to nearest multiple of 25,
except dark magenta whose value is moved up to 100 (which seems to be consistent
with the dark-light pattern of the other colors). So the colors with a 2 in column W
are assumed to be (SAT,VAL)==(100,100). The colors with W 1 are just dark versions
of the corresponding colors with W 3; the relation between W 1->3 for (SAT,VAL) is
assumed to be either (100,100)->(50,100) or (100,50)->(100,100).




COLOR CIRCLE POSITION:   0 7 6 5 4 3 2 1  (HUE = 33+POSITION*45 DEGREES)
                        \/\/\/\/\/\/\/\/

             +HI-->     +-------+       
             |          |       |          COLOR BURST SIGNAL
             +LO-->     +       +-------


N BITS REVERSED         SIGNAL WAVEFORM    HUE     W   SAT VAL    REF NAME         APPLE NAME
-----------------------------------------------------------------------------------------------
                         ___ ___ ___ ___
    BIT BOUNDARIES:     +   +   +   +   +

             +HI-->     +----+                                    
1 0001 1000  |          | /\ |             348     1   100 100    DARK  MAGENTA    MAGENTA
             +LO-->     +    +----------                          
                                                                  
                                                                  
             +HI-->     ---------+  +---                          
B 1011 1101  |            /\     |  |      348     3    50 100    LIGHT MAGENTA    PINK
             +LO-->              +--+                             
                                                                  
                                                                  
             +HI-->     +--------+                                
3 0011 1100  |          |   /\   |         303     2   100 100    HIRES VIOLET     VIOLET, PURPLE
             +LO-->     +        +------                          
                                                                  
                                                                  
             +HI-->         +----+                                
2 0010 0100  |              | /\ |         258     1   100 100    DARK  BLUE       DARK BLUE
             +LO-->     ----+    +------                          
                                                                  
                                                                  
             +HI-->     +------------+                            
7 0111 1110  |          |     /\     |     258     3    50 100    LIGHT BLUE       LIGHT BLUE
             +LO-->     +            +--                          
                                                                  
                                                                  
             +HI-->         +--------+                            
6 0110 0110  |              |   /\   |     213     2   100 100    HIRES BLUE       MEDIUM BLUE
             +LO-->     ----+        +--                          
                                                                  
                                                                  
             +HI-->             +----+                            
4 0100 0010  |                  | /\ |     168     1   100  50    DARK  BLUE-GREEN DARK GREEN
             +LO-->     --------+    +--                          
                                                                  
                                                                  
             +HI-->     -+  +-----------                          
E 1110 0111  |           |  |     /\       168     3   100 100    LIGHT BLUE-GREEN AQUA
             +LO-->      +--+                                     
                                                                  
                                                                  
             +HI-->     -+      +-------                          
C 1100 0011  |           |      |   /\     123     2   100 100    HIRES GREEN      BRIGHT GREEN, LIGHT GREEN
             +LO-->      +------+                                 
                                                                  
                                                                  
             +HI-->     -+          +---                          
8 1000 0001  |           |          | /\    78     1   100  50    DARK  BROWN      BROWN
             +LO-->      +----------+                             
                                                                  
                                                                  
             +HI-->     -----+  +-------                          
D 1101 1011  |               |  |     /\    78     3   100 100    LIGHT BROWN      YELLOW
             +LO-->          +--+                                 
                                                                  
                                                                  
             +HI-->     -----+      +---                          
9 1001 1001  |          /\   |      |       33     2   100 100    HIRES ORANGE     ORANGE
             +LO-->          +------+                             
                                                                  
                                                                  
             +HI-->     +----+  +----+                            
5 0101 1010  |          | /\ |  | /\ |     N/A     1+1   0  50    GREY             GREY
             +LO-->     +    +--+    +--                          
                                                                  
                                                                  
             +HI-->     -+  +----+  +---                          
A 1010 0101  |           |  | /\ |  | /\   N/A     1+1   0  50    GREY             GREY
             +LO-->      +--+    +--+                             
                                                                  
                                                                  
             +HI-->     ----------------                          
F 1111 1111  |                             N/A     4     0 100    WHITE            WHITE
             +LO-->                                               
                                                                  
                                                                  
             +HI-->                                               
0 0000 0000  |                             N/A     0     0   0    BLACK            BLACK
             +LO-->     ----------------

*/



	// Columns from table above:

	// N:
	private static final int[] clr = { 0x1, 0xB, 0x3, 0x2, 0x7, 0x6, 0x4, 0xE, 0xC, 0x8, 0xD, 0x9, 0x5, 0xA, 0xF, 0x0 };
	// COLOR CIRCLE POSITION (above center of crest in SIGNAL WAVEFORM):
	private static final int[] pos = {   7,   7,   6,   5,   5,   4,   3,   3,   2,   1,   1,   0,  -1,  -1,  -1,  -1 };
	// SAT:
	private static final int[] sat = { 100,  50, 100, 100,  50, 100, 100, 100, 100, 100, 100, 100,   0,   0,   0,   0 };
	// VAL:
	private static final int[] val = { 100, 100, 100, 100, 100, 100,  50, 100, 100,  50, 100, 100,  50,  50, 100,   0 };
	// RRGGBB:
	public static final int[] COLOR = new int[clr.length];
	static
	{
		for (int i = 0; i < COLOR.length; ++i)
		{
			COLOR[clr[i]] = Color.HSVToColor(new float[] {hue(i),sat[i]/100f,val[i]/100f});
		}
	}
	private static int hue(final int ipos)
	{
		return 33 /*degrees*/   +   ( pos[ipos] * 45 /*degrees*/ );
	}
}
