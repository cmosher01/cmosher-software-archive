package video;

import java.io.IOException;
import chipset.AddressBus;
import chipset.TimingGenerator;



/*
 * Created on Aug 2, 2007
 */
public class Video
{
	private static final int[][] textAddrTables = { VideoAddressing.buildLUT(0x0400,0x0400), VideoAddressing.buildLUT(0x0800,0x0400) };
	private static final int[][] hiresAddrTables = { VideoAddressing.buildLUT(0x2000,0x2000), VideoAddressing.buildLUT(0x4000,0x2000) };

	public static final int XSIZE = VideoAddressing.VISIBLE_BITS_PER_BYTE*VideoAddressing.VISIBLE_BYTES_PER_ROW;
	public static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;

	public static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	/*
	 * Note: on the real Apple ][, text flashing rate is not really controlled by the system timing generator,
	 * but rather by a separate 2 Hz 555 timing chip driven by a simple capacitor.
	 */
	private static final int FLASH_HALF_PERIOD = TimingGenerator.AVG_CPU_HZ/4; // 2 Hz period = 4 Hz half-period






	private final VideoMode mode;
	private final AddressBus addressBus;
	private final PictureGenerator picgen;

	private final TextCharacters textRows = new TextCharacters();



	private int t;

	private boolean flash;
	private int cflash;





	public Video(final VideoMode mode, final AddressBus addressBus, final PictureGenerator picgen) throws IOException
	{
		this.mode = mode;
		this.addressBus = addressBus;
		this.picgen = picgen;

		readCharacterRom();
	}

	private void readCharacterRom() throws IOException
	{
		int off = this.textRows.read();
		// TODO: move this code out of here (let read throw)
//		if (off != 0)
//		{
//			final String big;
//			if (off > 0)
//			{
//				big = "big";
//			}
//			else
//			{
//				big = "small";
//				off = -off;
//			}
//			this.ui.showMessage("Text-character-ROM file GI2513.ROM is invalid: the file is "+
//				off+" bytes too "+big+". Text may not be displayed correctly.");
//		}
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
