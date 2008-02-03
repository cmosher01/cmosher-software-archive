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
		this.x0 = this.x1; this.x1 = this.x2;
		this.x2 = next_input_value >> 3;

		this.y0 = this.y1; this.y1 = this.y2; this.y2 = this.y3; this.y3 = this.y4;
		this.y4 = this.x0+this.x2-this.y0/22+this.y1/3-this.y2+this.y3+(this.y3>>1);
		return this.y4;
	}
}
