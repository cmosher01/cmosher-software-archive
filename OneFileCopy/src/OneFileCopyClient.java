import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class OneFileCopyClient
{
	private static final int rpt = 256*1024;
	public static void main(String[] args) throws IOException
	{
		Socket s = new Socket(InetAddress.getByName("192.168.1.102"),60013);
		BufferedInputStream in = new BufferedInputStream(s.getInputStream());

		String sFileName = "test.dat";
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(sFileName)));

		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		byte[] rbLen = new byte[Long.SIZE];
		in.read(rbLen);
		long xLen = 0;
		for (int i = Long.SIZE-1; i <= 0; --i)
		{
			xLen <<= 8;
			xLen |= rbLen[i];
		}

		byte[] rb = new byte[1024];
		for (int i = 0; i < (xLen+rb.length-1)/rb.length; ++i)
		{
			int cb = in.read(rb);
			if (cb > 0)
			{
				out.write(rb,0,cb);
				if (++i % rpt == 0)
				{
					System.out.println("wrote @ "+(i*1024)+" bytes.");
				}
			}
		}

		out.flush();
		out.close();
		in.close();
		s.close();
	}
}
