package video;

/*
 * Created on Jan 21, 2008
 */
public final class Lowpass_1_5_MHz
{
	private static final int NZEROS = 5;
	private static final int NPOLES = 5;
	private static final int GAIN = 98;
	private final int[] xv = new int[NZEROS + 1];
	private final int[] yv = new int[NPOLES + 1];

	public int transition(final int next_input_value)
	{
		xv[0] = xv[1];
		xv[1] = xv[2];
		xv[2] = xv[3];
		xv[3] = xv[4];
		xv[4] = xv[5];
		xv[5] = next_input_value / GAIN;

		yv[0] = yv[1];
		yv[1] = yv[2];
		yv[2] = yv[3];
		yv[3] = yv[4];
		yv[4] = yv[5];
		mult();
		return yv[5];
	}

	private void mult()
	{
		yv[5] = (xv[0] + xv[5]) + 3 * (xv[1]+xv[4])  +((xv[2]+xv[3])<<2)    + (yv[2]>>2) - ((yv[3]*19)>>4)  +(yv[4]*7/4);
	}
}
