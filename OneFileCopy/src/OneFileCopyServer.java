import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OneFileCopyServer
{
	private static final int cBufMax = 0x10000000;
	private static final int rpt = 256*1024;

	public static void main(String[] args) throws IOException
	{
		String sFileName = "C:\\Documents and Settings\\Chris\\My Documents\\My Videos\\199912\\test5.avi";
		File f = new File(sFileName).getCanonicalFile().getAbsoluteFile();
		if (!f.canRead())
		{
			throw new IllegalArgumentException("Bad input file: "+f.getAbsolutePath());
		}

		int cBuf = cBufMax;
		if (f.length() < cBuf)
		{
			cBuf = (int)f.length();
		}

		ServerSocket srv = new ServerSocket(60013);
		System.out.println("Ready to serve:");
		System.out.println("file: "+f.getAbsolutePath());
		System.out.println("port: 60013");

		Socket s = srv.accept();
		System.out.println("accepted connection, sending file:");

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
		BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream(),cBuf);

		byte[] rb = new byte[1024];
		
		int i = 0;
		while (in.available() != 0)
		{
			int cb = in.read(rb);
			if (cb > 0)
			{
				out.write(rb,0,cb);
			}
			if (++i % rpt == 0)
			{
				System.out.println("sent @ "+(i*1024)+" bytes.");
			}
		}

		out.flush();
		out.close();
		in.close();
		s.close();
		srv.close();
		System.out.println("done.");
	}
}
