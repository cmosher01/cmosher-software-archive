package nu.mine.mosher.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formats a log record as a CSV line:
 * date,time,levelName,className,methodName,message\n
 * followed by throwable stack dump, if any.
 * 
 * @author Chris Mosher
 */
public class SimplestFormatter extends Formatter
{
    private final String lineSeparator = System.getProperty("line.separator");
    private final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss.SSS");
	private final Date date = new Date();

    public synchronized String format(LogRecord record)
    {
        StringBuffer sb = new StringBuffer(256);

		date.setTime(record.getMillis());
        sb.append(formatDate.format(date));

        appendField(sb,record.getLevel().getLocalizedName());
        appendField(sb,record.getSourceClassName());
        appendField(sb,record.getSourceMethodName());
        appendField(sb,record.getMessage());

        sb.append(lineSeparator);

        appendThrowable(sb, record.getThrown());

        return sb.toString();
    }

    protected void appendThrowable(StringBuffer buf, Throwable throwable)
    {
        if (throwable == null)
        {
            return;
        }

        try
        {
            StringWriter sw = new StringWriter(8192);
            throwable.printStackTrace(new PrintWriter(sw));
            buf.append(sw.getBuffer());
        }
        catch (Throwable ignore)
        {
        }
    }

    protected void appendField(StringBuffer buf, String s)
    {
        if (s == null)
        {
            s = "";
        }

        buf.append(",");
        buf.append(s);
    }
}
