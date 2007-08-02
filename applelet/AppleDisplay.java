import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

public class AppleDisplay extends Panel implements Runnable
{
	private static final int GR_TXMODE = 1;
	private static final int GR_MIXMODE = 2;
	private static final int GR_PAGE1 = 4;
	private static final int GR_HIRES = 8;

	private static final int XSIZE = 280;
	private static final int YSIZE = 192;
	private static final int PIXELON = /* 0x0c */0x0f;
	private static final int PIXELOFF = 0x00;
	private static final Dimension displaySize = new Dimension(40 * 7,24 * 8);
	public EmAppleII apple;
	// the pixels on the screen
	private int pixels[] = new int[YSIZE * XSIZE];
	// the pixels in the text chars.
	private int textbuf[][];
	private Thread refresher;
	public int refresh_interval;
	private int oldgrmode;
	private Image scrnimage;
	private MemoryImageSource memImgSrc;
	private boolean dirty[];
	private boolean appleDirty[];
	private boolean allDirty[];
	private boolean pixelupdate;
	private int dispwidth, dispheight; // display size of bitmap
	private static int texels[] = new int[256 * 8];
	private static byte colormap[] =
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
	private static ColorModel cmodel = new IndexColorModel(4,16,colormap,0,false);
	private static int flashInterval = 500;
	private static int text_lut[] =
	{
		0x000, 0x080, 0x100, 0x180, 0x200, 0x280, 0x300, 0x380,
		0x028, 0x0a8, 0x128, 0x1a8, 0x228, 0x2a8, 0x328, 0x3a8,
		0x050, 0x0d0, 0x150, 0x1d0, 0x250, 0x2d0, 0x350, 0x3d0
	};
	private static int hires_lut[] =
	{ 
		0x0000, 0x0400, 0x0800, 0x0c00, 0x1000, 0x1400, 0x1800, 0x1c00, 0x0080, 0x0480, 0x0880, 0x0c80, 0x1080, 0x1480, 0x1880, 0x1c80, 
		0x0100, 0x0500, 0x0900, 0x0d00, 0x1100, 0x1500, 0x1900, 0x1d00, 0x0180, 0x0580, 0x0980, 0x0d80, 0x1180, 0x1580, 0x1980, 0x1d80, 
		0x0200, 0x0600, 0x0a00, 0x0e00, 0x1200, 0x1600, 0x1a00, 0x1e00, 0x0280, 0x0680, 0x0a80, 0x0e80, 0x1280, 0x1680, 0x1a80, 0x1e80,
		0x0300, 0x0700, 0x0b00, 0x0f00, 0x1300, 0x1700, 0x1b00, 0x1f00, 0x0380, 0x0780, 0x0b80, 0x0f80, 0x1380, 0x1780, 0x1b80, 0x1f80, 

		0x0028, 0x0428, 0x0828, 0x0c28, 0x1028, 0x1428, 0x1828, 0x1c28, 0x00a8, 0x04a8, 0x08a8, 0x0ca8, 0x10a8, 0x14a8, 0x18a8, 0x1ca8, 
		0x0128, 0x0528, 0x0928, 0x0d28, 0x1128, 0x1528, 0x1928, 0x1d28, 0x01a8, 0x05a8, 0x09a8, 0x0da8, 0x11a8, 0x15a8, 0x19a8, 0x1da8,
		0x0228, 0x0628, 0x0a28, 0x0e28, 0x1228, 0x1628, 0x1a28, 0x1e28, 0x02a8, 0x06a8, 0x0aa8, 0x0ea8, 0x12a8, 0x16a8, 0x1aa8, 0x1ea8, 
		0x0328, 0x0728, 0x0b28, 0x0f28, 0x1328, 0x1728, 0x1b28, 0x1f28, 0x03a8, 0x07a8, 0x0ba8, 0x0fa8, 0x13a8, 0x17a8, 0x1ba8, 0x1fa8, 

		0x0050, 0x0450, 0x0850, 0x0c50, 0x1050, 0x1450, 0x1850, 0x1c50, 0x00d0, 0x04d0, 0x08d0, 0x0cd0, 0x10d0, 0x14d0, 0x18d0, 0x1cd0,
		0x0150, 0x0550, 0x0950, 0x0d50, 0x1150, 0x1550, 0x1950, 0x1d50, 0x01d0, 0x05d0, 0x09d0, 0x0dd0, 0x11d0, 0x15d0, 0x19d0, 0x1dd0, 
		0x0250, 0x0650, 0x0a50, 0x0e50, 0x1250, 0x1650, 0x1a50, 0x1e50, 0x02d0, 0x06d0, 0x0ad0, 0x0ed0, 0x12d0, 0x16d0, 0x1ad0, 0x1ed0, 
		0x0350, 0x0750, 0x0b50, 0x0f50, 0x1350, 0x1750, 0x1b50, 0x1f50, 0x03d0, 0x07d0, 0x0bd0, 0x0fd0, 0x13d0, 0x17d0, 0x1bd0, 0x1fd0
	};
	private static int colors_lut[];

