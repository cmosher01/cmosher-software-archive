import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class OneFileCopyClient
{
	public static void main(String[] args) throws IOException
	{
		Socket s = new Socket(InetAddress.getByName("192.168.1.102"),60013);
	}
}
