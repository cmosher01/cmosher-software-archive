/*
 * Created on Jun 4, 2004
 */
package com.surveysampling.bulkemailer;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.surveysampling.bulkemailer.job.Job;
import com.surveysampling.util.DeltaTime;
import com.surveysampling.util.text.ExcelCSVFieldizer;



/**
 * Manages the history of jobs in the Bulk Emailer.
 * The history is stored in text (comma separated
 * value) files. Not thread safe.
 * 
 * @author Chris Mosher
 */
public class JobHistory
{
    private static final char COMMA = ',';
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("MM/dd/yyyy"+COMMA+"HH:mm");
    private static final SimpleDateFormat YYYYMM = new SimpleDateFormat("yyyyMM");

    private final File mDirHistory;



    /**
     * Initializes the job history manager. The given
     * <code>File</code> represents a directory to contain
     * the history files; the directory must already exist.
     * @param dirHistory directory for history files
     */
    public JobHistory(File dirHistory)
    {
        mDirHistory = dirHistory;
    }



    /**
     * Appends the given <code>Job</code> to the
     * correct history file (based on the job's end time).
     * @param job
     * @throws IOException
     */
    public void put(Job job) throws IOException
    {
        BufferedWriter out = null;
        try
        {
            StringBuffer s = new StringBuffer(256);
            appendHistoryString(job,s);

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFile(new Date(job.getEndTime())),true)));

            out.write(s.toString());
            out.newLine();

            out.flush();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Throwable ignore)
                {
                    ignore.printStackTrace();
                }
            }
        }
    }

    /**
     * Reads a collection of <code>JobHistory.JobInfo</code>
     * objects from the history file(s). The jobs read are those
     * whose end times are greater than or equal to the given time.
     * @param dateAsOf as-of date
     * @param rJob Collection to append JobInfo objects to
     * @throws IOException if an error occurs trying to read the history file
     * @throws ParseException if a history record cannot be parsed
     * @throws NumberFormatException if a history record cannot be parsed
     * @throws NoSuchElementException if a history record cannot be parsed
     */
    public void getAsOf(Date dateAsOf, Collection<JobInfo> rJob) throws IOException, NoSuchElementException, NumberFormatException, ParseException
    {
        Calendar c = Calendar.getInstance();
        c.setTime(dateAsOf);

        File f = getFile(c.getTime());
        while (f.exists())
        {
            getAsOfFromFile(dateAsOf,f,rJob);
            c.add(Calendar.MONTH,1);
            f = getFile(c.getTime());
        }
    }



    /**
     * Reads a collection of <code>JobHistory.JobInfo</code>
     * objects from the given file. The jobs read are those
     * whose end times are greater than or equal to the given time.
     * @param dateAsOf as-of date
     * @param file history file to read from
     * @param rJob Collection to append JobInfo objects to
     * @throws IOException if an error occurs trying to read the history file
     * @throws ParseException if a history record cannot be parsed
     * @throws NumberFormatException if a history record cannot be parsed
     * @throws NoSuchElementException if a history record cannot be parsed
     */
    protected static void getAsOfFromFile(Date dateAsOf, File file, Collection<JobInfo> rJob) throws IOException, NoSuchElementException, NumberFormatException, ParseException
    {
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String s = in.readLine(); s != null; s = in.readLine())
            {
                addHistoryRowIfSince(s,dateAsOf,rJob);
            }
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Throwable ignore)
                {
                    ignore.printStackTrace();
                }
            }
        }
    }

    /**
     * Holds the information for one job
     * (as read from a history file).
     */
    public static class JobInfo
    {
        /**
         * job name
         */
        public String name;
        /**
         * job ID
         */
        public int jobID;
        /**
         * sample size (number of emails)
         */
        public int sampleSize;
        /**
         * number of emails processed
         */
        public int processed;
        /**
         * number of bad emails processed
         */
        public int bad;
        /**
         * start time
         */
        public Date start;
        /**
         * end time
         */
        public Date end;
    }

    /**
     * Parses the given history row and, if its end time is
     * greater than or equal to the given date, adds a
     * corresponding <code>JobInfo</code> object to the
     * given <code>Collection</code>.
     * @param historyRow the record from the history file
     * @param dateAsOf as-of date
     * @param rJob Collection to append JobInfo objects to
     * @throws ParseException if a history record cannot be parsed
     * @throws NumberFormatException if a history record cannot be parsed
     * @throws NoSuchElementException if a history record cannot be parsed
     */
    protected static void addHistoryRowIfSince(String historyRow, Date dateAsOf, Collection<JobInfo> rJob) throws ParseException, NumberFormatException, NoSuchElementException
    {
        Iterator i = new ExcelCSVFieldizer(historyRow).iterator();

        JobInfo job = new JobInfo();

        job.name = (String)i.next();
        job.jobID = Integer.parseInt((String)i.next());
        job.sampleSize = Integer.parseInt((String)i.next());
        job.processed = Integer.parseInt((String)i.next());
        job.bad = Integer.parseInt((String)i.next());
        i.next(); // ignore old E/m
        i.next(); // ignore status text

        String sDateStart = (String)i.next();
        String sTimeStart = (String)i.next();

        String sDateEnd = (String)i.next();
        String sTimeEnd = (String)i.next();

        job.end = formatDate.parse(sDateEnd+COMMA+sTimeEnd);
        if (job.end.before(dateAsOf))
        {
            return;
        }

        job.start = formatDate.parse(sDateStart+COMMA+sTimeStart);

        rJob.add(job);
    }

    /**
     * Gets the history file which does (or should) contain
     * the given date (treated as a job-end time).
     * @param dateFor job-end time
     * @return the history file
     */
    protected File getFile(Date dateFor)
    {
        StringBuffer filename = new StringBuffer(20);
        appendHistoryFileName(dateFor,filename);
        return new File(mDirHistory,filename.toString());
    }

    /**
     * Appends the name of the history file for the
     * given date to the given StringBuffer.
     * @param dateFor job-end time
     * @param filename StringBuffer to append file name to
     */
    protected static void appendHistoryFileName(Date dateFor, StringBuffer filename)
    {
        filename.append("Hist");
        filename.append(YYYYMM.format(dateFor));
        filename.append(".csv");
    }

    /**
     * Builds a string containing the job history data separated by commas.  
     * Used for writing to the history log.
     * 
     * The data fields include:
     *    job name,id,size,sent,bad,e/m,state,start date,start time,
     *    end date,end time,proc time,elap time  
     * 
     * @param job the job object from which the data is retrieved
     * @param s the StringBuffer to append the history record to
     */
    protected static void appendHistoryString(Job job, StringBuffer s)
    {
        // Get the job name and double any quotes
        String name = job.getJobName().replaceAll("\"","\"\"");
        // append quoted job name (and comma)
        s.append("\"").append(name).append("\"").append(COMMA);
        // append each field followed by a comma
        s.append(job.getJobID()).append(COMMA);
        s.append(job.getSampleSize()).append(COMMA);
        s.append(job.getProcessedCount()).append(COMMA);
        s.append(job.getBadCount()).append(COMMA);
        s.append("0").append(COMMA); // E/m (maintained for backward compatibility)
        s.append(job.getJobStatusText()).append(COMMA);

        s.append(formatDate.format(new Date(job.getStartTime()))).append(COMMA);

        s.append(formatDate.format(new Date(job.getEndTime()))).append(COMMA);

        DeltaTime.format(job.getEndTime()-job.getStartTime(), s);
    }
}
