// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.util.StringUtils;
import danger.net.*;

import java.util.Calendar;

public class Network
{
	//*	--------------------	Post

	public static void
	Post(JournalDataRecord j)
	{
	StringBuffer	params = new StringBuffer();
	Calendar		date = Calendar.getInstance();
	String			journal = j.Journal();

		PrepParamData(params, "postevent");
		AddParam(params, "event", j.Entry());
		AddParam(params, "lineendings", "mac");
		AddParam(params, "subject", j.Subject());

		if (0 == j.Security())
			AddParam(params, "security", "public");
		else
			AddParam(params, "security", "private");

		if (! StringUtils.isEmpty(journal))
			AddParam(params, "usejournal", journal);

		date.setTimeInMillis(((long) j.Date()) * 1000);
		AddParam(params, "year", "" + date.get(Calendar.YEAR));
		AddParam(params, "mon", "" + date.get(Calendar.MONTH));
		AddParam(params, "day", "" + date.get(Calendar.DATE));
		AddParam(params, "hour", "" + date.get(Calendar.HOUR));
		AddParam(params, "min", "" + date.get(Calendar.MINUTE));
		
		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	Edit

	public static void
	Edit(JournalDataRecord j, boolean inDelete)
	{
	StringBuffer	params = new StringBuffer();
	Calendar		date = Calendar.getInstance();
	String			journal = j.Journal();
	
		PrepParamData(params, "editevent");
		AddParam(params, "itemid", "" + j.ItemID());

		if (inDelete)
			AddParam(params, "event", "");
		else
			AddParam(params, "event", j.Entry());
			
		AddParam(params, "lineendings", "mac");
		AddParam(params, "subject", j.Subject());

		if (0 == j.Security())
			AddParam(params, "security", "public");
		else
			AddParam(params, "security", "private");

		if (! StringUtils.isEmpty(journal))
			AddParam(params, "usejournal", journal);

		date.setTimeInMillis(((long) j.Date()) * 1000);

		AddParam(params, "year", "" + date.get(Calendar.YEAR));
		AddParam(params, "mon", "" + date.get(Calendar.MONTH));
		AddParam(params, "day", "" + date.get(Calendar.DATE));
		AddParam(params, "hour", "" + date.get(Calendar.HOUR));
		AddParam(params, "min", "" + date.get(Calendar.MINUTE));

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	Login

	public static void
	Login()
	{
	StringBuffer	params = new StringBuffer();

		PrepParamData(params, "login");
		AddParam(params, "clientversion", "Danger-hiptop/0.2.0");

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	GetFriends

	public static void
	GetFriends()
	{
	StringBuffer	params = new StringBuffer();

		PrepParamData(params, "getfriends");

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	AddFriend

	public static void
	AddFriend(String inFriendName)
	{
	StringBuffer	params = new StringBuffer();

		PrepParamData(params, "editfriends");
		AddParam(params, "editfriend_add_1_user", inFriendName);

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	DeleteFriend

	public static void
	DeleteFriend(String inFriendName)
	{
	StringBuffer	params = new StringBuffer();

		PrepParamData(params, "editfriends");
		AddParam(params, "editfriend_delete_" + inFriendName, "" + 1);

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	Sync

	public static void
	Sync(String inLastDate)
	{
	StringBuffer	params = new StringBuffer();

		PrepParamData(params, "syncitems");

		if (! StringUtils.isEmpty(inLastDate))
			AddParam(params, "lastsync", inLastDate);

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	SyncItem

	public static void
	SyncItem(int inItemNum)
	{
	StringBuffer	params = new StringBuffer();

		PrepParamData(params, "getevents");
		AddParam(params, "prefersubject", "0");
		AddParam(params, "noprops", "1");
		AddParam(params, "selecttype", "one");
		AddParam(params, "itemid", "" + inItemNum);
		AddParam(params, "lineendings", "mac");

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	GetSyncDelta

	public static void
	GetSyncDelta(String inLastDate)
	{
	StringBuffer	params = new StringBuffer();

		PrepParamData(params, "getevents");
		AddParam(params, "prefersubject", "0");
		AddParam(params, "noprops", "1");
		AddParam(params, "selecttype", "syncitems");
		AddParam(params, "lastsync", inLastDate);
		AddParam(params, "lineendings", "mac");

		HTTPConnection.post(kStr_Server, null, params.toString(), (short) 0, 0);
	}

	//*	--------------------	PrepParamData

	private static void
	PrepParamData(StringBuffer params, String mode)
	{
		AddParam(params, "mode", mode);
		AddParam(params, "user", LiveJournal.Instance().Username());
		AddParam(params, "password", LiveJournal.Instance().Password());
		AddParam(params, "ver", "1");
	}

	//*	--------------------	AddParam

	private static void
	AddParam(StringBuffer params, String argument, String value)
	{
		if (params.length() > 0)
			params.append('&');
			
		params.append(argument);
		params.append('=');
		params.append(StringUtils.urlEncodeUTF8(value));
	}

	//*	--------------------------------	Constants

	private static final String	kStr_Server		=	"http://www.livejournal.com/interface/flat";
}
