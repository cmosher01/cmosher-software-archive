public class DiffWriter
{
	private StringBuffer s = new StringBuffer(1024);

	public DiffWriter()
	{
	}

	public void outTag(String type, int c)
	{
		s.append(">>>");
		s.append(type);
		s.append(" ");
		s.append(c);
		if (c == 1)
		{
			s.append(" BYTE\n");
		}
		else
		{
			s.append(" BYTES\n");
		}
	}

	private int[] line = new int[32];
	private int i;
	public void outByte(int b)
	{
		line[i++] = b;

		s.append(hexByte(b));
		s.append(" ");

		if (i >= line.length)
		{
			i = 0;
			outAscii();
		}
	}

	public void flush()
	{
		if (s.length() == 0)
		{
			return;
		}

		System.out.print(s.toString());
		System.out.flush();
		s = new StringBuffer(1024);
	}

	protected String hexByte(int b)
	{
		String s = Integer.toHexString(b);
		s = s.toUpperCase();
		if (s.length() == 1)
		{
			s = "0"+s;
		}
		return s;
	}
}
