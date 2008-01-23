package video;

import gui.UI;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
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
	private final UI ui;
	private final AddressBus addressBus;
	private final DataBuffer buf;
	private final PictureGenerator picgen;

	private final TextCharacters textRows = new TextCharacters();



	// somewhat arbitrary starting point for scanning... helps start
	// an Apple ][ plus with a clean screen
	private int t = 0;//VideoAddressing.BYTES_PER_FIELD-12*VideoAddressing.BYTES_PER_ROW;

	private boolean flash;
	private int cflash;

	private int prevPlotByte;

	private boolean killColor = true;
	private boolean observedColors = true;





	public Video(final VideoMode mode, final UI ui, final AddressBus addressBus, final BufferedImage screenImage, final PictureGenerator picgen) throws IOException
	{
		this.mode = mode;
		this.ui = ui;
		this.addressBus = addressBus;
		this.buf = screenImage.getRaster().getDataBuffer();
		this.picgen = picgen;
		this.picgen.resetFrame();

		readCharacterRom();
	}

	private void readCharacterRom() throws IOException
	{
		int off = this.textRows.read();
		if (off != 0)
		{
			final String big;
			if (off > 0)
			{
				big = "big";
			}
			else
			{
				big = "small";
				off = -off;
			}
			this.ui.showMessage("Text-character-ROM file GI2513.ROM is invalid: the file is "+
				off+" bytes too "+big+". Text may not be displayed correctly.");
		}
	}

	public void tick()
	{
		final byte data = getDataByte();
		plotDataByte(data);

		this.picgen.tick(this.t);

		updateFlash();

		++this.t;

		if (this.t >= VideoAddressing.BYTES_PER_FIELD)
		{
			this.picgen.draw();
			this.ui.updateScreen();
			this.t = 0;
			this.picgen.resetFrame();
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

    private void plotDataByte(final byte data)
	{
		final int x = this.t % VideoAddressing.BYTES_PER_ROW;
		if (x < VISIBLE_X_OFFSET)
		{
			//return;
		}

		final int y = this.t / VideoAddressing.BYTES_PER_ROW;
		if (y >= VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			//return;
		}

		final int rowToPlot = getRowToPlot(data,y);
		if (this.mode.isDisplayingText(this.t))
			this.picgen.loadText(rowToPlot);
		else
			this.picgen.loadGraphics(rowToPlot);
//		plotRow(rowToPlot,x,y);
	}

	private int getRowToPlot(int d, final int y)
	{
		if (this.mode.isDisplayingText(this.t))
		{
			d &= 0xFF;
			final boolean inverse = inverseChar(d);
			d = this.textRows.get(((d & 0x3F) << 3) | (y & 0x07));
			if (inverse)
			{
				d = ~d;
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

	private void plotRow(final int rowToPlot, int x, int y)
	{
		final int ox = x;
		final int oy = y;

		x -= VISIBLE_X_OFFSET;
		x *= VideoAddressing.VISIBLE_BITS_PER_BYTE;
		y *= XSIZE;
		x += y;

		if (this.mode.isDisplayingText(this.t) || this.mode.isHiRes())
		{
			plotByteAsHiRes(x,ox,rowToPlot);
		}
		else
        {
			plotByteAsLoRes(x,oy,rowToPlot);
        }

		this.prevPlotByte = rowToPlot;
	}

	private void plotByteAsLoRes(int x, final int oy, final int d)
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

	private void plotByteAsHiRes(int x, final int ox, final int d)
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
			setHiRes(x, d & 0x10, d & 0x20, d & 0x40, 0, compl, color, black, white);
		}
	}

	private int[] getCurrentColorMap()
	{
		return this.observedColors ? A2ColorsObserved.COLOR : A2Colors.COLOR;
	}

    private void setHiRes(final int x, final int nextLeftBit, final int leftBit, final int bit, final int rightBit, final int color, final int compl, final int black, final int white)
    {
		final int c;
		if (bit == 0)
    	{
    		if (leftBit == 0 || rightBit == 0 || this.mode.isDisplayingText(this.t))
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
    	this.buf.setElem(x,c);
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
