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
	private Memory memory;

	private boolean swText;
	private boolean swMixed;
	private boolean swPage2;
	private boolean swHiRes;

	private int t;

	private int[] lutText0 = VideoAddressing.buildLUT(0x0400,0x0400);
	private int[] lutText1 = VideoAddressing.buildLUT(0x0800,0x0800);
	private int[][] lutText = { this.lutText0, this.lutText1 };
	private int[][] lutLoRes = { this.lutText0, this.lutText1 };
	private int[] lutHiRes0 = VideoAddressing.buildLUT(0x2000,0x2000);
	private int[] lutHiRes1 = VideoAddressing.buildLUT(0x4000,0x2000);
	private int[][] lutHiRes = { this.lutHiRes0, this.lutHiRes1 };

	private byte[] char_rom;




	private static final int VISIBLE_BITS_PER_BYTE = 7;

	private static final int XSIZE = VISIBLE_BITS_PER_BYTE*VideoAddressing.VISIBLE_BYTES_PER_ROW;
	private static final int YSIZE = VideoAddressing.VISIBLE_ROWS_PER_FIELD;
	private static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	BufferedImage screenImage;
	DataBuffer buf;



	public Video() throws IOException
	{
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
				r[cc] = xlateCharRom(c);
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
		final int sw = (addr & 0x0007) >> 1;
		final boolean on = (addr & 0x0001) != 0;
		switch (sw)
		{
			case 0: this.swText = on; break;
			case 1: this.swMixed = on; break;
			case 2: this.swPage2 = on; break;
			case 3: this.swHiRes = on; break;
		}
		return 0;
	}

	public void tick()
	{
		final int a = getAddr();

		int d = this.memory.read(a);
		this.memory.write(0xC051,(byte)d); // floating bus

		boolean inverse = false;
		if (this.swText)
		{
			if (d >= 0)
			{
				inverse = true;
			}
			d = this.char_rom[((d&0x7F)<<3)+((this.t / VideoAddressing.BYTES_PER_ROW) & 0x07)];
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
		int addr;
		final int page = this.swPage2 ? 1 : 0;
		if (this.swText)
		{
			addr = this.lutText[page][this.t];
		}
		else if (this.swHiRes)
		{
			addr = this.lutHiRes[page][this.t];
		}
		else
		{
			addr = this.lutLoRes[page][this.t];
		}
		// TODO mixed text/graphics
		return addr;
	}

	void plotScreen()
	{
		if (this.screenImage == null)
		{
			return;
		}
		Graphics gr = getGraphics();
		if (gr == null)
		{
			return;
		}
		gr.drawImage(this.screenImage,0,0,this);
	}

	private void createOffscreenImage()
	{
		final Dimension size = getSize();
		this.screenImage = new BufferedImage(size.width,size.height,BufferedImage.TYPE_INT_RGB);
		this.buf = this.screenImage.getRaster().getDataBuffer();
	}

    static final int BLACK = Color.BLACK.getRGB();
    static final int GREEN = Color.GREEN.getRGB();

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
		if (y >= VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			return;
		}


		y *= XSIZE;
		y += x;
        if (inverse)
        {
        	this.buf.setElem(0, y++, ((d & 0x01) != 0) ? BLACK : GREEN);
        	this.buf.setElem(0, y++, ((d & 0x02) != 0) ? BLACK : GREEN);
        	this.buf.setElem(0, y++, ((d & 0x04) != 0) ? BLACK : GREEN);
        	this.buf.setElem(0, y++, ((d & 0x08) != 0) ? BLACK : GREEN);
        	this.buf.setElem(0, y++, ((d & 0x10) != 0) ? BLACK : GREEN);
        	this.buf.setElem(0, y++, ((d & 0x20) != 0) ? BLACK : GREEN);
        	this.buf.setElem(0, y++, ((d & 0x40) != 0) ? BLACK : GREEN);
        }
        else
        {
        	this.buf.setElem(0, y++, ((d & 0x01) != 0) ? GREEN : BLACK);
        	this.buf.setElem(0, y++, ((d & 0x02) != 0) ? GREEN : BLACK);
        	this.buf.setElem(0, y++, ((d & 0x04) != 0) ? GREEN : BLACK);
        	this.buf.setElem(0, y++, ((d & 0x08) != 0) ? GREEN : BLACK);
        	this.buf.setElem(0, y++, ((d & 0x10) != 0) ? GREEN : BLACK);
        	this.buf.setElem(0, y++, ((d & 0x20) != 0) ? GREEN : BLACK);
        	this.buf.setElem(0, y++, ((d & 0x40) != 0) ? GREEN : BLACK);
        }
		// TODO high-order bit half-dot shift
	}

	private byte getTextCharLine(int d, int i)
	{
		return this.char_rom[((d&0x7F)<<3)+i];
	}

	public void setMemory(final Memory memory)
	{
		this.memory = memory;
	}
}
