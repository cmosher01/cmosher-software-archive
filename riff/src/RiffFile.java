import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Created on May 9, 2006
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RiffFile
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			throw new IllegalArgumentException("usage: java Riff in-file out-file");
		}

		final File fileIn = new File(args[0]);
		final File fileOut = new File(args[1]);
		final RiffFile riff = new RiffFile(fileIn,fileOut);
		riff.readList();
	}

	private final File fileRiffIn;
	private final File fileRiffOut;

	/**
	 * @param fileRiffIn
	 * @param fileRiffOut
	 */
	public RiffFile(final File fileRiffIn, final File fileRiffOut)
	{
		this.fileRiffIn = fileRiffIn;
		this.fileRiffOut = fileRiffOut;
	}

	/**
	 * @throws IOException
	 */
	public void readList() throws IOException
	{
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.fileRiffIn));
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.fileRiffOut));
		final ChunkReader cr = new ChunkReader(in,out);
		cr.readList(this.fileRiffIn.length(),RiffPath.EMPTY);
		in.close();
		out.flush();
		out.close();
	}
}
