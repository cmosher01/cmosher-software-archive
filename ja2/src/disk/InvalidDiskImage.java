/*
 * Created on Nov 28, 2007
 */
package disk;

public class InvalidDiskImage extends Exception
{
	private static final String MSG = "That does not appear to be a valid nibble disk image. This emulator only accepts NIBBLE images.";

	/**
	 * 
	 */
	public InvalidDiskImage()
	{
		super(MSG);
	}

	/**
	 * @param cause
	 */
	public InvalidDiskImage(Throwable cause)
	{
		super(MSG,cause);
	}
}
