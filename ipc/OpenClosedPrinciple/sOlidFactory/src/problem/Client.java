package problem;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;


public class Client
{
	void printDailyReport()
	{
		Calendar cal = Calendar.getInstance();
		int weekday = cal.get(Calendar.DAY_OF_WEEK);
		Report report;
		if (weekday == Calendar.FRIDAY)
		{
			report = new FullReport();
		}
		else if (weekday == Calendar.SATURDAY || weekday == Calendar.SUNDAY)
		{
			report = new WeekendReport();
		}
		else
		{
			report = new ShortReport();
		}

		report.setFont(Font.SERIF);
		report.setMargins(1.5);
		report.setDate(new Date());
		report.print(System.out);
	}
}