	/**
	 * This function makes the color lookup table for hires mode. We make a
	 * table of 1024 * 2 * 7 entries. Why? Because we assume each color byte has
	 * 10 bits (8 real bits + 1 on each side) and we need different colors for
	 * odd and even addresses (2) and each byte displays 7 pixels.
	 */
	static void makeColorsLUT()
	{
		if (colors_lut != null)
			return;
		colors_lut = new int[256 * 4 * 2 * 7];
		int i, j;
		int c1, c2 = 15;
		int base = 0;
		// go thru odd and even
		for (j = 0; j < 2; j++)
		{
			// go thru 1024 values
			for (int b1 = 0; b1 < 1024; b1++)
			{
				// see if the hi bit is set
				if ((b1 & 0x80) == 0)
				{
					c1 = 1;
					c2 = 12; // red & green
				}
				else
				{
					c1 = 7;
					c2 = 9; // blue & orange
				}
				// make a value consisting of:
				// the 8th bit, then bits 0-7, then the 9th bit
				int b = ((b1 & 0x100) >> 8) | ((b1 & 0x7f) << 1) | ((b1 & 0x200) >> 1);
				// go through each pixel
				for (i = 0; i < 7; i++)
				{
					// is this pixel lit?
					if (((2 << i) & b) != 0)
					{
						// are there pixels lit on both sides of this one?
						if (((7 << i) & b) == (7 << i))
							// yes, make it white
							colors_lut[base] = 15;
						else
							// no, choose color based on odd/even byte
							// and odd/even pixel column
							colors_lut[base] = (byte)((((j ^ i) & 1) == 0) ? c1 : c2);
					}
					else
					{
						// are there pixels lit in the previous & next
						// column but none in this?
						if (((5 << i) & b) == (5 << i))
							// color this pixel
							colors_lut[base] = (byte)((((j ^ i) & 1) != 0) ? c1 : c2);
					}
					base++;
				}
			}
		}
	}

	public AppleDisplay()
	{
		textbuf = new int[24][40];
		makeColorsLUT();
		/*
		 * for (int y=0; y<YSIZE; y++) for (int x=0; x<XSIZE; x++) {
		 * pixels[x+y*XSIZE] = y & 0x0; }
		 */
		memImgSrc = new MemoryImageSource(XSIZE,YSIZE,cmodel,pixels,0,XSIZE);
		scrnimage = createImage(memImgSrc);
		allDirty = new boolean[0xc000 >> 7 /* 384 */];
		appleDirty = new boolean[0xc000 >> 7 /* 384 */];
		for (int i = 0; i < allDirty.length; i++)
			allDirty[i] = true;
		dirty = allDirty;
		refresh_interval = 500;
		refresher = new Thread(this);
	}

	public void addNotify()
	{
		super.addNotify();
		refresher.start();
	}

	public void setCharROM(byte[] rom)
	{
		for (int i = 0; i < 1024; i++)
		{
			texels[i] = (rom[i] >> 1) & 0xff;
		}
	}

	private void computeDisplaySize(int width, int height)
	{
		dispwidth = (width / XSIZE) * XSIZE;
		dispheight = (height / YSIZE) * YSIZE;
		if (dispwidth < XSIZE)
			dispwidth = width;
		if (dispheight < YSIZE)
			dispwidth = height;
		System.out.println(dispwidth + " " + dispheight);
	}

	public void layout()
	{
		super.layout();
		Dimension d = size();
		computeDisplaySize(d.width,d.height);
	}

	public void resize(int width, int height)
	{
		super.resize(width,height);
		computeDisplaySize(width,height);
	}

	public void run()
	{
		// infinite loop, updating the graphics screen every refresh_interval
		// ms.
		Graphics g;
		long t1 = System.currentTimeMillis();
		try
		{
			while (true)
			{
				// System.out.println("updateScreen()");
				updateScreen(false);
				if (pixelupdate)
				{
					g = getGraphics();
					if (g != null)
					{
						g.drawImage(scrnimage,0,0,dispwidth,dispheight,this);
					}
				}
				// govern the refresh interval
				long t2 = System.currentTimeMillis();
				long t = refresh_interval - t2 + t1;
				t1 += refresh_interval;
				// System.out.println(t);
				if (t > 0)
					Thread.sleep(t);
				Thread.yield();
			}
		}
		catch (InterruptedException e)
		{
		}
	}

	public void stop()
	{
		refresher.stop();
	}

