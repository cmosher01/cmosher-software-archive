/**
 * @(#)Log.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS
 * SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES
 * THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK. Author :
 * Steve Yeong-Ching Hsueh
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;



/**
 * Log class writes message to a log
 */
public class Log
{
    private File log = null;

    private boolean logopened = false;

    private FileOutputStream fos = null;

    private static byte[] newline = new byte[2];



    /**
     * constructor
     */
    public Log(String filename)
    {
        newline[0] = '\r';
        newline[1] = '\n';
        log = new File(filename);
        try
        {
            fos = new FileOutputStream(log);
        }
        catch (IOException e)
        {
            logopened = false;
            System.out.println("Error: Can't open " + filename + " for writing");
            System.out.println(e.toString());
        }
        logopened = true;
    }

    /**
     * is log opened?
     */
    public boolean isLogopened()
    {

        return logopened;
    }

    /**
     * rotate this log(currently not implemented)
     */
    public boolean rotateLog()
    {

        return true;
    }

    /**
     * add message to this log
     */
    public boolean addMessage(String message)
    {

        return addMessage("N/A",message);

    }

    /**
     * add message to this log
     */
    public boolean addMessage(String source, String message)
    {
        if (!logopened)
            return false;


        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        // print out a bunch of interesting things
        StringBuffer sb_now = new StringBuffer();

        sb_now.append(calendar.get(Calendar.MONTH)).append("/");
        sb_now.append(calendar.get(Calendar.DAY_OF_MONTH)).append("/");
        sb_now.append(calendar.get(Calendar.YEAR)).append(" ");
        sb_now.append(calendar.get(Calendar.HOUR_OF_DAY)).append(":");
        sb_now.append(calendar.get(Calendar.MINUTE)).append(":");
        sb_now.append(calendar.get(Calendar.SECOND)).append(" ");
        String now = sb_now.toString();

        try
        {
            fos.write(now.getBytes());
            fos.write('\t');
            fos.write(source.getBytes());
            fos.write('\t');
            fos.write(message.getBytes());
            fos.write(newline);
            fos.flush();
        }
        catch (IOException e)
        {
            return false;
        }

        return true;
    }
}