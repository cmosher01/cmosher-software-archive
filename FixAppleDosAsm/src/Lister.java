import java.io.PrintWriter;

/**
 * Created on Nov 15, 2003
 * @author Chris Mosher
 */
public class Lister
{
	private final PrintWriter pw;
	private int col;

    public Lister(PrintWriter out)
    {
    	pw = out;
    }

	public void print(String s)
	{
		col += s.length();
		pw.print(s);
	}

	public void newline()
	{
		pw.println();
		pw.flush();
		col = 0;
	}

	public void tab(int n)
	{
		if (n <= col)
		{
			newline();
		}
		for (int i = col; i < n; ++i)
		{
			print(" ");
		}
	}

	public int getTab()
	{
		return col;
	}

    public void close()
    {
        pw.close();
    }
}
