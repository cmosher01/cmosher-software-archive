package video;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import chipset.Clock;
import chipset.Memory;
import chipset.Clock.Timed;

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

	private byte[] char_rom;

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
//	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.BYTES_PER_ROW;
//	private static final int YSIZE = VideoAddressing.NTSC_LINES_PER_FIELD;
	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.VISIBLE_BYTES_PER_ROW;
	private static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;

	private final int pixels[] = new int[YSIZE * XSIZE];
	private final ImageProducer memImgSrc = new MemoryImageSource(XSIZE,YSIZE,cmodel,pixels,0,XSIZE);

	private Image scrnimage;

	volatile private int plot_t;
	volatile private int plot_d;

	/*Volatile*/Image vImg;
	Graphics grimg;

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

	public void stopped()
	{
		
	}

	private byte[] readCharRom() throws IOException
	{
		final byte[] r = new byte[0x800];
		final InputStream rom = getClass().getResourceAsStream("charrom.bin");
		int cc = 0;
		for (int c = rom.read(); c != EOF; c = rom.read())
		{
			if (cc < r.length)
			{
				r[cc] = (byte)((c >> 1) & 0x7F);
			}
			++cc;
		}
		rom.close();
		if (cc != 0x800)
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
		final int a = getAddr();

		byte d = memory.read(a);
//		memory.write(0xC051,d);
//		if (d != 0)
//			System.err.println("read from video ram "+d);

		boolean inverse = false;
		if (this.mode == VideoMode.TEXT)
		{
			if (d >= 0)
			{
				inverse = true;
			}
			d = getTextCharLine(d,(this.t / VideoAddressing.BYTES_PER_ROW) % TEXT_CHAR_ROWS);
//			StringBuilder sb = new StringBuilder(8);
//			bits((byte)d,sb);
//			sb.append(" ");
//			System.out.print (sb.toString());
		}
//		setPlot(d);
//		this.plot_t = t;
//		this.plot_d = d;

//			System.out.println();
//			System.out.println();
			plotByte(this.t,d,inverse);
//		}

		++this.t;
		if (this.t == VideoAddressing.BYTES_PER_FIELD)
		{
//			try
//			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						plotScreen();
					}
				});
//			}
//			catch (InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//			catch (InvocationTargetException e)
//			{
//				e.printStackTrace();
//			}
		}
		this.t %= VideoAddressing.BYTES_PER_FIELD;
	}

//	private void invokePlotByte(int t, int d)
//	{
//		plotByte(t,d);
//		try
//		{
//			SwingUtilities.invokeAndWait(new SwingPlotByte(t,d));
////				new Runnable()
////				{
////					public void run()
////					{
////						plotByte();
//////						swing(getGraphics());
////					}
////				}
////				);
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
//		catch (InvocationTargetException e)
//		{
//			e.printStackTrace();
//		}
//	}

	private int getAddr()
	{
		switch (this.mode)
		{
			case TEXT:
				return lutText[page][t];
			case LORES:
				return lutLoRes[page][t];
			case HIRES:
				return lutHiRes[page][t];
			default:
				throw new IllegalStateException();
		}
	}

//	private class SwingPlotByte implements Runnable
//	{
//		volatile private int t;
//		volatile private int d;
//		public SwingPlotByte(int t, int d)
//		{
//			this.t = t;
//			this.d = d;
//		}
//		public void run()
//		{
//			plotByte(t,d);
//		}
//	}
//
//	private /*synchronized*/ void setPlot(int d)
//	{
//		this.plot_t = t;
//		this.plot_d = d;
//	}
//	private /*synchronized*/ int getPlotT() { return this.plot_t; }
//	private /*synchronized*/ int getPlotD() { return this.plot_d; }
//
//	@Override
//	protected void paintComponent(final Graphics g)
//	{
//		swing(g);
//	}
//
//	private void swing(final Graphics g)
//	{
//		if (g != null)
//		{
//			final Dimension size = getSize();
////			System.err.println("drawing image "+size);
////			g.drawImage(scrnimage,0,0,size.width,size.height,this);
////			dumpImageAsText();
////			getParent().repaint();
//		}
//	}

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

	void plotScreen()
	{
		if (vImg == null)
		{
			return;
		}
		Graphics gr = getGraphics();
		if (gr == null)
		{
			return;
		}
		vImg.flush();
		gr.drawImage(vImg,0,0,this);
	}

	void plotByte(int t, byte d, boolean inverse)
	{
		if (vImg == null)
		{
//			GraphicsConfiguration gc = getGraphicsConfiguration();
			createOffscreenImage();
		}

//		int d = getPlotD();
//		int pt = getPlotT();
//		Color orig = grimg.getColor();
//		final int base = t*VISIBLE_BITS_PER_BYTE;
		plotByteGr(t,d,grimg,inverse);
//		grimg.setColor(orig);
	}

	private void createOffscreenImage()
	{
		final Dimension size = getSize();
		vImg = createImage(size.width,size.height);
		grimg = vImg.getGraphics();
	}

	private void plotByteGr(int t, byte d, Graphics g, boolean inverse)
	{
		d >>= 1; // TODO high-order bit half-dot shift
		int x = (t % VideoAddressing.BYTES_PER_ROW - (VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW)) * VISIBLE_BITS_PER_BYTE;
		if (x < 0)
		{
			return;
		}
		int y = (t / VideoAddressing.BYTES_PER_ROW);
		if (y > VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			return;
		}
//		System.out.println(""+pt+": plot 7 pixels at "+x+","+y);
		boolean prevOn = g.getColor().equals(Color.GREEN);
		for (int i = 0; i < VISIBLE_BITS_PER_BYTE; ++i)
		{
//			final int p = ((d & 0x80) != 0) ? PIXELON : PIXELOFF;
//			pixels[base+y] = p;
//			System.err.println("setting pixel @ "+(base+y)+" to "+p);
			boolean on = (d & 1) != 0;
			if (inverse)
			{
				on = !on;
			}
			if (on)
			{
				if (!prevOn)
				{
					g.setColor(Color.GREEN);
					prevOn = true;
				}
			}
			else
			{
				if (prevOn)
				{
					g.setColor(Color.BLACK);
					prevOn = false;
				}
			}
			g.drawLine(x,y,x,y);
			++x;
			d = (byte)((d & 0xFF) >>> 1);
		}
	}

	private void setColor(int d, Graphics g)
	{
	}

	private void doFill(Graphics g, int x, int y)
	{
//		g.fillRect(x,y,DISPLAY_FACTOR,DISPLAY_FACTOR);
	}

	private byte getTextCharLine(byte d, int i)
	{
		return char_rom[(d&0x7F)*TEXT_CHAR_ROWS+i];
	}
}
