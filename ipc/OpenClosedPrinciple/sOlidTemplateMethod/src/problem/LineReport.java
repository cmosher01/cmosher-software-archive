package problem;
import java.io.PrintStream;


public class LineReport extends Report
{
	@Override
	public void printReport(PrintStream out)
	{
		// header
		out.println("Line Report");
		out.println("LAC");

		// body
		out.println("1024");
		out.println("1025");

		// footer
		out.println("End of Line Report");
	}
}
