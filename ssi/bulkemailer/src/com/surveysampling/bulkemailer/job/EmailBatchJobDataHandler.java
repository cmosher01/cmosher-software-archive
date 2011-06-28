package com.surveysampling.bulkemailer.job;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.surveysampling.util.FIFO;
import com.surveysampling.util.ThreadUtil;
import com.surveysampling.xml.XMLUtil;

/**
 * This is a SAX content handler that processes the XML in
 * a job spec file (jobid.XML file). This handler processes
 * only the &lt;rowset&gt; element, and its &lt;row&gt; children.
 * 
 * An object of this class runs an internal thread that reads
 * &lt;row&gt;s from the XML and puts them into an internal queue (of
 * a fixed size). (If the queue fills up, the thread blocks.)
 * The user calls getNextRow to get a row off queue.
 * 
 * @author Chris Mosher
 */
public class EmailBatchJobDataHandler extends DefaultHandler
{
	private boolean mInRowset = false;
	private boolean mInRow = false;
	private StringBuffer mCurrentValue = new StringBuffer(1024);
	private String mTagName = "";
	private final Map<String,String> mmapRow = new HashMap<String,String>();
	private final FIFO mRowQueue = new FIFO(64);
	private boolean mEOF = false;

	private int mRecNum = 0;
	private int mToBeSkipped = 0;

	private Object mlockGetRow = new Object();

	private File mSource = null;
    private final Thread mThread = new Thread(new Runnable()
    {
        @SuppressWarnings("synthetic-access")
        public void run()
        {
            try
            {
                doRun();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    },
	getClass().getName());



	/**
     * Parses the xml file
	 * @param xmlfile
	 */
	public void parse(File xmlfile)
	{
		mSource = xmlfile;
		mThread.start();
	}

	private void doRun()
	{
		try
		{
			parse();
		}
		catch (InterruptedException intex)
		{
            // just die
            Thread.interrupted();
            intex.printStackTrace();
		}
		catch (SAXException e)
		{
            e.printStackTrace();
			try
			{
				/*
				 * if we get an exception while reading data,
				 * put the exception onto our queue, so the
				 * next call to getNextRow will throw it.
				 */
				mRowQueue.add(e);
			}
			catch (InterruptedException intex)
			{
				/*
				 * This happens when the queue is full, so
				 * we are waiting to put the exception on
				 * it, AND this thread gets interrupted.
				 * I guess we should just die.
				 */
                Thread.interrupted();
                intex.printStackTrace();
			}
		}
	}

    private void parse() throws SAXException, InterruptedException
	{
		try
        {
            URL url = getClass().getClassLoader().getResource("com/surveysampling/bulkemailer/job/job.xsd");
            XMLUtil.parse(mSource,url.toString(),this);
        }
        catch (SAXException saxe)
        {
            /*
             * if we caught an InterruptedException nested inside
             * the SAXException, throw the interrupted exception.
             * Otherwise, rethrow the SAXException.
             */
            final Throwable nested = saxe.getCause();
            if (nested != null && nested instanceof InterruptedException)
                throw (InterruptedException)nested;

            throw saxe;
        }
        catch (IOException ioe)
        {
            /*
             * wrap any IOException in a SAXException (for convenience)
             */
            throw (SAXException)new SAXException().initCause(ioe);
        }
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	@Override
    public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException
	{
		if (Thread.currentThread().isInterrupted())
			throw (SAXException)new SAXException().initCause(new InterruptedException());

		if (mInRowset)
		{
			if (mInRow)
			{
				if (mToBeSkipped > 0)
				{
                    // do nothing
				}
				else
				{
					mTagName = qName;					
					mCurrentValue = new StringBuffer(1024);
				}
			}
			else
			{
				if (qName.equalsIgnoreCase("row"))
				{
					mInRow = true;
					mmapRow.clear();
				}
			}
		}
		else
		{
			if (qName.equalsIgnoreCase("rowset"))
				mInRowset = true;
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	@Override
    public void endElement(String uri, String name, String qName) throws SAXException
	{
		try
		{
			if (mInRowset)
			{
				if (mInRow)
				{
					if (qName.equalsIgnoreCase("row"))
					{
						if (mToBeSkipped > 0)
						{
							--mToBeSkipped;
						}
						else
						{
                            // this row is done, so add all its elements
                            // (in the map) to our internal queue of rows.
							Map<String,String> mapRow = new HashMap<String,String>();
							mapRow.putAll(mmapRow);
							mRowQueue.add(mapRow);							
						}
						mInRow = false;
					}
					else
					{
						if (mToBeSkipped > 0)
						{
                            // do nothing
						}
						else
						{
                            // for this row, add a new element to the map
							mmapRow.put(mTagName,mCurrentValue.toString().trim());
							mTagName = "";
						}
					}
				}
				else
				{
					if (qName.equalsIgnoreCase("rowset"))
					{
						mRowQueue.add(null); // indicate eof
						mInRowset = false;
					}
				}
			}
			else
			{
				// ignore everything outside of <rowset>
			}
		}
		catch (Exception e)
		{
			throw (SAXException)new SAXException().initCause(e);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
    public void characters(char[] ch, int start, int length)
	{
		if (mInRowset && mToBeSkipped <= 0)
		{
			mCurrentValue.append(ch,start,length);
		}
		else
		{
			// ignore everything outside of <rowset>
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@Override
    public void ignorableWhitespace(char[] ch, int start, int length)
	{
		// keep whitespace characters (just handle them as
		// ordinary characters)
		characters(ch,start,length);
	}

	/**
     * Gets the next row from this handler.
	 * @return next row
	 * @throws SAXException if an error happened during parsing
	 * @throws InterruptedException
	 */
	public Map<String,String> getNextRow() throws SAXException, InterruptedException
	{
		Object next = null;
		synchronized (mlockGetRow)
		{
			if (mEOF)
				return null;

			next = mRowQueue.remove();
			if (next == null)
			{
                mEOF = true;
				return null;
			}
			if (next instanceof SAXException)
				throw (SAXException)next;

			++mRecNum;
		}

		return (Map<String,String>)next;
	}

	/**
	 * Reads skip number of rows off the internal queue, and
	 * discards them. This method is not synchronized, and so
	 * should only be called before calling parse.
	 * 
	 * @param skip the number of rows to discard
	 */
	public void skip(int skip)
	{
		mToBeSkipped = skip;
		mRecNum = mToBeSkipped;
	}

	/**
	 * Gets the current record number.
	 * @return the current record number
	 */
	public int getRecordNumber()
	{
		synchronized (mlockGetRow)
		{
			return mRecNum;
		}
	}

	/**
	 * Stops this object's internal reading thread.
	 */
	public void close()
	{
		mThread.interrupt();
	}

	/**
	 * Waits for this object's internal thread to
	 * terminate. Call this method after calling close.
	 */
	public void join()
	{
		ThreadUtil.joinUninterruptable(mThread);
	}
}
