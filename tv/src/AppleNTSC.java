/*
 * Created on Jan 18, 2008
 */
public class AppleNTSC
{
	/* Apple doesn't do interlace, and has 262 total lines per field */
	public static final int V = 262;
	// 70 blanked lines during VBL
	public static final int SYNC_LINES = 70;

	/*
	 * HBL takes 25 normal CPU cycles (14 clock cycles each).
	 * Visible width takes 39 normal CPU cycles plus 1 long CPU cycle (16 clock cycles).
	 * So, total clock cycles per row is H:
	 */
	public static final int H = (25+39)*14+(1)*16;

	public static final int SIGNAL_LEN = V*H;

	public static final int FP_START = 0;
	public static final int SYNC_START = FP_START+126;
	public static final int BP_START = SYNC_START+112;
	public static final int CB_START = BP_START+0;
	public static final int CB_END = CB_START+56;
	public static final int SPIKE = CB_END+42;
	public static final int PIC_START = CB_END+56;

	/* AnalogTV.signal is in IRE units, as defined below: */
	public static final int WHITE_LEVEL = 100;
	public static final int BLACK_LEVEL = 7;
	public static final int BLANK_LEVEL = 0;
	public static final int SYNC_LEVEL = -40;

	public static final int LEVEL_RANGE = WHITE_LEVEL-SYNC_LEVEL;

	public static final int CB_LEVEL = 20; // +/- 20 from 0
}
