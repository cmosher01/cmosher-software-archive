package solution;

import java.io.PrintStream;

public class LineReport extends Report
{
	@Override
	public void printHeader(PrintStream out)
	{
		out.println("Line Report");
		out.println("LAC");
	}

	@Override
	public void printBody(PrintStream out)
	{
		out.println("1024");
		out.println("1025");
	}

	@Override
	public void printFooter(PrintStream out)
	{
		out.println("End of Line Report");
	}
}