	final void drawLoresChar(int x, int y, int b)
	{
		int i, base, c;
		base = (y << 3) * XSIZE + x * 7; // (x<<2) + (x<<1) + x
		c = b & 0x0f;
		for (i = 0; i < 4; i++)
		{
			pixels[base] = pixels[base + 1] = pixels[base + 2] = pixels[base + 3] = pixels[base + 4] = pixels[base + 5] = pixels[base + 6] = c;
			base += XSIZE;
		}
		c = b >> 4;
		for (i = 0; i < 4; i++)
		{
			pixels[base] = pixels[base + 1] = pixels[base + 2] = pixels[base + 3] = pixels[base + 4] = pixels[base + 5] = pixels[base + 6] = c;
			base += XSIZE;
		}
	}

	final void drawTextChar(int x, int y, int b, boolean invert)
	{
		int base = (y << 3) * XSIZE + x * 7; // (x<<2) + (x<<1) + x
		int on, off;
		if (invert)
		{
			on = PIXELOFF;
			off = PIXELON;
		}
		else
		{
			on = PIXELON;
			off = PIXELOFF;
		}
		for (int yy = 0; yy < 8; yy++)
		{
			int chr = texels[(b << 3) + yy];
			pixels[base] = ((chr & 1) > 0) ? on : off;
			pixels[base + 1] = ((chr & 2) > 0) ? on : off;
			pixels[base + 2] = ((chr & 4) > 0) ? on : off;
			pixels[base + 3] = ((chr & 8) > 0) ? on : off;
			pixels[base + 4] = ((chr & 16) > 0) ? on : off;
			pixels[base + 5] = ((chr & 32) > 0) ? on : off;
			pixels[base + 6] = ((chr & 64) > 0) ? on : off;
			base += XSIZE;
		}
	}

	final void drawHiresLines(int y, int maxy)
	{
		int yb = y * XSIZE;
		for (; y < maxy; y++)
		{
			int base = hires_lut[y] + (((apple.grswitch & GR_PAGE1) != 0) ? 0x4000 : 0x2000);
			if (!dirty[base >> 7])
			{
				yb += XSIZE;
				continue;
			}
			pixelupdate = true;
			int b = 0;
			int b1 = apple.mem[base] & 0xff;
			for (int x1 = 0; x1 < 20; x1++)
			{
				int b2 = apple.mem[base + 1] & 0xff;
				int b3 = apple.mem[base + 2] & 0xff;
				int d1 = (((b & 0x40) << 2) | b1 | b2 << 9) & 0x3ff;
				System.arraycopy(colors_lut,(d1 * 7),pixels,yb,7);
				int d2 = (((b1 & 0x40) << 2) | b2 | b3 << 9) & 0x3ff;
				System.arraycopy(colors_lut,(d2 * 7) + 7168,pixels,yb + 7,7);
				yb += 14;
				base += 2;
				b = b2;
				b1 = b3;
			}
		}
	}

	final void drawLoresLine(int y)
	{
		// get the base address of this line
		int base = text_lut[y] + (((apple.grswitch & GR_PAGE1) != 0) ? 0x800 : 0x400);
		// if (!dirty[base >> 7])
		// return;
		for (int x = 0; x < 40; x++)
		{
			int b = apple.mem[base + x] & 0xff;
			// if the char. changed, draw it
			if (b != textbuf[y][x])
			{
				drawLoresChar(x,y,b);
				textbuf[y][x] = b;
				pixelupdate = true;
			}
		}
	}

	final void drawTextLine(int y, boolean flash)
	{
		// get the base address of this line
		int base = text_lut[y] + (((apple.grswitch & GR_PAGE1) != 0) ? 0x800 : 0x400);
		// if (!dirty[base >> 7])
		// return;
		for (int x = 0; x < 40; x++)
		{
			int b = apple.mem[base + x] & 0xff;
			boolean invert;
			// invert flash characters 1/2 of the time
			if (b >= 0x80)
			{
				invert = false;
			}
			else if (b >= 0x40)
			{
				invert = flash;
				if (flash)
					b -= 0x40;
				else
					b += 0x40;
			}
			else
				invert = true;
			// if the char. changed, draw it
			if (b != textbuf[y][x])
			{
				drawTextChar(x,y,b & 0x7f,invert);
				textbuf[y][x] = b;
				pixelupdate = true;
			}
		}
	}

	/* old, slow, readable code */
	
	 final void drawLoresCharOLD(int x, int y, int b)
	{
		int i, base, adr, c;
		base = y * 8 * XSIZE + x * 7;
		c = b & 0x0f;
		for (i = 0; i < 4; i++)
		{
			for (adr = base; adr < base + 7; adr++)
				pixels[adr] = c;
			base += XSIZE;
		}
		c = b >> 4;
		for (i = 0; i < 4; i++)
		{
			for (adr = base; adr < base + 7; adr++)
				pixels[adr] = c;
			base += XSIZE;
		}
	}

