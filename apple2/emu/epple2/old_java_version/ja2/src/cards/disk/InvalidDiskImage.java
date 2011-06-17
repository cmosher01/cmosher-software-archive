/*
 * Created on Nov 28, 2007
 */
package cards.disk;

public class InvalidDiskImage extends Exception
{
	private static final String MSG = " does not appear to be a valid nibble disk image. This emulator only accepts NIBBLE images.";

	/**
	 * 
	 */
	public InvalidDiskImage(final String filename)
	{
		super(filename+MSG);
	}

	/**
	 * @param cause
	 */
	public InvalidDiskImage(final String filename, final Throwable cause)
	{
		super(filename+MSG,cause);
	}
}
