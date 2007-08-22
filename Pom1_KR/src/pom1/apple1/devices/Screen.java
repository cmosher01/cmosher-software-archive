package pom1.apple1.devices;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Screen extends JPanel implements OutputDevice
{
	private static final int CHARSET_SIZE = 128;
	private static final int Y_PIX = 8;
	private static final int X_PIX = 7;
	private static final int Y_CHARS = 24;
	private static final int X_CHARS = 40;
	private AtomicBoolean ready = new AtomicBoolean();

	public Screen() throws IOException
	{
		charac = new int[CHARSET_SIZE][Y_PIX];
//		screenTbl = new int[X_CHARS][Y_CHARS];
		scanline = false;
		loadCharac();
		this.pixelSize = 1;
		setPreferredSize(new Dimension(X_CHARS * X_PIX * this.pixelSize,Y_CHARS * Y_PIX * this.pixelSize));
		reset();
	}

//	public void setPixelSize(int ps)
//	{
//		pixelSize = ps;
//	}
//
//	public void setScanline(boolean scanline)
//	{
//		this.scanline = scanline;
//	}

	/**
	 * 
	 */
	public void reset()
	{
		indexX = indexY = 0;
		ready.set(false);
//		initScreenTbl();
		repaint();
	}

	/**
	 * @param c
	 */
	public void putCharacter(final int c)
	{
//		if (loggingOutput)
//		{
//			if (c == 0x0A || c == 0x0D)
//			{
//				System.out.println();
//			}
//			else if (0x20 <= c && c < 0x5F) // legal characters
//			{
//				System.out.print((char)c);
//			}
//			else
//			{
//				System.out.println("<x" + Integer.toHexString(c) + ">");
//			}
//		}
		while (!ready.get())
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		Graphics gr = offScrImg.getGraphics();
		switch (c)
		{
			case '_': // Backspace
				drawCharacCurr(gr,0);
				if (indexX == 0)
				{
					indexY--;
					indexX = 39;
				}
				else
				{
					indexX--;
				}
//				screenTbl[indexX][indexY] = 0x00;
			break;
			case '\n':
			case '\r':
				drawCharacCurr(gr,0);
				indexX = 0;
				indexY++;
			break;
			default:
				if (' ' <= c && c < '_') // legal characters
				{
//					screenTbl[indexX][indexY] = dsp;
					drawCharacCurr(gr,c);
					indexX++;
				}
			break;
		}
		if (X_CHARS <= indexX)
		{
			indexX = 0;
			indexY++;
		}
		if (Y_CHARS <= indexY)
		{
			scroll(gr);
			//newLine();
			indexY--;
		}
		drawCharacCurr(gr,1);
		gr.dispose();
		SwingUtilities.invokeLater(repaint);
	}

	private final Runnable repaint = new Runnable()
	{
		public void run()
		{
			repaint();
		}
	};
//	public void update(Graphics gc)
//	{
//		if (offScrImg == null)
//			offScrImg = createVolatileImage(X_CHARS * X_PIX * pixelSize,Y_CHARS * Y_PIX * pixelSize);
//		Graphics og = offScrImg.getGraphics();
//		paint(og);
//		gc.drawImage(offScrImg,0,0,this);
//		og.dispose();
//	}


	/**
	 * @param g
	 */
	@Override
	public void paintComponent(final Graphics gc)
	{
		super.paintComponent(gc);
		if (!ready.get())
		{
			offScrImg = createVolatileImage(X_CHARS * X_PIX * pixelSize,Y_CHARS * Y_PIX * pixelSize);
			createCharImages();
			Graphics g = offScrImg.getGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,X_CHARS * X_PIX * pixelSize,Y_CHARS * Y_PIX * pixelSize);
			g.dispose();
			ready.set(true);
		}
		gc.drawImage(offScrImg,0,0,this);
	}

	private void createCharImages()
	{
		for (int i = 0; i < CHARSET_SIZE; i++)
		{
			final Image img = createVolatileImage(X_PIX*pixelSize,Y_PIX*pixelSize);
			Graphics g = img.getGraphics();
			drawCharacImg(g,0,0,i);
			g.dispose();
			this.offScrImgChar.add(img);
		}
	}

