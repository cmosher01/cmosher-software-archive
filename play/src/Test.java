import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Test
{
    private static final String ISO8601_RFC3339_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final SimpleDateFormat fmtDateTime = new SimpleDateFormat(ISO8601_RFC3339_DATE_TIME_FORMAT,Locale.KOREAN);

    public static void main(final String... args) throws ParseException
	{
		System.out.println(fmtDateTime.format(new Date()));
		Date date = fmtDateTime.parse("2005-10-21T18:15:12.017-0400");
		System.out.println(fmtDateTime.format(date));
	}
}
