/*
 * Created on May 26, 2004
 */
package com.surveysampling.bulkemailer.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.surveysampling.bulkemailer.util.DailyWindow;
import com.surveysampling.bulkemailer.util.ThrottlePrecise;

/**
 * Handles the checkpoint file for a job.
 * @author Chris Mosher
 */
public class Checkpoint
{
    private static final int CHECKPOINT_EVERY = 500;

    private final Job mJob;
    private final File mFile;
    private final File mFileBackup;

    private int mLastCheckpoint;

    // -- Values retrieved from the checkpoint file
    private int mPriority; // 0 = not found
    private int mHoldStatus = -1; // (user) hold status, 1=true, 0=false, -1=not found
    private int mTier; // 0 = not found
    private List<Date> mrThrottleTimes = new ArrayList<Date>();
    private String mWindowStart = ""; // "" = not found
    private String mWindowEnd = ""; // "" = not found
    private int mRate; // 0 = not found
    private int mRatePer;



    /**
     * Initializes a checkpoint handler for the given job.
     * @param job job to keep a checkpoint for
     * @param file checkpoint file to use
     * @param fileBackup backup (old) checkpoint file
     */
    public Checkpoint(Job job, File file, File fileBackup)
    {
        mJob = job;
        mFile = file;
        mFileBackup = fileBackup;
    }



    /**
     * @return the hold status
     */
    public int getHoldStatus()
    {
        return mHoldStatus;
    }

    /**
     * @return the tier
     */
    public int getTier()
    {
        return mTier;
    }

    /**
     * @return the priority
     */
    public int getPriority()
    {
        return mPriority;
    }

    /**
     * @return the last checkpoint
     */
    public int getLastCheckpoint()
    {
        return mLastCheckpoint;
    }

    /**
     * @return list of throttle times
     */
    public List<Date> getThrottleTimes()
    {
        return mrThrottleTimes;
    }

    /**
     * @return the rate
     */
    public int getRate()
    {
        return mRate;
    }

    /**
     * @return the rate per what?
     */
    public int getRatePer()
    {
        return mRatePer;
    }

    /**
     * @return the end of the sending window
     */
    public String getWindowEnd()
    {
        return mWindowEnd;
    }

    /**
     * @return the start of the sending window
     */
    public String getWindowStart()
    {
        return mWindowStart;
    }



    /**
     * Gets information from this job and writes
     * it to the checkpoint file. Note that this
     * method does not update the values of the
     * corresponding internal variables. The checkpoint
     * is only written once every 500 calls to this method
     * (based on the job's processedCount).
     */
    public void write()
    {
        write(false);
    }

