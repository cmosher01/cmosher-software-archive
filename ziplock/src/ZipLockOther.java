/* $Log: ZipLock.java,v $
 * Revision 1.10  2000/11/25  04:26:56  stuart
 * Look inside ZIP/JAR when listing classes in a package
 *
 * Revision 1.9  2000/06/06  19:05:42  stuart
 * resource name bug
 *
 * Revision 1.8  1999/08/24  02:06:19  stuart
 * Expand default exclude list
 *
 * Revision 1.7  1998/11/17  02:20:40  stuart
 * use imports instead of qualified names
 *
 * Revision 1.6  1998/11/11  19:29:01  stuart
 * rename to ZipLock
 * remove indents on file list
 *
 * Revision 1.5  1998/11/10  03:59:02  stuart
 * Not fully reading zip entries!
 *
 * Revision 1.4  1998/11/10  02:28:50  stuart
 * improve documentation
 * improve HandleTable
 *
 * Revision 1.3  1998/11/05  20:21:06  stuart
 * Support non-class resources
 *
 * Revision 1.2  1998/11/04  05:01:59  stuart
 * package list, performance improved, use pathsep and filesep properties
 *
 *
 * Enhanced by Stuart D. Gathman from an original progam named RollCall.
 * Copyright (c) 1998 Business Management Systems, Inc.
 *
 * Original Copyright (c) 1998 Karl Moss. All Rights Reserved.
 *
 * You may study, use, modify, and distribute this software for any
 * purpose provided that this copyright notice appears in all copies.
 *
 * This software is provided WITHOUT WARRANTY either expressed or
 * implied.
 *
 * @original_author  Karl Moss
 * @original_date    29Jan98
 *
 */
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
/** This is a utility class for examining a list of class names for
  all of their dependencies.
 
  <p>ZipLock will read each class file and search the internal
  structures for all references to outside classes and resources. The checks
  are recursive, so all classes will be examined. A list of
  dependencies will be returned.
 
  <p>None of the java.* classes will be examined.  Additional system
  packages may be excluded with <code>setExcludes()</code>.
 
  <p>In addition to classes, we look for other resources loaded via
  <code>Class.getResource()</code> or
  <code>Class.getResourceAsStream()</code>.  If a class calls these
  methods, then every String constant in the class is checked to see
  if a file by that name exists on the CLASSPATH in the same directory
  as the class - in other words where <code>getResource</code> would
  find it.  This heuristic only works if your resource names appear
  as String constants - which seems to be the case in my practice so far.
 
  <p>We can optionally write all the classes and resources
  found to a zip or jar, or the list of files can be retrieved
  with <code>getDependencies()</code>.
 
 @author Stuart D. Gathman
 Copyright (C) 1998 Business Management Systems, Inc.
 <p>Original version Copyright (c) 1998 Karl Moss. All Rights Reserved.
 */
