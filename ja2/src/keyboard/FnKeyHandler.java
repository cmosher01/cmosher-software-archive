package keyboard;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import video.ScreenImage;
import video.VideoDisplayDevice;
import chipset.Memory;
import chipset.Throttle;
import emu.Apple2;

/*
 * Created on Sep 12, 2007
 */
public class FnKeyHandler extends KeyAdapter implements KeyListener
{
	private final Apple2 a2;
	private final ScreenImage screenImage;
	private final Memory memory;
	private final Throttle throttle;
	private final VideoDisplayDevice device;
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	 * @param cpu
	 * @param screenImage 
	 * @param memory 
	 * @param device 
	 */
	public FnKeyHandler(final Apple2 a2, final ScreenImage screenImage, final Memory memory, final Throttle throttle, final VideoDisplayDevice device)
	{
		this.a2 = a2;
		this.screenImage = screenImage;
		this.memory = memory;
		this.throttle = throttle;
		this.device = device;
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
			this.a2.reset();
		}
		else if (key == KeyEvent.VK_SCROLL_LOCK)
		{
			this.throttle.toggleSuspend();
		}
		else if (key == KeyEvent.VK_F9)
		{
			try
			{
				final String name = "dump"+FnKeyHandler.fmt.format(new Date())+".png";
				this.screenImage.dump("PNG",new File(name));
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

				final String name = "dump"+FnKeyHandler.fmt.format(new Date())+".bin";
				final OutputStream fil = new BufferedOutputStream(new FileOutputStream(new File(name)));
				for (int i = 0; i < this.memory.size(); ++i)
				{
					fil.write(this.memory.read(i));
				}
				fil.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		else if (key == KeyEvent.VK_F2)
		{
			this.device.dump();
		}
	}
}
