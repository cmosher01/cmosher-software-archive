// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import java.lang.StringBuffer;
import java.util.Vector;

public class Formatify
{
	//*	--------------------	Formatify

	public
	Formatify()
	{
		mDelimiters = new Vector();
	}

	//*	--------------------	initForHTML

	public void
	initForHTML()
	{
		addDelimiter('*', "<b>", "</b>");
		addDelimiter('_', "<u>", "</u>");
		addDelimiter('!', "<i>", "</i>");
		addDelimiter('@', "<code>", "</code>");
		addDelimiter('-', "<del>", "</del>");
		addDelimiter('+', "<ins>", "</ins>");
		addDelimiter('^', "<sup>", "</sup>");
		addDelimiter('~', "<sub>", "</sub>");
	}

	//*	--------------------	initForHiplog

	public void
	initForHiplog()
	{
		addDelimiter('*', "[b]", "[/b]");
		addDelimiter('_', "[u]", "[/u]");
		addDelimiter('!', "[i]", "[/i]");
	}
 
	//*	--------------------	addDelimiter

	public void
	addDelimiter(char inDelimiter, String inStartTag, String inEndTag)
	{
		mDelimiters.addElement(new Delimiter(inDelimiter, inStartTag, inEndTag));
	}

	//*	--------------------	process(String)

	public String
	process(String inText)
	{
		return process(new StringBuffer(inText));
	}

	//*	--------------------	process(StringBuffer)

	public String
	process(StringBuffer inText)
	{
	int	numDelimiters = mDelimiters.size();
	
		for (int i = 0; i < numDelimiters; i++)
			processDelimiter((Delimiter) mDelimiters.elementAt(i), inText);

		return inText.toString();
	}

	//*	--------------------	processDelimiter

	private final void
	processDelimiter(Delimiter inDelimiter, StringBuffer inText)
	{
	boolean	done = false;
	int		offset = 0;
	
		while (! done)
		{
		int	start;
		int	end;
		
			start = findStartTag(inDelimiter.mDelimiter, inText, offset);
			
			if (-1 == start)
				return;
				
			end = findEndTag(inDelimiter.mDelimiter, inText, start + 1);
			
			if (-1 == end)
				return;

			//*	There must be at least one character between the tags
			if (end - start < 2)
				return;
				
			applyTags(inDelimiter, inText, start, end);
		}
	}

	//*	--------------------	findStartTag

	private final int
	findStartTag(char d, StringBuffer s, int offset)
	{
	int	len = s.length();
	
		for (int i = offset; i < len; i++)
		{
			if (d != s.charAt(i))
				continue;
				
			//*	Make sure that there is at least one character after the delimiter
			if (i >= len - 1)
				return -1;
				
			if (Character.isWhitespace(s.charAt(i + 1)))
				continue;
				
			return i;
		}
		
		return -1;
	}

	//*	--------------------	applyTags

	private final void
	applyTags(Delimiter inDelimiter, StringBuffer inText, int start, int end)
	{
		inText.deleteCharAt(start);
		inText.insert(start, inDelimiter.mStartTag);
		
		end = end - 1 + inDelimiter.mStartTag.length();
		
		inText.deleteCharAt(end);
		inText.insert(end, inDelimiter.mEndTag);
	}

	//*	--------------------	findEndTag

	private final int
	findEndTag(char d, StringBuffer s, int offset)
	{
	int	len = s.length();

		if (offset == 0)
			return -1;
	
		for (int i = offset; i < len; i++)
		{
			if (d != s.charAt(i))
				continue;
				
			if (Character.isWhitespace(s.charAt(i - 1)))
				continue;
				
			return i;
		}
		
		return -1;
	}

	//*	--------------------------------	Instance Variables

	Vector	mDelimiters;
}

class Delimiter
{
	Delimiter(char inDelimiter, String inStartTag, String inEndTag)
	{
		mDelimiter = inDelimiter;
		mStartTag = inStartTag;
		mEndTag = inEndTag;
	}

	//*	--------------------------------	Instance Variables

	char	mDelimiter;
	String	mStartTag;
	String	mEndTag;
}
