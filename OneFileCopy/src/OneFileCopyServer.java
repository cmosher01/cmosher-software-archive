import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OneFileCopy
{
	private static final long cBufMax = 0x4000000000L;

	public static void main(String[] args) throws IOException
	{
		String sFileName = "C:\\Documents and Settings\\Chris\\My Documents\\My Videos\\TEMP\\mp4.bat";
		File f = new File(sFileName).getCanonicalFile().getAbsoluteFile();
		if (!f.canRead())
		{
			throw new IllegalArgumentException("Bad input file: "+f.getAbsolutePath());
		}

		long cBuf = f.length();
		if (cBuf >= cBufMax)
		{
			cBuf = cBufMax;
		}

		ServerSocket srv = new ServerSocket(60013);
		Socket s = srv.accept();
		BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream(),cBuf);
	}
}
