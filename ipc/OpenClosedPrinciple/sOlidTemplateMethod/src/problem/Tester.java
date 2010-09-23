package problem;
import support.Trader;


public class Tester
{
	public static void main(String[] args)
	{
		TraderReport rpt = new TraderReport();
		rpt.add(new Trader());

		printReportToStandardOutput(rpt);
	}

	private static void printReportToStandardOutput(Report rpt)
	{
		rpt.printReport(System.out);
	}
}
