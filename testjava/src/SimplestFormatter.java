import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimplestFormatter extends Formatter
{
    public String format(LogRecord recLog)
    {
        StringBuffer sb = new StringBuffer(256);

        sb.append(formatMessage(recLog));
        sb.append(System.getProperty("line.separator"));

        return sb.toString();
    }
}
