package com.surveysampling.bulkemailer.mta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.surveysampling.bulkemailer.util.HttpUtil;

/**
 * A URI for a Mail Transport Authority.
 * 
 * @author Chris Mosher
 */
public class MtaURI
{
	private final URI mURI;

	private final int mRate;
	private final int mTimeout;



    /**
     * Parses the given URI. The URI is of the
     * form "protocol://host:port?rate=n&timeout=n",
     * where port, rate, and timeout are optional. For
     * example "smtp://mail.surveysampling.com?rate=25000"
     * or "smtp://mail.surveysampling.com:2525?rate=30000&timeout=10000".
     * Port defaults to 25 (smtp) (regardless of the scheme), and
     * timeout defaults to 120000 ms (2 mins.).
     * @param sMTA
     * @throws URISyntaxException
     */
	public MtaURI(String sMTA) throws URISyntaxException
	{
        URI uri = new URI(sMTA);

        if (uri.getPort() < 0)
        {
            // default to port 25 (smtp)
            uri = new URI(uri.getScheme(),uri.getUserInfo(),uri.getHost(),25,uri.getPath(),uri.getQuery(),uri.getFragment());
        }
        mURI = uri;



        // pre-parse the uri to pull out the rate and timeout
        // values from the query string
        Map mapParamToValue = new HashMap();
        if (uri.getQuery() != null)
        {
            HttpUtil.parseQueryStringSimple(uri.getQuery(),mapParamToValue);
        }

        int nRate = 0;
        try
        {
            nRate = Integer.parseInt((String)mapParamToValue.get("rate"));
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            nRate = 0;
        }
        mRate = nRate;

        int nTimeout = 120000;
        try
        {
            nTimeout = Integer.parseInt((String)mapParamToValue.get("timeout"));
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            nTimeout = 120000;
        }
        mTimeout = nTimeout;
	}

    public URI getURI()
    {
        return mURI;
    }

	public int getRate()
	{
		return mRate;
	}

	public int getTimeout()
	{
		return mTimeout;
	}

	/**
	 * Returns the URI as a string
	 */
	public String toString()
	{
        return mURI.toASCIIString();
	}

	/**
	 * Checks if two MtaURIs are equal. Two
	 * MtaURIs are considered equal if they have
	 * the same protocol, host, and port.
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof MtaURI))
			return false;

		MtaURI that = (MtaURI)obj;
		return this.mURI.equals(that.mURI);
	}
}
