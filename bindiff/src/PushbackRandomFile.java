import java.io.IOException;
import java.io.RandomAccessFile;

public class PushbackRandomFile
{
	private RandomAccessFile f;
	private int unread = -1;

	public PushbackRandomFile(RandomAccessFile f)
	{
		this.f = f;
	}

    public int read() throws IOException
    {
    	return f.read();
    }

    /**
     * @param c1
     */
    public void unread(int c)
    {
    	if (unread != -1)
    	{
    		throw new IllegalStateException("only one byte can be unread at a time.");
    	}

    	unread = c;
    }
}
