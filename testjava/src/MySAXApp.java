import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author chrism
 */
public class MySAXApp extends DefaultHandler //implements ContentHandler, LexicalHandler
{
	private PrintStream mOut;
	private boolean mInRowset = false;
	private boolean mInDefaultTags = false;
	private boolean mInMessage= false;
	private boolean mInHeader = false;
	private boolean mInControl = false;
	private boolean mInName = false;
	private boolean mInSubmitter = false;
	private boolean mInSampleSize = false;
	private boolean mInNotify = false;
	private boolean mInRate = false;
	private boolean mInWindow = false;
	private boolean mInStart = false;
	private boolean mInEnd = false;
	private StringBuffer mCurrentValue = new StringBuffer(1024);
	private StringBuffer mMessage = new StringBuffer(1024);
	private StringBuffer mName = new StringBuffer(1024);
	private StringBuffer mSubmitter = new StringBuffer(1024);
	private StringBuffer mSampleSize = new StringBuffer(1024);
	private StringBuffer mNotify = new StringBuffer(1024);
	private StringBuffer mRate = new StringBuffer(1024);
	private StringBuffer mStart = new StringBuffer(1024);
	private StringBuffer mEnd = new StringBuffer(1024);
	private String mTagName = "";
	private Map mmapDefaultTags = new HashMap();
	private Map mmapHeaders = new HashMap();

	public static void main(String args[]) throws Exception
	{
		MySAXApp handler = new MySAXApp(System.out);

//		System.setProperty("org.xml.sax.driver","org.apache.xerces.parsers.SAXParser");
//		XMLReader xr = XMLReaderFactory.createXMLReader();
//		xr.setContentHandler(handler);
//		xr.setErrorHandler(handler);
		SAXParser par = new SAXParser();
		par.setContentHandler(handler);
		par.setErrorHandler(handler);
//		par.setProperty("http://xml.org/sax/properties/lexical-handler",handler);

		// Parse each file provided on the
		// command line.
		for (int i = 0; i < args.length; i++)
		{
			FileReader r = new FileReader(args[i]);
			par.parse(new InputSource(r));
//			xr.parse(new InputSource(r));

		}
	}

	public MySAXApp(PrintStream streamout)
	{
		mOut = streamout;
	}

	////////////////////////////////////////////////////////////////////
	// Event handlers.
	////////////////////////////////////////////////////////////////////
//	public void startDTD(String name, String publicId, String systemId) throws SAXException {}
//	public void endDTD() throws SAXException {}
//	public void startEntity(String name) throws SAXException {}
//	public void endEntity(String name) throws SAXException {}
//	public void startCDATA() throws SAXException
//	{
//		if (mInRowset)
//			return;
//		System.out.print("<![CDATA[");
//	}
//	public void endCDATA() throws SAXException
//	{
//		if (mInRowset)
//			return;
//		System.out.print("]]>");
//	}
//	public void comment(char[] arg0, int arg1, int arg2) throws SAXException {}
//	public void startDocument()
//	{
//		//System.out.println("Start document");
//	}
	public void endDocument()
	{
		System.out.println("message: "+mMessage.toString());
		System.out.println("default-tag-values:");
		for (Iterator i = mmapDefaultTags.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry e = (Map.Entry) i.next();
			System.out.print((String)e.getKey());
			System.out.print(": ");
			System.out.println((String)e.getValue());
		}
		System.out.println("headers:");
		for (Iterator i = mmapHeaders.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry e = (Map.Entry) i.next();
			System.out.print((String)e.getKey());
			System.out.print(": ");
			System.out.println((String)e.getValue());
		}
		System.out.println("mName: "+mName);
		System.out.println("mSubmitter: "+mSubmitter);
		System.out.println("mSampleSize: "+mSampleSize);
		System.out.println("mNotify: "+mNotify);
		System.out.println("mRate: "+mRate);
		System.out.println("mStart: "+mStart);
		System.out.println("mEnd: "+mEnd);
	}

	public void startElement(String uri, String name, String qName, Attributes atts)
	{
        Util.unused(uri);
        Util.unused(name);
        Util.unused(atts);
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
				else if (qName.equalsIgnoreCase("sample-size"))
					mInSampleSize = true;
				else if (qName.equalsIgnoreCase("notify-email"))
					mInNotify = true;
				else if (qName.equalsIgnoreCase("ship-rate"))
					mInRate = true;
				else if (qName.equalsIgnoreCase("send-window"))
					mInWindow = true;
			}
		}
		else
		{
			if (qName.equalsIgnoreCase("rowset"))
				mInRowset = true;
			else if (qName.equalsIgnoreCase("default-tag-values"))
				mInDefaultTags = true;
			else if (qName.equalsIgnoreCase("message"))
			{
				mCurrentValue.setLength(0);
				mInMessage = true;
			}
			else if (qName.equalsIgnoreCase("header"))
				mInHeader = true;
			else if (qName.equalsIgnoreCase("control"))
				mInControl = true;
		}
	}

	public void endElement(String uri, String name, String qName)
	{
        Util.unused(uri);
        Util.unused(name);
		if (mInRowset)
		{
			if (qName.equalsIgnoreCase("rowset"))
				mInRowset = false;
		}
		else if (mInHeader)
		{
			if (qName.equalsIgnoreCase("header"))
			{
				mInHeader = false;
			}
			else
			{
				mmapHeaders.put(mTagName,mCurrentValue.toString().trim());
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
		else if (mInMessage)
		{
			if (qName.equalsIgnoreCase("message"))
			{
				mMessage.append(mCurrentValue.toString().trim());
				mInMessage = false;
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
					else if (qName.equalsIgnoreCase("sample-size"))
					{
						mInSampleSize = false;
						mSampleSize.append(mCurrentValue.toString().trim());
					}
					else if (qName.equalsIgnoreCase("notify-email"))
					{
						mInNotify = false;
						mNotify.append(mCurrentValue.toString().trim());
					}
					else if (qName.equalsIgnoreCase("ship-rate"))
					{
						mInRate = false;
						mRate.append(mCurrentValue.toString().trim());
					}
				}
			}
		}
	}

	public void characters(char ch[], int start, int length)
	{
		if (mInRowset)
		{
			// ignore entire <rowset>
		}
		else
		{
			mCurrentValue.append(ch,start,length);
		}
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
	{
		characters(ch,start,length);
	}
}
