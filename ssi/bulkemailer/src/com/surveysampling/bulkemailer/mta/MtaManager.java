package com.surveysampling.bulkemailer.mta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.surveysampling.util.Flag;

/**
 * Manages a bank of Email servers (MTAs).
 * 
 * @author Chris Mosher
 */
public class MtaManager implements Iterable<Mta>
{
    private final List<Mta> mrMta = new LinkedList<Mta>();
    private final Map<Integer,Mta> mmapIDtoMta = new HashMap<Integer,Mta>();
    private int mLastID;
    private final File mDirMTA;
    private final File mFile;
    private final Flag bCheckSendable = new Flag(true);



    /**
     * @param dirMTA
     */
    public MtaManager(File dirMTA)
    {
        mDirMTA = dirMTA;
        mFile = new File(mDirMTA,"mta.txt");
    }



    /**
     * @return iterator over the MTAs
     */
    public Iterator<Mta> iterator()
    {
        return mrMta.iterator();
    }


    /**
     * @throws IOException
     */
    public void load() throws IOException
    {
        boolean ok = false;
        try
        {
            tryLoad();
            ok = true;
        }
        finally
        {
            if (!ok)
            {
                close();
            }
        }
    }

    /**
     * @throws IOException
     */
    protected void tryLoad() throws IOException
    {
        List<Integer> rid = new ArrayList<Integer>();
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(mFile)));
            for (String s = in.readLine(); s != null; s = in.readLine())
            {
                s = s.trim();
                if (s.length() == 0)
                {
                    continue;
                }

                rid.add(Integer.valueOf(s));
            }
        }
        catch (final FileNotFoundException e)
        {
            e.printStackTrace();
            System.err.println("There will be no MTAs initially, because the mta.txt file was not found.");
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
        if (rid.isEmpty())
        {
            mLastID = 0;
        }
        else
        {
            mLastID = Collections.max(rid);
        }

        for (final int id : rid)
        {
            Mta mta = new Mta(id,this);
            try
            {
                mta.load();
            }
            catch (Throwable e)
            {
                System.err.println("Error reading state of mta from "+mta.getFile().getCanonicalPath());
                e.printStackTrace();
                continue;
            }
            mta.startAutoSave();
            synchronized (mrMta)
            {
                mrMta.add(mta);
                mmapIDtoMta.put(new Integer(id),mta);
            }
        }
        checkSend();
    }

    /**
     * Save the state of this manager's list of MTAs.
     * This method does not serialize the MTAs.
     * @throws IOException
     */
    public void save() throws IOException
    {
        BufferedWriter out = null;
        try
        {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mFile)));
            for (final Mta mta : mrMta)
            {
                out.write(""+mta.getID());
                out.newLine();
            }
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
     * Gets the first sendable MTA. If there are
     * no sendable MTAs, this method blocks.
     * @param minTier minimum tier of MTA to get
     * @return Mta the first sendable MTA
     * @throws InterruptedException 
     */
    public synchronized Mta waitForSendableMta(int minTier) throws InterruptedException
    {
        Mta mta = getSendableMta(minTier);
        while (mta == null)
        {
            /*
             * There are no sendable MTAs, so we
             * just pause here. (Note that since
             * this method is called by the job
             * worker threads, no jobs will send
             * messages while we wait here.)
             * We will be woken up when another
             * thread calls checkSend.
             */
            bCheckSendable.waitToSetFalse();

            mta = getSendableMta(minTier);
        }

        return mta;
    }

    /**
     * Flags that this manager needs to re-check
     * to see which MTA is sendable.
     */
    public void checkSend()
    {
        bCheckSendable.set(true);
    }

    /**
     * Closes this manager and all its MTAs.
     */
    public void close()
    {
        synchronized (mrMta)
        {
            for (final Mta mta : this)
            {
                mta.close();
            }
            mrMta.clear();
        }
    }

    /**
     * Gets the first mta in our list of mtas that is greater than
     * or equal to the given tier, is not over its maximum allowed
     * throughput, is not on hold, and hasn't been marked as bad.
     * @param minTier the minimum tier of MTA to get
     * @return the sendable Mta, or null if there are none
     */
    public Mta getSendableMta(int minTier)
    {
        synchronized (mrMta)
        {
            for (final Mta mta : this)
            {
                if (mta.getTier() >= minTier && mta.isSendable() && !mta.isOnHold() && !mta.isBadIO())
                {
                    return mta;
                }
            }
            return null;
        }
    }

    /**
     * Gets the MTA directory (to save its state in)
     * @return MTA directory
     */
    public File getDirMTA()
    {
        return mDirMTA;
    }

    /**
     * Moves the specified MTA higher in the list.
     * @param id of the MTa to move
     * @throws MtaNotFoundException
     */
    public void upMta(int id) throws MtaNotFoundException
    {
        Mta mta = getByID(id);
        synchronized (mrMta)
        {
            int i = mrMta.indexOf(mta);
            if (i <= 0)
            {
                return;
            }

            mrMta.remove(i);
            mrMta.add(--i,mta);
        }
    }

    /**
     * Moves the specified MTA lower in the list.
     * @param id of the MTa to move
     * @throws MtaNotFoundException
     */
    public void downMta(int id) throws MtaNotFoundException
    {
        Mta mta = getByID(id);
        synchronized (mrMta)
        {
            int i = mrMta.indexOf(mta);
            if (i >= mrMta.size()-1)
            {
                return;
            }

            mrMta.remove(i);
            mrMta.add(++i,mta);
        }
    }

    /**
     * Removes the specified MTA.
     * @param id
     * @throws MtaNotFoundException
     */
    public void deleteMta(int id) throws MtaNotFoundException
    {
        Mta mta = getByID(id);
        
        synchronized (mrMta)
        {
            if (mrMta.contains(mta))
            {
                mrMta.remove(mta);
                mta.close();
            }
            mmapIDtoMta.remove(new Integer(id));
        }
    }

    /**
     * Adds a new MTA.
     * @return the new MTA
     */
    public Mta addMta()
    {
        synchronized (mrMta)
        {
            Mta mta = new Mta(++mLastID,this);
            mta.startAutoSave();
            mrMta.add(mta);
            mmapIDtoMta.put(new Integer(mLastID),mta);

            checkSend();

            return mta;
        }
    }

    /**
     * Gets an MTA.
     * @param id ID of the MTA to get
     * @return the MTA (never null)
     * @throws MtaNotFoundException
     */
    public Mta getByID(int id) throws MtaNotFoundException
    {
        Mta mta = mmapIDtoMta.get(id);
        if (mta == null)
        {
            throw new MtaNotFoundException();
        }
        return mta;
    }
}
