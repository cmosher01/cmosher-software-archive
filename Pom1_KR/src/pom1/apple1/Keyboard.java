package pom1.apple1;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard extends KeyAdapter implements KeyListener
{
	private static final char BS = 0x08;
	private static final char LF = 0x0A;
	private static final char CR = 0x0D;

	private final Pia6820 pia;

	public Keyboard(final Pia6820 pia)
	{
		this.pia = pia;
	}

	public void keyTyped(final KeyEvent e)
	{
		keyTyped(e.getKeyChar());
	}

	public void keyTyped(final char key)
	{
		if (pia.getKbdInterrups())
		{
			pia.writeKbd(translateKey(key));
			pia.writeKbdCr(0xA7);
		}
	}

	private static int translateKey(char key)
	{
		if (key == LF)
		{
			key = CR;
		}
		else if (key == BS)
		{
			key = '_';
		}

		key = Character.toUpperCase(key);

		key |= 0x80;

		return key;
	}
}
