package video;

import gui.UI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import chipset.Memory;

/*
 * Created on Aug 2, 2007
 */
public class Video
{
	private final UI ui;
	private final Memory memory;

	private final byte[] char_rom = new byte[0x800];

	private boolean swText = true;
	private boolean swMixed;
	private int swPage2;
	private boolean swHiRes;

	// somewhat arbitrary starting point for scanning... helps start
	// an Apple ][ plus with a clean screen
	private int t = VideoAddressing.BYTES_PER_FIELD-12*VideoAddressing.BYTES_PER_ROW;
	private int f;
	private boolean flash;

	private byte prevDataByte;
	private byte dataByte;

	private final BufferedImage screenImage;
	private final DataBuffer buf;





    private static final int EOF = -1;

	private static final int VISIBLE_BITS_PER_BYTE = 7;

	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.VISIBLE_BYTES_PER_ROW;
	private static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;
	public static final Dimension SIZE = new Dimension(XSIZE,YSIZE);
	private static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	private static final int MIXED_TEXT_LINES = 4;
	private static final int ROWS_PER_TEXT_LINE = 8;
	private static final int MIXED_TEXT_CYCLE = (VideoAddressing.VISIBLE_ROWS_PER_FIELD-(MIXED_TEXT_LINES*ROWS_PER_TEXT_LINE))*VideoAddressing.BYTES_PER_ROW;

	private static final int FLASH_HALFPERIOD = 4;

	private int[] lutText0 = VideoAddressing.buildLUT(0x0400,0x0400);
	private int[] lutText1 = VideoAddressing.buildLUT(0x0800,0x0400);
	private int[][] lutText = { this.lutText0, this.lutText1 };
	private int[] lutHiRes0 = VideoAddressing.buildLUT(0x2000,0x2000);
	private int[] lutHiRes1 = VideoAddressing.buildLUT(0x4000,0x2000);
	private int[][] lutHiRes = { this.lutHiRes0, this.lutHiRes1 };

    static final int BLACK = Color.BLACK.getRGB();
    static final int WHITE = Color.WHITE.getRGB();
    static final int GREEN = Color.GREEN.getRGB();

    private static final int HIRES_MAGENTA = 0xFF00FF;
	private static final int HIRES_GREEN   = 0x00FF00;
	private static final int HIRES_BLUE    = 0x0080FF;
	private static final int HIRES_ORANGE  = 0xFF8000;

	static final int loresColors[] =
    {
    	Color.BLACK.getRGB(),
    	0xD00030,
    	0x000080,
    	HIRES_MAGENTA, //Color.MAGENTA.getRGB(),
    	0x008000,
    	0x555555,
    	HIRES_BLUE, //Color.BLUE.getRGB(),
    	0x60A0FF,
    	0x805000,
    	HIRES_ORANGE, //0xFF8000,
    	0xAAAAAA,
    	0xFF9080,
    	HIRES_GREEN, //Color.GREEN.getRGB(),
    	Color.YELLOW.getRGB(),
    	0x40FF90,
    	Color.WHITE.getRGB(),
    };





	public Video(final UI ui, final Memory memory, final BufferedImage screenImage)
	{
		this.ui = ui;
		this.memory = memory;
		this.screenImage = screenImage;
		this.buf = this.screenImage.getRaster().getDataBuffer();

		try
		{
			readCharRom();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			this.ui.showMessage(e.getMessage());
		}
	}

	private void readCharRom() throws IOException
	{
		final InputStream rom = Video.class.getResourceAsStream("3410036.BIN");
		int cc = 0;
		for (int c = rom.read(); c != EOF; c = rom.read())
		{
			if (cc < this.char_rom.length)
			{
				this.char_rom[cc] = xlateCharRom(c);
			}
			++cc;
		}
		rom.close();
		if (cc != 0x800)
		{
			throw new IOException("Text-character-ROM file 3410036.BIN is invalid: length is "+
				cc+" but should be 2048. Text may not be displayed correctly.");
		}
	}

	private static byte xlateCharRom(int b)
	{
		// xlateCharRom(abcdefgh) == 0hgfedcb
		byte r = 0;
		for (int i = 0; i < 7; ++i)
		{
			r <<= 1;
			if ((b & 1) != 0)
				r |= 1;
			b >>= 1;
		}
		return r;
	}

	public byte io(final int addr, @SuppressWarnings("unused") final byte b)
	{
		final int sw = (addr & 0x000E) >> 1;
		final boolean on = (addr & 0x0001) != 0;
		switch (sw)
		{
			case 0:
				this.swText = on; break;
			case 1:
				this.swMixed = on; break;
			case 2:
				this.swPage2 = on ? 1 : 0; break;
			case 3:
				this.swHiRes = on; break;
		}
		return this.dataByte; // emulates "floating bus"
	}

	public void tick()
	{
		setDataByte();
		plotDataByte();

		++this.t;

		if (this.t >= VideoAddressing.BYTES_PER_FIELD)
		{
			this.ui.updateScreen();
			updateFlashingState();
			this.t = 0;
		}
	}

