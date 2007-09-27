package video;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
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




	private static final int VISIBLE_BITS_PER_BYTE = 7;

//	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.BYTES_PER_ROW;
//	private static final int YSIZE = VideoAddressing.NTSC_LINES_PER_FIELD;
//	private static final int VISIBLE_X_OFFSET = 0;
	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.VISIBLE_BYTES_PER_ROW;
	private static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;
	private static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	BufferedImage screenImage;

	public Video(final Memory memory) throws IOException
	{
		this.memory = memory;

		setOpaque(true);
		setPreferredSize(new Dimension(XSIZE,YSIZE));
		addNotify();

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
		final int a = getAddr();

		int d = memory.read(a);
		memory.write(0xC051,(byte)d); // floating bus

		boolean inverse = false;
		if (this.mode == VideoMode.TEXT)
		{
			if (d >= 0)
			{
				inverse = true;
			}
			d = getTextCharLine(d,(this.t / VideoAddressing.BYTES_PER_ROW) & 0x07);
		}
		plotByte(this.t,d,inverse);

		++this.t;

		if (this.t >= VideoAddressing.BYTES_PER_FIELD)
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						plotScreen();
					}
				});
			}
			catch (Throwable e)
			{
				throw new IllegalStateException(e);
			}
			this.t = 0;
		}
	}

	/**
	 * @param g
	 */
	@Override
	public void paint(Graphics g)
	{
		// we don't need to paint anything; we just let the
		// emulated screen refresh do it's thing
	}

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

	void plotScreen()
	{
		if (screenImage == null)
		{
			return;
		}
		Graphics gr = getGraphics();
		if (gr == null)
		{
			return;
		}
		screenImage.flush();
		gr.drawImage(screenImage,0,0,this);
	}

	private void createOffscreenImage()
	{
		final Dimension size = getSize();
		screenImage = new BufferedImage(size.width,size.height,BufferedImage.TYPE_INT_RGB);
	}

    static final int BLACK = Color.BLACK.getRGB();
    static final int GREEN = Color.WHITE.getRGB();

    private void plotByte(int tt, int d, boolean inverse)
	{
		if (screenImage == null)
		{
			createOffscreenImage();
		}
		d >>= 1; // TODO high-order bit half-dot shift
		int x = tt % VideoAddressing.BYTES_PER_ROW - VISIBLE_X_OFFSET;
		if (x < 0)
		{
			return;
		}
		x *= VISIBLE_BITS_PER_BYTE;
		int y = tt / VideoAddressing.BYTES_PER_ROW;
		if (y >= VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			return;
		}


		DataBuffer buf = screenImage.getRaster().getDataBuffer();
        int yOffset = y*VideoAddressing.VISIBLE_BYTES_PER_ROW*VISIBLE_BITS_PER_BYTE+x;
		for (int i = 0; i < VISIBLE_BITS_PER_BYTE; ++i)
		{
			boolean on = (d & 1) != 0;
            buf.setElem(yOffset++, on==inverse ? BLACK : GREEN);
			d >>= 1;
		}
	}

	private byte getTextCharLine(int d, int i)
	{
		return char_rom[((d&0x7F)<<3)+i];
	}
}
