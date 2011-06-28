import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * Created on Aug 7, 2007
 */
public class Bits
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		final InputStream in = new FileInputStream("C:\\apple2\\charrom.bin");
		for (int c = in.read(); c != -1; c = in.read())
		{
			for (int b = 0; b < 8; ++b)
			{
				if ((c & 1) != 0)
				{
					System.out.print("o");
				}
				else
				{
					System.out.print(" ");
				}
				c >>>= 1;
			}
			System.out.println();
		}
		in.close();
	}
}
