/*
 * Created on Jan 18, 2008
 */
public class ANALOGT_V
{
	/* We don't handle interlace here */
	public static final int V=262;
//	public static final int TOP=30;
//	public static final int VISLINES=200;
//	public static final int BOT=TOP + VISLINES;

	/* This really defines our sampling rate; 4x the colorburst
	   frequency. Handily equal to the Apple II's dot clock.
	   You could also make a case for using 3x the colorburst freq;
	   but 4x isn't hard to deal with. */
	/*
	 * HBL takes 25 normal CPU cycles (14 clock cycles each).
	 * Visible width takes 39 normal CPU cycles plus 1 long CPU cycle (16 clock cycles).
	 * So, total clock cycles per row is H:
	 */
	public static final int H=(25+39)*14+(1)*16;

	/* Each line is 63500 nS long. The sync pulse is 4700 nS long; etc.
	   Define sync; back porch; colorburst; picture; and front porch
	   positions */
//	public static final int SYNC_START=0;
//	public static final int BP_START=4700*H/63500;
//	public static final int CB_START=5800*H/63500;
//	public static final int CB_END=CB_START+36;
//	/* signal[row][PIC_START] is the first displayed pixel */
//	public static final int PIC_START=9400*H/63500;
//	public static final int PIC_LEN=52600*H/63500;
//	public static final int FP_START=62000*H/63500;
//	public static final int PIC_END=FP_START;

	public static final int SYNC_START=0;
	public static final int BP_START=SYNC_START+112;
	public static final int CB_START=BP_START+0;
	public static final int CB_END=CB_START+56;
	/* signal[row][PIC_START] is the first displayed pixel */
	public static final int SPIKE=CB_END+52;
	public static final int PIC_START=CB_END+56;
	public static final int PIC_LEN=562;
	public static final int PIC_END=PIC_START+PIC_LEN;
	public static final int FP_START=PIC_END;

	/* TVs scan past the edges of the picture tube; so normally you only
	   want to use about the middle 3/4 of the nominal scan line.
	*/
//	public static final int VIS_START=PIC_START + (PIC_LEN*1/8);
//	public static final int VIS_END=PIC_START + (PIC_LEN*7/8);
//	public static final int VIS_LEN=VIS_END-VIS_START;

//	public static final int HASHNOISE_LEN=6;

//	public static final int GHOSTFIR_LEN=4;

	/* analogtv.signal is in IRE units; as defined below: */
	public static final int WHITE_LEVEL=100;
//	public static final int GRAY50_LEVEL=55;
//	public static final int GRAY30_LEVEL=35;
	public static final int BLACK_LEVEL=7;
	public static final int BLANK_LEVEL=0;
	public static final int SYNC_LEVEL=-40;
	public static final int CB_LEVEL=20; // +/- 20 from 0

	public static final int SIGNAL_LEN=V*H;

	/* The number of intensity levels we deal with for gamma correction &c */
//	public static final int CV_MAX=1024;
}
