/*
 * Created on Jul 13, 2006
 */
package nu.mine.mosher.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import net.sourceforge.templat.Templat;
import net.sourceforge.templat.exception.TemplateLexingException;
import net.sourceforge.templat.exception.TemplateParsingException;

public class VepWeb
{
	public  static class Image
	{
		private final String name;
		private final String date;
		private final String title;
		public Image(final String name, final String date, final String title)
		{
			this.name = name;
			this.date = date;
			this.title = title;
		}
		public String getDate()
		{
			return this.date;
		}
		public String getName()
		{
			return this.name;
		}
		public String getTitle()
		{
			return this.title;
		}
	}

	public static void main(String[] args) throws IOException, TemplateLexingException, TemplateParsingException
	{
		final Templat tat = new Templat(new File("vep.tat").toURL());

		final Collection<Image> rImage = new ArrayList<Image>();
		readImages(rImage);

		final StringBuilder s = new StringBuilder(4096);
		tat.render(s,rImage);

		System.out.println(s);
	}

	private static void readImages(final Collection<Image> rImage) throws IOException
	{
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/Documents and Settings/Chris/My Documents/My Pictures/vep/figgers/0000work.txt"))));
		for (String sLine = in.readLine(); sLine != null; sLine = in.readLine())
		{
			if (sLine.length() == 0)
			{
				continue;
			}

			final String name = sLine.substring(0,4);
			if (!isNumber(name))
			{
				continue;
			}

			final String date = sLine.substring(5,15);
			final String title = titleCase(sLine.substring(16));

			rImage.add(new Image(name,date,title));
		}
		in.close();
	}

	// doesn't handle punctuation
	private static String titleCase(final String title)
	{
		final StringBuilder ret = new StringBuilder(256);
		final StringTokenizer tok = new StringTokenizer(title);
		boolean first = true;
		while (tok.hasMoreTokens())
		{
			final String word = tok.nextToken();
			final String lower = word.toLowerCase();
			if ((lower.equals("the") || lower.equals("and") || lower.equals("a") || lower.equals("of")) && !first)
			{
				ret.append(lower);
			}
			else
			{
				ret.append(word.substring(0,1).toUpperCase());
				ret.append(lower.substring(1));
			}
			ret.append(" ");
			first = false;
		}
		return ret.toString();
	}

	private static boolean isNumber(final String name)
	{
		try
		{
			return Integer.parseInt(name) > 0;
		}
		catch (final Throwable e)
		{
			return false;
		}
	}
}
