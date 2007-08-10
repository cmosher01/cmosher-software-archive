package pom1.apple1;

public class Keyboard
{
	private static final char BS = 0x08;
	private static final char LF = 0x0A;
	private static final char CR = 0x0D;

	public static int translateKey(char key)
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
