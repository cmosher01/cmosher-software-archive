package video;

import gui.UI;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.InputStream;
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

	private int[][] lutText = { VideoAddressing.buildLUT(0x0400,0x0400), VideoAddressing.buildLUT(0x0800,0x0400) };
	private int[][] lutHiGr = { VideoAddressing.buildLUT(0x2000,0x2000), VideoAddressing.buildLUT(0x4000,0x2000) };






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
			lookupTable = this.lutHiGr[this.swPage2];
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
			this.prevDataByte = 0;
		}

		x -= VISIBLE_X_OFFSET;
		x *= VISIBLE_BITS_PER_BYTE;
		y *= XSIZE;
		x += y;

		if (isDisplayingText())
		{
//			plotByteAsHiRes(x,ox,d); // use this instead to simulate lack of a "color killer"
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
		int c = A2ColorsObserved.HIRES_ORANGE;
		this.buf.setElem(x++, ((d & 0x01) != 0) ? c : A2ColorsObserved.BLACK);
		this.buf.setElem(x++, ((d & 0x02) != 0) ? c : A2ColorsObserved.BLACK);
		this.buf.setElem(x++, ((d & 0x04) != 0) ? c : A2ColorsObserved.BLACK);
		this.buf.setElem(x++, ((d & 0x08) != 0) ? c : A2ColorsObserved.BLACK);
		this.buf.setElem(x++, ((d & 0x10) != 0) ? c : A2ColorsObserved.BLACK);
		this.buf.setElem(x++, ((d & 0x20) != 0) ? c : A2ColorsObserved.BLACK);
		this.buf.setElem(x++, ((d & 0x40) != 0) ? c : A2ColorsObserved.BLACK);
	}

	private void plotByteAsLoRes(int x, final int oy, int d)
	{
		final int i;
		if (((oy >> 2) & 1) != 0)
			i = (d >> 4) & 0xF;
		else
			i = d & 0xF;
		final int color = A2ColorsObserved.COLOR[i];

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
		int color0, color1;
		if ((d & 0x80) != 0)
		{
			color0 = A2ColorsObserved.HIRES_ORANGE;
			color1 = A2ColorsObserved.HIRES_BLUE;
		}
		else
		{
			color0 = A2ColorsObserved.HIRES_GREEN;
			color1 = A2ColorsObserved.HIRES_VIOLET;
		}
		if ((x & 0x01) != 0)
		{
			int tmp = color0; color0 = color1; color1 = tmp;
		}

		if (VISIBLE_X_OFFSET < ox)
			setHiRes(x-1, this.prevDataByte & 0x10, this.prevDataByte & 0x20, this.prevDataByte & 0x40, d & 0x01, color0, color1);
		setHiRes(x++, VISIBLE_X_OFFSET < ox ? this.prevDataByte & 0x20 : 0, VISIBLE_X_OFFSET < ox ? this.prevDataByte & 0x40 : 0, d & 0x01, d & 0x02, color1, color0);
		setHiRes(x++, VISIBLE_X_OFFSET < ox ? this.prevDataByte & 0x40 : 0, d & 0x01, d & 0x02, d & 0x04, color0, color1);
		setHiRes(x++, d & 0x01, d & 0x02, d & 0x04, d & 0x08, color1, color0);
		setHiRes(x++, d & 0x02, d & 0x04, d & 0x08, d & 0x10, color0, color1);
		setHiRes(x++, d & 0x04, d & 0x08, d & 0x10, d & 0x20, color1, color0);
		setHiRes(x++, d & 0x08, d & 0x10, d & 0x20, d & 0x40, color0, color1);
		if (ox == VideoAddressing.BYTES_PER_ROW-1)
			setHiRes(x++, d & 0x10, d & 0x20, d & 0x40, 0, color1, color0);
	}

    private void setHiRes(int x, int nextLeftBit, int leftBit, int bit, int rightBit, int color, int compl)
    {
//    	this.buf.setElem(x, (bit == 0) ? (leftBit == 0 ? A2ColorsObserved.BLACK : nextLeftBit == 1 ? A2ColorsObserved.WHITE : compl) : (leftBit == 0 && rightBit == 0) ? color : A2ColorsObserved.WHITE);
    	int c;
    	if (bit == 0)
    	{
    		if (leftBit == 0)
    		{
    			c = A2ColorsObserved.BLACK;
    		}
    		else
    		{
    			if (nextLeftBit == 0)
    			{
    				c = compl;
    			}
    			else
    			{
    				c = A2ColorsObserved.WHITE;
    			}
    		}
    	}
    	else
    	{
    		if (leftBit == 0 && rightBit == 0)
    		{
    			c = color;
    		}
    		else
    		{
    			c = A2ColorsObserved.WHITE;
    		}
    	}
//    	this.buf.setElem(x, (bit == 0) ? A2ColorsObserved.BLACK : (leftBit == 0 && rightBit == 0) ? color : A2ColorsObserved.WHITE);
    	this.buf.setElem(x,c & 0x00ffffff);
    }

    public boolean isText()
	{
		return this.swText;
	}
}
