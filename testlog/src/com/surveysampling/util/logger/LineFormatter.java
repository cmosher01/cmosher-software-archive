package com.surveysampling.util.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * One-line format for logging via Java 1.4 Logger.
 * 
 * @author erik
 * @date Mar 3, 2003
 */
public class LineFormatter extends Formatter
{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    /**
     * @see java.util.logging.Formatter#format(LogRecord)
     */
    public String format(LogRecord record)
    {
        String line;
        
        //?? output blank line for null message
        if (record.getMessage() == null)
        {
            line = System.getProperty("line.separator");
        }
        else
        {
            String date = DATE_FORMAT.format(new Date(record.getMillis()));
            String message = formatMessage(record);
            
            // extract UNqualified name of class that issued message
            String source = record.getSourceClassName();
            int lastp = source.lastIndexOf('.');
            source = source.substring(lastp+1);
            
            //?? any need to limit max line length?
            
            line = "|"+date
                 + "\t" + record.getLevel()  //?? tab-delim?
                 + "\t" + source
                 + "\t" + message;

            line += System.getProperty("line.separator");
        }
        return line;
    }
}
