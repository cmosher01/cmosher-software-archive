package pom1.apple1;

public class Keyboard
{
	private static final char BS = 0x08;
	private static final char LF = 0x0A;
	private static final char CR = 0x0D;
	private final Pia6820 pia;

	public Keyboard(Pia6820 pia)
	{
		this.pia = pia;
	}

	public void keyTyped(char key)
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

		return key | 0x80;
	}
}
