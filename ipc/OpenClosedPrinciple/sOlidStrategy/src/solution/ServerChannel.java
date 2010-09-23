package solution;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ServerChannel
{
	private Socket socket;
	public void sendWithNonce(byte data[], NonceStrategy nonce) throws IOException
	{
		OutputStream server = this.socket.getOutputStream();
		server.write(data);
		server.write(nonce.generateNonce().getBytes());
		server.close();
	}
}
