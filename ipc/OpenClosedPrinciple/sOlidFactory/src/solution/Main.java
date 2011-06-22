package solution;

import solution.client.Client;
import solution.reports.ReportFactoryImpl;

public class Main
{
	public static void main(String... args)
	{
		Client cl = new Client();
		cl.printDailyReport(new ReportFactoryImpl());
	}
}
