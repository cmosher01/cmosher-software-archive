package solution;
import java.awt.Font;
import java.util.Date;


public class Client
{
	void printDailyReport(ReportFactory factory)
	{
		final Report report = factory.createDailyReport();
		report.setFont(Font.SERIF);
		report.setMargins(1.5);
		report.setDate(new Date());
		report.print(System.out);
	}
}
