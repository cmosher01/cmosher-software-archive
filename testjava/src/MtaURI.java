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

	/**
	 * Gets the protocol portion of the URI.
	 */
	public String getProtocol()
	{
		return mProtocol;
	}

	/**
	 * Gets the host portion of the URI.
	 */
	public String getHost()
	{
		return mHost;
	}

	/**
	 * Gets the port portion of the URI.
	 */
	public int getPort()
	{
		return mPort;
	}

	/**
	 * Returns the URI as a string of the form
	 * "protocol://host:port"
	 */
	public String toString()
	{
		StringBuffer s = new StringBuffer(64);
		s.append(mProtocol);
		s.append("://");
		s.append(mHost);
		s.append(":");
		s.append(mPort);
		return s.toString();
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

		MtaURI o = (MtaURI)obj;
		return
			mProtocol.equalsIgnoreCase(o.mProtocol) &&
			mHost.equalsIgnoreCase(o.mHost) &&
			mPort==o.mPort;
	}
}
