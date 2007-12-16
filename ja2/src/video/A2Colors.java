/*
 * Created on Dec 15, 2007
 */
package video;

import java.awt.Color;

/**
 * Defines Apple ][ colors.
 *
 * @author Chris Mosher
 */
public class A2Colors
{
/*

This chart shows how to get from the lo-res color number (N) to the
HSV hue value. Also shows saturation, value, RGB equivalent, and name.

N = lo-res color number
BITS = bits of N
REVERSED = reversal of BITS
SIGNAL WAVEFORM = REVERSED diagrammed as waves (1 = high, 0 = low)
/\ = center of crest in SIGNAL WAVEFORM
POSITION = position of center on color circle (eighths of rotation = 45 degrees)
HUE = HSV hue value (degrees), calculated as ((POSITION * 45 degrees) + 33 degrees)
SAT = HSV saturation (percent)
VAL = HSV value (percent)
(Note: SAT and VAL are computed from the width of the wave crest, by some unknown method.)

 COLOR CIRCLE POSITION:  0 7 6 5 4 3 2 1  (HUE = 33+POSITION*45 DEGREES)
                        \/\/\/\/\/\/\/\/

             +HI-->     +-------+       
             |          |       |          COLOR BURST SIGNAL
             +LO-->     +       +-------


N BITS REVERSED         SIGNAL WAVEFORM    HUE     W   SAT VAL    RRGGBB    REF NAME         APPLE NAME
---------------------------------------------------------------------------------------------------------
                                                                            
             +HI-->     +----+                                              
1 0001 1000  |          | /\ |             348     1   100 100    FF0033    DARK  MAGENTA    MAGENTA
             +LO-->     +    +----------                                    
                                                                            
                                                                            
             +HI-->     ---------+  +---                                    
B 1011 1101  |            /\     |  |      348     3    25 100    FFBFCC    LIGHT MAGENTA    PINK
             +LO-->              +--+                                       
                                                                            
                                                                            
             +HI-->     +--------+                                          
3 0011 1100  |          |   /\   |         303     2   100 100    FF00F2    HIRES VIOLET     VIOLET
             +LO-->     +        +------                                    
                                                                            
                                                                            
             +HI-->         +----+                                          
2 0010 0100  |              | /\ |         258     1   100 100    4D00FF    DARK  BLUE       DARK BLUE
             +LO-->     ----+    +------                                    
                                                                            
                                                                            
             +HI-->     +------------+                                      
7 0111 1110  |          |     /\     |     258     3    50 100    D2BFFF    LIGHT BLUE       LIGHT BLUE
             +LO-->     +            +--                                    
                                                                            
                                                                            
             +HI-->         +--------+                                      
6 0110 0110  |              |   /\   |     213     2   100 100    0073FF    HIRES BLUE       MEDIUM BLUE
             +LO-->     ----+        +--                                    
                                                                            
                                                                            
             +HI-->             +----+                                      
4 0100 0010  |                  | /\ |     168     1   100  50    008066    DARK  BLUE-GREEN DARK GREEN
             +LO-->     --------+    +--                                    
                                                                            
                                                                            
             +HI-->     -+  +-----------                                    
E 1110 0111  |           |  |     /\       168     3   100  75    00BF99    LIGHT BLUE-GREEN AQUA
             +LO-->      +--+                                               
                                                                            
                                                                            
             +HI-->     -+      +-------                                    
C 1100 0011  |           |      |   /\     123     2   100  75    00BF0A    HIRES GREEN      BRIGHT GREEN
             +LO-->      +------+                                           
                                                                            
                                                                            
             +HI-->     -+          +---                                    
8 1000 0001  |           |          | /\    78     1   100  50    598000    DARK  BROWN      BROWN
             +LO-->      +----------+                                       
                                                                            
                                                                            
             +HI-->     -----+  +-------                                    
D 1011 1101  |               |  |     /\    78     3   100 100    B3FF00    LIGHT BROWN      YELLOW
             +LO-->          +--+                                           
                                                                            
                                                                            
             +HI-->     -----+      +---                                    
9 1001 1001  |          /\   |      |       33     2   100 100    FF8C00    HIRES ORANGE     ORANGE
             +LO-->          +------+                                       
                                                                            
                                                                            
             +HI-->     +----+  +----+                                      
5 0101 1010  |          | /\ |  | /\ |     N/A     1+1   0  75    BFBFBF    GREY             GREY
             +LO-->     +    +--+    +--                                    
                                                                            
                                                                            
             +HI-->     -+  +----+  +---                                    
A 1010 0101  |           |  | /\ |  | /\   N/A     1+1   0  75    BFBFBF    GREY             GREY
             +LO-->      +--+    +--+                                       
                                                                            
                                                                            
             +HI-->     ----------------                                    
F 1111 1111  |                             N/A     4     0 100    FFFFFF    WHITE            WHITE
             +LO-->                                                         
                                                                            
                                                                            
             +HI-->                                                         
0 0000 0000  |                             N/A     0     0   0    000000    BLACK            BLACK
             +LO-->     ----------------

*/



	// Columns from table above:

	// N:
	private static final int[] clr = { 0x1, 0xB, 0x3, 0x2, 0x7, 0x6, 0x4, 0xE, 0xC, 0x8, 0xD, 0x9, 0x5, 0xA, 0xF, 0x0 };
	// COLOR CIRCLE POSITION (above center of crest in SIGNAL WAVEFORM)
	private static final int[] pos = {   7,   7,   6,   5,   5,   4,   3,   3,   2,   1,   1,   0,  -1,  -1,  -1,  -1 };
	// SAT:
	private static final int[] sat = { 100,  25, 100, 100,  50, 100, 100, 100, 100, 100, 100, 100,   0,   0,   0,   0 };
	// VAL:
	private static final int[] val = { 100, 100, 100, 100, 100, 100,  50,  75,  75,  50, 100, 100,  75,  75, 100,   0 };
	// RRGGBB:
	public static final int[] COLOR = new int[clr.length];
	static
	{
		for (int i = 0; i < COLOR.length; ++i)
		{
			COLOR[clr[i]] = Color.HSBtoRGB(hue(i)/360f,sat[i]/100f,val[i]/100f);
		}
	}
	private static int hue(final int ipos)
	{
		return 33 /*degrees*/   +   ( pos[ipos] * 45 /*degrees*/ );
	}



	// REF_NAME defined as R,G,B values:
	public static final int BLACK            = COLOR[0x0];
	public static final int DARK_MAGENTA     = COLOR[0x1];
	public static final int DARK_BLUE        = COLOR[0x2];
	public static final int HIRES_VIOLET     = COLOR[0x3];
	public static final int DARK_BLUE_GREEN  = COLOR[0x4];
	public static final int GREY             = COLOR[0x5];
	public static final int HIRES_BLUE       = COLOR[0x6];
	public static final int LIGHT_BLUE       = COLOR[0x7];
	public static final int DARK_BROWN       = COLOR[0x8];
	public static final int HIRES_ORANGE     = COLOR[0x9];
//	public static final int GREY             = COLOR[0xA];
	public static final int LIGHT_MAGENTA    = COLOR[0xB];
	public static final int HIRES_GREEN      = COLOR[0xC];
	public static final int LIGHT_BROWN      = COLOR[0xD];
	public static final int LIGHT_BLUE_GREEN = COLOR[0xE];
	public static final int WHITE            = COLOR[0xF];



	// Test program to print out the RGB values
	public static void main(final String... args)
	{
		for (int i = 0; i < pos.length; ++i)
		{
			System.out.println(Integer.toHexString(COLOR[i]));
		}
	}

}
