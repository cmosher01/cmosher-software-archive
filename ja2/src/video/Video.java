package video;

import gui.UI;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
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

	private int prevPlotByte;
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
	private boolean tv = true;
	private int colorMap;
	private boolean killColor = true;






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
		}

		x -= VISIBLE_X_OFFSET;
		x *= VISIBLE_BITS_PER_BYTE;
		y *= XSIZE;
		x += y;

		if (isDisplayingText() || this.swHiRes)
		{
			plotByteAsHiRes(x,ox,d);
		}
		else
        {
			plotByteAsLoRes(x,oy,d);
        }

		this.prevPlotByte = d & 0xFF;
	}

	private void plotByteAsLoRes(int x, final int oy, int d)
	{
		final int i;
		if ((oy & 4) != 0)
			i = (d >> 4) & 0xF;
		else
			i = d & 0xF;
		final int color, black;
		if (this.colorMap == 0)
		{
			color = A2ColorsObserved.COLOR[i];
			black = A2ColorsObserved.COLOR[A2ColorIndex.BLACK.ordinal()];
		}
		else if (this.colorMap == 1)
		{
			color = A2Colors.COLOR[i];
			black = A2Colors.COLOR[A2ColorIndex.BLACK.ordinal()];
		}
		else if (this.colorMap == 2)
		{
			color = A2Monochrome.WHITE[i];
			black = A2Monochrome.WHITE[A2ColorIndex.BLACK.ordinal()];
		}
		else if (this.colorMap == 3)
		{
			color = A2Monochrome.GREEN[i];
			black = A2Monochrome.GREEN[A2ColorIndex.BLACK.ordinal()];
		}
		else if (this.colorMap == 4)
		{
			color = A2Monochrome.ORANGE[i];
			black = A2Monochrome.ORANGE[A2ColorIndex.BLACK.ordinal()];
		}
		else
		{
			throw new IllegalStateException("Invalid colorMap value.");
		}

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
		int color0, color1, black, white;
		int index0, index1;
		if ((d & 0x80) != 0)
		{
			index0 = A2ColorIndex.HIRES_ORANGE.ordinal();
			index1 = A2ColorIndex.HIRES_BLUE.ordinal();
		}
		else
		{
			index0 = A2ColorIndex.HIRES_GREEN.ordinal();
			index1 = A2ColorIndex.HIRES_VIOLET.ordinal();
		}
		if ((x & 0x01) != 0)
		{
			int tmp = index0; index0 = index1; index1 = tmp;
		}

		if (this.colorMap == 0)
		{
			color0 = A2ColorsObserved.COLOR[index0];
			color1 = A2ColorsObserved.COLOR[index1];
			black = A2ColorsObserved.COLOR[A2ColorIndex.BLACK.ordinal()];
			white = A2ColorsObserved.COLOR[A2ColorIndex.WHITE.ordinal()];
		}
		else if (this.colorMap == 1)
		{
			color0 = A2Colors.COLOR[index0];
			color1 = A2Colors.COLOR[index1];
			black = A2Colors.COLOR[A2ColorIndex.BLACK.ordinal()];
			white = A2Colors.COLOR[A2ColorIndex.WHITE.ordinal()];
		}
		else if (this.colorMap == 2)
		{
			color0 = A2Monochrome.WHITE[index0];
			color1 = A2Monochrome.WHITE[index1];
			black = A2Monochrome.WHITE[A2ColorIndex.BLACK.ordinal()];
			white = A2Monochrome.WHITE[A2ColorIndex.WHITE.ordinal()];
		}
		else if (this.colorMap == 3)
		{
			color0 = A2Monochrome.GREEN[index0];
			color1 = A2Monochrome.GREEN[index1];
			black = A2Monochrome.GREEN[A2ColorIndex.BLACK.ordinal()];
			white = A2Monochrome.GREEN[A2ColorIndex.WHITE.ordinal()];
		}
		else if (this.colorMap == 4)
		{
			color0 = A2Monochrome.ORANGE[index0];
			color1 = A2Monochrome.ORANGE[index1];
			black = A2Monochrome.ORANGE[A2ColorIndex.BLACK.ordinal()];
			white = A2Monochrome.ORANGE[A2ColorIndex.WHITE.ordinal()];
		}
		else
		{
			throw new IllegalStateException("Invalid colorMap value.");
		}

		if (ox <= VISIBLE_X_OFFSET)
		{
			this.prevPlotByte = 0;
		}
		else
		{
			setHiRes(x-1, this.prevPlotByte & 0x10, this.prevPlotByte & 0x20, this.prevPlotByte & 0x40, d & 0x01, color0, color1, black, white);
		}
		setHiRes(x++, this.prevPlotByte & 0x20, this.prevPlotByte & 0x40, d & 0x01, d & 0x02, color1, color0, black, white);
		setHiRes(x++, this.prevPlotByte & 0x40, d & 0x01, d & 0x02, d & 0x04, color0, color1, black, white);
		setHiRes(x++, d & 0x01, d & 0x02, d & 0x04, d & 0x08, color1, color0, black, white);
		setHiRes(x++, d & 0x02, d & 0x04, d & 0x08, d & 0x10, color0, color1, black, white);
		setHiRes(x++, d & 0x04, d & 0x08, d & 0x10, d & 0x20, color1, color0, black, white);
		setHiRes(x++, d & 0x08, d & 0x10, d & 0x20, d & 0x40, color0, color1, black, white);
		if (ox == VideoAddressing.BYTES_PER_ROW-1)
		{
			setHiRes(x++, d & 0x10, d & 0x20, d & 0x40, 0, color1, color0, black, white);
		}
	}

    private void setHiRes(int x, int nextLeftBit, int leftBit, int bit, int rightBit, int color, int compl, int black, int white)
    {
    	final int c = calcHiResColor(nextLeftBit,leftBit,bit,rightBit,color,compl,black,white);
    	this.buf.setElem(x,c);
    }

	private int calcHiResColor(int nextLeftBit, int leftBit, int bit, int rightBit, int color, int compl, int black, int white)
	{
		int c;
		if (bit == 0)
    	{
    		if (leftBit == 0 || rightBit == 0 || isDisplayingText() || !this.tv)
    		{
    			c = black;
    		}
    		else if (nextLeftBit == 0)
			{
				c = compl;
			}
			else
			{
    			c = white;
    		}
    	}
    	else
    	{
    		if (leftBit == 0 && rightBit == 0 && !(this.killColor && isDisplayingText()))
    		{
    			c = color;
    		}
    		else
    		{
    			c = white;
    		}
    	}
		return c;
	}

    public boolean isText()
	{
		return this.swText;
	}

	public void toggleHiResMode()
	{
		this.tv = !this.tv;
	}

	public void toggleColorMap()
	{
		++this.colorMap;
		this.colorMap %= 5;
	}

	public void toggleColorKiller()
	{
		this.killColor = !this.killColor;
	}
}
