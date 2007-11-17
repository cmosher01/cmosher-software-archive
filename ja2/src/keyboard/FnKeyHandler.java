package keyboard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
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

	/**
	 * @param cpu
	 */
	public FnKeyHandler(final CPU6502 cpu, final DiskInterface disk, final ClipboardHandler clip)
	{
		this.cpu = cpu;
		this.disk = disk;
		this.clip = clip;
	}

	/**
	 * @param e
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
//		System.out.println("raw key down: "+Integer.toHexString(e.getKeyCode()));
		final int key = e.getKeyCode();
		if (key == KeyEvent.VK_PAUSE)
		{
			cpu.reset();
			disk.reset();
		}
		else if (key == KeyEvent.VK_INSERT)
		{
			this.clip.paste();
		}
		else if (key == KeyEvent.VK_PRINTSCREEN)
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
