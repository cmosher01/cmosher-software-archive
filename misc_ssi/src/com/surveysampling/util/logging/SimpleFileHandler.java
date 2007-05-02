/*
 * Created on May 21, 2004
 */
package com.surveysampling.util.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Like <code>java.util.logging.FileHandler</code> but
 * without the patterned file rotation stuff, and without
 * handling concurrent access from multiple JVMs. Objects
 * of this class are thread-safe.
 * 
 * @author Chris Mosher
 */
public class SimpleFileHandler extends Handler 
{
    private final File file;

    private BufferedWriter outLog;



    /**
     * Initializes the handler to write to the given file
     * @param file the log file to be written (appended) to
     */
    public SimpleFileHandler(File file)
    {
        super();
        this.file = file;
    }

    /**
     * Writes the given <code>LogRecord</code> to this object's log file.
     * If the log file doesn't exist, it is created first (along with
     * any parent directories as necessary). Any buffered data is
     * then flushed to the log file.
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     * @param record description of the log event
     */
    public synchronized void publish(LogRecord record)
    {
        if (!isLoggable(record))
        {
            return;
        }

        open();
        if (outLog == null)
        {
            return;
        }

        try
        {
            outLog.write(getFormatter().format(record));
        }
        catch (Exception e)
        {
            reportError("Cannot write to log file", e, ErrorManager.WRITE_FAILURE);
        }

        flush();
    }

    /**
     * Opens this object's log file (for appending).
     * If the file is already open, this method does nothing.
     * This method creates the file if it doesn't exist,
     * along with any necessary parent directories.
     */
    protected void open()
    {
        if (outLog != null)
        {
            return;
        }
        try
        {
            file.getParentFile().mkdirs();
            outLog = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
        }
        catch (Exception e)
        {
            reportError("Cannot open log file", e, ErrorManager.OPEN_FAILURE);
        }
    }

    /**
     * Flushes any buffered data to the log file.
     * If the log file is not open, this method does nothing.
     */
    public synchronized void flush()
    {
        if (outLog == null)
        {
            return;
        }
        try
        {
            outLog.flush();
        }
        catch (Exception e)
        {
            reportError("Cannot flush log file", e, ErrorManager.FLUSH_FAILURE);
        }
    }

    /**
     * Closes the log file.
     * If the log file is already closed, this method does nothing.
     */
    public synchronized void close()
    {
        if (outLog == null)
        {
            return;
        }
        try
        {
            outLog.close();
        }
        catch (Exception e)
        {
            reportError("Cannot close log file", e, ErrorManager.CLOSE_FAILURE);
        }
        outLog = null;
    }
}
