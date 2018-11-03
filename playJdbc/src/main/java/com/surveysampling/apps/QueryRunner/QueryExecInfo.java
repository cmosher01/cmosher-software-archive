package com.surveysampling.apps.QueryRunner;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueryExecInfo
{
	private static final int MAX_ROWS = 256;

	private static final Pattern reTimes = Pattern.compile("^.*CPU time = (\\d+) ms,\\s*elapsed time = (\\d+) ms\\.$");
	private static final Pattern reReads = Pattern.compile("^Table \'[^\']+\'\\.\\s*(.*)$");
	private static final Pattern reIoLogicalReads = Pattern.compile("^.*logical reads\\s*(\\d+)$");

	private List<String> columnsPlan;
	private List<ArrayList<String>> rowsPlan;
	private List<String> columns;
	private List<ArrayList<String>> rows;
	private List<String> other = new ArrayList<String>(32);

	private int msElapsed;
	private int msCpu;
	private int readsLogical;



	public int getElapsed()
	{
		return this.msElapsed;
	}

	public int getCpu()
	{
		return this.msCpu;
	}

	public int getReads()
	{
		return this.readsLogical;
	}



	private static int getLogicalReads(final String stats)
	{
		int readsLogicalTot = 0;

		final StringTokenizer strtok = new StringTokenizer(stats, ",");
		while (strtok.hasMoreTokens())
		{
			final String stat = strtok.nextToken().trim();
			final Matcher maLogicalReads = reIoLogicalReads.matcher(stat);
			if (maLogicalReads.matches())
			{
				final int readsLogical = Integer.parseInt(maLogicalReads.group(1));
				readsLogicalTot += readsLogical;
			}
		}

		return readsLogicalTot;
	}



	public void addExecutionTimes(final String msg)
	{
//		System.out.println("EXECUTION TIMES {" + msg + "}");
		final Matcher m = reTimes.matcher(msg);
		if (!m.matches())
		{
			throw new IllegalStateException();
		}
		final int e = Integer.parseInt(m.group(2));
		this.msElapsed += e;
		final int c = Integer.parseInt(m.group(1));
		this.msCpu += c;
	}

	public void addParseTimes(final String msg)
	{
//		System.out.println("PARSE TIMES {" + msg + "}");
	}

	public void addReadCounts(final String msg)
	{
//		System.out.println("READ COUNTS {" + msg + "}");
		final Matcher m = reReads.matcher(msg);
		if (!m.matches())
		{
			throw new IllegalStateException();
		}
		final int g = getLogicalReads(m.group(1));
		this.readsLogical += g;
	}

	public void addOther(final String msg)
	{
//		System.out.println("OTHER MESSAGE {" + msg + "}");
		final String msgTrimmed = msg.trim();
		if (!msgTrimmed.isEmpty())
		{
			this.other.add(msgTrimmed);
		}
	}

	public void addResultSet(final ResultSet r) throws SQLException
	{
//		System.out.println("RESULT SET {}");

		final List<String> cols = new ArrayList<String>(64);
		getColumns(r, cols);

		final boolean plan = checkForQueryPlanResultSet(cols);

		if (plan)
		{
			this.columnsPlan = cols;
			this.rowsPlan = new ArrayList<ArrayList<String>>(MAX_ROWS);
			getFirstRows(r, this.rowsPlan);
		}
		else
		{
			this.columns = cols;
			this.rows = new ArrayList<ArrayList<String>>(MAX_ROWS);
			getFirstRows(r, this.rows);
		}
	}



	private static void getFirstRows(final ResultSet r, final List<ArrayList<String>> addTo) throws SQLException
	{
		final ResultSetMetaData m = r.getMetaData();

		final int cc = m.getColumnCount();

		int ir = 0;
		while (r.next() && ir++ < MAX_ROWS)
		{
			final ArrayList<String> row = new ArrayList<String>(cc);
			for (int ic = 0; ic < cc; ++ic)
			{
				row.add(r.getString(ic + 1));
			}
			addTo.add(row);
		}

		while (r.next())
		{
			/* exhaust */
		}
	}



	private static boolean checkForQueryPlanResultSet(final List<String> columns)
	{
		/* @formatter:off */
		return
			columns.size() >= 4 &&
			columns.get(0).equalsIgnoreCase("ROWS") &&
			columns.get(1).equalsIgnoreCase("EXECUTES") &&
			columns.get(2).equalsIgnoreCase("STMTTEXT") &&
			columns.get(3).equalsIgnoreCase("STMTID");
		/* @formatter:on */
	}



	private static void getColumns(final ResultSet r, final List<String> addTo) throws SQLException
	{
		final ResultSetMetaData m = r.getMetaData();

		final int cc = m.getColumnCount();

		for (int ic = 0; ic < cc; ++ic)
		{
			addTo.add(m.getColumnLabel(ic + 1));
		}
	}

	public void dump(final PrintStream out)
	{
		if (this.msElapsed == 0 && this.readsLogical == 0 && this.msCpu == 0 && this.columnsPlan == null && this.columns == null)
		{
			/* we got nothing */
			return;
		}

		if (this.msElapsed < 10 && this.readsLogical < 10 && this.msCpu < 10)
		{
			/* don't output the insignificant ones */
			/* return; */
		}

		out.println("   ELAPSED MS: " + this.msElapsed);
		out.println("       CPU MS: " + this.msCpu);
		out.println("LOGICAL READS: " + this.readsLogical);
		if (this.columnsPlan != null)
		{
			out.println("   QUERY PLAN: ");
			dumpResultSet(this.columnsPlan, this.rowsPlan, out);
		}

		out.println();
		for (final String msg : this.other)
		{
			out.println("PRINT: " + msg);
		}

		out.println("=====================================================================================");
		out.println();
		out.println();
		out.println();
	}



	private static String filter(String value)
	{
		value = value.replace("\r\n", "\n");
		value = value.replace("\r", "\n");
		value = value.replace("\n", "\\n");
		value = value.replace("\"", "\"\"");
		return value;
	}

	private static String quote(String value)
	{
		if (value == null)
		{
			return "[NULL]"; /* with no quotes */
		}

		value = filter(value);

		return "\"" + value + "\"";
	}

	private static final char DELIM = '\t';

	private void dumpResultSet(final List<String> columns, final List<ArrayList<String>> rows, final PrintStream out)
	{
		final int cc = columns.size();

		for (int ic = 0; ic < cc; ++ic)
		{
			if (ic > 0)
			{
				out.print(DELIM);
			}
			out.print(quote(columns.get(ic)));
		}
		out.println();

		for (int ir = 0; ir < rows.size(); ++ir)
		{
			for (int ic = 0; ic < cc; ++ic)
			{
				if (ic > 0)
				{
					out.print(DELIM);
				}
				out.print(quote(rows.get(ir).get(ic)));
			}
			out.println();
		}
	}
}
