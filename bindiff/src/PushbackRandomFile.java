import java.io.IOException;
import java.io.RandomAccessFile;

public class PushbackRandomFile
{
	private RandomAccessFile f;

	public PushbackRandomFile(RandomAccessFile f)
	{
		this.f = f;
	}

    public int read() throws IOException
    {
    	return f.read();
    }
}
