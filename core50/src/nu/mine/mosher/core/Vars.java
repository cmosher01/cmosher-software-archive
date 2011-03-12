/*
 * Created on Apr 14, 2004
 */
package nu.mine.mosher.core;

/**
 * @author Chris Mosher
 */
public final class Vars
{
	private Vars() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}



	public static final byte BYTE_DEFAULT = (byte)0;
	public static final short SHORT_DEFAULT = (short)0;
	public static final int INT_DEFAULT = 0;
	public static final long LONG_DEFAULT = 0L;
	public static final float FLOAT_DEFAULT = 0.0f;
	public static final double DOUBLE_DEFAULT = 0.0d;
	public static final char CHAR_DEFAULT = '\u0000';
	public static final boolean BOOLEAN_DEFAULT = false;
	public static final Object REFERENCE_DEFAULT = null;



	public static final Class<?>[] rclassIntegralType = new Class[]
	{
		Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE
	};
	public static final Class<?>[] rclassFloatingPointType = new Class[]
	{
		Float.TYPE, Double.TYPE
	};
}
