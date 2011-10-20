package nu.mine.mosher.gedcom.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.templat.Templat;
import net.sourceforge.templat.exception.TemplateLexingException;
import net.sourceforge.templat.exception.TemplateParsingException;
import nu.mine.mosher.gedcom.Gedcom;
import nu.mine.mosher.gedcom.GedcomTree;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.gedcom.servlet.struct.GedcomFile;
import nu.mine.mosher.gedcom.servlet.struct.Person;
import nu.mine.mosher.gedcom.servlet.struct.Source;

/**
 * @author Chris Mosher
 * Created 2006-09-24.
 */
public class GedcomServlet extends HttpServlet
{
	private final Map<String,Loader> mapLoader = new TreeMap<String,Loader>();

	@Override
	public void init(final ServletConfig servletConfig) throws ServletException
	{
		try
		{
			tryInit(servletConfig);
		}
		catch (final ServletException e)
		{
			throw e;
		}
		catch (final Throwable e)
		{
			throw new ServletException(e);
		}
	}

	private void tryInit(final ServletConfig servletConfig) throws ServletException, IOException, InvalidLevel
	{
		super.init(servletConfig);

		final File[] rFileGedcom = getGedcomFiles(servletConfig);

		for (final File fileGedcom : rFileGedcom)
		{
			final GedcomTree gt = Gedcom.parseFile(fileGedcom);

			final Loader loader = new Loader(gt);
			loader.parse();
			this.mapLoader.put(fileGedcom.getName(),loader);
		}
	}

	private File[] getGedcomFiles(final ServletConfig servletConfig) throws IOException, ServletException
	{
		final File dirGedcom = new File(getGedcomDir(servletConfig)).getCanonicalFile();
		final File[] rFile = dirGedcom.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(final File file)
			{
				return
					file.isFile() &&
					file.canRead() &&
					(file.getName().endsWith(".ged") || file.getName().endsWith(".GED"));
			}
		});
		if (rFile == null || rFile.length == 0)
		{
			throw new ServletException("Cannot find any readable files in "+dirGedcom);
		}
		return rFile;
	}

	private String getGedcomDir(final ServletConfig servletConfig)
	{
		String dir = servletConfig.getServletContext().getInitParameter("gedcom.directory");
		if (dir == null || dir.length() == 0)
		{
			dir = "gedcom";
		}
		return dir;
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException
	{
		try
		{
			tryGet(request,response);
		}
		catch (final Throwable e)
		{
			throw new ServletException(e);
		}
	}

	private void tryGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, TemplateLexingException, TemplateParsingException
	{
		final String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.length() <= 1)
		{
			response.sendRedirect(request.getContextPath()+"/index.html");
			return;
		}

		if (pathInfo.equals("/index.html"))
		{
			final Writer out = openPage(response);
			showFirstPage(out);
			closePage(out);
			return;
		}

		final String paramPersonUUID = request.getParameter("person_uuid");
		if (paramPersonUUID != null && paramPersonUUID.length() > 0)
		{
			final Person person = findPersonByUuid(pathInfo.substring(1),paramPersonUUID);
			if (person == null)
			{
				showErrorPage(response);
				return;
			}
			final Writer out = openPage(response);
			showPersonPage(person,out);
			closePage(out);
			return;
		}

		final String paramSourceUUID = request.getParameter("source_uuid");
		if (paramSourceUUID != null && paramSourceUUID.length() > 0)
		{
			final Source source = findSourceByUuid(pathInfo.substring(1),paramSourceUUID);
			if (source == null)
			{
				showErrorPage(response);
				return;
			}
			final Writer out = openPage(response);
			showSourcePage(source,out);
			closePage(out);
			return;
		}

		/* oops, bad requested URL; go back to the home page */
		response.sendRedirect(request.getContextPath()+"/index.html");
	}

	private void showSourcePage(final Source source, final Writer out) throws TemplateLexingException, TemplateParsingException, IOException
	{
		final StringBuilder sb = new StringBuilder(256);
		final Templat tat = new Templat(GedcomServlet.class.getResource("template/source.tat"));
		tat.render(sb,source);
		out.write(sb.toString());
	}

	private Writer openPage(final HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		return new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"UTF-8"));
	}

	private void closePage(final Writer out) throws IOException
	{
		out.flush();
		out.close();
	}

	private void showFirstPage(final Writer out) throws TemplateLexingException, TemplateParsingException, IOException
	{
		final StringBuilder sb = new StringBuilder(256);
		final Templat tat = new Templat(GedcomServlet.class.getResource("template/index.tat"));
		final List<GedcomFile> rFile = new ArrayList<GedcomFile>(this.mapLoader.size());
		buildGedcomFilesList(rFile);
		tat.render(sb,rFile);
		out.write(sb.toString());
	}

	private Person findPersonByUuid(final String pathInfo, final String uuid)
	{
		if (uuid == null || uuid.length() == 0)
		{
			return null;
		}
		if (pathInfo == null || pathInfo.length() == 0)
		{
			return null;
		}
		final Loader loader = this.mapLoader.get(pathInfo);
		if (loader == null)
		{
			return null;
		}
		return loader.lookUpPerson(UUID.fromString(uuid));
	}

	private Source findSourceByUuid(final String pathInfo, final String uuid)
	{
		if (uuid == null || uuid.length() == 0)
		{
			return null;
		}
		if (pathInfo == null || pathInfo.length() == 0)
		{
			return null;
		}
		final Loader loader = this.mapLoader.get(pathInfo);
		if (loader == null)
		{
			return null;
		}
		return loader.lookUpSource(UUID.fromString(uuid));
	}

	private static void showPersonPage(final Person person, final Writer out) throws TemplateLexingException, TemplateParsingException, IOException
	{
		final StringBuilder sb = new StringBuilder(256);
		final Templat tat = new Templat(GedcomServlet.class.getResource("template/person.tat"));
		tat.render(sb,person);
		out.write(sb.toString());
	}

	private static void showErrorPage(HttpServletResponse response) throws IOException
	{
		response.sendError(404,"Sorry, I cannot find that web page. Please press the back button and then try something else.");
	}

	private void buildGedcomFilesList(final Collection<GedcomFile> rFile)
	{
		for (final Map.Entry<String,Loader> entry : this.mapLoader.entrySet())
		{
			final Person first = entry.getValue().getFirstPerson();
			final String descrip = entry.getValue().getDescription();
			final GedcomFile file = new GedcomFile(entry.getKey(),first,HtmlUtil.escapeHtml(descrip==null ? "" : descrip));
			rFile.add(file);
		}
	}
}
