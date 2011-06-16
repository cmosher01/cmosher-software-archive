package video;

import chipset.AddressBus;
import chipset.TimingGenerator;



/*
 * Created on Aug 2, 2007
 */
public class Video
{
	private static final int[][] textAddrTables = { VideoAddressing.buildLUT(0x0400,0x0400), VideoAddressing.buildLUT(0x0800,0x0400) };
	private static final int[][] hiresAddrTables = { VideoAddressing.buildLUT(0x2000,0x2000), VideoAddressing.buildLUT(0x4000,0x2000) };

	/*
	 * Note: on the real Apple ][, text flashing rate is not really controlled by the system timing generator,
	 * but rather by a separate 2 Hz 555 timing chip driven by a simple capacitor.
	 */
	private static final int FLASH_HALF_PERIOD = TimingGenerator.AVG_CPU_HZ/4; // 2 Hz period = 4 Hz half-period






	private final VideoMode mode;
	private final AddressBus addressBus;
	private final PictureGenerator picgen;

	private final TextCharacters textRows;



	private int t;

	private boolean flash;
	private int cflash;





	public Video(final VideoMode mode, final AddressBus addressBus, final PictureGenerator picgen, final TextCharacters textRows)
	{
		this.mode = mode;
		this.addressBus = addressBus;
		this.picgen = picgen;
		this.textRows = textRows;
	}

	public void powerOn()
	{
		this.t = 0;
		this.flash = false;
		this.cflash = 0;
	}

	public void tick()
	{
		final byte data = getDataByte();
		final int rowToPlot = getRowToPlot(data);

		this.picgen.tick(this.t,rowToPlot);

		updateFlash();

		++this.t;

		if (this.t >= VideoAddressing.BYTES_PER_FIELD)
		{
			this.t = 0;
		}
	}

	private void updateFlash()
	{
		++this.cflash;
		if (this.cflash >= FLASH_HALF_PERIOD)
		{
			this.flash = !this.flash;
			this.cflash = 0;
		}
	}

	private byte getDataByte()
	{
		final int[][] addrTables;
		// TODO should fix the mixed-mode scanning during VBL (see U.A.][, p. 5-13)
		if (this.mode.isDisplayingText(this.t) || !this.mode.isHiRes())
		{
			addrTables = Video.textAddrTables;
		}
		else
		{
			addrTables = Video.hiresAddrTables;
		}
		return this.addressBus.read(addrTables[this.mode.getPage()][this.t]);
	}

	private int getRowToPlot(int d)
	{
		if (this.mode.isDisplayingText(this.t))
		{
			d &= 0xFF;
			final boolean inverse = inverseChar(d);
			final int y = this.t / VideoAddressing.BYTES_PER_ROW;
			d = this.textRows.get(((d & 0x3F) << 3) | (y & 0x07));
			if (inverse)
			{
				d = ~d & 0x7F;
			}
		}

		return d & 0xFF;
	}

	private boolean inverseChar(final int d)
	{
		final boolean inverse;

		final int cs = (d >> 6) & 3;
		if (cs == 0)
		{
			inverse = true;
		}
		else if (cs == 1)
		{
			inverse = this.flash;
		}
		else
		{
			inverse = false;
		}

		return inverse;
	}
}
