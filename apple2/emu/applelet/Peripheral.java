public class Peripheral
{
	EmAppleII emu;

	public Peripheral(EmAppleII theapple)
	{
		emu = theapple;
	}

	public int doIO(int address, int value)
	{
		return emu.noise();
	}

	public int doHighIO(int address, int value)
	{
		return emu.noise();
	}
}