	final void drawTextCharOLD(int x, int y, int b, boolean invert)
	{
		int base = (y * 8) * XSIZE + x * 7;
		int on, off;
		if (invert)
		{
			on = PIXELOFF;
			off = PIXELON;
		}
		else
		{
			on = PIXELON;
			off = PIXELOFF;
		}
		for (int yy = 0; yy < 8; yy++)
		{
			int chr = texels[b * 8 + yy];
			for (int xx = 0; xx < 7; xx++)
			{
				int p = ((chr & 1) > 0) ? on : off;
				pixels[base] = p;
				base++;
				chr = chr >> 1;
			}
			base += XSIZE - 7;
		}
	}

	final void drawHiresLine(int y)
	{
		int xx = 0;
		int yy = y;
		int base = hires_lut[y] + (((apple.grswitch & GR_PAGE1) != 0) ? 0x4000 : 0x2000);
		if (!dirty[base >> 7])
			return;
		int ybase = y * XSIZE;
		xx = 0;
		for (int x1 = 0; x1 < 40; x1++)
		{
			int b = apple.mem[base + x1] & 0x7f;
			for (int l = 1; l < 128; l = l << 1)
			{
				int p = ((b & l) != 0) ? PIXELON : PIXELOFF;
				pixels[ybase + xx] = p;
				xx++;
			}
		}
	}

//	final Image getPixelsImage()
//	{
//		if (imagebuf == null)
//			imagebuf = createImage(memImgSrc);
//		else
//			imagebuf.flush();
//		return imagebuf; // return
//		createImage(memImgSrc);
//	}
	 
	public void updateScreen(boolean totalrepaint)
	{
		if (apple == null)
			return;
		int y;
		boolean flash = (System.currentTimeMillis() % (flashInterval << 1)) > flashInterval;
		// if graphics mode changed, repaint whole screen
		if (apple.grswitch != oldgrmode)
		{
			oldgrmode = apple.grswitch;
			totalrepaint = true;
		}
		if (totalrepaint)
		{
			dirty = allDirty;
			// clear textbuf if in text mode
			if ((apple.grswitch & GR_TXMODE) != 0 || (apple.grswitch & GR_MIXMODE) != 0)
			{
				for (y = 0; y < 24; y++)
					for (int x = 0; x < 40; x++)
						textbuf[y][x] = -1;
			}
			pixelupdate = true;
		}
		else
		{
			for (int i = 0; i < 384; i++)
			{
				appleDirty[i] = apple.dirty[i];
				apple.dirty[i] = false;
			}
			// System.arraycopy(apple.dirty, 0, appleDirty, 0,
			// appleDirty.length);
			dirty = appleDirty;
			pixelupdate = false;
		}
		// first, draw top part of window
		if ((apple.grswitch & GR_TXMODE) != 0)
		{
			for (y = 0; y < 20; y++)
				drawTextLine(y,flash);
		}
		else
		{
			if ((apple.grswitch & GR_HIRES) != 0)
				drawHiresLines(0,160);
			else
				for (y = 0; y < 20; y++)
					drawLoresLine(y);
		}
		// now do mixed part of window
		if ((apple.grswitch & GR_TXMODE) != 0 || (apple.grswitch & GR_MIXMODE) != 0)
		{
			for (y = 20; y < 24; y++)
				drawTextLine(y,flash);
		}
		else
		{
			if ((apple.grswitch & GR_HIRES) != 0)
				drawHiresLines(160,192);
			else
				for (y = 20; y < 24; y++)
					drawLoresLine(y);
		}
		// if the image has changed at all, flush
		// the image so that it is rebuilt next time
		// we try to draw it
		if (pixelupdate)
			scrnimage.flush();
	}

	public boolean keyDown(Event evt, int key)
	{
		if (apple == null)
			return false;
		// translate Java keys to Apple keys
		switch (key)
		{
			case 10:
				key = 13;
			break;
			case Event.LEFT:
				key = 8;
			break;
			case Event.RIGHT:
				key = 21;
			break;
			case Event.UP:
				key = 11;
			break;
			case Event.DOWN:
				key = 10;
			break;
			// case Event.DELETE : key = 127; break;
		}
		if (key < 128)
			apple.keyPressed(key);
		return true;
	}

	public Dimension minimumSize()
	{
		return displaySize;
	}

	public Dimension preferredSize()
	{
		return displaySize;
	}

	public void update(Graphics g)
	{
		// System.out.println("update()");
		/*
		 * g.drawImage(scrnimage, 0, 0, this); dirtyImage = false;
		 * System.out.println("update");
		 */
	}

	public void paint(Graphics g)
	{
		// System.out.println("paint()");
		g.drawImage(scrnimage,0,0,dispwidth,dispheight,this);
		// System.out.println("paint");
		// update(g);
	}
}
