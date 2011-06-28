import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;



/*
 * Created on May 9, 2006
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RiffPath
{
	/**
	 * an empty path
	 */
	public static final RiffPath EMPTY = new RiffPath(null,null);

	private final List<ChunkType> rChunk = new ArrayList<ChunkType>();

	private RiffPath(final List<ChunkType> rChunk, final ChunkType typeChunk)
	{
		if (rChunk != null)
		{
			this.rChunk.addAll(rChunk);
		}
		if (typeChunk != null)
		{
			this.rChunk.add(typeChunk);
		}
	}

	/**
	 * @param path
	 * @return new RiffPath
	 * @throws UnsupportedEncodingException
	 */
	public static RiffPath parse(final String path) throws UnsupportedEncodingException
	{
		final StringTokenizer tok = new StringTokenizer(path,"/");
		final List<ChunkType> rChunk = new ArrayList<ChunkType>();
		while (tok.hasMoreTokens())
		{
			rChunk.add(new ChunkType(tok.nextToken()));
		}
		return new RiffPath(rChunk,null);
	}

	/**
	 * @param typeChunk
	 * @return new RiffPath
	 */
	public RiffPath add(final ChunkType typeChunk)
	{
		return new RiffPath(this.rChunk,typeChunk);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof RiffPath))
		{
			return false;
		}
		final RiffPath that = (RiffPath)object;
		return this.rChunk.equals(that.rChunk);
	}

	@Override
	public int hashCode()
	{
		return this.rChunk.hashCode();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		for (final ChunkType typeChunk : this.rChunk)
		{
			sb.append('/');
			sb.append(typeChunk.toString());
		}
		return sb.toString();
	}

	/**
	 * @param riffPath
	 * @return true if this path starts with the given path
	 */
	public boolean startsWith(final RiffPath riffPath)
	{
		if (riffPath.rChunk.size() > this.rChunk.size())
		{
			return false;
		}
		final Iterator<ChunkType> iThat = riffPath.rChunk.iterator();
		final Iterator<ChunkType> iThis = this.rChunk.iterator();
		while (iThat.hasNext())
		{
			final ChunkType thatChunk = iThat.next();
			final ChunkType thisChunk = iThis.next();
			if (!thatChunk.equals(thisChunk))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the (last) chunk type in this path
	 */
	public ChunkType getChunk()
	{
		return this.rChunk.get(this.rChunk.size()-1);
	}
}
