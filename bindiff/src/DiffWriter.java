public class DiffWriter
{
	StringBuffer s = new StringBuffer(1024);

	public DiffWriter()
	{
	}

	public void outTag(String type, int c)
	{
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

		System.out.println(s.toString());
		System.out.flush();
		s = new StringBuffer(1024);
	}
}
