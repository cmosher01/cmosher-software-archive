package com.surveysampling.mosher.test;

import java.util.Collections;
import java.util.Map;

/**
 * @author chrism
 */
public class EmailData
{
	private final String mMessage;
	private final Map mmapHeaders;

	/**
	 * Constructor for EmailData.
	 * @param message The text of the E-Mail message
	 * @param mapHeaders The map of header names (String) to
	 * header values (String)
	 */
	public EmailData(String message, Map mapHeaders)
	{
		mMessage = message;
		mmapHeaders = Collections.unmodifiableMap(mapHeaders);
	}

	public String getMessage()
	{
		return mMessage;
	}

	public String getHeader(String name)
	{
		String s;

		if (mmapHeaders.containsKey(name))
			s = (String)mmapHeaders.get(name);
		else
			s = "";

		return s;
	}
}
