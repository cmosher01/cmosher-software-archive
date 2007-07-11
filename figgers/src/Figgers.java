import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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

		final Set<Date> holidays = new HashSet<Date>();
		holidays.add(fmt.parse("1927-07-04"));
		holidays.add(fmt.parse("1928-01-02"));

		final List<Group> groups = new ArrayList<Group>();
		getGroups(groups);

		final Map<Date,String> titles = new HashMap<Date,String>();
		getTitles(titles);

		for (int igroup = 0; igroup < groups.size(); ++igroup)
		{
			Date date = groups.get(igroup).date;
			final String title = groups.get(igroup).title;
			if (title.isEmpty())
			{
				continue;
			}
			final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("C:/genealogy/vep/figgers_family/cropped/"+fmt.format(date)+".html"))));
			final Date limdate = groups.get(igroup+1).date;
			final List<Dat> dates = new ArrayList<Dat>(520);
			int size = 0;
			while (date.before(limdate))
			{
				cal.setTime(date);
				final int weekday = cal.get(Calendar.DAY_OF_WEEK);
				if (weekday != Calendar.SUNDAY && !holidays.contains(date))
				{
					++size;
					dates.add(new Dat(date,titles.get(date)));
				}
				cal.add(Calendar.DATE,1);
				date = cal.getTime();
			}
			groups.get(igroup).setSize(size);
			final Templat tmp = new Templat(Figgers.class.getResource("figgers.tat"));
			final StringBuilder s = new StringBuilder();
			tmp.render(s,title,dates);
			out.write(s.toString());
			out.flush();
			out.close();
		}

		final Templat tmp = new Templat(Figgers.class.getResource("index.tat"));
		final StringBuilder s = new StringBuilder();
		tmp.render(s,groups);
		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("C:/genealogy/vep/figgers_family/cropped/index.html"))));
		out.write(s.toString());
		out.flush();
		out.close();
	}

	private static void getTitles(final Map<Date,String> titles) throws ParseException, IOException
	{
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/genealogy/vep/figgers_family/figgers.txt"))));
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			final StringTokenizer tok = new StringTokenizer(line,"\t");
			final String sdate = tok.nextToken();
			String title;
			if (tok.hasMoreTokens())
			{
				title = tok.nextToken();
			}
			else
			{
				title = "";
			}
			final Date date = fmt.parse(sdate);
			titles.put(date,title);
		}
		in.close();
	}

	public static int next(final int i)
	{
		return i+1;
	}

	public static class Group
	{
		public final Date date;
		public final String title;
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		public int size;
		public Group(final Date date, final String title)
		{
			this.date = new Date(date.getTime());
			this.title = title;
		}
		public void setSize(int size) { this.size = size; }
		public String getTitle() { return this.title; }
		public String getLink() { return getDate()+".html"; }
		public String getDate() { return this.fmt.format(this.date); }
		public String getSize() { return Integer.toString(this.size); }
	}

	private static void getGroups(final Collection<Group> groups) throws IOException, ParseException
	{
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/genealogy/vep/figgers_family/groups.txt"))));
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			final StringTokenizer tok = new StringTokenizer(line,"\t");
			final String sdate = tok.nextToken();
			String title;
			if (tok.hasMoreTokens())
			{
				title = tok.nextToken();
			}
			else
			{
				title = "";
			}
			final Date date = fmt.parse(sdate);
			groups.add(new Group(date,title));
		}
		in.close();
	}

	public static class Dat
	{
		private final Date date;
		private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		private final String title;
		public Dat(Date date, String title) { this.date = new Date(date.getTime()); this.title = title; }
		public String toString() { return this.fmt.format(this.date); }
		public String getTitle() { return this.title; }
	};
}
