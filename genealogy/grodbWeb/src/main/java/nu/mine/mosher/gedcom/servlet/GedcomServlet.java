package nu.mine.mosher.gedcom.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private final Map<UUID,Set<Loader>> mapPersonCrossRef = new HashMap<UUID,Set<Loader>>(32);



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

		final List<File> rFileGedcom = new ArrayList<File>(16);
		getGedcomFiles(servletConfig,rFileGedcom);

		final Map<UUID,Loader> mapMasterUuidToLoader = new HashMap<UUID,Loader>(1024);
		for (final File fileGedcom : rFileGedcom)
		{
			final GedcomTree gt = Gedcom.parseFile(fileGedcom);

			final Loader loader = new Loader(gt,fileGedcom.getName());
			loader.parse();
			this.mapLoader.put(loader.getName(),loader);

			buildPersonCrossReferences(loader,mapMasterUuidToLoader);
		}
	}

	private void buildPersonCrossReferences(final Loader loader, final Map<UUID,Loader> mapMasterUuidToLoader)
	{
		final Set<UUID> uuids = new HashSet<UUID>(256);
		loader.appendAllUuids(uuids);
		for (final UUID uuid : uuids)
		{
			if (mapMasterUuidToLoader.containsKey(uuid))
			{
				Set<Loader> loaders = this.mapPersonCrossRef.get(uuid);
				if (loaders == null)
				{
					loaders = new HashSet<Loader>(2);
					this.mapPersonCrossRef.put(uuid, loaders);

					loaders.add(mapMasterUuidToLoader.get(uuid));
				}
				loaders.add(loader);
			}
			else
			{
				mapMasterUuidToLoader.put(uuid,loader);
			}
		}
	}

	private static void getGedcomFiles(final ServletConfig servletConfig, final Collection<File> rFileGedcom) throws IOException, ServletException
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

		rFileGedcom.addAll(Arrays.<File>asList(rFile));
	}

	private static String getGedcomDir(final ServletConfig servletConfig)
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
		/*
		 * If URL after context path was empty or just "/"
		 * redirect to /index.html page.
		 */
		final String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.length() <= 1)
		{
			response.sendRedirect(request.getContextPath()+"/index.html");
			return;
		}

		handleValidRequest(request, response);
	}

	private void handleValidRequest(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, TemplateLexingException, TemplateParsingException {
		final String pathInfo = request.getPathInfo();
		if (pathInfo.equals("/index.html"))
		{
			final List<GedcomFile> rFile = new ArrayList<GedcomFile>(this.mapLoader.size());
			buildGedcomFilesList(rFile);

			final Writer out = openPage(response);
			showFirstPage(rFile,out);
			closePage(out);
			return;
		}

		String paramPersonUUID = request.getParameter("person_uuid");
		if (paramPersonUUID != null && paramPersonUUID.length() > 0)
		{
			final Person person = findPersonByUuid(pathInfo.substring(1),paramPersonUUID);
			if (person == null)
			{
				showErrorPage(response);
				return;
			}
			final UUID uuid = UUID.fromString(paramPersonUUID);
			final Loader loader = this.mapLoader.get(pathInfo.substring(1));
			final Writer out = openPage(response);
			final List<String> otherFiles = new ArrayList<String>();
			if (this.mapPersonCrossRef.containsKey(uuid))
			{
				for (final Loader gedcom : this.mapPersonCrossRef.get(uuid))
				{
					if (gedcom != loader)
					{
						otherFiles.add(gedcom.getName());
					}
				}
			}
			showPersonPage(person,false,out,otherFiles);
			closePage(out);
			return;
		}

		paramPersonUUID = request.getParameter("personfam_uuid");
		if (paramPersonUUID != null && paramPersonUUID.length() > 0)
		{
			final Person person = findPersonByUuid(pathInfo.substring(1),paramPersonUUID);
			if (person == null)
			{
				showErrorPage(response);
				return;
			}
			final UUID uuid = UUID.fromString(paramPersonUUID);
			final Loader loader = this.mapLoader.get(pathInfo.substring(1));
			final Writer out = openPage(response);
			final List<String> otherFiles = new ArrayList<String>();
			if (this.mapPersonCrossRef.containsKey(uuid))
			{
				for (final Loader gedcom : this.mapPersonCrossRef.get(uuid))
				{
					if (gedcom != loader)
					{
						otherFiles.add(gedcom.getName());
					}
				}
			}
			showPersonPage(person,true,out,otherFiles);
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

	private void buildGedcomFilesList(final List<GedcomFile> rFile)
	{
		for (final Map.Entry<String,Loader> entry : this.mapLoader.entrySet())
		{
			final Person first = entry.getValue().getFirstPerson();
			final String descrip = entry.getValue().getDescription();
			final GedcomFile file = new GedcomFile(entry.getKey(),first,HtmlUtil.escapeHtml(descrip==null ? "" : descrip));
			rFile.add(file);
		}

		final Collator collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);
		collator.setDecomposition(Collator.FULL_DECOMPOSITION);

		Collections.<GedcomFile>sort(rFile, new Comparator<GedcomFile>()
		{
			@Override
			public int compare(final GedcomFile f1, final GedcomFile f2)
			{
				return collator.compare(f1.getFile(), f2.getFile());
			}
		});
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



	private static Writer openPage(final HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		return new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"UTF-8"));
	}

	private static void closePage(final Writer out) throws IOException
	{
		out.flush();
		out.close();
	}



	private static void showFirstPage(final List<GedcomFile> rFile, final Writer out) throws TemplateLexingException, TemplateParsingException, IOException
	{
		final StringBuilder sb = new StringBuilder(256);
		final Templat tat = new Templat(GedcomServlet.class.getResource("template/index.tat"));
		tat.render(sb,rFile);
		out.write(sb.toString());
	}

	private static void showPersonPage(final Person person, final boolean isFamilyEvents, final Writer out, final List<String> otherFiles) throws TemplateLexingException, TemplateParsingException, IOException
	{
		final StringBuilder sb = new StringBuilder(256);
		final Templat tat = new Templat(GedcomServlet.class.getResource("template/person.tat"));
		tat.render(sb,person,Boolean.valueOf(isFamilyEvents),otherFiles);
		out.write(sb.toString());
	}

	private static void showSourcePage(final Source source, final Writer out) throws TemplateLexingException, TemplateParsingException, IOException
	{
		final StringBuilder sb = new StringBuilder(256);
		final Templat tat = new Templat(GedcomServlet.class.getResource("template/source.tat"));
		tat.render(sb,source);
		out.write(sb.toString());
	}

	private static void showErrorPage(HttpServletResponse response) throws IOException
	{
		response.sendError(404,"Sorry, I cannot find that web page. Please press the back button and then try something else.");
	}
}
