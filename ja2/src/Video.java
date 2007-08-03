/*
 * Created on Aug 2, 2007
 */
public class Video implements Clock.Timed
{
	private final Memory memory;

	private VideoMode mode;
	private int page;

	private int t;

	private int[] lutText0 = VideoAddressing.buildLUT(0x0400,0x0400);
	private int[] lutText1 = VideoAddressing.buildLUT(0x0800,0x0800);
	private int[][] lutText = { lutText0, lutText1 };
	private int[][] lutLoRes = { lutText0, lutText1 };
	private int[] lutHiRes0 = VideoAddressing.buildLUT(0x2000,0x2000);
	private int[] lutHiRes1 = VideoAddressing.buildLUT(0x4000,0x2000);
	private int[][] lutHiRes = { lutHiRes0, lutHiRes1 };

	Video(final Memory memory)
	{
		this.memory = memory;
	}

	/**
	 * 
	 * @param mode VideoMode
	 * @param page 0 or 1
	 */
	public void setMode(final VideoMode mode, final int page)
	{
		this.mode = mode;
		this.page = page;

	}

	public void tick()
	{
		int a = 0;
		switch (this.mode)
		{
			case TEXT:
				a = lutText[page][t];
			break;
			case LORES:
				a = lutLoRes[page][t];
			break;
			case HIRES:
				a = lutHiRes[page][t];
			break;
		}

		int d = memory.read(a);

		++this.t;
		this.t %= VideoAddressing.BYTES_PER_FRAME;
	}
}
