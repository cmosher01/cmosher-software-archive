import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OneFileCopy
{
	private static final int cBufMax = 0x10000000;

	public static void main(String[] args) throws IOException
	{
		String sFileName = "C:\\Documents and Settings\\Chris\\My Documents\\My Videos\\TEMP\\mp4.bat";
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
		Socket s = srv.accept();

		byte[] rb = new byte[1024];

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
		BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream(),cBuf);

		int cb = in.read(rb);
		while (cb > 0)
		{
			out.write(rb,0,cb);
		}

		out.flush();
		out.close();
		in.close();
		s.close();
		srv.close();
	}
}
