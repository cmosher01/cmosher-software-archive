package video;

/*
 * Created on Jan 21, 2008
 */
public final class Lowpass_1_5_MHz
{
	private int x0,x1,x2;
	private int y0,y1,y2,y3,y4;

	public int transition(final int next_input_value)
	{
		x0 = x1; x1 = x2;
		x2 = next_input_value >> 3;

		y0 = y1; y1 = y2; y2 = y3; y3 = y4;
		y4 = x0+x2-y0/22+y1/3-y2+y3+(y3>>1);
		return y4;
	}
}
