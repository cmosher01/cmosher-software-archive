import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Created on Aug 7, 2007
 */
public class S2Bin
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(FileDescriptor.out));

		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			if (!line.startsWith("S"))
			{
				continue;
			}
			final int type = Integer.parseInt(line.substring(1,2));
			switch (type)
			{
				case 1:
				{
					int len = Integer.decode("0x"+line.substring(2,4));
					len -= 3; // addr and checksum
//					final int addr = Integer.parseInt(line.substring(4,8));
					int st = 8;
					while (len > 0)
					{
						final int byt = Integer.decode("0x"+line.substring(st,st+2));
						out.write(byt);
						--len;
						++st; ++st;
					}
				}
				break;
			}
		}
		out.flush();
		out.close();
		in.close();
	}
}
