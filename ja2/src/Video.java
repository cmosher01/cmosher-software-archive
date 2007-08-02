/*
 * Created on Aug 2, 2007
 */
public class Video implements Clock.Timed
{
	private final Memory memory;

	private VideoMode mode;
	private int page;

	private int base;
	private int mod;
	private int cur;

	private int scanbyte;

	private int scanline;

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

		switch (this.mode)
		{
			case TEXT:
				this.base = 0x0400*this.page;
				this.mod = 0x0400;
			break;
			case LORES:
				this.base = 0x0400*this.page;
				this.mod = 0x0400; //???
			break;
			case HIRES:
				this.base = 0x2000*this.page;
				this.mod = 0x2000;
				this.cur = mod(this.base-25);
				this.scanbyte = 0;
				this.scanline = 0;
			break;
		}
	}

	private int mod(int i)
	{
		while (i < 0)
		{
			i += this.mod;
		}
		return i % this.mod;
	}

	public void tick()
	{
		memory.read(this.cur);

		if (this.scanbyte > 24)
		{
			// display
		}

		this.cur = mod(this.cur + 1);

		++this.scanbyte;
		if (this.scanbyte > 64)
		{
			this.scanbyte = 0;
			++this.scanline;
			this.cur += 0x0400;
			if (this.cur >= this.mod)
			{
				this.cur -= 0x1F80;
			}
			if (this.scanline > 0) { } //???
		}
	}
}
