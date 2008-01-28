package video;

/*
 * Created on Jan 21, 2008
 */
public final class Lowpass_3_58_MHz
{
	private static final int NZEROS = 6;
	private static final int NPOLES = 6;
	private static final int GAIN = 12;
	private final int[] xv = new int[NZEROS + 1];
	private final int[] yv = new int[NPOLES + 1];

	public int transition(final int next_input_value)
	{
		xv[0] = xv[1];
		xv[1] = xv[2];
		xv[2] = xv[3];
		xv[3] = xv[4];
		xv[4] = xv[5];
		xv[5] = xv[6];
		xv[6] = next_input_value / GAIN;

		yv[0] = yv[1];
		yv[1] = yv[2];
		yv[2] = yv[3];
		yv[3] = yv[4];
		yv[4] = yv[5];
		yv[5] = yv[6];

		mult();

        return yv[6];
	}
	private void mult()
	{
        yv[6] = (xv[0] + xv[6]) + ((xv[1]+xv[5])<<2) + 7*(xv[2]+xv[4]) + (xv[3]<<3) - (yv[2]>>6) - (yv[3]>>3) - (yv[4]>>1) - yv[5];
	}
}