public class ZipLock
{
	private static final String[] default_exclude = { };//"java/", "sun/", "javax/" };
	private String[] m_excludePackageList = default_exclude;
	private final Hashtable m_dep = new Hashtable();
	private int m_level = 0;
	private ZipOutputStream m_archiveStream;
	private static final String filesep = System.getProperty("file.separator");
	private static final String[] classPath = getClassPath();
	private final Vector rootSet = new Vector();
	private static final Hashtable ziptbl = new Hashtable(); // cache jars
	// reuse these data structures for getClassResources()
	private final IntList classInfo = new IntList(); // which names are classes
	private final IntList stringTbl = new IntList(); // which entries are Strings
	private final Vector methInfo = new Vector();
	private static final FilenameFilter classFilter = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			dir = dir;
			return name.endsWith(".class");
		}
	};
	private static final FilenameFilter dirFilter = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			name = name;
			return dir.isDirectory();
		}
	};

	private boolean mVerbose = false;

	/** Return the system class path as an array of strings. */
	public static String[] getClassPath()
	{
		String classPath = System.getProperty("java.class.path");
		return getClassPath(classPath);
	}
	/** Convert a class path to an array of strings. A class path is
		a list of path names separated by the character in the
		<code>path.separator</code> system property.
	 */
	public static String[] getClassPath(String path)
	{
		String pathsep = System.getProperty("path.separator");
		StringTokenizer tok = new StringTokenizer(path, pathsep);
		String[] s = new String[tok.countTokens()];
		for (int i = 0; tok.hasMoreTokens(); ++i)
			s[i] = tok.nextToken();
		return s;
	}
	/**
	  * <p>Sets the list of package names to exclude. If the package
	  * of the class starts with any of the names given, the class
	  * will be excluded. An example would be "foo.package".
	  *
	  * @param excludeList An array of package names to exclude.
	  */
	public void setExcludes(String e[])
	{
		if (e == null)
		{
			m_excludePackageList = default_exclude;
			return;
		}
		m_excludePackageList = new String[e.length + default_exclude.length];
		for (int i = 0; i < e.length; ++i)
		{
			if (!e[i].endsWith("/")) //CAM make sure it ends with a slash
				e[i] = e[i]+"/";
			m_excludePackageList[i] = e[i].replace('.', '/');
		}
		System.arraycopy(default_exclude, 0, m_excludePackageList, e.length, default_exclude.length);
	}
	/**
	  * <p>Start the roll call. After the class list and package exclude
	  * list have been set, this method will perform the class
	  * examination. Once complete, call getDependencies() to get a
	  * full list of class dependencies. If an archive file was
	  * specified, the archive file will contain all of the dependencies.
	  *
	  * @exception Exception An error occurred while processing
	  * @see #getDependencies
	  * @param m_archive the output archive or null
	  */
	public void start(String m_archive) throws Exception
	{
		// Attempt to create the archive if one was given
		if (m_archive != null)
		{
			System.err.println("Creating archive " + m_archive);
			File f = new File(m_archive);
			FileOutputStream fo = new FileOutputStream(f);
			// A new file was created. Create our zip output stream
			m_archiveStream = new ZipOutputStream(fo);
		}
		// Loop for each class given in the list
		m_dep.clear();
		if (mVerbose)//CAM
			dumpRootSet();
		String[] m_classList = getRootSet();
		for (int i = 0; i < m_classList.length; i++)
		{
			String name = m_classList[i];
			storeClass(name);
		}
		// Close the archive if necessary
		if (m_archiveStream != null)
			m_archiveStream.close();
		if (m_archive != null)
			System.err.println("\n" + m_archive + " created.");
	}
	/** Return the list of classes in the root set.
	 */
	public synchronized String[] getRootSet()
	{
		int n = rootSet.size();
		String[] s = new String[n];
		rootSet.copyInto(s);
		return s;
	}
	protected synchronized void dumpRootSet()//CAM
	{
		System.err.println("----------------------------------------------------");
		System.err.println("processing classes: ");
		for (Iterator i = rootSet.iterator(); i.hasNext();)
		{
			String s = (String) i.next();
			System.err.println(s);
		}
		System.err.println("----------------------------------------------------");
	}
	/** Clear the root set and the list of dependencies - leaving this
	    object ready for reuse. */
	public void clear()
	{
		m_dep.clear();
		rootSet.removeAllElements();
	}
	/** Return the list of class and other resources with their dependencies.  */
	public synchronized String[] getDependencies()
	{
		int n = m_dep.size();
		String[] s = new String[n];
		Enumeration k = m_dep.keys();
		for (int i = 0; i < n; ++i)
			s[i] = (String) k.nextElement();
		return s;
	}
	private void storeClass(String className) throws Exception
	{
		// First, convert the given class name into a resource file name
		String res = className.replace('.', '/') + ".class";
		// ignore if already processed
		if (m_dep.get(res) != null)
			return;
		// Read the class into a memory buffer
		byte buf[] = openResource(res);
		if (buf == null)
			throw new Exception("Class " + className + " not found");
		// Now process the class
		processClass(res, buf);
	}
	/**
	  * Given a class name, open it and return a buffer with
	  * the contents. The class is loaded from
	  * the current CLASSPATH setting
	  */
	protected static byte[] openResource(String name) throws Exception
	{
		byte buf[] = null;
		// Get the defined classpath
		// Walk through the classpath
		for (int i = 0; i < classPath.length; ++i)
		{
			String element = classPath[i];
			// We've got an element from the classpath. Look for
			// the resource here
			buf = openResource(name, element);
			// Got it! Exit the loop
			if (buf != null)
				break;
		}
		return buf;
	}
	/**
	  * Given a resource name and path, open the resource and
	  * return a buffer with the contents. Returns null if
	  * not found
	  */
	protected static byte[] openResource(String name, String path) throws Exception
	{
		byte buf[] = null;
		// If the path is a zip or jar file, look inside for the
		// resource
		String lPath = path.toLowerCase();
		if (lPath.endsWith(".zip") || lPath.endsWith(".jar"))
		{
			buf = openResourceFromJar(name, path);
		}
		else
		{
			// Not a zip or jar file. Look for the resource as
			// a file
			String fullName = path;
			// Put in the directory separator if necessary
			if (!path.endsWith("\\") && !path.endsWith("/"))
			{
				fullName += filesep;
			}
			fullName += name;
			File f = new File(fullName);
			// Check to make sure the file exists and it truely
			// is a file
			if (f.exists() && f.isFile())
			{
				// Create an input stream and read the file
				FileInputStream fi = new FileInputStream(f);
				long length = f.length();
				buf = new byte[(int) length];
				DataInputStream ds = new DataInputStream(fi);
				ds.readFully(buf);
				ds.close();
//System.err.println("opening: "+f.getCanonicalPath());
			}
		}
		return buf;
	}
	protected static ZipFile findJar(String jarFile) throws IOException
	{
		ZipFile zip = (ZipFile) ziptbl.get(jarFile);
		if (zip == null)
		{
			File f = new File(jarFile);
			// Make sure the file exists before opening it
			if (f.exists() && f.isFile())
			{
				// Open the zip file
				zip = new ZipFile(f);
				ziptbl.put(jarFile, zip);
			}
		}
		return zip;
	}
	/** Given a resource name and jar file name, open the jar file
	  * and return a buffer containing the contents. Returns null
	  * if the jar file could not be found or the resource could
	  * not be found
	  */
	protected static byte[] openResourceFromJar(String name, String jarFile) throws Exception
	{
		ZipFile zip = findJar(jarFile);
		if (zip == null)
			return null;
		// Is the entry in the zip file?
		ZipEntry entry = zip.getEntry(name);
		if (entry == null)
			return null;
//System.err.println("opening: "+new File(jarFile).getCanonicalPath());
		// If found, read the corresponding buffer for the entry
		InputStream in = zip.getInputStream(entry);
		DataInputStream ds = new DataInputStream(in);
		// Get the number of bytes available
		int len = (int) entry.getSize();
		// Read the contents of the class
		byte[] buf = new byte[len];
		ds.readFully(buf);
		ds.close();
		return buf;
	}
	/**
	  * Given a class or resource name and buffer containing the contents,
	  * process the raw bytes of the class file. 
	  */
	protected void processClass(String className, byte buf[]) throws Exception
	{
		log(className,true);//CAM
		// Save the fact that we have processed this class
		setClassProcessed(className);
		// If we are creating an archive, add this class to the
		// archive now
		if (m_archiveStream != null)
			addToArchive(className, buf);
		// If not a class resource, we are done
		if (!className.endsWith(".class"))
			return;
		String[] a = getClassResources(buf, className);
		for (int i = 0; i < a.length; ++i)
		{
			String s = a[i];
			// Make sure we don't process classes we've already seen
			// and those on our exclude list
			if (isClassProcessed(s))
				continue;
			// Process this resource
			buf = openResource(s);
			// Ignore if the class could not be found
			if (buf == null)
			{
//CAM
log(s,false);
//System.out.print("("+(m_level+1)+") ");
//System.out.println(s+" (not found)");
				continue;
			}
			// Keep track of how deep we are
			m_level++;
			// Process the resource
			processClass(s, buf);
			m_level--;
		}
	}
	static class Pair
	{
		final int a, b;
		Pair(int a, int b)
		{
			this.a = a;
			this.b = b;
		}
	}
	static class HandleTable
	{
		private Object[] tbl;
		public HandleTable(int n)
		{
			tbl = new Object[n];
		}
		public void put(int i, Object name)
		{
			tbl[i] = name;
		}
		public void put(int i, int val)
		{
			put(i, new Integer(val));
		}
		public void put(int i, int a, int b)
		{
			put(i, new Pair(a, b));
		}
		public String getString(int i)
		{
			if (i >= tbl.length)
				return null;
			return (String) tbl[i];
		}
		public int getInt(int i)
		{
			return ((Integer) tbl[i]).intValue();
		}
		public Pair getPair(int i)
		{
			return (Pair) tbl[i];
		}
	}
	/** Examine a class file to see what resources it uses, especially other
	   classes. */
	private String[] getClassResources(byte[] buf, String className) throws Exception
	{
		// Create a DataInputStream using the buffer. This will
		// make reading the buffer very easy
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		DataInputStream in = new DataInputStream(bais);
		// Read the magic number. It should be 0xCAFEBABE
		int magic = in.readInt();
		if (magic != 0xCAFEBABE)
			throw new Exception("Invalid magic number in " + className);
		// Validate the version numbers
		in.readShort(); // short minor
		short major = in.readShort();
		if ((major != 45) && (major != 46))
		{
//			throw new Exception("Invalid version number in " + className);
		}
		// Get the number of items in the constant pool
		short count = in.readShort();
		// Track which CP entries are classes and String contants
		classInfo.removeAll();
		stringTbl.removeAll();
		// Keep a list of method references
		methInfo.removeAllElements();
		// Initialize the constant pool handle table
		HandleTable cp = new HandleTable(count); // Constant Pool
		// Now walk through the constant pool looking for entries
		// we are interested in.  Others can be ignored, but we need
		// to understand the format so they can be skipped.
		readcp : for (int i = 1; i < count; i++)
		{
			// Read the tag
			byte tag = in.readByte();
			switch (tag)
			{
				case 7 : // CONSTANT_Class
					// Save the constant pool index for the class name
					short nameIndex = in.readShort();
					classInfo.add(nameIndex);
					cp.put(i, nameIndex);
					break;
				case 10 : // CONSTANT_Methodref
					short clazz = in.readShort(); // class
					short nt = in.readShort(); // name and type
					methInfo.addElement(new Pair(clazz, nt));
					break;
				case 9 : // CONSTANT_Fieldref
				case 11 : // CONSTANT_InterfaceMethodref
					// Skip past the structure
					in.skipBytes(4);
					break;
				case 8 : // CONSTANT_String
					// Skip past the string index
					short strIndex = in.readShort();
					stringTbl.add(strIndex);
					break;
				case 3 : // CONSTANT_Integer
				case 4 : // CONSTANT_Float
					// Skip past the data
					in.skipBytes(4);
					break;
				case 5 : // CONSTANT_Long
				case 6 : // CONSTANT_Double
					// Skip past the data
					in.skipBytes(8);
					// As dictated by the Java Virtual Machine specification,
					// CONSTANT_Long and CONSTANT_Double consume two
					// constant pool entries.
					i++;
					break;
				case 12 : // CONSTANT_NameAndType
					int name = in.readShort();
					int sig = in.readShort();
					cp.put(i, name, sig);
					break;
				case 1 : // CONSTANT_Utf8
					String s = in.readUTF();
					cp.put(i, s);
					break;
				default :
					System.err.println("WARNING: Unknown constant tag (" + tag + "@" + i + " of " + count + ") in " + className);
					break readcp;
			}
		}
		// We're done with the buffer and input streams
		in.close();
		Vector v = new Vector(); // collect resources used by this class
		// Walk through our vector of class name
		// index values and get the actual class names
		// Copy the actual class names so tables can get reused
		int[] ia = classInfo.elements();
		for (int i = 0; i < ia.length; i++)
		{
			int idx = ia[i];
			String s = cp.getString(idx);
			if (s == null)
				continue;
			// Look for arrays. Only process arrays of objects
			if (s.startsWith("["))
			{
				// Strip off all of the array indicators
				while (s.startsWith("["))
					s = s.substring(1);
				// Only use the array if it is an object. If it is,
				// the next character will be an 'L'
				if (!s.startsWith("L"))
					continue;
				// Strip off the leading 'L' and trailing ';'
				s = s.substring(1, s.length() - 1);
			}
			v.addElement(s + ".class");
		}
		// examine methods used for calls to getResource*()
		boolean resourceUsed = false;
		Pair[] p = new Pair[methInfo.size()];
		methInfo.copyInto(p);
		for (int i = 0; i < p.length; ++i)
		{
			try
			{
				String clazz = cp.getString(cp.getInt(p[i].a));
				if ("java/lang/Class".equals(clazz))
				{
					Pair nt = cp.getPair(p[i].b);
					String name = cp.getString(nt.a);
					if (name.startsWith("getResource"))
					{
						resourceUsed = true;
						System.err.println("getResource used in " + className);
						break;
					}
				}
			}
			catch (IndexOutOfBoundsException x)
			{
			}
		}
		if (resourceUsed)
		{
			/* string constants might be resource file names  Those that aren't
			will get ignored when the resulting path is not found. */
			int pos = className.lastIndexOf('/');
			String res = className.substring(0, pos + 1);
			ia = stringTbl.elements();
			for (int i = 0; i < ia.length; ++i)
			{
				//int idx = ia[i];
				String s = res + cp.getString(ia[i]);
				v.addElement(s);
			}
		}
		String[] a = new String[v.size()];
		v.copyInto(a);
		return a;
	}
	/** Determine if the given class is in our list or is part
	   of a system package such as java.* or a package specified
	   with <coded>setExcludes()</code>.
	 */
	public boolean isClassProcessed(String name)
	{
		// Exclude any packages in the exclude list
		for (int i = 0; i < m_excludePackageList.length; i++)
		{
			if (name.startsWith(m_excludePackageList[i]))
				return true;
		}
		// Search through the dependency list. If the class is
		// already there, skip it
		return m_dep.get(name) != null;
	}
	/** Add a class to the root set.  */
	public void addClass(String name)
	{
		rootSet.addElement(name);
	}
	/** Mark a class or other resource as processed. */
	private void setClassProcessed(String name)
	{
		// Save the class in our list
		m_dep.put(name, name);
	}
	/**
	 * Adds the given buffer to the archive with the given
	 * name
	 */
	private void addToArchive(String name, byte buf[]) throws Exception
	{
		// Create a zip entry
		ZipEntry entry = new ZipEntry(name);
		entry.setSize(buf.length);
		// Add the next entry
		m_archiveStream.putNextEntry(entry);
		// Write the contents out as well
		m_archiveStream.write(buf, 0, buf.length);
		m_archiveStream.closeEntry();
	}
	/**
	 * For display purposes, return a string to indent the proper
	 * number of spaces
	 */
	private void log(String name, boolean found)
	{
		System.out.print(name);

//		int i = m_level;
//		if (!found)
//			++i;
//		System.out.print(" ("+i+")");

		String s = "";
		if (!found)
			s = " (not found)";
		System.out.println(s);
	}
	/** Return a list of classes found in the current class path for
	   a given package.
	 */
	public static String[] packageList(String pkg)
	{
		Vector v = new Vector();
		String pkgpath = pkg.replace('.', '/');
		for (int i = 0; i < classPath.length; ++i)
		{
			String path = classPath[i];
			String lPath = path.toLowerCase();
			if (lPath.endsWith(".zip") || lPath.endsWith(".jar"))
			{
				try
				{
					ZipFile zip = findJar(path);
					if (zip == null)
						continue;
					Enumeration e = zip.entries();
					while (e.hasMoreElements())
					{
						ZipEntry ze = (ZipEntry) e.nextElement();
						String zpath = ze.getName();
						int plen = pkgpath.length();
						if (zpath.startsWith(pkgpath) && zpath.endsWith(".class"))
						{
							String fname = zpath.substring(plen, zpath.length() - 6);
							if (fname.lastIndexOf('/') != 0)
								continue;
							String c = pkg + '.' + fname.substring(1);
							v.addElement(c);
						}
					}
				}
				catch (IOException x)
				{
					System.err.println(x);
				}
				continue;
			}
			path = path + filesep + pkgpath + filesep;
			File file = new File(path);
			if (file.isDirectory() && file.exists())
			{
				String[] s = file.list(classFilter);
				for (int j = 0; j < s.length; ++j)
				{
					String c = pkg + '.' + s[j].substring(0, s[j].length() - 6);
					v.addElement(c);
				}
				//CAM: also do nested packages
				String[] sdir = file.list(dirFilter);
				for (int j = 0; j < sdir.length; j++)
				{
					String d = sdir[j];
					d = pkg + '.' + d;
					String[] subpacks = packageList(d);
					v.addAll(Arrays.asList(subpacks));
				}
				//CAM end
			}
		}
		String[] a = new String[v.size()];
		v.copyInto(a);
		return a;
	}
	public static void main(String[] argv) throws Exception
	{
		if (argv.length < 1)
		{
			System.err.print(
				"	$Id: ZipLock.java,v 1.10 2000/11/25 04:26:56 stuart Exp $\n"
					+ "	Finds all depencencies of selected classes.\n"
					+ "Usage:	java ZipLock [-a archive] [-x prefix] [-p package] class ...\n"
					+ "	-a archive	create archive with result\n"
					+ "	-p package	add all classes in a package\n"
					+ "	-x prefix	exclude classes beginning with prefix\n"
					+ "	-v			verbose\n"
					+ "	class		add a specific class\n");
			return;
		}
		Vector x = new Vector();
		String archive = null;
		ZipLock rc = new ZipLock();
		for (int i = 0; i < argv.length; ++i)
		{
			if (argv[i].startsWith("-"))
			{
				int n = argv[i].length();
				if (n >= 2)
					switch (argv[i].charAt(1))
					{
						case 'a' :
							if (n > 2)
								archive = argv[i].substring(2);
							else
								archive = argv[++i];
							continue;
						case 'x' :
							String exc = (n > 2) ? argv[i].substring(2) : argv[++i];
							x.addElement(exc);
							continue;
						case 'p' :
							String pkg = (n > 2) ? argv[i].substring(2) : argv[++i];
							String[] p = packageList(pkg);
							for (int j = 0; j < p.length; ++j)
								rc.addClass(p[j]);
							continue;
						case 'v' :
							rc.setVerbose(true);
							continue;
					}
				throw new Exception("Invalid switch " + argv[i]);
			}
			rc.addClass(argv[i]);
		}
		String[] xs = new String[x.size()];
		x.copyInto(xs);
		rc.setExcludes(xs);
		rc.start(archive);
	}

	/**
	 * Method setVerbose.
	 * @param b
	 */
	private void setVerbose(boolean b)
	{
		mVerbose = b;
	}

	public static class IntList
	{
		private int[] a = new int[8];
		private int size = 0;
		public final int size()
		{
			return size;
		}
		public void add(int i)
		{
			if (size >= a.length)
			{
				int[] na = new int[a.length * 2];
				System.arraycopy(a, 0, na, 0, size);
				a = na;
			}
			a[size++] = i;
		}
		public void removeAll()
		{
			size = 0;
		}
		public int[] elements()
		{
			int[] na = new int[size];
			System.arraycopy(a, 0, na, 0, size);
			return na;
		}
	}
}
