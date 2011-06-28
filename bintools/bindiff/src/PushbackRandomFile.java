import java.io.IOException;
import java.io.RandomAccessFile;

public class PushbackRandomFile
{
	private final RandomAccessFile f;
	private int unread = -1;

	public PushbackRandomFile(RandomAccessFile f)
	{
		this.f = f;
	}

    public int read() throws IOException
    {
    	int c;

    	if (unread != -1)
    	{
    		c = unread;
    		unread = -1;
    	}
    	else
    	{
			c = f.read();
    	}

    	return c;
    }

    public void unread(int c)
    {
    	if (unread != -1)
    	{
    		throw new IllegalStateException("only one byte can be unread at a time.");
    	}

    	unread = c;
    }

    public long tell() throws IOException
    {
    	long pos = f.getFilePointer();
    	if (unread != -1)
    	{
    		--pos;
    	}
    	return pos;
    }

    public void seek(long pos) throws IOException
    {
//		if (unread != -1)
//		{
//			throw new IllegalStateException("can't seek with an unread character.");
//		}
		f.seek(pos);
    }
}
