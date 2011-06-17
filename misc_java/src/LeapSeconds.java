import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import sun.util.calendar.Gregorian;

/*
 * Created on Nov 18, 2006
 */
public class LeapSeconds
{
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException
	{
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		long ms = fmt.parse("1978-12-31 23:59:59.0000").getTime();
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(ms);
		for (int i = 0; i < 13; ++i)
		{
			System.out.println(fmt.format(cal.getTime()));
			cal.add(Calendar.MILLISECOND,100);
		}
	}
}
