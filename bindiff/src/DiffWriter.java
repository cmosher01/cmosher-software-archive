public class DiffWriter
{
	private StringBuffer s = new StringBuffer(1024);
	private int pos;

	public DiffWriter()
	{
	}

	public void outTag(String type, int c)
	{
		if (pos > 0)
		{
			s.append("\n");
		}
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
		pos = 0;
	}

	public void outByte(int b)
	{
//		if (pos > 93)
//		{
//			s.append("\n");
//			pos = 0;
//		}
//		s.append(hexByte(b));
//		s.append(" ");
//		pos += 3;
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
