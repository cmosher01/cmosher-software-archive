package keyboard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import chipset.CPU6502;
import disk.DiskInterface;

/*
 * Created on Sep 12, 2007
 */
public class FnKeyHandler extends KeyAdapter implements KeyListener
{
	private final CPU6502 cpu;
	private final DiskInterface disk;

	/**
	 * @param cpu
	 */
	public FnKeyHandler(final CPU6502 cpu, final DiskInterface disk)
	{
		this.cpu = cpu;
		this.disk = disk;
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
			
		}
	}
}
