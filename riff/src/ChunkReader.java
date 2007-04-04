/*
 * Created on May 9, 2006
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class ChunkReader
{
	private final BufferedInputStream stream;
	private final BufferedOutputStream riffOut;

	private boolean inVidsHeader;
	private int cStream;
	private int iStreamVids = -1;

	/**
	 * @param stream
	 * @param riffOut 
	 */
	public ChunkReader(final BufferedInputStream stream, final BufferedOutputStream riffOut)
	{
		this.stream = stream;
		this.riffOut = riffOut;
	}

	/**
	 * @param siz
	 * @param riffPath
	 * @throws IOException
	 */
	public void readList(final long siz, final RiffPath riffPath) throws IOException
	{
		long remaining = siz;
		while (remaining > 0)
		{
			remaining -= readChunk(riffPath);
		}
	}

	private long readChunk(final RiffPath riffPath) throws IOException
	{
		final ChunkType chunkType = readFourCC();
		writeFourCC(chunkType);

		final long siz = read32uint();
		write32uint(siz);

		if (chunkType.isList())
		{
			final ChunkType listType = readFourCC();
			writeFourCC(listType);

			readList(siz-4L,riffPath.add(listType));
		}
		else
		{
			parseChunk(riffPath.add(chunkType),siz);
		}
		return siz+8L;
	}

	private void parseChunk(final RiffPath riffPath, final long siz) throws IOException
	{
		System.out.println("----------------------------------"+riffPath);

		long remain = siz;
		if (riffPath.equals(RiffPath.parse("/AVI/hdrl/strl/strh")))
		{
			++this.cStream;

			final ChunkType fccType = readFourCC();
			remain -= 4;
			writeFourCC(fccType);

			if (fccType.equals(new ChunkType("vids")))
			{
				final ChunkType encoding = readFourCC(); // see www.fourcc.org for definitions
				remain -= 4;

				if (encoding.equals(new ChunkType("UYVY")))
				{
					writeFourCC(new ChunkType("YUY2"));
					this.inVidsHeader = true;
					this.iStreamVids = this.cStream-1;
					System.out.println("UYVY encoded stream #"+this.iStreamVids+"; will convert to YUY2");
				}
				else
				{
					writeFourCC(encoding);
				}
			}
			else
			{
				this.inVidsHeader = false;
			}
		}
		else if (riffPath.equals(RiffPath.parse("/AVI/hdrl/strl/strf")))
		{
			if (this.inVidsHeader)
			{
				final byte[] rb16 = new byte[16];
				this.stream.read(rb16);
				remain -= 16;
				this.riffOut.write(rb16);

				final ChunkType encoding = readFourCC();
				remain -= 4;

				if (!encoding.equals(new ChunkType("UYVY")))
				{
					throw new IllegalStateException("vids encoding was UYVY, but bitmap encoding was different: "+encoding);
				}

				writeFourCC(new ChunkType("YUY2"));
			}
		}
		else if (riffPath.startsWith(RiffPath.parse("/AVI/movi")))
		{
			final ChunkType chunk = riffPath.getChunk();
			final int iStream = chunk.getStreamNumber();
			if (iStream == this.iStreamVids)
			{
				final byte[] rb = new byte[2];
				while (remain > 0)
				{
					this.stream.read(rb);
					this.riffOut.write(rb,1,1);
					this.riffOut.write(rb,0,1);
					remain -= 2;
				}
			}
		}

		if (remain >= 0x100000000L)
		{
			throw new IllegalStateException("chunk is too big");
		}
		final byte[] rb = new byte[(int)remain];
		this.stream.read(rb);
		this.riffOut.write(rb);
	}

//	private void skip(final long siz) throws IOException
//	{
//		if (siz <= 0)
//		{
//			return;
//		}
//		long toSkip = siz;
//		while (toSkip > 0)
//		{
//			toSkip -= this.stream.skip(toSkip);
//		}
//	}

	private ChunkType readFourCC() throws IOException
	{
		final byte[] rbFourCC = new byte[4];
		this.stream.read(rbFourCC);
		return new ChunkType(rbFourCC);
	}

	private void writeFourCC(final ChunkType chunkType) throws IOException
	{
		this.riffOut.write(chunkType.getBytes());
	}

	private long read32uint() throws IOException
	{
		final byte[] rb = new byte[4];
		this.stream.read(rb);

		long siz = 0;
		for (int i = rb.length-1; i >= 0; --i)
		{
			siz <<= 8;
			siz |= rb[i] & 0xffL;
		}
		return siz;
	}

	private void write32uint(final long siz) throws IOException
	{
		final int b0 = (int)(siz & 0xffL);
		final int b1 = (int)(siz>>>8 & 0xffL);
		final int b2 = (int)(siz>>>16 & 0xffL);
		final int b3 = (int)(siz>>>24 & 0xffL);
		this.riffOut.write(b0);
		this.riffOut.write(b1);
		this.riffOut.write(b2);
		this.riffOut.write(b3);
	}
}