	private void setDataByte()
	{
		this.prevDataByte = this.dataByte;

		final int[] lookupTable;
		if (isDisplayingText() || !this.swHiRes)
		{
			lookupTable = this.lutText[this.swPage2];
		}
		else
		{
			lookupTable = this.lutHiRes[this.swPage2];
		}
		this.dataByte = this.memory.read(lookupTable[this.t]);
	}

	private void updateFlashingState()
	{
		++this.f;

		if (this.f >= FLASH_HALFPERIOD)
		{
			this.f = 0;
			this.flash = ! this.flash;
		}
	}

	private boolean isDisplayingText()
	{
		return this.swText || (this.swMixed && this.t >= MIXED_TEXT_CYCLE);
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

    private void plotDataByte()
	{
		int x = this.t % VideoAddressing.BYTES_PER_ROW;
		final int ox = x;
		if (x < VISIBLE_X_OFFSET)
		{
			return;
		}

		int y = this.t / VideoAddressing.BYTES_PER_ROW;
		final int oy = y;
		if (y >= VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			return;
		}

		int d = this.dataByte & 0xFF;
		if (isDisplayingText())
		{
			final boolean inverse = inverseChar(d);
			d = this.char_rom[(d << 3) | (y & 0x07)];
			if (inverse)
			{
				d = ~d;
			}
		}

		x -= VISIBLE_X_OFFSET;
		x *= VISIBLE_BITS_PER_BYTE;
		y *= XSIZE;
		x += y;

		if (isDisplayingText())
		{
        	plotByteAsText(x,d);
		}
		else if (this.swHiRes)
		{
			plotByteAsHiRes(x,ox,d);
		}
		else
        {
			plotByteAsLoRes(x,oy,d);
        }
	}

	private void plotByteAsText(int x, int d)
	{
		this.buf.setElem(x++, ((d & 0x01) != 0) ? GREEN : BLACK);
		this.buf.setElem(x++, ((d & 0x02) != 0) ? GREEN : BLACK);
		this.buf.setElem(x++, ((d & 0x04) != 0) ? GREEN : BLACK);
		this.buf.setElem(x++, ((d & 0x08) != 0) ? GREEN : BLACK);
		this.buf.setElem(x++, ((d & 0x10) != 0) ? GREEN : BLACK);
		this.buf.setElem(x++, ((d & 0x20) != 0) ? GREEN : BLACK);
		this.buf.setElem(x++, ((d & 0x40) != 0) ? GREEN : BLACK);
	}

	private void plotByteAsLoRes(int x, final int oy, int d)
	{
		final int i;
		if (((oy >> 2) & 1) != 0)
			i = (d >> 4) & 0xF;
		else
			i = d & 0xF;
		final int color = loresColors[i];

		this.buf.setElem(x++, color);
		this.buf.setElem(x++, color);
		this.buf.setElem(x++, color);
		this.buf.setElem(x++, color);
		this.buf.setElem(x++, color);
		this.buf.setElem(x++, color);
		this.buf.setElem(x++, color);
	}

	private void plotByteAsHiRes(int x, int ox, int d)
	{
		final boolean odd   = (x & 0x01) != 0;
		final boolean shift = (d & 0x80) != 0;
		final int c0, c1;
		if (odd)
		{
			if (shift)
			{
				c0 = HIRES_BLUE; c1 = HIRES_ORANGE;
			}
			else
			{
				c0 = HIRES_MAGENTA; c1 = HIRES_GREEN;
			}
		}
		else
		{
			if (shift)
			{
				c0 = HIRES_ORANGE; c1 = HIRES_BLUE;
			}
			else
			{
				c0 = HIRES_GREEN; c1 = HIRES_MAGENTA;
			}
		}
		if (VISIBLE_X_OFFSET < ox)
			setHiRes(x-1, this.prevDataByte & 0x20, this.prevDataByte & 0x40, d & 0x01, c0);
		setHiRes(x++, VISIBLE_X_OFFSET < ox ? this.prevDataByte & 0x40 : 0, d & 0x01, d & 0x02, c1);
		setHiRes(x++, d & 0x01, d & 0x02, d & 0x04, c0);
		setHiRes(x++, d & 0x02, d & 0x04, d & 0x08, c1);
		setHiRes(x++, d & 0x04, d & 0x08, d & 0x10, c0);
		setHiRes(x++, d & 0x08, d & 0x10, d & 0x20, c1);
		setHiRes(x++, d & 0x10, d & 0x20, d & 0x40, c0);
		if (ox == VideoAddressing.BYTES_PER_ROW-1)
			setHiRes(x++, d & 0x20, d & 0x40, 0, c1);
	}

    private void setHiRes(int x, int leftBit, int bit, int rightBit, int color)
    {
    	this.buf.setElem(x, (bit == 0) ? BLACK : (leftBit == 0 && rightBit == 0) ? color : WHITE);
    }

    public boolean isText()
	{
		return this.swText;
	}
}
