import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.sourceforge.templat.Templat;
import net.sourceforge.templat.exception.TemplateLexingException;
import net.sourceforge.templat.exception.TemplateParsingException;

/*
 * Created on Jul 2, 2007
 */
public class Figgers
{
	/**
	 * @param args
	 */
	public static void main(String[] args) throws ParseException, TemplateLexingException, TemplateParsingException, IOException
	{
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar cal = Calendar.getInstance();

		Date date = fmt.parse("1927-01-01");
		final List<Dat> dates = new ArrayList<Dat>(520);
		for (int i = 0; i < 520; ++i)
		{
			cal.setTime(date);
			final int weekday = cal.get(Calendar.DAY_OF_WEEK);
			if (weekday != Calendar.SUNDAY)
			{
				dates.add(new Dat(date));
			}
			cal.add(Calendar.DATE,1);
			date = cal.getTime();
		}

		final Templat tmp = new Templat(Figgers.class.getResource("figgers.tat"));
		final StringBuilder s = new StringBuilder();
		tmp.render(s,dates);
		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("c:/temp/figgers.html"))));
		out.write(s.toString());
		out.flush();
		out.close();
	}

	public static class Dat
	{
		private final Date date;
		private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		public Dat(Date date) { this.date = date; }
		public String toString() { return fmt.format(this.date); }
	};
}
