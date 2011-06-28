package com.surveysampling.bulkemailer.job;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.surveysampling.util.template.Template;
import com.surveysampling.xml.XMLUtil;

/**
 * This is a SAX content handler that processes the XML in
 * a job spec file (jobid.XML file).
 * Basically this class operates as follows:
 * When we start an element, set a flag to indicate that
 * we are "in" that element, and clear out the value string.
 * When we hit the end of an element, we save the value
 * string in the appropriate member variable (depending on
 * the element we are in). When we hit characters (or
 * ignorable whitespace), just append them to the value string.
 * 
 * This handler ignores the entire &lt;rowset&gt;&lt;/rowset&gt; element.
 * 
 * For &lt;message&gt; and &lt;headers&gt;, we store a {@link com.surveysampling.util.template.Template Template};
 * for other elements we store a StringBuffer or an int.
 * 
 * @author Chris Mosher
 */
public class EmailBatchJobHandler extends DefaultHandler
{
    private boolean mInRowset;
	private boolean mInDefaultTags;
	private boolean mInHeader;
	private boolean mInControl;
	private boolean mInName;
	private boolean mInSubmitter;
	private boolean mInNotify;
	private boolean mInRate;
    private boolean mInRatePer;
	private boolean mInWindow;
	private boolean mInStart;
	private boolean mInEnd;
	private boolean mInMsg;
    private boolean mInPriority;
    private boolean mInHold;
    private boolean mInTier;

	private StringBuffer mCurrentValue = new StringBuffer(1024);
	private String mTagName = "";
	
	private StringBuffer mName = new StringBuffer(1024);
	private StringBuffer mSubmitter = new StringBuffer(1024);
	private int mSampleSize;
	private StringBuffer mNotify = new StringBuffer(1024);
	private int mRate;
    private int mRatePer;
	private StringBuffer mStart = new StringBuffer(1024);
	private StringBuffer mEnd = new StringBuffer(1024);
    private int mPriority;
    private boolean mHold;
    private int mTier;
	private Map<String,String> mmapDefaultTags = new HashMap<String,String>();
	private Map<String,Template> mmapHeaders = new HashMap<String,Template>();

	private static final String delimStart = "<field>";
	private static final String delimEnd = "</field>";



