package nu.mine.mosher.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Chris Mosher
 */
public class MessageOnlyFormatter extends Formatter
{
	private static final String lineSeparator = System.getProperty("line.separator");

	public synchronized String format(LogRecord record)
	{
		StringBuffer sb = new StringBuffer(256);

		String sMessage = record.getMessage();
		if (sMessage == null)
		{
			sMessage = "";
		}
		sb.append(sMessage);

		sb.append(lineSeparator);

		return sb.toString();
	}
}