//	private void x(Graphics gc)
//	{
//		//gc.setColor(Color.black);
//		//gc.fillRect(0,0,X_CHARS * X_PIX * pixelSize,Y_CHARS * Y_PIX * pixelSize);
//		//gc.setColor(Color.green);
//		int yPosition = 0;
//		for (int j = 0; j < Y_CHARS; ++j)
//		{
//			int xPosition = 0;
//			for (int i = 0; i < X_CHARS; ++i)
//			{
//				drawCharac(gc,xPosition,yPosition,screenTbl[i][j]);
//				xPosition += pixelSize * X_PIX;
//			}
//			yPosition += pixelSize * Y_PIX;
//		}
//		drawCharac(gc,indexX * (pixelSize * X_PIX),indexY * (pixelSize * Y_PIX),1); // cursor
//	}

//	private void drawCharacCurrTbl(final Graphics gc)
//	{
//		drawCharac(gc, indexX * (pixelSize * X_PIX), indexY * (pixelSize * Y_PIX), screenTbl[indexX][indexY]);
//	}

	private void drawCharacCurr(final Graphics gc, final int characNumber)
	{
		drawCharac(gc, indexX * (pixelSize * X_PIX), indexY * (pixelSize * Y_PIX), characNumber);
	}

	private void drawCharac(final Graphics gc, final int xPosition, final int yPosition, final int characNumber)
	{
		gc.drawImage(this.offScrImgChar.get(characNumber),xPosition,yPosition,this);
	}

	private void drawCharacImg(final Graphics gc, final int xPosition, final int yPosition, final int characNumber)
	{
		final int[] rc = this.charac[characNumber];
		for (int y = 0; y < Y_PIX; ++y)
		{
			int c = rc[y];
			for (int x = 0; x < X_PIX; ++x)
			{
				c >>>= 1;
				gc.setColor((c&1) == 0 ? Color.black : Color.green);
				drawPixel(gc,xPosition,yPosition,y,x);
			}
		}
	}

	private void drawPixel(final Graphics gc, final int xPosition, final int yPosition, int y, int x)
	{
		gc.fillRect(
			xPosition + pixelSize * x,
			yPosition + pixelSize * y,
			pixelSize,
			pixelSize /*- (scanline ? 1 : 0)*/);
	}

	private void scroll(final Graphics gc)
	{
		gc.copyArea(
			0, 0,
			X_CHARS * X_PIX * pixelSize, Y_CHARS * Y_PIX * pixelSize,
			0, - Y_PIX * pixelSize);

		gc.setColor(Color.black);
		gc.fillRect(
			0, (Y_CHARS-1) * Y_PIX * pixelSize,
			X_CHARS * X_PIX * pixelSize, Y_PIX * pixelSize);
	}

	private void loadCharac() throws IOException
	{
		String filename = System.getProperty("user.dir") + "/bios/apple1.vid";
		FileInputStream fis = new FileInputStream(filename);
		for (int i = 0; i < CHARSET_SIZE; i++)
		{
			for (int j = 0; j < Y_PIX; j++)
			{
				charac[i][j] = fis.read();
			}
		}
		fis.close();
		//charac[95][6] = 63; underscore???
	}

//	private void initScreenTbl()
//	{
//		for (int i = 0; i < X_CHARS; i++)
//		{
//			for (int j = 0; j < Y_CHARS; j++)
//				screenTbl[i][j] = 0;
//		}
//	}

//	private void newLine()
//	{
//		for (int x = 0; x < X_CHARS; x++)
//		{
//			for (int y = 0; y < Y_CHARS - 1; y++)
//			{
//				screenTbl[x][y] = screenTbl[x][y + 1];
//			}
//		}
//		for (int i = 0; i < X_CHARS; i++)
//		{
//			screenTbl[i][Y_CHARS - 1] = 0;
//		}
//	}

	private int charac[][];
//	private volatile int screenTbl[][];
	private volatile int indexX;
	private volatile int indexY;
	private final int pixelSize;
	private final boolean scanline;
	private volatile Image offScrImg;
	private volatile List<Image> offScrImgChar = new ArrayList<Image>(CHARSET_SIZE);
//	private static final boolean loggingOutput = false;
}
