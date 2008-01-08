package video;

import gui.UI;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import chipset.AddressBus;
import chipset.Memory;

/*
 * Created on Aug 2, 2007
 */
public class Video
{
	private final VideoMode mode;
	private final UI ui;
	private final AddressBus addressBus;
	private final BufferedImage screenImage;
	private final DataBuffer buf;

	private final TextCharacters charRom = new TextCharacters();

	// somewhat arbitrary starting point for scanning... helps start
	// an Apple ][ plus with a clean screen
	private int t = VideoAddressing.BYTES_PER_FIELD-12*VideoAddressing.BYTES_PER_ROW;
	private int f;
	private boolean flash;

	private int prevPlotByte;
	private byte dataByte;


	private boolean killColor = true;
	private boolean observedColors = true;




	private static final int XSIZE = VideoAddressing.VISIBLE_BITS_PER_BYTE*VideoAddressing.VISIBLE_BYTES_PER_ROW;
	private static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;
	public static final Dimension SIZE = new Dimension(XSIZE,YSIZE);
	private static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	private static final int MIXED_TEXT_LINES = 4;
	private static final int ROWS_PER_TEXT_LINE = 8;
	private static final int MIXED_TEXT_CYCLE = (VideoAddressing.VISIBLE_ROWS_PER_FIELD-(MIXED_TEXT_LINES*ROWS_PER_TEXT_LINE))*VideoAddressing.BYTES_PER_ROW;

	private static final int FLASH_HALFPERIOD = 1;

	private int[][] lutText = { VideoAddressing.buildLUT(0x0400,0x0400), VideoAddressing.buildLUT(0x0800,0x0400) };
	private int[][] lutHiGr = { VideoAddressing.buildLUT(0x2000,0x2000), VideoAddressing.buildLUT(0x4000,0x2000) };






	public Video(final VideoMode mode, final UI ui, final AddressBus addressBus, final BufferedImage screenImage)
	{
		this.mode = mode;
		this.ui = ui;
		this.addressBus = addressBus;
		this.screenImage = screenImage;
		this.buf = this.screenImage.getRaster().getDataBuffer();

		try
		{
			this.charRom.readCharRom();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			this.ui.showMessage(e.getMessage());
		}
	}

	public byte getDataBus()
	{
		return this.dataByte; // emulate floating data bus
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
		if (isDisplayingText() || !this.mode.isHiRes())
		{
			lookupTable = this.lutText[this.mode.getPage()];
		}
		else
		{
			lookupTable = this.lutHiGr[this.mode.getPage()];
		}
		this.dataByte = this.addressBus.read(lookupTable[this.t]);
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
		return this.mode.isText() || (this.mode.isMixed() && this.t >= MIXED_TEXT_CYCLE);
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
			d = this.charRom.get(((d & 0x3F) << 3) | (y & 0x07));
			if (inverse)
			{
				d = ~d;
			}
		}
		d &= 0xFF;

		x -= VISIBLE_X_OFFSET;
		x *= VideoAddressing.VISIBLE_BITS_PER_BYTE;
		y *= XSIZE;
		x += y;

		if (isDisplayingText() || this.mode.isHiRes())
		{
			plotByteAsHiRes(x,ox,d);
		}
		else
        {
			plotByteAsLoRes(x,oy,d);
        }

		this.prevPlotByte = d;
	}

	private void plotByteAsLoRes(int x, final int oy, int d)
	{
		final int icolor;
		if ((oy & 4) != 0)
		{
			icolor = (d >> 4) & 0xF;
		}
		else
		{
			icolor = d & 0xF;
		}

		final int[] colormap = getCurrentColorMap();
		final int color = colormap[icolor];

		final int lim = x+7;
		for (;x < lim; ++x)
			this.buf.setElem(x,color);
	}

	private void plotByteAsHiRes(int x, int ox, int d)
	{
		int icolor, icompl;
		if ((d & 0x80) != 0)
		{
			icolor = A2ColorIndex.HIRES_ORANGE.ordinal();
			icompl = A2ColorIndex.HIRES_BLUE.ordinal();
		}
		else
		{
			icolor = A2ColorIndex.HIRES_GREEN.ordinal();
			icompl = A2ColorIndex.HIRES_VIOLET.ordinal();
		}
		if ((x & 0x01) != 0)
		{
			int tmp = icolor; icolor = icompl; icompl = tmp;
		}

		final int[] colormap = getCurrentColorMap();
		final int color = colormap[icolor];
		final int compl = colormap[icompl];
		final int black = colormap[A2ColorIndex.BLACK.ordinal()];
		final int white = colormap[A2ColorIndex.WHITE.ordinal()];



		if (ox <= VISIBLE_X_OFFSET)
		{
			this.prevPlotByte = 0;
		}
		else
		{
			setHiRes(x-1, this.prevPlotByte & 0x10, this.prevPlotByte & 0x20, this.prevPlotByte & 0x40, d & 0x01, color, compl, black, white);
		}
		setHiRes(x++, this.prevPlotByte & 0x20, this.prevPlotByte & 0x40, d & 0x01, d & 0x02, compl, color, black, white);
		setHiRes(x++, this.prevPlotByte & 0x40, d & 0x01, d & 0x02, d & 0x04, color, compl, black, white);
		setHiRes(x++, d & 0x01, d & 0x02, d & 0x04, d & 0x08, compl, color, black, white);
		setHiRes(x++, d & 0x02, d & 0x04, d & 0x08, d & 0x10, color, compl, black, white);
		setHiRes(x++, d & 0x04, d & 0x08, d & 0x10, d & 0x20, compl, color, black, white);
		setHiRes(x++, d & 0x08, d & 0x10, d & 0x20, d & 0x40, color, compl, black, white);
		if (ox == VideoAddressing.BYTES_PER_ROW-1)
		{
			setHiRes(x++, d & 0x10, d & 0x20, d & 0x40, 0, compl, color, black, white);
		}
	}

	private int[] getCurrentColorMap()
	{
		final int[] colormap;
		if (this.observedColors)
		{
			colormap = A2ColorsObserved.COLOR;
		}
		else
		{
			colormap = A2Colors.COLOR;
		}
		return colormap;
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
    		if (leftBit == 0 || rightBit == 0 || isDisplayingText())
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
    		if (leftBit == 0 && rightBit == 0 && !(this.killColor && this.mode.isText()))
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

	public void toggleColorMap()
	{
		this.observedColors = !this.observedColors;
	}

	public void toggleColorKiller()
	{
		this.killColor = !this.killColor;
	}
}
