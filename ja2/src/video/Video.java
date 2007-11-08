package video;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import chipset.Clock;
import chipset.Memory;

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
	DataBuffer buf;



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

	private static byte[] readCharRom() throws IOException
	{
		final byte[] r = new byte[0x800];
		final InputStream rom = Video.class.getResourceAsStream("3410036.BIN");
		int cc = 0;
		for (int c = rom.read(); c != EOF; c = rom.read())
		{
			if (cc < r.length)
			{
				r[cc] = (byte)c;
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
//			d = getTextCharLine(d,(this.t / VideoAddressing.BYTES_PER_ROW) & 0x07);
//			return char_rom[((d&0x7F)<<3)+i];
			d = char_rom[((d&0x7F)<<3)+((this.t / VideoAddressing.BYTES_PER_ROW) & 0x07)];
		}
		plotByte(this.t,d,inverse);

		++this.t;

		if (this.t >= VideoAddressing.BYTES_PER_FIELD)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					plotScreen();
				}
			});
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
		//screenImage.flush();
		gr.drawImage(screenImage,0,0,this);
	}

	private void createOffscreenImage()
	{
		final Dimension size = getSize();
		screenImage = new BufferedImage(size.width,size.height,BufferedImage.TYPE_INT_RGB);
		buf = screenImage.getRaster().getDataBuffer();
	}

    static final int BLACK = Color.BLACK.getRGB();
    static final int GREEN = Color.GREEN.getRGB();

    private void plotByte(int tt, int d, boolean inverse)
	{
		if (screenImage == null)
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
		if (y >= VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			return;
		}


		y *= XSIZE;
		y += x;
        if (inverse)
        {
	        buf.setElem(0, y++, ((d & 0x40) != 0) ? BLACK : GREEN);
	        buf.setElem(0, y++, ((d & 0x20) != 0) ? BLACK : GREEN);
	        buf.setElem(0, y++, ((d & 0x10) != 0) ? BLACK : GREEN);
	        buf.setElem(0, y++, ((d & 0x08) != 0) ? BLACK : GREEN);
	        buf.setElem(0, y++, ((d & 0x04) != 0) ? BLACK : GREEN);
	        buf.setElem(0, y++, ((d & 0x02) != 0) ? BLACK : GREEN);
	        buf.setElem(0, y++, ((d & 0x01) != 0) ? BLACK : GREEN);
        }
        else
        {
	        buf.setElem(0, y++, ((d & 0x40) != 0) ? GREEN : BLACK);
	        buf.setElem(0, y++, ((d & 0x20) != 0) ? GREEN : BLACK);
	        buf.setElem(0, y++, ((d & 0x10) != 0) ? GREEN : BLACK);
	        buf.setElem(0, y++, ((d & 0x08) != 0) ? GREEN : BLACK);
	        buf.setElem(0, y++, ((d & 0x04) != 0) ? GREEN : BLACK);
	        buf.setElem(0, y++, ((d & 0x02) != 0) ? GREEN : BLACK);
	        buf.setElem(0, y++, ((d & 0x01) != 0) ? GREEN : BLACK);
        }
		// TODO high-order bit half-dot shift
	}

	private byte getTextCharLine(int d, int i)
	{
		return char_rom[((d&0x7F)<<3)+i];
	}
}
