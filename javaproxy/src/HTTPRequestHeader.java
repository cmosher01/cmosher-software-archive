/**
 * @(#)HTTPRequestHeader.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE
 * SUITABILITY OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE
 * LIABLE FOR ANY DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR
 * OWN RISK. Author : Steve Yeong-Ching Hsueh
 */

import java.util.HashMap;
import java.util.StringTokenizer;



/**
 * This class contains common functions for parsing HTTP request headers and has
 * member elements for storing header values.
 */
class HTTPRequestHeader
{

    private String mMethod = null;

    private String mURI = null;

    private String mVersion = null;

    private String mPrimeHeader = "";

    private HashMap mHeaderFields = new HashMap();

    private boolean mMalFormedHeader = false;

    private int mContentLength = 0;



    /**
     * constructor
     */
    HTTPRequestHeader()
    {

    }

    /**
     * constructor
     */
    HTTPRequestHeader(String in)
    {
        if (!parseHeader(in))
            mMalFormedHeader = true;
    }

    /**
     * get first line of the request header
     */
    public String getPrimeHeader()
    {
        return mPrimeHeader;
    }

    /**
     * get header value by name
     */
    public String getHeader(String name)
    {
        return (String)mHeaderFields.get(name);
    }

    /**
     * get request method
     */
    public String getMethod()
    {
        return mMethod;
    }

    /**
     * get request URI
     */
    public String getURI()
    {
        return mURI;
    }

    /**
     * get HTTP version
     */
    public String getVersion()
    {
        return mVersion;
    }

    /**
     * get content length
     */
    public int getContentLength()
    {
        return mContentLength;
    }

    /**
     * get all headers in a Hashtable
     */
    public HashMap getHeaderFields()
    {
        return mHeaderFields;
    }

    /**
     * parse HTTP request headers
     */
    public boolean parseHeader(String input)
    {
        StringTokenizer st;
        String token, name, value, delimiter;
        int pos;


        if (input == null || input.equals(""))
        {
            return mMalFormedHeader = true;
        }


        mMalFormedHeader = false;

        if (input.endsWith("\r\n"))
            delimiter = "\r\n";
        else
            delimiter = "\n";

        // read the first line to get method, URI, and version
        if ((pos = input.indexOf(delimiter)) < 0)
        {
            mMalFormedHeader = true;
            return false;
        }

        mPrimeHeader = input.substring(0,pos);
        // System.out.println(primeheader);
        st = new StringTokenizer(mPrimeHeader," ");
        for (int i = 0; st.hasMoreTokens(); i++)
        {
            switch (i)
            {
                case 0:
                    mMethod = st.nextToken();
                break;
                case 1:
                    mURI = st.nextToken();
                break;
                case 2:
                    mVersion = st.nextToken();
                break;
                default:
                    mMalFormedHeader = true;
                    return false;
            }
        }

        if (mMethod == null || mURI == null || mVersion == null)
        {
            mMalFormedHeader = true;
            return false;
        }


        // remaining header fields
        st = new StringTokenizer(input.substring(pos),delimiter);
        while (st.hasMoreTokens())
        {
            token = st.nextToken();
            if ((pos = token.indexOf(": ")) < 0)
            {
                mMalFormedHeader = true;
                return false;
            }
            name = token.substring(0,pos);
            value = token.substring(pos + 2);
            // System.out.println(name + "<=>" + value);
            if (name.equalsIgnoreCase("Content-Length"))
            {
                mContentLength = Integer.parseInt(value);
            }
            mHeaderFields.put(name,value);
        }

        return true;
    }

    /**
     * see if this header is ok.
     */
    public boolean isMalFormedHeader()
    {
        return mMalFormedHeader;
    }
}

