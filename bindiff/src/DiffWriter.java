public class DiffWriter
{
	StringBuffer s = new StringBuffer(1024);

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

	public void outByte(int b)
	{
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
}
