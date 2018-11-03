package com.quuxo.jdbctool;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;

public class ResultPrinter
{
	public void printResults(ResultSet results) throws SQLException
	{
		exhumeWarnings(results);

		ResultSetMetaData meta = results.getMetaData();
		int cols = meta.getColumnCount();

		String[] names = new String[cols];
		for (int i = 0; i < cols; i++)
		{
			names[i] = meta.getColumnName(i + 1);
		}

		int[] widths = new int[cols];
		int maxRows = 100;
		ArrayList<String[]> rows = new ArrayList<>(maxRows);
		while (results != null)
		{
			// init widths
			for (int i = 0; i < cols; i++)
			{
				widths[i] = names[i].length();
			}

			// get values
			for (int row = 0; row < maxRows; row++)
			{
				if (!results.next())
				{
					results.close();
					results = null;
					break;
				}
				String[] values = new String[cols];
				for (int i = 0; i < cols; i++)
				{
					String v = results.getString(i + 1);
					String colorOn = "";
					String colorOff = "";
					if (v != null)
					{
						v = filter(v);
					}
					else
					{
						v = "<NULL>";
						// colorOn = ANSI_PURPLE;
						// colorOff = ANSI_RESET;
					}
					values[i] = colorOn + v + colorOff;
					widths[i] = Math.max(widths[i], v.length());
				}
				rows.add(values);
				exhumeWarnings(results);
			}
			if (results != null)
			{
				results.close();
				results = null;
			}

			// print it
			printLine(widths);
			printRow(names, widths);
			printLine(widths);
			for (String[] values : rows)
			{
				printRow(values, widths);
			}
			printLine(widths);
		}
		System.out.println();
	}

	private static void printRow(String[] values, int[] widths)
	{
		System.out.print("|");
		for (int i = 0; i < values.length; i++)
		{
			System.out.print(" ");
			System.out.print(values[i]);
			for (int w = values[i].length(); w < widths[i]; w++)
			{
				System.out.print(" ");
			}
			System.out.print(" |");
		}
		System.out.println();
	}

	private static void printLine(int[] widths)
	{
		System.out.print("-");
		for (int i = 0; i < widths.length; i++)
		{
			for (int w = -3; w < widths[i]; w++)
			{
				System.out.print("-");
			}
		}
		System.out.println();
	}

	private static void exhumeWarnings(ResultSet results) throws SQLException
	{
		SQLWarning w = results.getWarnings();
		while (w != null)
		{
			printLineWarning(w.toString());
			w = w.getNextWarning();
		}
		results.clearWarnings();
	}

	private static void printLineWarning(String message)
	{
		System.out.println("Warning: " + message);
	}

	private static String filter(final String message)
	{
		return message.trim().replace("\r\n", "\n").replace("\r", "\n").replace("\n", "\\n");
	}
}
