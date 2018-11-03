import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;





/* all input files must be of the form DATABASE.SCHEMA.PROCNAME.sql */

public class main
{
	public static void main(final String... args) throws IOException
	{
		if (args.length == 0)
		{
			throw new IllegalArgumentException("usage: java -jar scriptproc.jar DB.SCM.PROC.sql [...]");
		}

		final List<File> rFile = new ArrayList<File>(16);
		for (final String line : args)
		{
			System.err.println("input line: " + line);

			final String filename = getFileNameFromLine(line);
			if (filename.isEmpty())
			{
				continue;
			}
			final File file = new File(filename).getAbsoluteFile().getCanonicalFile();
			if (file.canRead())
			{
				rFile.add(file);
				System.err.println("added file: " + file.getPath());
			}
			else
			{
				throw new IOException("cannot read file " + file.getPath());
			}
		}

		System.err.println("files to be processed:");

		for (final File f : rFile)
		{
			System.err.println(f.getName().toUpperCase());
		}



		System.err.println("processing...");

		for (final File f : rFile)
		{
			System.err.println(f.getName().toUpperCase() + "...");
			processFile(f);
		}

		System.out.flush();
		System.err.flush();
	}

	private static void processFile(final File f) throws IOException
	{
		final StringTokenizer strtok = new StringTokenizer(f.getName().toUpperCase(), ".");
		final String db = strtok.hasMoreTokens() ? strtok.nextToken() : "";
		final String schema = strtok.hasMoreTokens() ? strtok.nextToken() : "";
		final String obj = strtok.hasMoreTokens() ? strtok.nextToken() : "";

		if (db.isEmpty() || schema.isEmpty() || obj.isEmpty())
		{
			throw new IllegalArgumentException(f.getName()+" is not of the form DATABASE.SCHEMA.PROCNAME.sql");
		}

		System.out.println("USE "+db+";");
		System.out.println("GO");
		System.out.println();

		System.out.println("DECLARE @ROUTINE_TYPE VARCHAR(64);");
		System.out.println("SELECT @ROUTINE_TYPE = ROUTINE_TYPE FROM INFORMATION_SCHEMA.ROUTINES WHERE SPECIFIC_SCHEMA = \'"+schema+"\' AND SPECIFIC_NAME = \'"+obj+"\';");
		System.out.println("IF (@ROUTINE_TYPE = \'PROCEDURE\') BEGIN");
		System.out.println("  DROP PROCEDURE "+obj+";");
		System.out.println("END;");
		System.out.println("ELSE IF (@ROUTINE_TYPE = \'FUNCTION\') BEGIN");
		System.out.println("  DROP FUNCTION "+obj+";");
		System.out.println("END;");
		System.out.println("GO");
		System.out.println();

		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		for (String s = in.readLine(); s != null; s = in.readLine())
		{
			System.out.println(s);
		}
		in.close();
		System.out.println("GO");
	}

	private static String getFileNameFromLine(final String line)
	{
		final StringTokenizer strtok = new StringTokenizer(line);
		while (strtok.hasMoreTokens())
		{
			final String tok = strtok.nextToken().trim();
			final char c = tok.charAt(0);
			if (c == '.' || c == '/' || Character.isUnicodeIdentifierStart(c))
			{
				return tok;
			}
		}
		return "";
	}
}
