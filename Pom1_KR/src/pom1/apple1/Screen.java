package pom1.apple1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JPanel;

public class Screen extends JPanel
{
	private static final int CHARSET_SIZE = 128;
	private static final int Y_PIX = 8;
	private static final int X_PIX = 7;
	private static final int Y_CHARS = 24;
	private static final int X_CHARS = 40;

	public Screen() throws IOException
	{
		lastTime = System.currentTimeMillis();
		loggingOutput = false;
		charac = new int[CHARSET_SIZE][Y_PIX];
		screenTbl = new int[X_CHARS][Y_CHARS];
		scanline = false;
		loadCharac();
		this.pixelSize = 1;
		terminalSpeed = 60000;
		setPreferredSize(new Dimension(X_CHARS * X_PIX * this.pixelSize,Y_CHARS * Y_PIX * this.pixelSize));
		reset();
	}

	public void setPixelSize(int ps)
	{
		pixelSize = ps;
	}

	public void setScanline(boolean scanline)
	{
		this.scanline = scanline;
	}

	public void setTerminalSpeed(int ts)
	{
		terminalSpeed = ts;
	}

//	public int getTerminalSpeed()
//	{
//		return terminalSpeed;
//	}

	public void reset()
	{
		indexX = indexY = 0;
		initScreenTbl();
		repaint();
	}

	public void outputDsp(int dsp)
	{
		if (loggingOutput)
		{
			if (dsp == 0x0A || dsp == 0x0D)
			{
				System.out.println();
			}
			else if (0x20 <= dsp && dsp < 0x5F) // legal characters
			{
				System.out.print((char)dsp);
			}
			else
			{
				System.out.println("<x" + Integer.toHexString(dsp) + ">");
			}
		}
		switch (dsp)
		{
			case 0x5F: // Backspace
				if (indexX == 0)
				{
					indexY--;
					indexX = 39;
				}
				else
				{
					indexX--;
				}
				screenTbl[indexX][indexY] = 0x00;
			break;
			case 0x0A: // '\n'
			case 0x0D: // '\r'
				indexX = 0;
				indexY++;
			break;
			default:
				if (0x20 <= dsp && dsp < 0x5F) // legal characters
				{
					screenTbl[indexX][indexY] = dsp;
					indexX++;
				}
			break;
		}
		if (indexX == X_CHARS)
		{
			indexX = 0;
			indexY++;
		}
		if (indexY == Y_CHARS)
		{
			newLine();
			indexY--;
		}
		repaint();
		synchronizeOutput();
	}

	public void update(Graphics gc)
	{
		if (offScrImg == null)
			offScrImg = createImage(X_CHARS * X_PIX * pixelSize,Y_CHARS * Y_PIX * pixelSize);
		Graphics og = offScrImg.getGraphics();
		paint(og);
		gc.drawImage(offScrImg,0,0,this);
		og.dispose();
	}

	public void paint(Graphics gc)
	{
		gc.setColor(Color.black);
		gc.fillRect(0,0,X_CHARS * X_PIX * pixelSize,Y_CHARS * Y_PIX * pixelSize);
		gc.setColor(Color.green);
		for (int i = 0; i < X_CHARS; i++)
		{
			for (int j = 0; j < Y_CHARS; j++)
			{
				int xPosition = i * (pixelSize * X_PIX);
				int yPosition = j * (pixelSize * Y_PIX);
				drawCharac(gc,xPosition,yPosition,screenTbl[i][j]);
			}
		}
		drawCharac(gc,indexX * (pixelSize * X_PIX),indexY * (pixelSize * Y_PIX),1); // cursor
	}

	private void synchronizeOutput()
	{
		if (!synchronise)
		{
			return;
		}
		int sleepMillis = (int)((1000 / terminalSpeed) - (System.currentTimeMillis() - lastTime));
		if (sleepMillis > 0)
			try
			{
				if (synchronise)
					Thread.sleep(sleepMillis);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		lastTime = System.currentTimeMillis();
	}

	private void drawCharac(Graphics gc, int xPosition, int yPosition, int characNumber)
	{
		gc.setColor(Color.green);
		for (int k = 0; k < Y_PIX; k++)
		{
			for (int l = 1; l < Y_PIX; l++)
				if ((charac[characNumber][k] & 1 << l) == 1 << l)
					gc.fillRect(xPosition + pixelSize * (l - 1),yPosition + pixelSize * k,pixelSize,pixelSize - (scanline ? 1 : 0));
		}
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

	private void initScreenTbl()
	{
		for (int i = 0; i < X_CHARS; i++)
		{
			for (int j = 0; j < Y_CHARS; j++)
				screenTbl[i][j] = 0;
		}
	}

	private void newLine()
	{
		for (int i = 0; i < X_CHARS; i++)
		{
			for (int j = 0; j < Y_CHARS - 1; j++)
			{
				screenTbl[i][j] = screenTbl[i][j + 1];
			}
		}
		for (int i = 0; i < X_CHARS; i++)
		{
			screenTbl[i][Y_CHARS - 1] = 0;
		}
	}

	public void setSynchronise(boolean sync)
	{
		synchronise = sync;
	}

	private int charac[][];
	private int screenTbl[][];
	private int indexX;
	private int indexY;
	private int pixelSize;
	private boolean scanline;
	private int terminalSpeed;
	private Image offScrImg;
	private long lastTime;
	private boolean loggingOutput;
	boolean synchronise = true;
}
