/*
 * Created on May 21, 2004
 */
package com.surveysampling.util.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Like <code>java.util.logging.FileHandler</code> but
 * the log file rotation is based on time, not size.
 * For each log record published, this class checks its
 * event time, and writes it to the appropriate day's log file.
 * This class doesn't handle log files being
 * accessed concurrent from multiple JVMs.
 * 
 * @author Chris Mosher
 */
public class DailyFileHandler extends Handler 
{
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    private final File dir;
    private final String base;

    private String sLog = "";
    private BufferedWriter outLog;



    /**
     * Initializes a <code>DailyFileHandler</code> to create
     * log files named baseYYYYMMDD.log in the given directory,
     * where base is the given prefix string, and YYYYMMDD is
     * the year, month, and day of the event time in the
     * published log record.
     * @param dir log directory
     * @param base log file base name
     */
    public DailyFileHandler(File dir, String base)
    {
        super();
        this.dir = dir;
        this.base = base;
    }

    /**
     * Writes the given log message to its correct
     * daily log file.
     */
    public synchronized void publish(LogRecord record)
    {
        if (!isLoggable(record))
        {
            return;
        }

        /*
         * If the YYYYMMDD string for this log record
         * is different that the previous YYYYMMDD we
         * used, then close the previous file and open
         * the new file.
         */
        String sLogNew = fmt.format(new Date(record.getMillis()));
        if (!sLogNew.equals(sLog))
        {
            close();
            sLog = sLogNew;
            open();
        }
        if (outLog == null)
        {
            // we couldn't open this file
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
     * Opens the current log file.
     */
    protected void open()
    {
        try
        {
            dir.mkdirs();
            if (!dir.isDirectory())
            {
                throw new IOException("Cannot create directory "+dir.getAbsolutePath());
            }
            outLog = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir,base+sLog+".log"),true)));
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
     * Closes the current log file. If the log file is
     * already closed, this method does nothing.
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
        sLog = "";
    }
}
