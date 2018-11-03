package com.surveysampling.apps.QueryRunner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;


public class WarningGenerator
{
	private final Statement statement;
	private final ResultSet resultset;

	public WarningGenerator(final Statement statement)
	{
		this.statement = statement;
		this.resultset = null;
	}

	public WarningGenerator(final ResultSet resultset)
	{
		this.statement = null;
		this.resultset = resultset;
	}

	public SQLWarning getWarnings() throws SQLException
	{
		if (this.statement != null)
		{
			return this.statement.getWarnings();
		}
		if (this.resultset != null)
		{
			return this.resultset.getWarnings();
		}
		throw new IllegalStateException();
	}

	public void clearWarnings() throws SQLException
	{
		if (this.statement != null)
		{
			this.statement.clearWarnings();
			return;
		}
		if (this.resultset != null)
		{
			this.resultset.clearWarnings();
			return;
		}
		throw new IllegalStateException();
	}
}
