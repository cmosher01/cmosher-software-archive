import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HourlyFile
{
    private final static SimpleDateFormat formatYMDH = new SimpleDateFormat("yyyyMMddHH");
    private final static SimpleDateFormat formatYMDHMS = new SimpleDateFormat("yyyyMMddHHmmss");

    private final File mDir;
    private final String mBase;

    private String mLastFileName = "";
    private BufferedWriter mOut;

    private final Timer mTimer = new Timer(true);



    public HourlyFile(File dir, String base)
    {
        mDir = dir;
        mBase = base;
    }

    protected void init()
    {
        final int msHour = 1000*60*60;

        // round current time up to nearest whole hour
        long nextHour = (System.currentTimeMillis()+(msHour-1))/msHour*msHour;

        mTimer.scheduleAtFixedRate(new TimerTask(){ public void run() {}},nextHour,msHour);
    }

    public void appendTimestampLine(Date date, String s)
    {
        try
        {
            checkLogFile(date);
            if (mOut == null)
                return;

            mOut.write(formatYMDHMS.format(date));
            mOut.write(",");
            mOut.write(s);
            mOut.newLine();
        }
        catch (IOException e)
        {
            /*
             * If we have any problems opening or writing to
             * this file, just close the file and ignore.
             * We will try again next hour.
             */
            e.printStackTrace();
            close();
        }
    }

    protected void checkLogFile(Date date) throws IOException
    {
        // get the log file name associated with the given date
        String sThisFileName = getFileName(date);

        // See if we need to open a (different) file.
        if (mLastFileName.equalsIgnoreCase(sThisFileName))
            return;

        mLastFileName = sThisFileName;

        // open the new file
        openFile(sThisFileName);
    }

    /**
     * Opens the file with the given name as the current output file.
     * Closes any previous output file first.
     * 
     * @param sFileName the name of the file
     * @return BufferedWriter a buffered writer that writes to the new file
     */
    protected void openFile(String sFileName) throws IOException
    {
        close();

        File f = new File(mDir,sFileName);
        mOut = new BufferedWriter(new FileWriter(f.getAbsolutePath(),f.exists()));
    }

    protected void close()
    {
        if (mOut != null)
        {
            try
            {
                mOut.close();
            }
            catch (IOException e)
            {
                // we can safely ignore errors trying to close
                e.printStackTrace();
            }
            mOut = null;
        }
    }

    /**
     * Builds the filename corresponding to the
     * given date. Name is of the form:
     *     baseYYYYMMDDHH
     */
    protected String getFileName(Date date)
    {
        String sdate = formatYMDH.format(date);

        StringBuffer sb = new StringBuffer(mBase.length()+sdate.length());

        sb.append(mBase);
        sb.append(sdate);

        return sb.toString();
    }
}
