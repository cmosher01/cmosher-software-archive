package problem;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerChannel
{
	private static Format format = new SimpleDateFormat("yyyyMMddHHmmss");
	private Socket socket;
	public void sendWithNonce(byte data[]) throws IOException
	{
		OutputStream server = this.socket.getOutputStream();
		server.write(data);
		String nonce = format.format(new Date());
		server.write(nonce.getBytes());
		server.close();
	}
}
