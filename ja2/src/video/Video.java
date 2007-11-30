package video;

import gui.GUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
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
	private final GUI gui;
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
	private int[] currentLookupTable;

    static final int BLACK = Color.BLACK.getRGB();
    static final int GREEN = Color.GREEN.getRGB();

    static final int loresColors[] =
    {
    	0x000000,0xD00030,0x000080,0xFF00FF,0x008000,0x808080,0x0000FF,0x60A0FF,
    	0x805000,0xFF8000,0xC0C0C0,0xFF9080,0x00FF00,0xFFFF00,0x40FF90,0xFFFFFF
    };

    final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");





	public Video(final GUI gui, final Memory memory, final BufferedImage screenImage)
	{
		this.gui = gui;
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
			this.gui.showMessage(e.getMessage());
		}

		setCurrentLookupTable();
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
		setCurrentLookupTable();
		return this.dataByte; // emulates "floating bus"
	}

	private void setCurrentLookupTable()
	{
		if (this.swHiRes)
		{
			currentLookupTable = this.lutHiRes[this.swPage2];
		}
		else
		{
			currentLookupTable = this.lutText[this.swPage2];
		}
	}

	public void tick()
	{
		this.dataByte = this.memory.read(this.currentLookupTable[this.t]);

		plotDataByte();

		++this.t;

		if (this.t >= VideoAddressing.BYTES_PER_FIELD)
		{
			this.gui.updateScreen();
			updateFlashingState();
			this.t = 0;
		}
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
		int d = this.dataByte & 0xFF;
		if (isDisplayingText())
		{
			final boolean inverse = inverseChar(d);
			d = this.char_rom[(d << 3) + ((this.t / VideoAddressing.BYTES_PER_ROW) & 0x07)];
			if (inverse)
			{
				d = ~d;
			}
		}

		int x = this.t % VideoAddressing.BYTES_PER_ROW;
		if (x < VISIBLE_X_OFFSET)
		{
			return;
		}

		x -= VISIBLE_X_OFFSET;
		x *= VISIBLE_BITS_PER_BYTE;

		int y = this.t / VideoAddressing.BYTES_PER_ROW;
		if (y >= VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			return;
		}

		final int oy = y;
		y *= XSIZE;
		y += x;

		if (isDisplayingText() || this.swHiRes)
		{
			// text and hi-res
			// unrolled loop for speed
        	this.buf.setElem(y++, ((d & 0x01) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x02) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x04) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x08) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x10) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x20) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x40) != 0) ? GREEN : BLACK);

		}
		else
        {
			// lo-res
			final int i;
			if (((oy >> 2) & 1) != 0)
				i = (d >> 4) & 0xF;
			else
				i = d & 0xF;
			final int color = loresColors[i];
			// unrolled loop for speed
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        }
		// TODO high-order bit half-dot shift and hi-res colors
	}

	public boolean isText()
	{
		return this.swText;
	}

	public void dump() throws IOException
	{
		final String name = "dump"+this.fmt.format(new Date())+".png";
		ImageIO.write(this.screenImage,"PNG",new File(name));
	}

	public Image getImage()
	{
		return this.screenImage;
	}
}
