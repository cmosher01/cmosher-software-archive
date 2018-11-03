package com.surveysampling.apps.QueryRunner;
/**
 * Classification for messages from MS SQL Server, when the following are in effect:
 * <pre>
 * SET STATISTICS PROFILE ON;
 * SET STATISTICS IO ON;
 * SET STATISTICS TIME ON;
 * </pre>
 * Note that this does not include result sets, which is what PLANs come back as.
 * 
 * @author christopher_mosher
 */
public enum MessageType
{
	/**
	 * Sentinal value indicating no more message received, for this batch.
	 */
	END_OF_MESSAGES,

	/**
	 * Any other, unrecognized, message, such as output from PRINT statements.
	 */
	OTHER,

	/**
	 * "SQL Server Execution Times:\n   CPU time = 0 ms,  elapsed time = 0 ms."
	 */
	EXECUTION_TIMES,

	/**
	 * "SQL Server parse and compile time: \n   CPU time = 0 ms, elapsed time = 1 ms."
	 */
	PARSE_TIMES,

	/**
	 * "Table 'Worktable'. Scan count 0, logical reads 0, physical reads 0, read-ahead reads 0, lob logical reads 0, lob physical reads 0, lob read-ahead reads 0."
	 */
	READ_COUNTS,;

	public static MessageType guessMessageType(final String msg)
	{
		if (msg.isEmpty())
		{
			return END_OF_MESSAGES;
		}
		if (msg.startsWith("SQL Server Execution"))
		{
			return EXECUTION_TIMES;
		}
		if (msg.startsWith("SQL Server parse and compile"))
		{
			return PARSE_TIMES;
		}
		if (msg.startsWith("Table \'") && msg.contains("logical reads"))
		{
			return READ_COUNTS;
		}
		return OTHER;
	}
}
