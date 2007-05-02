package com.surveysampling.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Implements a persistant sequence of numbers.
 * Repeated calls to getNext return successive
 * numbers. The current number is saved (and read
 * from) a text file. If the file doesn't exist,
 * it is created, and the number returned is 1.
 * @author Chris Mosher
 */
public class SequenceNumberPersist
{
	private final File mFile;
	private int mNum;

	public SequenceNumberPersist(File filePersist) throws IOException
	{
		mFile = filePersist;
		load();
	}

	public int getNext() throws IOException
	{
		int n = mNum++;
		store();
		return n;
	}

	protected void load() throws IOException
	{
		Properties p = new Properties();
		try
        {
            p.load(new FileInputStream(mFile));
        }
        catch (FileNotFoundException e)
        {
            // file not found is OK
        }
		mNum = Integer.parseInt(p.getProperty("SequenceNumber","1"));
	}

    protected void store() throws IOException
	{
		Properties p = new Properties();
		p.setProperty("SequenceNumber",Integer.toString(mNum));
		p.store(new FileOutputStream(mFile),"SequenceNumberPersist File");
	}
}
