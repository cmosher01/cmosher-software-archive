package com.surveysampling.apps.QueryRunner;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.LinkedList;
import java.util.Queue;


public class SqlMessageStream
{
	private final WarningGenerator warnings;

	private final Queue<String> m = new LinkedList<String>();



	public SqlMessageStream(final WarningGenerator statement) throws SQLException
	{
		this.warnings = statement;
		if (this.warnings == null)
		{
			throw new IllegalStateException();
		}
	}



	private static String filter(String message)
	{
		message = message.trim();
		message = message.replace("\r\n", "\n");
		message = message.replace("\r", "\n");
		message = message.replace("\n", "\\n");
		return message;
	}

	private SQLWarning getWarningChain() throws SQLException
	{
		final SQLWarning w = this.warnings.getWarnings();
		this.warnings.clearWarnings();
		return w;
	}

	private void fill() throws SQLException
	{
		for (SQLWarning w = getWarningChain(); w != null; w = w.getNextWarning())
		{
			final String filteredMessage = filter(w.getMessage());
			if (!filteredMessage.isEmpty())
			{
				this.m.offer(filteredMessage);
			}
		}
	}

	public String get() throws SQLException
	{
		if (this.m.size() <= 0)
		{
			fill();
		}
		if (this.m.size() <= 0)
		{
			return "";
		}
		return this.m.poll();
	}
}
