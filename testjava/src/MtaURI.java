package com.surveysampling.bulkemailer.mta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.surveysampling.bulkemailer.util.HttpUtil;

/**
 * A URI for a Mail Transport Authority. URI is of the
 * form "protocol://host[:port]", for example,
 * "smtp://mail.surveysampling.com:2625", or
 * "smtp://mail.surveysampling.com". If the port
 * is omitted, it defaults to 25.
 * Does not perform any checking on the URI.
 * 
 * @author Chris Mosher
 */
public class MtaURI
{
	private final URI mURI;
	private final int mRate;
	private final int mTimeout;

	/**
	 * Constructs an MtaURI given a URI.
	 */
	public MtaURI(String sMTA) throws URISyntaxException
	{
        URI uri = new URI(sMTA);

        if (!uri.getScheme().equalsIgnoreCase("smtp"))
        {
            throw new URISyntaxException(uri.getScheme(),"scheme must be smtp");
        }

        if (uri.getPort() < 0)
        {
            // default to port 25 (smtp)
            uri = new URI(uri.getScheme(),uri.getUserInfo(),uri.getHost(),25,uri.getPath(),uri.getQuery(),uri.getFragment());
        }
        mURI = uri;

        Map mapParamToValue = new HashMap();
        HttpUtil.parseQueryStringSimple(uri.getQuery(),mapParamToValue);
        int nRate = 0;
        try
        {
            nRate = Integer.parseInt((String)mapParamToValue.get("rate"));
        }
        catch (Throwable e)
        {
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
