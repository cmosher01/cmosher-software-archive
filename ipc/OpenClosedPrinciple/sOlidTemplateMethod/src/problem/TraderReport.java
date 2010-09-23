package problem;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import support.Trader;


public class TraderReport extends Report
{
	private List<Trader> traders = new ArrayList<Trader>();

	public void add(Trader trader)
	{
		this.traders.add(trader);
	}

	@Override
	public void printReport(PrintStream out)
	{
		// header
		out.println("Trader Report");
		out.println("Name,TRID");

		// body
		for (Trader trader : this.traders)
		{
			out.print(trader.getName());
			out.print(",");
			out.println(trader.getTRID());
		}

		// footer
		out.println("End of Trader Report");
	}
}
