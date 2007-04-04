package nu.mine.mosher.template;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import net.sourceforge.templat.Templat;
import net.sourceforge.templat.exception.TemplateLexingException;
import net.sourceforge.templat.exception.TemplateParsingException;

/**
 * Demonstrates how to use the <code>Template</code> class.
 *
 * @author Chris Mosher
 */
public class TemplateApp
{
	public static void main(String[] args) throws IOException, TemplateLexingException, TemplateParsingException
	{
		new TemplateApp().run();
	}

	public static int getYearWritten()
	{
		return 2005;
	}

	public static String getAuthor()
	{
		return "Chris Mosher";
	}

	public static class Favorite
	{
		private final String name;
		private final String status;
		public Favorite(final String name, final String status) { this.name = name; this.status = status; }
		public String getName() { return this.name; }
		public String getStatus() { return this.status; }
	}

	public static class ConfigTest
	{
		ConfigTest() {}
		public boolean showOther() { return true; }
		public int[] getArray() { return new int[] { 3,5,7,9 }; }
		public String getColor() { return "red"; }
	}

	public static class Copyright
	{
		private final String who;
		private final int year;
		Copyright(String who, int year) { this.who = who; this.year = year; }
		public String getWhoWrote() { return this.who; }
		public int getYearBegun() { return this.year; }
	}

	public void run() throws IOException, TemplateLexingException, TemplateParsingException
	{
		final Templat templateMinimal = new Templat(new File("test/minimal.tat").toURL());
		showTemplate(templateMinimal);

		final Templat templateSimple = new Templat(new File("test/simple.tat").toURL());
		showTemplate(templateSimple,"Richard Roe","defendant");

		final Templat templateCopyright = new Templat(new File("test/copyright.tat").toURL());
		showTemplate(templateCopyright,2005,"Chris Mosher",new ConfigTest());

		final Templat templateTestInclude = new Templat(new File("test/testinc.tat").toURL());
		showTemplate(templateTestInclude,2005,"Chris Mosher",new ConfigTest());

		final Templat templateTestLoop = new Templat(new File("test/testloop.tat").toURL());
		final Collection<Copyright> cr = new ArrayList<Copyright>();
		cr.add(new Copyright("Chris Mosher",2005));
		cr.add(new Copyright("Jesse Sands",1790));
		showTemplate(templateTestLoop,"John Q. Public",cr,new ConfigTest());



		final Templat templateHomepage = new Templat(new File("test/homepage.tat").toURL());

		final Collection<Favorite> rFav = new ArrayList<Favorite>();
		rFav.add(new Favorite("Google","used"));
		rFav.add(new Favorite("Java","unused"));
		showTemplate(templateHomepage,"Chris Mosher","good",rFav,7,false);
	}

	private void showTemplate(final Templat template, final Object... args) throws TemplateLexingException, IOException, TemplateParsingException
	{
		final StringBuilder sbResult = new StringBuilder();
		boolean ok = false;
		try
		{
			template.render(sbResult,args);
			ok = true;
		}
		finally
		{
			if (ok)
			{
				System.out.println(sbResult);
			}
			else
			{
				System.err.println(template.toString());
			}
			System.out.println("------------------------------------------------------------------");
		}
	}
}