    /**
     * Gets information from this job and writes
     * it to the checkpoint file. Note that this
     * method does not update the values of the
     * corresponding internal variables. The checkpoint
     * is only written once every 500 calls to this method
     * (based on the job's processedCount),
     * unless force is true in which case the checkpoint
     * file is always written.
     * @param force forces the file to be written if true
     */
    public void write(boolean force)
    {
        try
        {
            tryWrite(force);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets information from this job and writes
     * it to the checkpoint file. Note that this
     * method does not update the values of the
     * corresponding internal variables. The checkpoint
     * is only written once every 500 calls to this method
     * (based on the job's processedCount),
     * unless force is true in which case the checkpoint
     * file is always written.
     * @param force
     * @throws IOException
     */
    protected synchronized void tryWrite(boolean force) throws IOException
    {
        int checkpoint = mJob.getProcessedCount();
        if (force || checkpoint >= mLastCheckpoint + CHECKPOINT_EVERY)
        {
            /*
             * Save checkpoint. Even if we fail to write, still
             * update this saved checkpoint, that way (if force
             * is false) we will not try to write it again until
             * the next CHECKPOINT_EVERY time.
             */
            mLastCheckpoint = checkpoint;

            /*
             * Get all fields we need to save from the
             * job object into a Properties obejct.
             */
            Properties p = new Properties();
            p.setProperty("Record", Integer.toString(checkpoint));
            p.setProperty("Priority", Integer.toString(mJob.getPriority()));
            p.setProperty("Hold", mJob.isUserHold() ? "1" : "0");
            p.setProperty("Tier", Integer.toString(mJob.getTier()));



            // Get the job's throttle times (list of Dates)
            // and save each one in an "indexed" property
            // ThrottleWhen0, ThrottleWhen1, etc.
            ThrottlePrecise throttle = mJob.getThrottle();
            p.setProperty("Rate", Integer.toString(throttle.getRate()));
            p.setProperty("RatePer", Integer.toString(throttle.getRatePer()));
            List<Date> rTimes = new ArrayList<Date>();
            throttle.appendTimes(rTimes);
            int c = 0;
            for (final Date d : rTimes)
            {
                p.setProperty("ThrottleWhen"+Integer.toString(c++),Long.toString(d.getTime()));
            }



            DailyWindow window = mJob.getSendWindow();
            if (window.isAlwaysOpen())
            {
                p.setProperty("WindowStart","unlimited");
                p.setProperty("WindowEnd","unlimited");
            }
            else if (window.isNeverOpen())
            {
                throw new RuntimeException("window is never open");
            }
            else
            {
                p.setProperty("WindowStart",window.getStart().toString());
                p.setProperty("WindowEnd",window.getEnd().toString());
            }



            /*
             * First we remove any old backup file, then
             * we rename any existing checkpoint file to
             * the backup file.
             */
            mFileBackup.delete();
            mFile.renameTo(mFileBackup); // ??? assume OS provides atomic renames

            /*
             * Now write the properties to the checkpoint file
             */
            writeProperties(p);
        }
    }

    /**
     * Reads the information from the checkpoint file,
     * if it exists.
     */
    public void read()
    {
        if (!mFile.exists())
        {
            return;
        }
        try
        {
            tryRead();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reads the information from the checkpoint file
     * and updates each internal variable with the
     * value read from the file (or with a default
     * value if it doesn't exist in the checkpoint file).
     * @throws IOException
     */
    protected synchronized void tryRead() throws IOException
    {
        Properties p = new Properties();
        readProperties(p);

        mLastCheckpoint = Integer.parseInt(p.getProperty("Record", "0"));

        // Update the job priority
        mPriority = Integer.parseInt(p.getProperty("Priority", "0"));

        // Update the hold status
        // -1 if not found
        mHoldStatus = Integer.parseInt(p.getProperty("Hold", "-1"));

        mTier = Integer.parseInt(p.getProperty("Tier", "0"));

        // Load the Throttle times
        mrThrottleTimes.clear();
        int c = 0;
        boolean end = false;
        while (!end)
        {
            String sProp = "ThrottleWhen" + Integer.toString(c++);
            if (p.containsKey(sProp))
            {
                mrThrottleTimes.add(new Date(Long.parseLong(p.getProperty(sProp))));
            }
            else
            {
                end = true;
            }
        }

        mWindowStart = p.getProperty("WindowStart","");
        mWindowEnd = p.getProperty("WindowEnd","");
        mRate = Integer.parseInt(p.getProperty("Rate","0"));
        mRatePer = Integer.parseInt(p.getProperty("RatePer","1000"));
    }



    /**
     * Writes the given <code>Properties</code> object
     * to the checkpoint file.
     * @param p <code>Properties</code> to be written
     * @throws IOException
     */
    protected void writeProperties(Properties p) throws IOException
    {
        OutputStream ckpt = null;
        try
        {
            ckpt = new FileOutputStream(mFile);
            p.store(ckpt, "Bulk Emailer Job Checkpoint File");
        }
        finally
        {
            if (ckpt != null)
            {
                try
                {
                    ckpt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Reads values from the checkpoint file into
     * the given <code>Properties</code> object.
     * @param p <code>Properties</code> to be appended to
     * @throws IOException
     */
    protected void readProperties(Properties p) throws IOException
    {
        InputStream ckpt = null;
        try
        {
            ckpt = new FileInputStream(mFile);
            p.load(ckpt);
        }
        finally
        {
            if (ckpt != null)
            {
                try
                {
                    ckpt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
