package nu.mine.mosher.grodb;

import java.util.Calendar;
import java.util.TimeZone;

public class DateRange
{
	YMD earliest;
	YMD latest;
	int hour;
	int minute;
	int hourOffGMT;
	int minuteOffGMT;
	boolean circa;
	TimeZone tz;

	void DateRange()
	{
		Calendar c;
	}
}
