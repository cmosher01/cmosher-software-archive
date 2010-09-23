package solution;

import java.util.Calendar;

public class ReportFactoryImpl implements ReportFactory
{
	@Override
	public Report createDailyReport()
	{
		Calendar cal = Calendar.getInstance();
		int weekday = cal.get(Calendar.DAY_OF_WEEK);
		if (weekday == Calendar.FRIDAY)
		{
			return new FullReport();
		}
		else if (weekday == Calendar.SATURDAY || weekday == Calendar.SUNDAY)
		{
			return new WeekendReport();
		}
		else
		{
			return new ShortReport();
		}
	}
}
