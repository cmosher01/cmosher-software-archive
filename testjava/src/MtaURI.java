package com.surveysampling.bulkemailer.mta;

import java.util.StringTokenizer;

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
	private final int mPort;
	private final String mProtocol;
	private final String mHost;

	/**
	 * Constructs an MtaURI given a URI.
	 */
	public MtaURI(String uri)
	{
		StringTokenizer t = new StringTokenizer(uri,":/");



		if (t.hasMoreTokens())
			mProtocol = t.nextToken();
		else
			mProtocol = "";



		if (t.hasMoreTokens())
			mHost = t.nextToken();
		else
			mHost = "";



		String sPort = "";
		if (t.hasMoreTokens())
			sPort = t.nextToken();

		int nPort = 25;
		try
		{
			nPort = Integer.parseInt(sPort);
		}
		catch (NumberFormatException e)
		{
			nPort = 25;
		}
		mPort = nPort;
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
