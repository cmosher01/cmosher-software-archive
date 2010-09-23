package solution;

public class Main
{
	public static void main(String... args)
	{
		Client cl = new Client();
		cl.printDailyReport(new ReportFactoryImpl());
	}
}
