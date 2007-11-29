package video;

import gui.GUI;
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
	private final Memory memory;

	private boolean swText = true;
	private boolean swMixed;
	private int swPage2;
	private boolean swHiRes;

	// somewhat arbitrary starting point for scanning... helps starting
	// an Apple ][ plus with a clean screen
	private int t = VideoAddressing.BYTES_PER_FIELD-12*VideoAddressing.BYTES_PER_ROW;
	private int f;
	private boolean flash;

	private int data;

	private int[] lutText0 = VideoAddressing.buildLUT(0x0400,0x0400);
	private int[] lutText1 = VideoAddressing.buildLUT(0x0800,0x0400);
	private int[][] lutText = { this.lutText0, this.lutText1 };
	private int[] lutHiRes0 = VideoAddressing.buildLUT(0x2000,0x2000);
	private int[] lutHiRes1 = VideoAddressing.buildLUT(0x4000,0x2000);
	private int[][] lutHiRes = { this.lutHiRes0, this.lutHiRes1 };

	private final byte[] char_rom = new byte[0x800];




	private static final int VISIBLE_BITS_PER_BYTE = 7;

	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.VISIBLE_BYTES_PER_ROW;
	private static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;
	public static final Dimension SIZE = new Dimension(XSIZE,YSIZE);
	private static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	private static final int MIXED_TEXT_LINES = 4;
	private static final int ROWS_PER_TEXT_LINE = 8;
	private static final int MIXED_TEXT_CYCLE = (VideoAddressing.VISIBLE_ROWS_PER_FIELD-(MIXED_TEXT_LINES*ROWS_PER_TEXT_LINE))*VideoAddressing.BYTES_PER_ROW;

	BufferedImage screenImage;
	DataBuffer buf;

	private final GUI gui;




	public Video(final GUI gui, final Memory memory)
	{
		this.gui = gui;
		this.memory = memory;

		try
		{
			readCharRom();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			this.gui.showMessage(e.getMessage());
		}
	}

	public void stopped()
	{
	}

	private static final int EOF = -1;

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
			this.gui.showMessage("Text-character-ROM file 3410036.BIN is invalid: length is "+
				cc+" but should be 2048. Text may not be displayed correctly.");
		}
	}

	private static byte xlateCharRom(int b)
	{
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

	public byte io(final int addr, final byte b)
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
		return (byte)this.data; // emulates "floating bus"
	}

	private static final int FLASH_HALFPERIOD = 4;

	public void tick()
	{
		final int a = getAddr();
		this.data = this.memory.read(a);

		int d = this.data;
		boolean inverse = false;
		if (this.swText || (this.swMixed && this.t >= MIXED_TEXT_CYCLE))
		{
			if (d >= 0)
			{
				if ((d >> 6) == 1)
				{
					inverse = this.flash;
				}
				else
				{
					inverse = true;
				}
			}
			d = this.char_rom[((d&0x7F)<<3)+((this.t / VideoAddressing.BYTES_PER_ROW) & 0x07)];
		}
		plotByte(this.t,d,inverse);

		++this.t;

		if (this.t >= VideoAddressing.BYTES_PER_FIELD)
		{
			this.gui.updateScreen(this.screenImage);
			this.t = 0;
			++this.f;
			if (this.f >= FLASH_HALFPERIOD)
			{
				this.f = 0;
				this.flash = ! this.flash;
			}
		}
	}

	private int getAddr()
	{
		if (this.swHiRes)
		{
			return this.lutHiRes[this.swPage2][this.t];
		}

		return this.lutText[this.swPage2][this.t];
	}

	private void createOffscreenImage()
	{
		this.screenImage = new BufferedImage(SIZE.width,SIZE.height,BufferedImage.TYPE_INT_RGB);
		this.buf = this.screenImage.getRaster().getDataBuffer();
	}

    static final int BLACK = Color.BLACK.getRGB();
    static final int GREEN = Color.GREEN.getRGB();

    static final int loresColors[] =
    {
    	0x000000,0xD00030,0x000080,0xFF00FF,0x008000,0x808080,0x0000FF,0x60A0FF,
    	0x805000,0xFF8000,0xC0C0C0,0xFF9080,0x00FF00,0xFFFF00,0x40FF90,0xFFFFFF
    };

    private void plotByte(int tt, int d, boolean inverse)
	{
		if (this.screenImage == null)
		{
			createOffscreenImage();
		}
		int x = tt % VideoAddressing.BYTES_PER_ROW - VISIBLE_X_OFFSET;
		if (x < 0)
		{
			return;
		}
		x *= VISIBLE_BITS_PER_BYTE;
		int y = tt / VideoAddressing.BYTES_PER_ROW;
		final int oy = y;
		if (y >= VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			return;
		}


		y *= XSIZE;
		y += x;
		if (!this.swText && !this.swHiRes && !(this.swMixed && this.t >= MIXED_TEXT_CYCLE))
		{
			// lo-res
			final int i;
			if (((oy >> 2) & 1) != 0)
				i = (d >> 4) & 0xF;
			else
				i = d & 0xF;
			final int color = loresColors[i];
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);
        	this.buf.setElem(y++, color);

		}
		else
        {
			// text and hi-res
			if (inverse)
			{
				d = ~d;
			}
        	this.buf.setElem(y++, ((d & 0x01) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x02) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x04) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x08) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x10) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x20) != 0) ? GREEN : BLACK);
        	this.buf.setElem(y++, ((d & 0x40) != 0) ? GREEN : BLACK);
        }
		// TODO high-order bit half-dot shift and hi-res colors
	}

	public boolean isText()
	{
		return this.swText;
	}

	final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public void dump() throws IOException
	{
		final String name = "dump"+this.fmt.format(new Date())+".png";
		ImageIO.write(this.screenImage,"PNG",new File(name));
	}
}
