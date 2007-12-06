package keyboard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import chipset.Memory;
import chipset.cpu.CPU6502;

/*
 * Created on Sep 12, 2007
 */
public class FnKeyHandler extends KeyAdapter implements KeyListener
{
	private final CPU6502 cpu;
	private final BufferedImage screenImage;
	private final Memory memory;

    final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	 * @param cpu
	 * @param screenImage 
	 */
	public FnKeyHandler(final CPU6502 cpu, final BufferedImage screenImage, final Memory memory)
	{
		this.cpu = cpu;
		this.screenImage = screenImage;
		this.memory = memory;
	}

	/**
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		final int key = e.getKeyCode();
		if (key == KeyEvent.VK_PAUSE)
		{
			this.cpu.reset();
		}
		else if (key == KeyEvent.VK_F9)
		{
			try
			{
				final String name = "dump"+this.fmt.format(new Date())+".png";
				ImageIO.write(this.screenImage,"PNG",new File(name));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		else if (key == KeyEvent.VK_F10)
		{
			try
			{
				this.memory.dump();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
