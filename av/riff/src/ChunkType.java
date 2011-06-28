import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created on May 9, 2006
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class ChunkType
{
	private static final Pattern streamID = Pattern.compile("(\\d\\d)(..)");

	private final String fourCC;
	private final int streamNumber;
	private final String streamType;

	/**
	 * @param sFourCC
	 * @throws UnsupportedEncodingException
	 */
	public ChunkType(final String sFourCC) throws UnsupportedEncodingException
	{
		this(sFourCC.getBytes("US-ASCII"));
	}

	/**
	 * @param rbFourCC
	 * @throws UnsupportedEncodingException
	 */
	public ChunkType(final byte[] rbFourCC) throws UnsupportedEncodingException
	{
		int len = rbFourCC.length;
		if (len > 4)
		{
			len = 4;
		}
		this.fourCC = new String(rbFourCC,0,len,"US-ASCII").trim();
		final Matcher matcher = streamID.matcher(this.fourCC);
		if (matcher.matches())
		{
			this.streamNumber = Integer.parseInt(matcher.group(1));
			this.streamType = matcher.group(2).trim();
		}
		else
		{
			this.streamNumber = -1;
			this.streamType = "";
		}
	}

	@Override
	public String toString()
	{
		return this.fourCC;
	}

	private static final byte SPACE = (byte)0x20;

	/**
	 * @return array of 4 bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] getBytes() throws UnsupportedEncodingException
	{
		final byte[] rb = this.fourCC.getBytes("US-ASCII");
		if (rb.length > 4)
		{
			throw new IllegalStateException();
		}
		if (rb.length < 4)
		{
			final byte[] ret = new byte[4];
			Arrays.fill(ret,SPACE);
			System.arraycopy(rb,0,ret,0,rb.length);
			return ret;
		}
		return rb;
	}

	/**
	 * @return true if this chunk is a list of chunks (RIFF or LIST)
	 */
	public boolean isList()
	{
		return this.fourCC.equals("LIST") || this.fourCC.equals("RIFF");
	}

	/**
	 * The stream number in this 4cc, or -1 if none.
	 * @return stream number or -1
	 */
	public int getStreamNumber()
	{
		return this.streamNumber;
	}

	/**
	 * @return The stream type, or empty string if none.
	 */
	public String getStreamType()
	{
		return this.streamType;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof ChunkType))
		{
			return false;
		}
		final ChunkType that = (ChunkType)object;
		return this.fourCC.equals(that.fourCC);
	}

	@Override
	public int hashCode()
	{
		return this.fourCC.hashCode();
	}
}
