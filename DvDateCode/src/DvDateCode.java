import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.List;
import java.util.StringTokenizer;

/*
 * Created on Jul 31, 2004
 */

/**
 * @author Chris Mosher
 */
public class DvDateCode
{
	public static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	public static Calendar cal = Calendar.getInstance();
	public static void main(String[] args) throws IOException, ParseException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[1]))));
		int amount = 0;
		int secs = 0;
		for (String s = in.readLine(); s != null; s = in.readLine())
		{
			if (s.length() == 0)
			{
				continue;
			}
			if (s.charAt(0) == '{')
			{
				int i = s.indexOf('}');
				if (i == -1)
				{
					throw new IllegalStateException("couldn't find }");
				}
				String sStart = s.substring(1,i);
				int start = Integer.parseInt(sStart);
				s = s.substring(i+2);

				i = s.indexOf('}');
				if (i == -1)
				{
					throw new IllegalStateException("couldn't find }");
				}
				String sEnd = s.substring(0,i);
				int end = Integer.parseInt(sEnd);
				s = s.substring(i+1);
				Date d = format.parse(s);
				if (secs != 0)
				{
					cal.setTime(d);
					cal.add(Calendar.SECOND,secs);
					d = cal.getTime();
				}

				start += amount;
				end += amount;
				out.write("{");
				out.write(""+start);
				out.write("}{");
				out.write(""+(end-1));
				out.write("}");
				out.write(format.format(d));
				out.newLine();
			}
			else if (s.charAt(0) == 'x')
			{
				int d = Integer.parseInt(s.substring(1));
				amount += d;
			}
			else if (s.charAt(0) == '+')
			{
				int d = Integer.parseInt(s.substring(1));
				secs += d;
			}
			else if (s.charAt(0) == '-')
			{
				int d = Integer.parseInt(s.substring(1));
				secs -= d;
			}
		}
		out.flush();
		out.close();
		in.close();
	}
	public static class Fixup
	{
		public int frame;
		public int amount;
	}
	public static void readFix(String sf, Collection r) throws IOException
	{
		BufferedReader fix = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sf))));
		for (String s = fix.readLine(); s != null; s = fix.readLine())
		{
			StringTokenizer tok = new StringTokenizer(s);
			Fixup fixup = new Fixup();
			String sframe = tok.nextToken();
			fixup.frame = Integer.parseInt(sframe);
			String samount = tok.nextToken();
			fixup.amount = Integer.parseInt(samount);
			r.add(fixup);
		}
		fix.close();
	}
}
