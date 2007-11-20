package keyboard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import video.Video;
import chipset.CPU6502;
import disk.DiskInterface;

/*
 * Created on Sep 12, 2007
 */
public class FnKeyHandler extends KeyAdapter implements KeyListener
{
	private final CPU6502 cpu;
	private final DiskInterface disk;
	private final ClipboardHandler clip;
	private final Video video;

	/**
	 * @param cpu
	 * @param video 
	 */
	public FnKeyHandler(final CPU6502 cpu, final DiskInterface disk, final ClipboardHandler clip, final Video video)
	{
		this.cpu = cpu;
		this.disk = disk;
		this.clip = clip;
		this.video = video;
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
			this.disk.reset();
		}
		else if (key == KeyEvent.VK_INSERT)
		{
			this.clip.paste();
		}
		else if (key == KeyEvent.VK_F9)
		{
			try
			{
				this.video.dump();
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
				this.cpu.dump();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
