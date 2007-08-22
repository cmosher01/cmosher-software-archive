/*
 * Created on Aug 22, 2007
 */
package pom1.apple1.devices;

public class StandardOutput implements OutputDevice
{
	public void putCharacter(int c)
	{
		if (c == '\r' || c == '\n')
		{
			System.out.println();
		}
		else
		{
			System.out.write(c);
		}
		System.out.flush();
	}

	public void reset()
	{
		// do nothing
	}
}
