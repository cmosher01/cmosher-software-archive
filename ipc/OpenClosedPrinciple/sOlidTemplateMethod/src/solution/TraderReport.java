package solution;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import support.Trader;

public class TraderReport extends Report
{
	private List<Trader> traders = new ArrayList<Trader>();

	@Override
	public void printHeader(PrintStream out)
	{
		out.println("Trader Report");
		out.println("Name,TRID");
	}

	@Override
	public void printBody(PrintStream out)
	{
		for (Trader trader : this.traders)
		{
			out.print(trader.getName());
			out.print(",");
			out.println(trader.getTRID());
		}
	}

	@Override
	public void printFooter(PrintStream out)
	{
		out.println("End of Trader Report");
	}
}
