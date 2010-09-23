package solution;

import java.io.PrintStream;

/*
 * The individual reports (hook methods) are closed for modification yet open for extension:
 * by modifying the template method here in this class we can enhance the reporting functionality.
 */
public abstract class Report
{
	// "template" method
	public void printReport(PrintStream out)
	{
		printHeader(out);
		printBody(out);
		printFooter(out);
	}

	// "hook" methods
	protected abstract void printHeader(PrintStream out);
	protected abstract void printBody(PrintStream out);
	protected abstract void printFooter(PrintStream out);
}
