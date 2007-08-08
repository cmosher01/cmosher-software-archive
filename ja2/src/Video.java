import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Created on Aug 2, 2007
 */
public class Video extends JPanel implements Clock.Timed
{
	private final Memory memory;

	private VideoMode mode = VideoMode.TEXT;
	private int page = 0;

	private int t;

	private int[] lutText0 = VideoAddressing.buildLUT(0x0400,0x0400);
	private int[] lutText1 = VideoAddressing.buildLUT(0x0800,0x0800);
	private int[][] lutText = { lutText0, lutText1 };
	private int[][] lutLoRes = { lutText0, lutText1 };
	private int[] lutHiRes0 = VideoAddressing.buildLUT(0x2000,0x2000);
	private int[] lutHiRes1 = VideoAddressing.buildLUT(0x4000,0x2000);
	private int[][] lutHiRes = { lutHiRes0, lutHiRes1 };

	private int[] char_rom;

	private static final int TEXT_CHAR_ROWS = 8;




	private static final int DISPLAY_FACTOR = 1;

	private static final int PIXELON = 0x0c;
	private static final int PIXELOFF = 0x00;

	private static final byte colormap[] =
	{
		(byte)0x00, (byte)0x00, (byte)0x00, 
		(byte)0xff, (byte)0x00, (byte)0xff, 
		(byte)0x00, (byte)0x00, (byte)0x7f, 
		(byte)0x7f, (byte)0x00, (byte)0x7f, 
		(byte)0x00, (byte)0x7f, (byte)0x00, 
		(byte)0x7f,	(byte)0x7f, (byte)0x7f, 
		(byte)0x00, (byte)0x00, (byte)0xbf, 
		(byte)0x00, (byte)0x00, (byte)0xff, 
		(byte)0xbf, (byte)0x7f, (byte)0x00, 
		(byte)0xff, (byte)0xbf, (byte)0x00, 
		(byte)0xbf, (byte)0xbf, (byte)0xbf, 
		(byte)0xff, (byte)0x7f, (byte)0x7f, 
		(byte)0x00, (byte)0xff, (byte)0x00, 
		(byte)0xff, (byte)0xff, (byte)0x00, 
		(byte)0x00, (byte)0xbf, (byte)0x7f, 
		(byte)0xff, (byte)0xff, (byte)0xff
	};
	private static final ColorModel cmodel = new IndexColorModel(4,16,colormap,0,false);

	private static final int VISIBLE_BITS_PER_BYTE = 7;

//	private static final int XSIZE = 7*VideoAddressing.VISIBLE_BYTES_PER_ROW;
//	private static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;
	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.BYTES_PER_ROW;
	private static final int YSIZE = VideoAddressing.NTSC_LINES_PER_FIELD;

	private final int pixels[] = new int[YSIZE * XSIZE];
	private final ImageProducer memImgSrc = new MemoryImageSource(XSIZE,YSIZE,cmodel,pixels,0,XSIZE);

	private Image scrnimage;

	private int plot_t;

	private int plot_d;

	Video(final Memory memory) throws IOException
	{
//		System.err.println("size of screen: "+pixels.length);
		this.memory = memory;

		setOpaque(true);
		setPreferredSize(new Dimension(XSIZE*DISPLAY_FACTOR,YSIZE*DISPLAY_FACTOR));
		addNotify();

		scrnimage = createImage(memImgSrc);

		this.char_rom = readCharRom();
	}

	private static final int EOF = -1;

	private int[] readCharRom() throws IOException
	{
		final int[] r = new int[0x400];
		final InputStream rom = getClass().getResourceAsStream("charrom2.bin");
		int cc = 0;
		for (int c = rom.read(); c != EOF; c = rom.read())
		{
			if (cc < r.length)
			{
				r[cc] = c >> 1;
			}
			++cc;
		}
		rom.close();
		if (cc != 0x400)
		{
			throw new IllegalStateException();
		}
		return r;
	}

	/**
	 * 
	 * @param mode VideoMode
	 * @param page 0 or 1
	 */
	public void setMode(final VideoMode mode, final int page)
	{
		this.mode = mode;
		this.page = page;

	}

	public void tick()
	{
//		if (t % VideoAddressing.BYTES_PER_ROW == 0)
//		{
//			System.out.println();
//		}
		int a = 0;
		switch (this.mode)
		{
			case TEXT:
				a = lutText[page][t];
			break;
			case LORES:
				a = lutLoRes[page][t];
			break;
			case HIRES:
				a = lutHiRes[page][t];
			break;
		}

		int d = memory.read(a);
//		if (d != 0)
//			System.err.println("read from video ram "+d);

		if (this.mode == VideoMode.TEXT)
		{
			d = getTextCharLine(d,(this.t / VideoAddressing.BYTES_PER_ROW) % TEXT_CHAR_ROWS);
//			StringBuilder sb = new StringBuilder(8);
//			bits((byte)d,sb);
//			sb.append(" ");
//			System.out.print (sb.toString());
		}
		setPlot(d);


//		if (t == 0)
//		{
//			System.out.println();
//			System.out.println();
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						plotByte();
//						swing(getGraphics());
					}
				});
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
//		}

		++this.t;
		this.t %= VideoAddressing.BYTES_PER_FIELD;
	}

	private synchronized void setPlot(int d)
	{
		this.plot_t = t;
		this.plot_d = d;
	}
	private synchronized int getPlotT() { return this.plot_t; }
	private synchronized int getPlotD() { return this.plot_d; }

	@Override
	protected void paintComponent(final Graphics g)
	{
		swing(g);
	}

	private void swing(final Graphics g)
	{
		if (g != null)
		{
			final Dimension size = getSize();
//			System.err.println("drawing image "+size);
//			g.drawImage(scrnimage,0,0,size.width,size.height,this);
//			dumpImageAsText();
//			getParent().repaint();
		}
	}

	private void dumpImageAsText()
	{
		for (int i = 0; i < YSIZE*XSIZE; ++i)
		{
			final int d = pixels[i];

			if (d != 0)
				System.out.print("o");
			else
				System.out.print(" ");
			if (i % XSIZE == 0)
				System.out.println();
		}
	}
	
	private void plotByte()
	{
		int d = getPlotD();
		int pt = getPlotT();
		Graphics g = getGraphics();
		Color orig = g.getColor();
		final int base = t*VISIBLE_BITS_PER_BYTE;
		d = d << 1; // TODO high-order bit half-dot shift
		int x = (pt % VideoAddressing.BYTES_PER_ROW) * VISIBLE_BITS_PER_BYTE;
		int y = (pt / VideoAddressing.BYTES_PER_ROW);
//		System.out.println(""+pt+": plot 7 pixels at "+x+","+y);
		for (int i = 0; i < VISIBLE_BITS_PER_BYTE; ++i)
		{
//			final int p = ((d & 0x80) != 0) ? PIXELON : PIXELOFF;
//			pixels[base+y] = p;
//			System.err.println("setting pixel @ "+(base+y)+" to "+p);
			if ((d & 0x80) != 0)
			{
				g.setColor(Color.GREEN);
			}
			else
			{
				g.setColor(Color.BLACK);
			}
			g.fillRect(x+i,y,DISPLAY_FACTOR,DISPLAY_FACTOR);
			d = d << 1;
		}
		g.setColor(orig);
	}

	private int getTextCharLine(int d, int i)
	{
		return char_rom[(d&0x7F)*TEXT_CHAR_ROWS+i];
	}
}
