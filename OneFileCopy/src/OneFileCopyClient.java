import java.net.InetAddress;
import java.net.Socket;

public class OneFileCopyClient
{
	public static void main(String[] args)
	{
		Socket s = new Socket(new InetAddress(),60013);
	}
}
