/**
 * JdbcTool: Command line pain relief for JDBC databases.
 * Copyright (C) 2007, Quuxo Software.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package com.quuxo.jdbctool;



import gnu.getopt.Getopt;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Command line program to execute SQL statements interactively.
 * 
 * @author <a href="mailto:michael@quuxo.com">Michael Gratton</a>
 */
public class JdbcTool implements AutoCloseable
{
	public static final String ESC = "\u001B";
	public static final String ANSI_RESET = ESC + "[0m";
	public static final String ANSI_BLACK = ESC + "[30m";
	public static final String ANSI_RED = ESC + "[31m";
	public static final String ANSI_GREEN = ESC + "[32m";
	public static final String ANSI_YELLOW = ESC + "[33m";
	public static final String ANSI_BLUE = ESC + "[34m";
	public static final String ANSI_PURPLE = ESC + "[35m";
	public static final String ANSI_CYAN = ESC + "[36m";
	public static final String ANSI_WHITE = ESC + "[37m";

	private static final String[] driverNames = new String[]
	{ "net.sourceforge.jtds.jdbc.Driver" };

	private Connection connection;
	private Statement statement;

	public static void main(String[] argv) throws SQLException, IOException
	{
		String password = null;
		String user = null;

		Getopt g = new Getopt("JdbcTool", argv, "p:Pu:");
		int c;
		while ((c = g.getopt()) != -1)
		{
			switch (c)
			{
				case 'p':
				password = g.getOptarg();
				break;
				case 'P':
				password = promptUser("Enter password");
				break;
				case 'u':
				user = g.getOptarg();
				break;
				default:
				printError("JdbcTool: unknown option `" + c + "'");
				System.exit(-1);
			}
		}

		int i = g.getOptind();
		if (i >= argv.length)
		{
			printError("No JDBC URL specified.");
			System.exit(-1);
		}
		final String url = argv[i];

		try (final JdbcTool jt = new JdbcTool(url, user, password))
		{

			++i;
			if (i >= argv.length)
			{
				jt.start();
			}
			else
			{
				try (final BufferedReader sqlScript = new BufferedReader(new InputStreamReader(new FileInputStream(new File(
					argv[i])))))
				{
					jt.script(sqlScript);
				}
			}
		}
	}

	private void script(final BufferedReader sqlReader) throws IOException, SQLException
	{
		final String sql = readEntireStream(sqlReader);
		execute(sql);
	}

	private static String readEntireStream(final BufferedReader sql) throws IOException
	{
		final StringBuilder sb = new StringBuilder();
		for (String sLine = sql.readLine(); sLine != null; sLine = sql.readLine())
		{
			sb.append(sLine);
			sb.append("\r\n");
		}
		return sb.toString();
	}

	private JdbcTool(String url, String username, String password) throws SQLException
	{
		loadDrivers();
		this.connection = DriverManager.getConnection(url, username, password);
	}

	private static void loadDrivers()
	{
		for (String name : JdbcTool.driverNames)
		{
			try
			{
				Class.forName(name);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws SQLException
	{
		this.statement.close();
		this.connection.close();
	}

	private void start()
	{
		final Console con = System.console();
		boolean exitProgram = false;
		while (!exitProgram)
		{
			try
			{
				final String line = con.readLine("jdbc> ");
				if (line != null)
				{
					switch (line)
					{
						case "quit":
						case "exit":
						{
							exitProgram = true;
							break;
						}
						default:
						{
							execute(line);
						}
					}
				}
			}
			catch (final SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void execute(String line) throws SQLException
	{
		this.statement = this.connection.createStatement();
		boolean hasResults = this.statement.execute(line);
		if (hasResults)
		{
			final ResultPrinter printer = new ResultPrinter();
			printer.printResults(this.statement.getResultSet());
		}
		else
		{
			System.out.println();
			System.out.println("Updated: " + this.statement.getUpdateCount());
			System.out.println();
		}
	}

	private static String promptUser(String message)
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			printError(message + ": ");
			return in.readLine();
		}
		catch (IOException ioe)
		{
			printError(ioe.getMessage());
			return null;
		}
	}

	private static void printError(String message)
	{
		System.err.println(message);
	}
}
