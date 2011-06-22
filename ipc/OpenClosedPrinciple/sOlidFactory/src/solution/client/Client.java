package solution.client;
import java.awt.Font;
import java.util.Date;

import solution.client.spi.Report;
import solution.client.spi.ReportFactory;


public class Client
{
	public void printDailyReport(ReportFactory factory)
	{
		final Report report = factory.createDailyReport();
		report.setFont(Font.SERIF);
		report.setMargins(1.5);
		report.setDate(new Date());
		report.print(System.out);
	}
}
