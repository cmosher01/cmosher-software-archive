/*
 * Created on May 21, 2004
 */
package nu.mine.mosher.logging;

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

public class DailyFileHandler extends Handler 
{
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    private final File dir;
    private final String base;

    private String sLog = "";
    private BufferedWriter outLog;



    public DailyFileHandler(File dir, String base)
    {
        super();
        this.dir = dir;
        this.base = base;
    }

    public synchronized void publish(LogRecord record)
    {
        if (!isLoggable(record))
        {
            return;
        }

        String sLogNew = fmt.format(new Date(record.getMillis()));
        if (!sLogNew.equals(sLog))
        {
            close();
            sLog = sLogNew;
            open();
        }
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
    }

    protected void open()
    {
        try
        {
            if (!dir.exists())
            {
                dir.mkdirs();
            }
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

    public synchronized void close() throws SecurityException
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
