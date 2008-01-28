/*
 * Created on Jan 25, 2008
 */
package video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class SimplePictureGenerator
{
	private final VideoMode mode;
	private final DataBuffer buf;

	private int prevPlotByte;

	/**
	 * @param mode
	 * @param image
	 */
	public SimplePictureGenerator(final VideoMode mode, final BufferedImage image)
	{
		this.mode = mode;
		this.buf = image.getRaster().getDataBuffer();
	}

	public void plotRow(final int rowToPlot, int x, int y, int t)
	{
		final int ox = x;
		final int oy = y;

		x -= Video.VISIBLE_X_OFFSET;
		x *= VideoAddressing.VISIBLE_BITS_PER_BYTE;
		y *= Video.XSIZE;
		x += y;

		if (this.mode.isDisplayingText(t) || this.mode.isHiRes())
		{
			plotByteAsHiRes(x,ox,rowToPlot,t);
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

	private void plotByteAsHiRes(int x, final int ox, final int d, final int t)
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



		if (ox <= Video.VISIBLE_X_OFFSET)
		{
			this.prevPlotByte = 0;
		}
		else
		{
			setHiRes(x-1, this.prevPlotByte & 0x10, this.prevPlotByte & 0x20, this.prevPlotByte & 0x40, d & 0x01, color, compl, black, white,t);
		}
		setHiRes(x++, this.prevPlotByte & 0x20, this.prevPlotByte & 0x40, d & 0x01, d & 0x02, compl, color, black, white,t);
		setHiRes(x++, this.prevPlotByte & 0x40, d & 0x01, d & 0x02, d & 0x04, color, compl, black, white,t);
		setHiRes(x++, d & 0x01, d & 0x02, d & 0x04, d & 0x08, compl, color, black, white,t);
		setHiRes(x++, d & 0x02, d & 0x04, d & 0x08, d & 0x10, color, compl, black, white,t);
		setHiRes(x++, d & 0x04, d & 0x08, d & 0x10, d & 0x20, compl, color, black, white,t);
		setHiRes(x++, d & 0x08, d & 0x10, d & 0x20, d & 0x40, color, compl, black, white,t);
		if (ox == VideoAddressing.BYTES_PER_ROW-1)
		{
			setHiRes(x, d & 0x10, d & 0x20, d & 0x40, 0, compl, color, black, white,t);
		}
	}

	private int[] getCurrentColorMap()
	{
		return /*this.observedColors ?*/ A2ColorsObserved.COLOR /*: A2Colors.COLOR*/;
	}

    private void setHiRes(final int x, final int nextLeftBit, final int leftBit, final int bit, final int rightBit, final int color, final int compl, final int black, final int white, final int t)
    {
		final int c;
		if (bit == 0)
    	{
    		if (leftBit == 0 || rightBit == 0 || this.mode.isDisplayingText(t))
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
    		if (leftBit == 0 && rightBit == 0 && !(false/*this.killColor*/ && this.mode.isText()))
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
}