	/**
	 * @param xmlfile
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(File xmlfile) throws SAXException, IOException
	{
        URL url = getClass().getClassLoader().getResource("com/surveysampling/bulkemailer/job/job.xsd");
		XMLUtil.parse(xmlfile,url.toString(),this);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	@Override
    public void startElement(String uri, String name, String qName, Attributes atts)
	{
		if (mInRowset)
		{
			// ignore entire <rowset>
		}
		else if (mInHeader)
		{
			mTagName = qName;
			mCurrentValue.setLength(0);
		}
		else if (mInDefaultTags)
		{
			mTagName = qName;
			mCurrentValue.setLength(0);
		}
		else if (mInControl)
		{
			mCurrentValue.setLength(0);
			if (mInWindow)
			{
				if (qName.equalsIgnoreCase("start"))
					mInStart = true;
				else if (qName.equalsIgnoreCase("end"))
					mInEnd = true;
			}
			else
			{
				if (qName.equalsIgnoreCase("name"))
					mInName = true;
				else if (qName.equalsIgnoreCase("submitter"))
					mInSubmitter = true;
				else if (qName.equalsIgnoreCase("notify-email"))
					mInNotify = true;
				else if (qName.equalsIgnoreCase("ship-rate"))
					mInRate = true;
                else if (qName.equalsIgnoreCase("ship-rate-per-ms"))
                    mInRatePer = true;
				else if (qName.equalsIgnoreCase("send-window"))
					mInWindow = true;
                else if (qName.equalsIgnoreCase("priority"))
                    mInPriority = true;
                else if (qName.equalsIgnoreCase("hold"))
                    mInHold = true;
                else if (qName.equalsIgnoreCase("tier"))
                    mInTier = true;
			}
		}
		else
		{
			mCurrentValue.setLength(0);
			if (qName.equalsIgnoreCase("rowset"))
			{
				mInRowset = true;
				mSampleSize = 0;
			}
			else if (qName.equalsIgnoreCase("message"))
			    mInMsg = true;
			else if (qName.equalsIgnoreCase("default-tag-values"))
				mInDefaultTags = true;
			else if (qName.equalsIgnoreCase("header"))
				mInHeader = true;
			else if (qName.equalsIgnoreCase("control"))
				mInControl = true;
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
				if (qName.equalsIgnoreCase("row"))
					++mSampleSize;
				else if (qName.equalsIgnoreCase("rowset"))
					mInRowset = false;
			}
			else if (qName.equalsIgnoreCase("message"))
			{
				mInMsg = false;
			}
			else if (mInHeader)
			{
				if (qName.equalsIgnoreCase("header"))
				{
					mInHeader = false;
				}
				else
				{
					mmapHeaders.put(mTagName,new Template(mCurrentValue.toString().trim(),delimStart,delimEnd));
					mTagName = "";
				}
			}
			else if (mInDefaultTags)
			{
				if (qName.equalsIgnoreCase("default-tag-values"))
				{
					mInDefaultTags = false;
				}
				else
				{
					mmapDefaultTags.put(mTagName,mCurrentValue.toString().trim());
					mTagName = "";
				}
			}
			else if (mInControl)
			{
				if (qName.equalsIgnoreCase("control"))
				{
					mInControl = false;
				}
				else
				{
					if (mInWindow)
					{
						if (qName.equalsIgnoreCase("send-window"))
						{
							mInWindow = false;
						}
						else
						{
							if (qName.equalsIgnoreCase("start"))
							{
								mInStart = false;
								mStart.append(mCurrentValue.toString().trim());
							}
							else if (qName.equalsIgnoreCase("end"))
							{
								mInEnd = false;
								mEnd.append(mCurrentValue.toString().trim());
							}
						}
					}
					else
					{
						if (qName.equalsIgnoreCase("name"))
						{
							mInName = false;
							mName.append(mCurrentValue.toString().trim());
						}
						else if (qName.equalsIgnoreCase("submitter"))
						{
							mInSubmitter = false;
							mSubmitter.append(mCurrentValue.toString().trim());
						}
						else if (qName.equalsIgnoreCase("notify-email"))
						{
							mInNotify = false;
							mNotify.append(mCurrentValue.toString().trim());
						}
						else if (qName.equalsIgnoreCase("ship-rate"))
						{
							mInRate = false;
							mRate = Integer.parseInt(mCurrentValue.toString().trim());
						}
                        else if (qName.equalsIgnoreCase("ship-rate-per-ms"))
                        {
                            mInRatePer = false;
                            mRatePer = Integer.parseInt(mCurrentValue.toString().trim());
                        }
                        else if (qName.equalsIgnoreCase("priority"))
                        {
                            mInPriority = false;
                            mPriority = Integer.parseInt(mCurrentValue.toString().trim());
                        }
                        else if (qName.equalsIgnoreCase("hold"))
                        {
                            mInHold = false;
                            mHold = true;
                        }
                        else if (qName.equalsIgnoreCase("tier"))
                        {
                            mInTier = false;
                            mTier = Integer.parseInt(mCurrentValue.toString().trim());
                        }
					}
				}
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
		if (mInRowset || mInMsg)
		{
			// ignore entire <rowset> and <message>
		}
		else
		{
			mCurrentValue.append(ch,start,length);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@Override
    public void ignorableWhitespace(char[] ch, int start, int length)
	{
		characters(ch,start,length);
	}

	/**
	 * Returns the map of default tag values.
	 * @return map of default tag value
	 */
	public Map<String,String> getDefaultTagValues()
	{
		return mmapDefaultTags;
	}

	/**
	 * Returns the map of headers (names to values).
     * @return map of headers
	 */
	public Map<String,Template> getHeaders()
	{
		return mmapHeaders;
	}

    /**
     * Returns the hold status.
     * @return whether on hold or not
     */
    public boolean getHold()
    {
        return mHold;
    }

	/**
	 * Returns the mName.
	 * @return the job name
	 */
	public String getName()
	{
		return mName.toString();
	}

	/**
	 * Returns the mNotify.
	 * @return notification email
	 */
	public String getNotify()
	{
		return mNotify.toString();
	}

    /**
     * Returns the priority.
     * @return job priority
     */
    public int getPriority()
    {
        return mPriority;
    }

    /**
     * Returns the tier.
     * @return tier
     */
    public int getTier()
    {
        return mTier;
    }

	/**
	 * Returns the max throughput rate.
	 * @return send rate
	 */
	public int getRate()
	{
		return mRate;
	}

    /**
     * Returns the max throughput rate.
     * @return send rate per what?
     */
    public int getRatePer()
    {
        return mRatePer;
    }

	/**
	 * Returns the sample size.
	 * @return sample size
	 */
	public int getSampleSize()
	{
		return mSampleSize;
	}

	/**
	 * Returns the send window start
	 * @return send window start
	 */
	public String getSendWindowStart()
	{
		return mStart.toString();
	}

	/**
	 * Returns the send window start
	 * @return send window end
	 */
	public String getSendWindowEnd()
	{
		return mEnd.toString();
	}

	/**
	 * Returns the submitter.
	 * @return submitter
	 */
	public String getSubmitter()
	{
		return mSubmitter.toString();
	}
}
