import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class OneFileCopyClient
{
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
		byte[] rb = new byte[1024];
		while (in.available() != 0)
		{
			int cb = in.read(rb);
			if (cb > 0)
			{
				out.write(rb,0,cb);
			}
		}

		out.flush();
		out.close();
		in.close();
		s.close();
	}
}
